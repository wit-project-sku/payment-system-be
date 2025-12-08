/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.tl3800.parser;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

import com.wit.payment.global.tl3800.proto.TLPacket;

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

    String dateStr = ascii(d, 62, 8);
    String timeStr = ascii(d, 70, 6);
    LocalDate approvedDate = LocalDate.parse(dateStr, DATE8);
    LocalTime approvedTime = LocalTime.parse(timeStr, TIME6);

    String vanTransactionId = ascii(d, 76, 12).trim();
    // 가맹점번호(88~102)는 현재 사용하지 않음
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
    return new String(src, offset, length, US_ASCII);
  }

  private static int parseAmount(byte[] src, int offset, int length) {
    String s = ascii(src, offset, length).trim();
    if (s.isEmpty()) {
      return 0;
    }
    return Integer.parseInt(s);
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
