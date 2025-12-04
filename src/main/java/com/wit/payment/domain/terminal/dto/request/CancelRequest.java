/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.terminal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CancelRequest(

    // 취소구분코드: “1”[요청전문 취소], “2”[직전 거래 취소], “3”[VAN무카드 취소], …
    @NotBlank @Pattern(regexp = "[1-6]") String cancelType, // 취소구분코드

    // 거래구분코드: “1”[IC 신용승인], “2”[RF/MS 신용승인], “3”[현금영수증], …
    @NotBlank @Pattern(regexp = "[1234568]") String tranType, // 거래구분코드

    // 승인금액(원거래금액+세금+봉사료), CHAR(10) 우측정렬 0패딩
    @NotBlank @Pattern(regexp = "\\d{1,10}") String amount,

    // 세금, CHAR(8) 우측정렬 0패딩
    @NotBlank @Pattern(regexp = "\\d{1,8}") String tax,

    // 봉사료, CHAR(8) 우측정렬 0패딩
    @NotBlank @Pattern(regexp = "\\d{1,8}") String svc,

    // 할부개월, CHAR(2) 우측정렬 0패딩
    // 현금영수증(거래구분코드=3)일 때 소비자=00, 사업자=01 필수
    @NotBlank @Pattern(regexp = "\\d{2}") String inst,

    // 서명 여부: true → “1”(비서명), false → “2”(서명)
    boolean noSign,

    // 승인번호, 좌측정렬 space 채움, CHAR(12)
    @NotBlank @Size(max = 12) String approvalNo,

    // 원거래일자[YYYYMMDD], CHAR(8)
    @NotBlank @Pattern(regexp = "\\d{8}") String orgDate,

    // 원거래시간[hhmmss], CHAR(6)
    // “무카드 취소” 시에는 거래일련번호 마지막 6자리
    @NotBlank @Pattern(regexp = "\\d{6}") String orgTime,

    // 부가정보(N자리, 좌측정렬), 없으면 null 또는 빈 문자열
    // 카카오페이/현금영수증/PG무카드 취소/빌키취소 등에서 사용
    @Size(max = 200) // 프로토콜 상 제한은 없지만, 보호 차원에서 임의 제한
        String extra) {}
