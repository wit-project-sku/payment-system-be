/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.domain.product.service.ProductService;
import com.wit.payment.domain.store.dto.request.CreateStoreRequest;
import com.wit.payment.domain.store.dto.response.FirstStoreResponse;
import com.wit.payment.domain.store.dto.response.StoreResponse;
import com.wit.payment.domain.store.entity.Store;
import com.wit.payment.domain.store.exception.StoreErrorCode;
import com.wit.payment.domain.store.mapper.StoreMapper;
import com.wit.payment.domain.store.repository.StoreRepository;
import com.wit.payment.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

  private final StoreRepository storeRepository;
  private final StoreMapper storeMapper;
  private final ProductService productService;

  @Override
  @Transactional
  public StoreResponse createStore(CreateStoreRequest request) {
    Store store = storeMapper.toStore(request);

    Store saved = storeRepository.save(store);
    log.info("가게 생성 성공 - storeId: {}", saved.getId());

    return storeMapper.toStoreResponse(saved);
  }

  @Override
  public List<StoreResponse> getStores() {
    List<Store> stores = storeRepository.findAll();

    List<StoreResponse> responses = storeMapper.toStoreResponseList(stores);

    log.info("전체 가게 조회 성공 - count: {}", responses.size());
    return responses;
  }

  @Override
  @Transactional
  public void deleteStore(Long storeId) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));

    storeRepository.delete(store);
    log.info("가게 삭제 성공(hard delete) - storeId: {}", storeId);
  }

  @Override
  public FirstStoreResponse getFirstStoreWithProducts() {

    List<Store> stores = storeRepository.findAll();

    if (stores.isEmpty()) {
      throw new CustomException(StoreErrorCode.STORE_NOT_FOUND);
    }

    // 첫번째로 조회할 스토어 선택
    Store firstStore = stores.get(0);

    List<ProductSummaryResponse> products = productService.getProductsByStore(firstStore.getId());

    log.info("첫 번째 가게의 상품 조회 완료 - storeId: {}, count: {}", firstStore.getId(), products.size());

    return new FirstStoreResponse(
        storeMapper.toStoreResponseList(stores),
        firstStore.getId(),
        firstStore.getName(),
        products);
  }
}
