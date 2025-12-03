/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.terminal.controller;

import com.wit.payment.domain.terminal.TL3800Gateway;
import com.wit.payment.domain.terminal.dto.request.ApproveRequest;
import com.wit.payment.domain.terminal.dto.response.PacketResponse;
import com.wit.payment.global.tl3800.proto.TLPacket;
import com.wit.payment.global.tl3800.util.Hex;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/tl3800", produces = MediaType.APPLICATION_JSON_VALUE)
public class TL3800Controller {

  private final TL3800Gateway gateway;

  public TL3800Controller(TL3800Gateway gateway) {
    this.gateway = gateway;
  }

  @PostMapping("/device-check")
  public PacketResponse deviceCheck() throws Exception {
    TLPacket p = gateway.deviceCheck();
    return new PacketResponse(
        p.catOrMid,
        p.dateTime14,
        String.valueOf(p.jobCode.code),
        Byte.toUnsignedInt(p.responseCode),
        Hex.toHex(p.data));
  }

  @PostMapping("/approve")
  public PacketResponse approve(@Valid @RequestBody ApproveRequest req) throws Exception {
    TLPacket p = gateway.approve(req.amount(), req.tax(), req.svc(), req.inst(), req.noSign());
    return new PacketResponse(
        p.catOrMid,
        p.dateTime14,
        String.valueOf(p.jobCode.code),
        Byte.toUnsignedInt(p.responseCode),
        Hex.toHex(p.data));
  }
}
