/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.service;

import java.util.List;

import com.wit.payment.domain.store.dto.request.CreateStoreRequest;
import com.wit.payment.domain.store.dto.response.StoreResponse;

/** 가게 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다. */
public interface StoreService {

  /**
   * 가게를 생성합니다.
   *
   * @param request 가게 생성 요청 정보
   * @return 생성된 가게 정보 응답
   */
  StoreResponse createStore(CreateStoreRequest request);

  /**
   * 모든 가게 목록을 조회합니다.
   *
   * @return 가게 정보 응답 리스트
   */
  List<StoreResponse> getStores();

  /**
   * 가게를 하드 삭제합니다.
   *
   * @param storeId 삭제할 가게 식별자
   */
  void deleteStore(Long storeId);
}
