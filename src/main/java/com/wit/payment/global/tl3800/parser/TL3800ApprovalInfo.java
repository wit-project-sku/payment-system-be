/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.global.tl3800.parser;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.wit.payment.global.tl3800.proto.TLPacket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TL3800ApprovalInfo(

    // 헤더 정보
    String terminalId,          // 헤더 CAT/MID (TID)
    int responseCode,

    // 거래 속성
    String tranTypeCode,        // 거래구분코드
    String mediaType,           // 거래매체

    // 금액/할부
    int approvedAmount,         // 승인금액(원거래금액+세금+봉사료)
    int vatAmount,              // 세금
    int svcAmount,              // 봉사료
    String installment,         // 할부개월

    // 승인/매출 정보
    String approvalNoRaw,       // 12자리, space 포함 원본
    String approvalNo,          // trim된 승인번호
    LocalDate approvedDate,     // 매출일자 YYYY-MM-DD
    LocalTime approvedTime,     // 매출시간 hh:mm:ss

    // 원거래 식별용
    String vanTransactionId,    // 거래고유번호(거래날짜6+일련번호6)
    String terminalNo,          // 단말기번호(TID+일련번호, 14자리)
    String terminalSeqNo,       // 단말 거래일련번호(terminalNo 마지막 4자리 등)

    // 카드/매입사/부가정보
    String cardNoMasked,        // 마스킹된 카드번호
    String issuerInfo,          // 발급사/거절메시지
    String acquirerInfo,        // 매입사 정보
    String vanExtraRaw          // 추가 응답 메시지/부가정보(있으면)
) {

  private static final DateTimeFormatter DATE8 = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
  private static final DateTimeFormatter TIME6 = DateTimeFormatter.ofPattern("HHmmss");
  private static final Pattern DT14_PATTERN = Pattern.compile("20\\d{12}");

  public static TL3800ApprovalInfo fromHex(String tlPacketHex) {
    byte[] frame = HexFormat.of().parseHex(tlPacketHex);
    TLPacket packet = TLPacket.parse(frame); // strict
    return fromPacket(packet);
  }

  public static TL3800ApprovalInfo fromPacket(TLPacket packet) {
    byte[] d = packet.data;

    // --- 필드 오프셋 (실제 응답전문[b] 기준) ---
    //
    //  0 : 거래구분코드         (1)
    //  1 : 거래매체             (1)
    //  2 : 카드번호(마스킹)     (20)
    // 22 : 거래일시(YYMMDDhhmm) (10)  ★ 추가 필드
    // 32 : 승인금액             (10)
    // 42 : 세금                 (8)
    // 50 : 봉사료               (8)
    // 58 : 할부개월             (2)
    // 60 : 승인번호             (12)
    // 72 : 매출일자             (8, yyyyMMdd)
    // 80 : 매출시간             (6, HHmmss)
    // 86 : 거래고유번호         (12)
    // 98 : 가맹점번호           (15)
    // 113: 단말기번호           (14)
    // 127: 발급사정보           (20)
    // 147: 매입사정보           (20)
    // 167~: (선택) VAN 거래거절/응답 메시지

    String tranTypeCode = ascii(d, 0, 1);
    String mediaType = ascii(d, 1, 1);

    String cardNoMasked = safeAscii(d, 2, 20).trim();

    // 22~31: YYMMDDhhmm (단말 자체 거래일시) – 현재는 사용하지 않지만, 필요하면 보강 가능
    String terminalDateTime10 = safeAscii(d, 22, 10); // 예: "2512082026"

    // 금액/세금/봉사료/할부
    int approvedAmount = parseAmount(d, 32, 10);
    int vatAmount = parseAmount(d, 42, 8);
    int svcAmount = parseAmount(d, 50, 8);
    String installment = safeAscii(d, 58, 2);

    // 승인번호
    String approvalNoRaw = safeAscii(d, 60, 12);
    String approvalNo = approvalNoRaw.trim();

    // --- 날짜/시간 파싱을 방어적으로 ---
    LocalDate approvedDate;
    LocalTime approvedTime;

    // 1차: 스펙 상 위치(72, 80) 기준으로 시도
    String dateRaw = safeAscii(d, 72, 8);
    String timeRaw = safeAscii(d, 80, 6);
    String dtDigits = (dateRaw + timeRaw).replaceAll("[^0-9]", ""); // 숫자만 추출

    try {
      if (dtDigits.length() >= 14 && dtDigits.startsWith("20")) {
        String dateStr = dtDigits.substring(0, 8);
        String timeStr = dtDigits.substring(8, 14);
        approvedDate = LocalDate.parse(dateStr, DATE8);
        approvedTime = LocalTime.parse(timeStr, TIME6);
      } else {
        // 2차: 데이터 전체에서 "YYYYMMDDHHMMSS" 패턴 검색
        String all = ascii(d, 0, d.length);
        Matcher m = DT14_PATTERN.matcher(all);

        LocalDate foundDate = null;
        LocalTime foundTime = null;

        while (m.find()) {
          String cand = m.group();      // 예: 20251208202639
          String candDate = cand.substring(0, 8);
          String candTime = cand.substring(8, 14);
          try {
            foundDate = LocalDate.parse(candDate, DATE8);
            foundTime = LocalTime.parse(candTime, TIME6);
            break; // 첫 번째 유효한 후보 사용
          } catch (Exception ignore) {
          }
        }

        if (foundDate == null) {
          // 최후 방어: 장애 내지 말고 현재 시각으로 대체
          java.time.LocalDateTime now = java.time.LocalDateTime.now();
          approvedDate = now.toLocalDate();
          approvedTime = now.toLocalTime();
        } else {
          approvedDate = foundDate;
          approvedTime = foundTime;
        }
      }
    } catch (Exception e) {
      // 혹시라도 위에서 또 예외가 나면, 현재 시각으로 대체
      java.time.LocalDateTime now = java.time.LocalDateTime.now();
      approvedDate = now.toLocalDate();
      approvedTime = now.toLocalTime();
    }

    // 이하 필드들: 모두 +10 이동
    String vanTransactionId = safeAscii(d, 86, 12).trim();

    // 98~112 가맹점번호(15)는 현재 사용하지 않으므로 건너뜀
    String terminalNo = safeAscii(d, 113, 14).trim();
    String terminalSeqNo = extractTerminalSeqNo(terminalNo);

    String issuerInfo = safeAscii(d, 127, 20).trim();
    String acquirerInfo = safeAscii(d, 147, 20).trim();

    String vanExtraRaw = null;
    if (d.length > 167) {
      vanExtraRaw = safeAscii(d, 167, d.length - 167).trim();
      if (vanExtraRaw.isEmpty()) {
        vanExtraRaw = null;
      }
    }

    String headerTid = packet.catOrMid != null ? packet.catOrMid.trim() : null;

    return new TL3800ApprovalInfo(
        headerTid,
        Byte.toUnsignedInt(packet.responseCode),
        tranTypeCode,
        mediaType,
        approvedAmount,
        vatAmount,
        svcAmount,
        installment,
        approvalNoRaw,
        approvalNo,
        approvedDate,
        approvedTime,
        vanTransactionId,
        terminalNo,
        terminalSeqNo,
        cardNoMasked,
        issuerInfo,
        acquirerInfo,
        vanExtraRaw
    );
  }

  private static String ascii(byte[] src, int offset, int length) {
    return new String(src, offset, length, US_ASCII);
  }

  /**
   * 범위 체크 포함한 방어적 ASCII 추출
   */
  private static String safeAscii(byte[] src, int offset, int length) {
    if (src == null || offset >= src.length) {
      return "";
    }
    int safeLen = Math.max(0, Math.min(length, src.length - offset));
    return new String(src, offset, safeLen, US_ASCII);
  }

  private static int parseAmount(byte[] src, int offset, int length) {
    if (src == null || src.length < offset + 1) {
      return 0;
    }

    int safeLen = Math.max(0, Math.min(length, src.length - offset));
    String raw = new String(src, offset, safeLen, US_ASCII);

    // 숫자만 추출 (카드사가 특수문자/공백 섞어 보내도 방어)
    String digits = raw.replaceAll("[^0-9]", "");

    if (digits.isEmpty()) {
      return 0;
    }

    try {
      long value = Long.parseLong(digits);
      if (value > Integer.MAX_VALUE) {
        // 비정상적인 값(예: 날짜 등)이 들어온 경우 방어적으로 0 처리
        return 0;
      }
      return (int) value;
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static String extractTerminalSeqNo(String terminalNo) {
    if (terminalNo == null) {
      return null;
    }
    String trimmed = terminalNo.trim();
    if (trimmed.length() <= 4) {
      return trimmed;
    }
    return trimmed.substring(trimmed.length() - 4);
  }
}