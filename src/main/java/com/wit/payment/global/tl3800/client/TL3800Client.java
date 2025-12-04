/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.tl3800.client;

import static com.wit.payment.global.tl3800.proto.Proto.HEADER_BYTES;

import com.wit.payment.global.tl3800.proto.JobCode;
import com.wit.payment.global.tl3800.proto.TLPacket;
import com.wit.payment.global.tl3800.transport.TLTransport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TL3800Client implements AutoCloseable {

  private final TLTransport t;
  private final int ackWaitMs;
  private final int respWaitMs;
  private final int maxAckRetry;

  // 결제 최종 응답까지 여유 있게
  private static final int FOLLOWUP_WINDOW_MS = 180_000;

  // TL 헤더 내 오프셋 (STX 기준)
  // STX(1) + ID(16) + DT(14) + JOB(1) + RESP(1) + LEN(2)
  private static final int POS_DT = 1 + 16; // 17
  private static final int POS_JOB = POS_DT + 14; // 31
  private static final int POS_LEN = POS_JOB + 1 + 1; // 33

  // ------- 내부 값 타입들 -------

  /** TL 헤더: 잡코드, 데이터 길이, 원본 35바이트(raw)를 함께 보관 */
  private record Header(JobCode jobCode, int dataLen, byte[] raw) {}

  /** 프레임 공통 super-type */
  private interface Frame {}

  /** EVENT 프레임 (ACK/NAK 금지, tail만 소비) */
  private static final class EventFrame implements Frame {

    private final Header header;

    private EventFrame(Header header) {
      this.header = header;
    }

    public Header header() {
      return header;
    }
  }

  /** 일반 응답 프레임 (TLPacket로 파싱 완료된 프레임) */
  private static final class NormalFrame implements Frame {

    private final Header header;
    private final TLPacket packet;

    private NormalFrame(Header header, TLPacket packet) {
      this.header = header;
      this.packet = packet;
    }

    public Header header() {
      return header;
    }

    public TLPacket packet() {
      return packet;
    }
  }

  // ------- ctor / lifecycle -------

  public TL3800Client(TLTransport transport, int ackWaitMs, int respWaitMs, int maxAckRetry) {
    this.t = transport;
    this.ackWaitMs = ackWaitMs;
    this.respWaitMs = respWaitMs;
    this.maxAckRetry = maxAckRetry;
  }

  public void open() throws Exception {
    t.open();
  }

  @Override
  public void close() {
    try {
      t.close();
    } catch (Exception ignore) {
    }
  }

  // ------- public API -------

  public TLPacket requestResponse(TLPacket req) throws Exception {
    final byte[] frame = req.toBytes();
    log.info("[TL3800] >> SEND job={} len={} HEX={}", req.jobCode, frame.length, hex(frame));

    // 요청 잡코드에 대응하는 "기대 응답" 잡코드 (예: B → b)
    JobCode expectedFinal = expectedResponseJob(req.jobCode);

    int tries = 0;
    while (true) {
      // 혹시 이전 쓰레기 바이트가 남아있다면 정리
      drainRx(120);

      // 요청 프레임 송신
      t.write(frame);
      sleepQuiet(8);

      // 1차: ACK / NAK / 즉시 STX를 짧게 대기
      Integer first = waitAckNakStx(ackWaitMs);
      if (first != null) {
        if (first == 0x15) { // NAK
          if (++tries <= maxAckRetry) {
            log.warn("[TL3800] << NAK → retry {}/{}", tries, maxAckRetry);
            continue;
          }
          throw new IllegalStateException("NAK received (exceeded retry)");
        }

        if (first == 0x06 || first == 0x02) { // ACK 또는 즉시 STX
          if (first == 0x06) {
            log.debug("[TL3800] << ACK");
          } else {
            log.debug("[TL3800] << STX (immediate)");
          }
          return handleInitialResponse(expectedFinal, req);
        }
      }

      // 2차 폴백: ACK가 없으면 STX/ACK/NAK 를 추가로 대기
      log.debug(
          "[TL3800] no-ACK within {} ms → waiting STX/ACK/NAK up to {} ms", ackWaitMs, respWaitMs);
      long start = System.currentTimeMillis();
      while ((System.currentTimeMillis() - start) < respWaitMs) {
        int b = t.readByte(50);
        if (b == 0x02 || b == 0x06) {
          if (b == 0x06) {
            log.debug("[TL3800] << late ACK");
          } else {
            log.debug("[TL3800] << late STX (immediate)");
          }
          return handleInitialResponse(expectedFinal, req);
        } else if (b == 0x15) {
          if (++tries <= maxAckRetry) {
            log.warn("[TL3800] << late NAK → retry {}/{}", tries, maxAckRetry);
            break;
          }
          throw new IllegalStateException("NAK received (exceeded retry)");
        } else if (b < 0) {
          // timeout step
          continue;
        } else {
          log.debug("[TL3800] << skip 0x{}", String.format("%02X", b));
        }
      }

      throw new IllegalStateException("ACK timeout");
    }
  }

  // ------- high-level 응답 처리 -------

  /**
   * ACK(또는 즉시 STX) 이후의 "첫 프레임"을 읽어 처리한다. 첫 프레임이 EVENT이면 후속 프레임을 기다리고, 일반 프레임이면 expectedFinal에 맞는지
   * 확인 후 필요 시 FOLLOWUP_WINDOW 동안 추가 프레임을 받는다.
   */
  private TLPacket handleInitialResponse(JobCode expectedFinal, TLPacket req) throws Exception {
    Frame frame = readFrame(respWaitMs, req);

    if (frame instanceof EventFrame ef) {
      log.warn(
          "[TL3800] EVENT frame received; waiting next non-EVENT frame (expect={})", expectedFinal);
      return waitFollowUp(expectedFinal);
    }

    NormalFrame nf = (NormalFrame) frame;
    TLPacket first = nf.packet();

    if (matchesExpected(expectedFinal, first.jobCode)) {
      return first;
    }

    log.warn(
        "[TL3800] unexpected first job={} (expect={}) — entering follow-up wait",
        first.jobCode,
        expectedFinal);
    return waitFollowUp(expectedFinal);
  }

  /** FOLLOWUP_WINDOW 동안 다음 프레임들을 계속 수신(매번 ACK)하여 expected 잡코드가 오면 반환. EVENT 프레임은 tail만 소비하고 무시. */
  private TLPacket waitFollowUp(JobCode expected) throws Exception {
    long deadline = System.currentTimeMillis() + FOLLOWUP_WINDOW_MS;

    while (System.currentTimeMillis() < deadline) {
      long remaining = deadline - System.currentTimeMillis();
      if (remaining <= 0) {
        break;
      }

      int perTry = (int) Math.min(respWaitMs, remaining);

      try {
        Frame frame = readFrame(perTry, null);

        if (frame instanceof EventFrame) {
          // EVENT 는 consumeEventFrame 에서 이미 로그 처리됨
          log.info("[TL3800] << RECV(seq) job=EVENT (ignored)");
          continue;
        }

        NormalFrame nf = (NormalFrame) frame;
        TLPacket pkt = nf.packet();
        log.info("[TL3800] << RECV(seq) job={} dataLen={}", pkt.jobCode, pkt.data.length);

        if (matchesExpected(expected, pkt.jobCode)) {
          return pkt;
        }

        log.warn("[TL3800] unexpected job={} (expect={}) — keep waiting", pkt.jobCode, expected);
      } catch (IllegalArgumentException e) {
        log.warn("[TL3800] follow-up parse failed: {}", e.getMessage());
      } catch (IllegalStateException e) {
        log.debug("[TL3800] follow-up per-try timeout: {}", e.getMessage());
      }
    }

    throw new IllegalStateException(
        "Follow-up window exceeded (" + FOLLOWUP_WINDOW_MS + " ms) without final " + expected);
  }

  // ------- frame 단위 primitive -------

  /**
   * STX를 기다렸다가 헤더를 읽고, EVENT면 tail을 읽어 버린 뒤 EventFrame 반환, NORMAL이면 tail을 읽어 strict/lenient 파싱 후
   * NormalFrame 반환.
   */
  private Frame readFrame(int waitMs, TLPacket req) throws Exception {
    Header header = readHeaderWithin(waitMs);

    if (header.jobCode() == JobCode.EVENT) {
      consumeEventFrame(header);
      return new EventFrame(header);
    }

    TLPacket pkt = readTailAndParse(header, req);
    return new NormalFrame(header, pkt);
  }

  /** STX를 대기하면서 헤더(35B)를 읽어 Header 객체로 만든다. 슬라이딩/추가 sanity 없이 "단순하게" 한 프레임 기준으로만 읽는다. */
  private Header readHeaderWithin(int waitMs) throws Exception {
    long deadline = System.currentTimeMillis() + waitMs;

    while (System.currentTimeMillis() < deadline) {
      int b = t.readByte(Math.min(50, waitMs));
      if (b < 0) {
        continue;
      }
      if (b != 0x02) {
        log.debug("[TL3800] << skip 0x{}", String.format("%02X", b));
        continue;
      }

      // STX 이후 34바이트를 그대로 읽어서 헤더를 구성
      final int restLen = HEADER_BYTES - 1; // 34
      byte[] rest = new byte[restLen];
      int n = t.readFully(rest, restLen, respWaitMs);
      if (n != restLen) {
        log.warn("[TL3800] simple header short: got={} need={}", n, restLen);
        throw new IllegalStateException("short header after STX");
      }

      byte[] raw = new byte[HEADER_BYTES];
      raw[0] = 0x02;
      System.arraycopy(rest, 0, raw, 1, restLen);

      JobCode job = JobCode.of(raw[POS_JOB]);
      int dataLen = (raw[POS_LEN] & 0xFF) | ((raw[POS_LEN + 1] & 0xFF) << 8);

      log.debug("[TL3800] header built job={} dataLen={} HEX={}", job, dataLen, hex(raw));
      return new Header(job, dataLen, raw);
    }

    throw new IllegalStateException("Frame header timeout");
  }

  /** EVENT 프레임의 tail(데이터+ETX+BCC)을 읽고 버린다. EVENT는 ACK/NACK 미전송. */
  private void consumeEventFrame(Header header) throws Exception {
    int dataLen = header.dataLen();
    int tailLen = dataLen + 2; // ETX + BCC

    byte[] tail = new byte[tailLen];
    int m = t.readFully(tail, tailLen, respWaitMs);

    log.info("[TL3800] << RECV(EVENT) dataLen={} readTail={}", dataLen, m);
    // EVENT 는 ACK/NACK 금지
  }

  /**
   * 꼬리(데이터+ETX+BCC) 수신 → strict/lenient 파싱 → (항상 ACK) strict 실패 시에도 lenient로 살려보고, 두 경우 모두 단말에는
   * ACK를 보낸다.
   */
  private TLPacket readTailAndParse(Header header, TLPacket req) throws Exception {
    int dataLen = header.dataLen();
    int tailLen = dataLen + 2; // ETX + BCC

    byte[] tail = new byte[tailLen];
    int m = t.readFully(tail, tailLen, respWaitMs);
    if (m != tailLen) {
      // body 부족: 예전에는 NAK 후 재전송을 유도했지만,
      // 현재는 strict/lenient 파서 위주의 동작을 유지하기 위해 그대로 예외만 던진다.
      try {
        t.write(new byte[] {0x15});
      } catch (Exception ignore) {
      }
      log.warn("[TL3800] >> NAK (body short: got={} need={})", m, tailLen);
      throw new IllegalArgumentException("short body");
    }

    byte[] resp = new byte[HEADER_BYTES + tailLen];
    System.arraycopy(header.raw(), 0, resp, 0, HEADER_BYTES);
    System.arraycopy(tail, 0, resp, HEADER_BYTES, tailLen);
    log.info("[TL3800] << RECV len={} HEX={}", resp.length, hex(resp));

    try {
      // 1차: strict 검증 (STX/ETX/BCC 다 맞는지 확인)
      TLPacket pkt = TLPacket.parseStrict(resp);

      if (req != null && !matchesExpected(req.jobCode, pkt.jobCode)) {
        log.warn("[TL3800] JOB changed: req={} resp={}", req.jobCode, pkt.jobCode);
      }

      t.write(new byte[] {0x06});
      log.debug("[TL3800] >> ACK");
      return pkt;
    } catch (IllegalArgumentException ex) {
      log.warn("[TL3800] strict parse failed: {} → trying lenient parse", ex.getMessage());

      // 2차: lenient 파서로 일단 내용만이라도 살려본다.
      TLPacket pkt = TLPacket.parseLenient(resp);

      try {
        t.write(new byte[] {0x06});
        log.debug("[TL3800] >> ACK (after lenient parse)");
      } catch (Exception ignore) {
      }

      return pkt;
    }
  }

  // ------- 공통 유틸 -------

  private Integer waitAckNakStx(int waitMs) {
    long end = System.currentTimeMillis() + waitMs;
    while (System.currentTimeMillis() < end) {
      int b = -1;
      try {
        b = t.readByte(50);
      } catch (Exception ignore) {
      }
      if (b < 0) {
        continue;
      }
      if (b == 0x06 || b == 0x15 || b == 0x02) {
        return b;
      }
    }
    return null;
  }

  private void drainRx(long windowMs) {
    long end = System.currentTimeMillis() + windowMs;
    while (System.currentTimeMillis() < end) {
      try {
        int b = t.readByte(20);
        if (b < 0) {
          break;
        }
      } catch (Exception ignore) {
        break;
      }
    }
  }

  private static void sleepQuiet(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignored) {
    }
  }

  private static String hex(byte[] b) {
    return java.util.HexFormat.of().formatHex(b);
  }

  /** 요청 잡코드에 대응하는 "기대 응답" 잡코드. 기본은 같은 코드, A→a, B→b 식으로 매핑 */
  private JobCode expectedResponseJob(JobCode reqJob) {
    char c = reqJob.code;
    if (c >= 'A' && c <= 'Z') {
      char respChar = Character.toLowerCase(c);
      try {
        return JobCode.of((byte) respChar);
      } catch (IllegalArgumentException ignored) {
        // 대응하는 소문자 JobCode 가 없으면 그냥 원래 값 사용
      }
    }
    return reqJob;
  }

  /** expected/actual 잡코드가 대소문자만 다른 경우까지 허용 */
  private boolean matchesExpected(JobCode expected, JobCode actual) {
    if (expected == actual) {
      return true;
    }
    if (expected == JobCode.EVENT || actual == JobCode.EVENT) {
      return false;
    }
    return Character.toLowerCase(expected.code) == Character.toLowerCase(actual.code);
  }
}
