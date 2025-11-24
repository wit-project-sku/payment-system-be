/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.kiosk.dto.request.KioskRequest;
import com.wit.payment.domain.kiosk.dto.response.KioskResponse;
import com.wit.payment.domain.kiosk.service.KioskService;
import com.wit.payment.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KioskControllerImpl implements KioskController {

  private final KioskService kioskService;

  @Override
  public ResponseEntity<BaseResponse<KioskResponse>> createKiosk(KioskRequest request) {
    KioskResponse response = kioskService.createKiosk(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("키오스크 생성이 완료되었습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<KioskResponse>>> getKiosks() {
    List<KioskResponse> responses = kioskService.getAllKiosks();
    return ResponseEntity.ok(BaseResponse.success("키오스크 목록 조회 성공", responses));
  }

  @Override
  public ResponseEntity<BaseResponse<KioskResponse>> updateKiosk(
      Long kioskId, KioskRequest request) {

    KioskResponse response = kioskService.updateKiosk(kioskId, request);
    return ResponseEntity.ok(BaseResponse.success("키오스크 수정 성공", response));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteKiosk(Long kioskId) {
    kioskService.deleteKiosk(kioskId);
    return ResponseEntity.ok(BaseResponse.success("키오스크 삭제 성공", null));
  }
}
