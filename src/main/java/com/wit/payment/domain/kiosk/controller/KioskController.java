/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wit.payment.domain.kiosk.dto.request.KioskRequest;
import com.wit.payment.domain.kiosk.dto.response.KioskResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Kiosk API", description = "관리자용 키오스크 관리 API")
@RequestMapping("/api/admin/kiosks")
public interface KioskController {

  @Operation(summary = "키오스크 생성 API", description = "새로운 키오스크를 생성합니다.")
  @PostMapping
  ResponseEntity<BaseResponse<KioskResponse>> createKiosk(@Valid @RequestBody KioskRequest request);

  @Operation(summary = "키오스크 목록 조회 API", description = "모든 키오스크 목록을 조회합니다.")
  @GetMapping
  ResponseEntity<BaseResponse<List<KioskResponse>>> getKiosks();

  @Operation(summary = "키오스크 수정 API", description = "키오스크 정보를 수정합니다.")
  @PutMapping("/{kiosk-id}")
  ResponseEntity<BaseResponse<KioskResponse>> updateKiosk(
      @PathVariable("kiosk-id") Long kioskId, @Valid @RequestBody KioskRequest request);

  @Operation(summary = "키오스크 삭제 API", description = "키오스크를 삭제합니다.")
  @DeleteMapping("/{kiosk-id}")
  ResponseEntity<BaseResponse<Void>> deleteKiosk(@PathVariable("kiosk-id") Long kioskId);
}
