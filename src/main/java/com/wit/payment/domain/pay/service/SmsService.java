/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

  private static final String API_URL = "https://apis.aligo.in/send/";

  @Value("${aligo.api.key}")
  private String apiKey;

  @Value("${aligo.api.user-id}")
  private String userId;

  @Value("${aligo.api.sender-number}")
  private String senderNumber;

  private final RestTemplate restTemplate = new RestTemplate();

  public void sendTestMessage(String receiver) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("key", apiKey);
    params.add("user_id", userId);
    params.add("sender", senderNumber);
    params.add("receiver", receiver);
    params.add(
        "msg",
        "안녕하세요, 고객님.\n"
            + "위트테리아를 이용해주셔서 감사합니다.\n\n"
            + "배송을 위해 아래 링크에서\n"
            + "필수 정보 및 배송 주소를 입력해주세요.\n\n"
            + "https://witteria.com/mobile/search");
    params.add("msg_type", "SMS");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<String> response =
          restTemplate.postForEntity(API_URL, requestEntity, String.class);
      log.info("[SMS] 발송 결과 = {}", response.getBody());
    } catch (Exception e) {
      log.error("[SMS] 발송 실패: {}", e.getMessage(), e);
      throw new RuntimeException("SMS 전송 실패", e);
    }
  }
}
