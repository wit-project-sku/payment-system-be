/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.terminal.service;

import org.springframework.stereotype.Service;

import com.wit.payment.global.tl3800.client.TL3800Client;
import com.wit.payment.global.tl3800.payload.Requests;
import com.wit.payment.global.tl3800.proto.TLPacket;

@Service
public class TL3800Service {

  private final TL3800Client client;
  private final Requests requests;

  public TL3800Service(TL3800Client client, Requests requests) {
    this.client = client;
    this.requests = requests;
  }

  public TLPacket checkDevice() throws Exception {
    client.open();
    try {
      return client.requestResponse(requests.deviceCheck());
    } finally {
      client.close();
    }
  }

  public TLPacket approve(String amount) throws Exception {
    client.open();
    try {
      return client.requestResponse(requests.approve(amount, "0", "0", "00", true));
    } finally {
      client.close();
    }
  }
}
