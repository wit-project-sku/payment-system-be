/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.tl3800.client;

import static com.wit.payment.global.tl3800.proto.Proto.HEADER_BYTES;

import com.wit.payment.global.tl3800.proto.TLPacket;
import com.wit.payment.global.tl3800.transport.TLTransport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TL3800Client implements AutoCloseable {

  private final TLTransport t;
  private final int ackWaitMs;
  private final int respWaitMs;
  private final int maxAckRetry;

  private static final int FOLLOWUP_WINDOW_MS = 120_000;

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

  public TLPacket requestResponse(TLPacket req) throws Exception {
    final byte[] frame = req.toBytes();
    log.info("[TL3800] >> SEND job={} len={} HEX={}", req.jobCode, frame.length, hex(frame));

    int tries = 0;
    while (true) {
      drainRx(120);
      t.write(frame);
      sleepQuiet(8);

      Integer first = waitAckNakStx(ackWaitMs);
      if (first != null) {
        if (first == 0x15) { // NAK
          if (++tries <= maxAckRetry) {
            log.warn("[TL3800] << NAK → retry {}/{}", tries, maxAckRetry);
            continue;
          }
          throw new IllegalStateException("NAK received (exceeded retry)");
        }
        if (first == 0x06) { // ACK
          log.debug("[TL3800] << ACK");
          byte[] header = new byte[HEADER_BYTES];
          int n = t.readFully(header, HEADER_BYTES, respWaitMs);
          if (n != HEADER_BYTES) {
            throw new IllegalStateException("Response header timeout");
          }
          return readTailParseAndAck(header, req); // ★ 여기서 단말에 ACK 회신
        }
        if (first == 0x02) { // 즉시 STX
          log.debug("[TL3800] << STX (immediate)");
          byte[] header = new byte[HEADER_BYTES];
          header[0] = 0x02;
          byte[] rest = new byte[HEADER_BYTES - 1];
          int n = t.readFully(rest, rest.length, respWaitMs);
          if (n != rest.length) {
            throw new IllegalStateException("Response header timeout (partial)");
          }
          System.arraycopy(rest, 0, header, 1, rest.length);
          return readTailParseAndAck(header, req); // ★ 여기서 단말에 ACK 회신
        }
      }

      // 폴백: ACK가 안 왔을 때 STX를 추가 대기
      log.debug("[TL3800] no-ACK within {} ms → waiting STX up to {} ms", ackWaitMs, respWaitMs);
      long start = System.currentTimeMillis();
      while ((System.currentTimeMillis() - start) < respWaitMs) {
        int b = t.readByte(50);
        if (b == 0x02) {
          byte[] header = new byte[HEADER_BYTES];
          header[0] = 0x02;
          byte[] rest = new byte[HEADER_BYTES - 1];
          int n = t.readFully(rest, rest.length, respWaitMs);
          if (n != rest.length) {
            throw new IllegalStateException("Response header timeout (partial)");
          }
          System.arraycopy(rest, 0, header, 1, rest.length);
          return readTailParseAndAck(header, req); // ★ 여기서 단말에 ACK 회신
        } else if (b == 0x15) {
          if (++tries <= maxAckRetry) {
            log.warn("[TL3800] << late NAK → retry {}/{}", tries, maxAckRetry);
            break;
          }
          throw new IllegalStateException("NAK received (exceeded retry)");
        } else if (b == 0x06) {
          log.debug("[TL3800] << late ACK");
          byte[] header = new byte[HEADER_BYTES];
          int n = t.readFully(header, HEADER_BYTES, respWaitMs);
          if (n != HEADER_BYTES) {
            throw new IllegalStateException("Response header timeout");
          }
          return readTailParseAndAck(header, req); // ★ 여기서 단말에 ACK 회신
        }
      }

      throw new IllegalStateException("ACK timeout");
    }
  }

  private TLPacket readTailParseAndAck(byte[] header, TLPacket req) throws Exception {
    final int posLen = 1 + 16 + 14 + 1 + 1;
    int dataLen = (header[posLen] & 0xFF) | ((header[posLen + 1] & 0xFF) << 8);
    int tailLen = dataLen + 2; // ETX + BCC

    byte[] tail = new byte[tailLen];
    int m = t.readFully(tail, tailLen, respWaitMs);
    if (m != tailLen) {
      throw new IllegalStateException("Response body timeout");
    }

    byte[] resp = new byte[HEADER_BYTES + tailLen];
    System.arraycopy(header, 0, resp, 0, HEADER_BYTES);
    System.arraycopy(tail, 0, resp, HEADER_BYTES, tailLen);
    log.info("[TL3800] << RECV len={} HEX={}", resp.length, hex(resp));

    // 파싱 및 BCC 검증
    try {
      TLPacket pkt = TLPacket.parse(resp);

      // 잡코드 변동 감지 로그
      if (req != null && pkt.jobCode != req.jobCode) {
        log.warn("[TL3800] JOB changed: req={} resp={}", req.jobCode, pkt.jobCode);
      }

      // ★ 단말에 ACK 회신 (응답 프레임을 정상 수신했음을 알림)
      t.write(new byte[] {0x06});
      log.debug("[TL3800] >> ACK");

      return pkt;
    } catch (IllegalArgumentException ex) {
      // ★ 파싱 실패/BCC 불일치 등 → 단말에 NAK 회신
      t.write(new byte[] {0x15});
      log.warn("[TL3800] >> NAK (parse fail: {})", ex.getMessage());
      throw ex;
    }
  }

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
}
