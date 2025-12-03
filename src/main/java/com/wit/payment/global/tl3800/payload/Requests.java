/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.tl3800.payload;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.wit.payment.global.tl3800.proto.JobCode;
import com.wit.payment.global.tl3800.proto.Proto;
import com.wit.payment.global.tl3800.proto.TLPacket;

public final class Requests {

  private final String terminalId;

  public Requests(String terminalId) {
    this.terminalId = terminalId;
  }

  // A: 장치체크 (Data 없음)
  public TLPacket deviceCheck() {
    return TLPacket.builder().catOrMid(terminalId).jobCode(JobCode.A).data(new byte[0]).build();
  }

  // B: 거래승인 (필수 30B + AuthNo(12, space) + D8(8) + 확장길이(2,"00") = 52B)
  public TLPacket approve(String amount, String tax, String svc, String inst, boolean noSign) {
    ByteBuffer bb = ByteBuffer.allocate(30);
    bb.put("1".getBytes(StandardCharsets.US_ASCII)); // 거래구분 1
    bb.put(Proto.asciiLeftPadZero(amount, 10)); // 금액(10)
    bb.put(Proto.asciiLeftPadZero(tax, 8)); // 부가세(8)
    bb.put(Proto.asciiLeftPadZero(svc, 8)); // 봉사료(8)
    bb.put(Proto.asciiLeftPadZero(inst, 2)); // 할부(2)
    bb.put((noSign ? "1" : "2").getBytes(StandardCharsets.US_ASCII)); // 서명여부(1)

    // ★ 꼭 position() 만큼만 잘라서 payload 만들기
    bb.flip();
    byte[] payload = new byte[bb.remaining()];
    bb.get(payload);

    return TLPacket.builder().catOrMid(terminalId).jobCode(JobCode.B).data(payload).build();
  }
}
