/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.global.tl3800;

import com.wit.payment.domain.terminal.TL3800Gateway;
import com.wit.payment.global.tl3800.client.TL3800Client;
import com.wit.payment.global.tl3800.payload.Requests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

  @Bean
  TL3800Gateway tl3800Gateway(TL3800Client client, Requests factory) {
    return new TL3800Gateway(client, factory);
  }
}
