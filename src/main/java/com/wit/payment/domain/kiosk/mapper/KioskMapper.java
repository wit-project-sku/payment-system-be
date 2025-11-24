/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.mapper;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.kiosk.dto.request.KioskRequest;
import com.wit.payment.domain.kiosk.dto.response.KioskResponse;
import com.wit.payment.domain.kiosk.entity.Kiosk;

@Component
public class KioskMapper {

  public Kiosk toKiosk(KioskRequest request) {
    return Kiosk.builder().name(request.getName()).build();
  }

  public KioskResponse toKioskResponse(Kiosk kiosk) {
    return KioskResponse.builder().id(kiosk.getId()).name(kiosk.getName()).build();
  }
}
