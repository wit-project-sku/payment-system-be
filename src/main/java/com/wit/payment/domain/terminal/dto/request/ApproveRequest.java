/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.terminal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ApproveRequest(
    @NotBlank @Pattern(regexp = "\\d{1,10}") String amount, // 금액(원) 1~10자리 숫자
    @NotBlank @Pattern(regexp = "\\d{1,8}") String tax, // 부가세 1~8자리 숫자
    @NotBlank @Pattern(regexp = "\\d{1,8}") String svc, // 봉사료 1~8자리 숫자
    @NotBlank @Pattern(regexp = "\\d{2}") String inst, // 할부개월 2자리(00, 02, 03…)
    boolean noSign // true=비서명("1"), false=서명("2")
    ) {}
