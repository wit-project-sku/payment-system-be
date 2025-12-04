/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "결제 내역 응답")
@Builder
public record PaymentSummaryResponse(
    @Schema(description = "결제 ID", example = "1") Long paymentId,
    @Schema(description = "승인일자", example = "2025-12-04") LocalDate approvedDate,
    @Schema(description = "승인시각", example = "13:45:21") LocalTime approvedTime,
    @Schema(description = "승인번호 (표시용)", example = "03304901") String approvalNo,
    @Schema(description = "승인금액(원)", example = "10") Integer amount,
    @Schema(description = "할부개월", example = "00") String installment,
    @Schema(description = "고객 전화번호", example = "01012345678") String phoneNumber,
    @Schema(description = "배송지 주소", example = "서울시 강남구 ...") String deliveryAddress,
    @Schema(description = "기타 메모", example = "핸드폰 기종") String etc) {}
