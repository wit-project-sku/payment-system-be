/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.protocol;

import java.math.BigDecimal;

/**
 * TL-3800 VAN 전문 생성 유틸리티. 실제 길이/필드 순서는 PDF "승인/취소 요청 Data Format" 표 기준으로 LEN_* 값만 조정.
 */
public final class Tl3800PacketBuilder {

  private Tl3800PacketBuilder() {
  }

  // 길이 상수 (실제 스펙에 맞게 수정)
  private static final int LEN_JOB_CODE = 1;   // "B" / "G" / "C" / "L" / "A"
  private static final int LEN_TID = 10;  // 단말기 TID(CAT ID)
  private static final int LEN_AMOUNT = 9;   // 금액(원단위)
  private static final int LEN_ORDER_ID = 20;  // 가맹점 거래키
  private static final int LEN_APPROVAL_NO = 12;  // 승인번호
  private static final int LEN_DATE = 8;   // YYYYMMDD
  private static final int LEN_TIME = 6;   // HHMMSS

  public static String buildApprovalPacket(
      boolean useExtended, // true → G, false → B
      String tid,
      BigDecimal amount,
      String orderId
  ) {
    StringBuilder sb = new StringBuilder();

    // 1. Job Code
    String jobCode = useExtended ? "G" : "B";
    sb.append(jobCode); // LEN_JOB_CODE = 1

    // 2. TID (CAT ID) – ★ 여기 반드시 TID가 들어가야 VAN 거래로 인식됨
    sb.append(leftPad(tid, LEN_TID, '0'));

    // 3. 금액
    sb.append(leftPad(Long.toString(amount.longValue()), LEN_AMOUNT, '0'));

    // 4. 주문번호/거래키
    sb.append(rightPad(orderId, LEN_ORDER_ID, ' '));

    // 5. 나머지 필드(할부/부가세/봉사료/서명 플래그 등)는 PDF 보고 뒤에 추가
    // sb.append(...);

    return sb.toString();
  }

  public static String buildCancelPacket(
      String tid,
      BigDecimal amount,
      String originalApprovalNo,
      String originalDate, // YYYYMMDD
      String originalTime  // HHMMSS
  ) {
    StringBuilder sb = new StringBuilder();

    // 1. Job Code "C"
    sb.append("C");

    // 2. TID
    sb.append(leftPad(tid, LEN_TID, '0'));

    // 3. 금액
    sb.append(leftPad(Long.toString(amount.longValue()), LEN_AMOUNT, '0'));

    // 4. 원승인번호
    sb.append(rightPad(originalApprovalNo, LEN_APPROVAL_NO, ' '));

    // 5. 원승인일시
    sb.append(rightPad(originalDate, LEN_DATE, '0'));
    sb.append(rightPad(originalTime, LEN_TIME, '0'));

    return sb.toString();
  }

  public static String buildLastApprovalPacket(String tid) {
    StringBuilder sb = new StringBuilder();
    sb.append("L");
    sb.append(leftPad(tid, LEN_TID, '0'));
    return sb.toString();
  }

  public static String buildStatusPacket() {
    return "A";
  }

  // ==== padding util ====

  private static String leftPad(String value, int length, char padChar) {
    if (value == null) {
      value = "";
    }
    if (value.length() >= length) {
      return value.substring(value.length() - length);
    }
    StringBuilder sb = new StringBuilder(length);
    for (int i = value.length(); i < length; i++) {
      sb.append(padChar);
    }
    sb.append(value);
    return sb.toString();
  }

  private static String rightPad(String value, int length, char padChar) {
    if (value == null) {
      value = "";
    }
    if (value.length() >= length) {
      return value.substring(0, length);
    }
    StringBuilder sb = new StringBuilder(length);
    sb.append(value);
    for (int i = value.length(); i < length; i++) {
      sb.append(padChar);
    }
    return sb.toString();
  }
}