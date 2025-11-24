/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.service;

import java.util.List;

import com.wit.payment.domain.kiosk.dto.request.KioskRequest;
import com.wit.payment.domain.kiosk.dto.response.KioskResponse;

public interface KioskService {

  KioskResponse createKiosk(KioskRequest request);

  KioskResponse updateKiosk(Long kioskId, KioskRequest request);

  void deleteKiosk(Long kioskId);

  KioskResponse getKiosk(Long kioskId);

  List<KioskResponse> getAllKiosks();
}
