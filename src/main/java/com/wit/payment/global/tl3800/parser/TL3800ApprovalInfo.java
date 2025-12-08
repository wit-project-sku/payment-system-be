/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.global.tl3800.parser;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.wit.payment.global.tl3800.proto.TLPacket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record TL3800ApprovalInfo(

    // 헤더 정보
    String terminalId, // 헤더 CAT/MID (TID)
    int responseCode,

    // 거래 속성
    String tranTypeCode, // 거래구분코드
    String mediaType, // 거래매체

    // 금액/할부
    int approvedAmount, // 승인금액(원거래금액+세금+봉사료)
    int vatAmount, // 세금
    int svcAmount, // 봉사료
    String installment, // 할부개월

    // 승인/매출 정보
    String approvalNoRaw, // 12자리, space 포함 원본
    String approvalNo, // trim된 승인번호
    LocalDate approvedDate, // 매출일자 YYYY-MM-DD
    LocalTime approvedTime, // 매출시간 hh:mm:ss

    // 원거래 식별용
    String vanTransactionId, // 거래고유번호(거래날짜6+일련번호6)
    String terminalNo, // 단말기번호(TID+일련번호, 14자리)
    String terminalSeqNo, // 단말 거래일련번호(terminalNo 마지막 4자리 등)

    // 카드/매입사/부가정보
    String cardNoMasked, // 마스킹된 카드번호
    String issuerInfo, // 발급사/거절메시지
    String acquirerInfo, // 매입사 정보
    String vanExtraRaw // 추가 응답 메시지/부가정보(있으면)
) {

  private static final DateTimeFormatter DATE8 = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
  private static final DateTimeFormatter TIME6 = DateTimeFormatter.ofPattern("HHmmss");

  public static TL3800ApprovalInfo fromHex(String tlPacketHex) {
    byte[] frame = HexFormat.of().parseHex(tlPacketHex);
    TLPacket packet = TLPacket.parse(frame); // strict
    return fromPacket(packet);
  }

  public static TL3800ApprovalInfo fromPacket(TLPacket packet) {
    byte[] d = packet.data;

    // 디버깅용: 데이터영역 덤프
    String dataHex = HexFormat.of().formatHex(d);
    String dataAscii = new String(d, US_ASCII);
    log.info("[TL3800][PAYMENT] dataLen={} DATA_HEX={}", d.length, dataHex);
    log.info("[TL3800][PAYMENT] DATA_ASCII={}", dataAscii.replace('\0', ' '));

    // --- 필드 오프셋 (응답전문[b] 기준) ---
    //  0 : 거래구분코드 (1)
    //  1 : 거래매체     (1)
    //  2 : 카드번호     (20)
    // 22 : 승인금액     (10)
    // 32 : 세금         (8)
    // 40 : 봉사료       (8)
    // 48 : 할부개월     (2)
    // 50 : 승인번호     (12)
    // 62 : 매출일자     (8, yyyyMMdd)
    // 70 : 매출시간     (6, HHmmss)
    // 76 : 거래고유번호 (12)
    // 88 : 가맹점번호   (15)
    // 103: 단말기번호   (14)
    // 117: 발급사정보   (20)
    // 137: 매입사정보   (20)
    // 157~ : (선택) VAN 거래거절/응답 메시지

    String tranTypeCode = ascii(d, 0, 1);
    String mediaType = ascii(d, 1, 1);

    String cardNoMasked = ascii(d, 2, 20).trim();

    int approvedAmount = parseAmount(d, 22, 10);
    int vatAmount = parseAmount(d, 32, 8);
    int svcAmount = parseAmount(d, 40, 8);
    String installment = ascii(d, 48, 2);

    String approvalNoRaw = ascii(d, 50, 12);
    String approvalNo = approvalNoRaw.trim();

    // --- 날짜/시간 파싱을 방어적으로 ---
    LocalDate approvedDate;
    LocalTime approvedTime;

    // 1차: 스펙 상 위치(62, 70) 기준으로 시도
    String dateRaw = ascii(d, 62, 8);
    String timeRaw = ascii(d, 70, 6);
    String dtDigits = (dateRaw + timeRaw).replaceAll("[^0-9]", "");

    if (dtDigits.length() >= 14 && dtDigits.startsWith("20")) {
      String dateStr = dtDigits.substring(0, 8);
      String timeStr = dtDigits.substring(8, 14);
      try {
        approvedDate = LocalDate.parse(dateStr, DATE8);
        approvedTime = LocalTime.parse(timeStr, TIME6);
      } catch (Exception e) {
        log.warn(
            "[TL3800] primary date/time parse failed raw='{}','{}' digits='{}' → fallback",
            dateRaw, timeRaw, dtDigits, e);
        LocalDateTime now = LocalDateTime.now();
        approvedDate = now.toLocalDate();
        approvedTime = now.toLocalTime();
      }
    } else {
      // 2차: 데이터 전체에서 "YYYYMMDDHHMMSS" 패턴 검색
      String all = ascii(d, 0, d.length);
      java.util.regex.Matcher m =
          java.util.regex.Pattern.compile("20\\d{12}").matcher(all);

      LocalDate foundDate = null;
      LocalTime foundTime = null;

      while (m.find()) {
        String cand = m.group(); // 예: 20251208202639
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
        log.warn(
            "[TL3800] cannot parse date/time from raw='{}','{}', all='{}' → use now()",
            dateRaw, timeRaw, all);
        LocalDateTime now = LocalDateTime.now();
        approvedDate = now.toLocalDate();
        approvedTime = now.toLocalTime();
      } else {
        approvedDate = foundDate;
        approvedTime = foundTime;
      }
    }

    String vanTransactionId = ascii(d, 76, 12).trim();
    String terminalNo = ascii(d, 103, 14).trim();
    String terminalSeqNo = extractTerminalSeqNo(terminalNo);

    String issuerInfo = ascii(d, 117, 20).trim();
    String acquirerInfo = ascii(d, 137, 20).trim();

    String vanExtraRaw = null;
    if (d.length > 157) {
      vanExtraRaw = ascii(d, 157, d.length - 157).trim();
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
        vanExtraRaw);
  }

  private static String ascii(byte[] src, int offset, int length) {
    if (src == null || length <= 0 || offset >= src.length) {
      return "";
    }
    int copyLen = Math.min(length, src.length - offset);
    return new String(src, offset, copyLen, US_ASCII);
  }

  private static int parseAmount(byte[] src, int offset, int length) {
    // 방어적 범위 체크
    if (src == null || src.length < offset + 1) {
      return 0;
    }

    String raw = ascii(src, offset, length);

    // 숫자만 추출 (카드사가 특수문자/공백 섞어 보내도 방어)
    String digits = raw.replaceAll("[^0-9]", "");

    if (digits.isEmpty()) {
      return 0;
    }

    try {
      long value = Long.parseLong(digits);
      if (value > Integer.MAX_VALUE) {
        log.warn("[TL3800] amount overflow value={} raw='{}'", value, raw);
        return 0;
      }
      return (int) value;
    } catch (NumberFormatException e) {
      log.warn("[TL3800] amount parse failed raw='{}' digits='{}'", raw, digits, e);
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