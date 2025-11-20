/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.store.dto.request.CreateStoreRequest;
import com.wit.payment.domain.store.dto.response.StoreResponse;
import com.wit.payment.domain.store.entity.Store;

@Component
public class StoreMapper {

  public Store toStore(CreateStoreRequest request) {
    if (request == null) {
      return null;
    }

    return Store.builder().name(request.getName()).build();
  }

  public StoreResponse toStoreResponse(Store store) {
    if (store == null) {
      return null;
    }

    return StoreResponse.builder().id(store.getId()).name(store.getName()).build();
  }

  public List<StoreResponse> toStoreResponseList(List<Store> stores) {
    if (stores == null) {
      return List.of();
    }

    return stores.stream().map(this::toStoreResponse).toList();
  }
}
