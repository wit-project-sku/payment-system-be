/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.serviceImpl;

import com.wit.payment.domain.tl3800.config.Tl3800Properties;
import com.wit.payment.domain.tl3800.dto.request.Tl3800ApproveRequest;
import com.wit.payment.domain.tl3800.dto.response.Tl3800ApproveResponse;
import com.wit.payment.domain.tl3800.entity.Tl3800Transaction;
import com.wit.payment.domain.tl3800.exception.Tl3800ErrorCode;
import com.wit.payment.domain.tl3800.exception.Tl3800Exception;
import com.wit.payment.domain.tl3800.mapper.Tl3800TransactionMapper;
import com.wit.payment.domain.tl3800.protocol.Tl3800JobCode;
import com.wit.payment.domain.tl3800.protocol.Tl3800ProtocolClient;
import com.wit.payment.domain.tl3800.protocol.Tl3800ProtocolResponse;
import com.wit.payment.domain.tl3800.repository.Tl3800TransactionRepository;
import com.wit.payment.domain.tl3800.service.Tl3800PaymentService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Tl3800PaymentServiceImpl implements Tl3800PaymentService {

  private final Tl3800Properties properties;
  private final Tl3800ProtocolClient protocolClient;
  private final Tl3800TransactionRepository transactionRepository;

  @Override
  @Transactional
  public Tl3800ApproveResponse approve(Tl3800ApproveRequest request) {

    Tl3800ProtocolResponse protocolResponse = protocolClient.requestApproval(
        properties.getHost(),
        properties.getPort(),
        request.getAmount(),
        request.getOrderId(),
        properties.getTid(),          // ★ TID는 항상 properties에서
        request.isUseExtended()
    );

    if (!"0000".equals(protocolResponse.getResponseCode())) {
      throw new Tl3800Exception(
          Tl3800ErrorCode.APPROVAL_FAILED,
          protocolResponse.getResponseMessage()
      );
    }

    Tl3800Transaction tx = Tl3800Transaction.builder()
        .jobCode(request.isUseExtended() ? Tl3800JobCode.G_APPROVAL : Tl3800JobCode.B_APPROVAL)
        .amount(protocolResponse.getAmount())
        .orderId(request.getOrderId())
        .approvalNo(protocolResponse.getApprovalNo())
        .approvedAt(LocalDateTime.of(
            protocolResponse.getApprovalDate(),
            protocolResponse.getApprovalTime()
        ))
        .responseCode(protocolResponse.getResponseCode())
        .responseMessage(protocolResponse.getResponseMessage())
        .cardNoMasked(protocolResponse.getCardNoMasked())
        .mediaType(protocolResponse.getMediaType())
        .rawRequest(protocolResponse.getRawRequest())
        .rawResponse(protocolResponse.getRawResponse())
        .build();

    transactionRepository.save(tx);
    return Tl3800TransactionMapper.toApproveResponse(tx);
  }

}