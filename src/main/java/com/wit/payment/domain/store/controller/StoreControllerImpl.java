/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.store.dto.request.CreateStoreRequest;
import com.wit.payment.domain.store.dto.response.FirstStoreResponse;
import com.wit.payment.domain.store.dto.response.StoreResponse;
import com.wit.payment.domain.store.service.StoreService;
import com.wit.payment.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreControllerImpl implements StoreController {

  private final StoreService storeService;

  @Override
  public ResponseEntity<BaseResponse<StoreResponse>> createStore(
      @RequestBody CreateStoreRequest request) {

    StoreResponse response = storeService.createStore(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("가게 생성이 완료되었습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<StoreResponse>>> getStores() {
    List<StoreResponse> responses = storeService.getStores();
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("가게 목록을 조회했습니다.", responses));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteStore(@PathVariable("store-id") Long storeId) {
    storeService.deleteStore(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("가게 삭제가 완료되었습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<FirstStoreResponse>> getFirstStoreWithProducts() {
    FirstStoreResponse response = storeService.getFirstStoreWithProducts();
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("첫 번째 가게 및 상품 목록을 조회했습니다.", response));
  }
}
