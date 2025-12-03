/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.terminal;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.wit.payment.global.tl3800.client.TL3800Client;
import com.wit.payment.global.tl3800.payload.Requests;
import com.wit.payment.global.tl3800.proto.TLPacket;

public class TL3800Gateway {

  private final TL3800Client client;
  private final Requests requests;
  private final ReentrantLock lock = new ReentrantLock(true);

  public TL3800Gateway(TL3800Client client, Requests requests) {
    this.client = client;
    this.requests = requests;
  }

  private TLPacket call(Supplier<TLPacket> supplier) throws Exception {
    lock.lock();
    try {
      client.open();
      try {
        return client.requestResponse(supplier.get());
      } finally {
        client.close();
      }
    } finally {
      lock.unlock();
    }
  }

  public TLPacket deviceCheck() throws Exception {
    return call(requests::deviceCheck);
  }

  public TLPacket approve(String amount, String tax, String svc, String inst, boolean noSign)
      throws Exception {
    return call(() -> requests.approve(amount, tax, svc, inst, noSign));
  }
}
