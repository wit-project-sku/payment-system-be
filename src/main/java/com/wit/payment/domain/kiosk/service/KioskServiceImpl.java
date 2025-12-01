/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.kiosk.service;

import com.wit.payment.domain.kiosk.dto.request.KioskRequest;
import com.wit.payment.domain.kiosk.dto.response.KioskResponse;
import com.wit.payment.domain.kiosk.entity.Kiosk;
import com.wit.payment.domain.kiosk.exception.KioskErrorCode;
import com.wit.payment.domain.kiosk.mapper.KioskMapper;
import com.wit.payment.domain.kiosk.repository.KioskRepository;
import com.wit.payment.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KioskServiceImpl implements KioskService {

  private final KioskRepository kioskRepository;
  private final KioskMapper kioskMapper;

  @Override
  @Transactional
  public KioskResponse createKiosk(KioskRequest request) {

    if (kioskRepository.existsByName(request.getName())) {
      throw new CustomException(KioskErrorCode.KIOSK_ALREADY_EXISTS);
    }

    Kiosk kiosk = kioskMapper.toKiosk(request);
    Kiosk saved = kioskRepository.save(kiosk);

    log.info("키오스크 생성 성공 - kioskId: {}, name: {}", saved.getId(), saved.getName());
    return kioskMapper.toKioskResponse(saved);
  }

  @Override
  @Transactional
  public KioskResponse updateKiosk(Long kioskId, KioskRequest request) {
    Kiosk kiosk =
        kioskRepository
            .findById(kioskId)
            .orElseThrow(() -> new CustomException(KioskErrorCode.KIOSK_NOT_FOUND));

    kiosk.update(request.getName());

    log.info("키오스크 수정 성공 - kioskId: {}", kioskId);
    return kioskMapper.toKioskResponse(kiosk);
  }

  @Override
  @Transactional
  public void deleteKiosk(Long kioskId) {
    Kiosk kiosk =
        kioskRepository
            .findById(kioskId)
            .orElseThrow(() -> new CustomException(KioskErrorCode.KIOSK_NOT_FOUND));

    kioskRepository.delete(kiosk);

    log.info("키오스크 삭제 성공 - kioskId: {}", kioskId);
  }

  @Override
  public KioskResponse getKiosk(Long kioskId) {
    Kiosk kiosk =
        kioskRepository
            .findById(kioskId)
            .orElseThrow(() -> new CustomException(KioskErrorCode.KIOSK_NOT_FOUND));

    log.info("키오스크 단건 조회 성공 - kioskId: {}", kioskId);
    return kioskMapper.toKioskResponse(kiosk);
  }

  @Override
  public List<KioskResponse> getAllKiosks() {
    List<Kiosk> kiosks = kioskRepository.findAll();
    List<KioskResponse> responses = kiosks.stream().map(kioskMapper::toKioskResponse).toList();

    log.info("키오스크 전체 조회 성공 - count: {}", responses.size());
    return responses;
  }
}
