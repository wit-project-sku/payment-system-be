/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 처리 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PayResponse(
    @Schema(description = "결제 성공 여부", example = "true") boolean success,
    @Schema(description = "결제 ID (성공 시)", example = "1001") Long paymentId,
    @Schema(description = "이슈 ID (실패 시)", example = "2001") Long issueId,
    @Schema(description = "응답 메시지 (성공/실패 사유)", example = "결제가 완료되었습니다.") String message) {}
