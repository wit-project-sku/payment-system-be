package com.wit.payment.domain.tl3800.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tl3800")
public class Tl3800Properties {

  /**
   * TL3800 단말기 TCP/IP 주소
   */
  private String host;

  /**
   * TL3800 단말기 포트
   */
  private int port;

  /**
   * VAN 단말기 TID (CAT ID) - SBIZ 조회 기준
   */
  private String tid;

  /**
   * 소켓 연결 타임아웃(ms)
   */
  private int connectTimeoutMillis = 3000;

  /**
   * 응답 타임아웃(ms)
   */
  private int readTimeoutMillis = 25000;
}
