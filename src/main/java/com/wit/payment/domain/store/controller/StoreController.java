/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wit.payment.domain.store.dto.request.CreateStoreRequest;
import com.wit.payment.domain.store.dto.response.StoreResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/stores")
@Tag(name = "Store", description = "키오스크 가게 관련 API")
public interface StoreController {

  @Operation(summary = "[관리자용] 가게 생성 API", description = "키오스크에서 사용할 가게를 생성합니다.")
  @PostMapping
  ResponseEntity<BaseResponse<StoreResponse>> createStore(@RequestBody CreateStoreRequest request);

  @Operation(summary = "전체 가게 조회 API", description = "모든 가게 목록을 조회합니다.")
  @GetMapping
  ResponseEntity<BaseResponse<List<StoreResponse>>> getStores();

  @Operation(summary = "[관리자용] 가게 삭제 API (hard delete)", description = "가게를 영구 삭제합니다.")
  @DeleteMapping("/{store-id}")
  ResponseEntity<BaseResponse<Void>> deleteStore(@PathVariable("store-id") Long storeId);
}
