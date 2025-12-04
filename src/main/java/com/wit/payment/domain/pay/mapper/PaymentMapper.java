/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.mapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.entity.PaymentIssueStatus;
import com.wit.payment.global.tl3800.proto.TLPacket;

@Component
public class PaymentMapper {

  // TLPacket.dateTime14 포맷
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

  /**
   * TL 승인 응답 + 비즈니스 금액/할부 정보 → Payment 엔티티로 변환
   *
   * @param packet TL3800 승인 응답 패킷(b)
   * @param amount 결제 금액(원) – 서버가 검증한 값
   * @param inst 할부개월(“00”, “02”, …)
   */
  public Payment toPayment(TLPacket packet, int amount, String inst) {
    LocalDate approvedDate = parseDate(packet.dateTime14);
    LocalTime approvedTime = parseTime(packet.dateTime14);

    String approvalNoRaw = extractApprovalNoRaw(packet.data);
    String approvalNo = approvalNoRaw.trim();

    return Payment.builder()
        .approvedDate(approvedDate)
        .approvedTime(approvedTime)
        .approvalNoRaw(approvalNoRaw)
        .approvalNo(approvalNo)
        .amount(amount)
        .installment(inst)
        .build();
  }

  /**
   * 에러 상황(타임아웃, NAK 반복, 응답코드 오류 등)을 PaymentIssue로 변환. – 발생일시: now() – status: UNRESOLVED
   * (엔티티의 @Builder.Default)
   */
  public PaymentIssue toIssue(int amount, String message) {
    LocalDate nowDate = LocalDate.now();
    LocalTime nowTime = LocalTime.now();

    return PaymentIssue.builder()
        .occurredDate(nowDate)
        .occurredTime(nowTime)
        .amount(amount)
        .message(message)
        .status(PaymentIssueStatus.UNRESOLVED) // 명시해두는 편이 의도 전달에 좋음
        .build();
  }

  /**
   * TL 프로토콜 기준 승인 응답(b)의 data 영역에서 승인번호(또는 선불정보) 12바이트를 추출.
   *
   * <p>data 구조(요약): 0: 카드번호(20) 20: 승인금액(10) 30: 세금/잔여횟수(8) 38: 봉사료/사용횟수(8) 46: 할부개월(2) 48:
   * 승인번호/선불정보(12) ← 여기
   */
  private String extractApprovalNoRaw(byte[] data) {
    if (data == null || data.length < 60) {
      // 프로토콜 위반/예외 상황 – 방어적인 처리
      return "";
    }
    final int offset = 20 + 10 + 8 + 8 + 2; // 48
    final int len = 12;
    return new String(data, offset, len, StandardCharsets.US_ASCII);
  }

  private LocalDate parseDate(String dateTime14) {
    // dateTime14: "YYYYMMDDhhmmss"
    String datePart = dateTime14.substring(0, 8);
    return LocalDate.parse(datePart, DATE_FORMAT);
  }

  private LocalTime parseTime(String dateTime14) {
    String timePart = dateTime14.substring(8, 14);
    return LocalTime.parse(timePart, TIME_FORMAT);
  }
}
