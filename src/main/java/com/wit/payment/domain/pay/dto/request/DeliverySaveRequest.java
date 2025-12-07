/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 후 배송 정보 입력 요청 DTO")
public record DeliverySaveRequest(
    @Schema(description = "수령인 이름", example = "홍길동") @NotBlank String name,
    @Schema(description = "우편번호", example = "04524") @NotBlank String zipCode,
    @Schema(description = "기본 주소", example = "서울특별시 종로구 인사동길 12") @NotBlank String address,
    @Schema(description = "상세 주소", example = "3층 WITH 전시관") @NotBlank String detailAddress) {}
