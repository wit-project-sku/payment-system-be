/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.protocol;

import com.wit.payment.domain.tl3800.config.Tl3800Properties;
import com.wit.payment.domain.tl3800.exception.Tl3800ErrorCode;
import com.wit.payment.global.exception.CustomException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Tl3800ProtocolClientImpl implements Tl3800ProtocolClient {

  private final Tl3800Properties properties;

  @Override
  public Tl3800ProtocolResponse requestApproval(
      String ip,
      int port,
      BigDecimal amount,
      String orderId,
      String tid,
      boolean useExtended
  ) {
    String request = Tl3800PacketBuilder.buildApprovalPacket(
        useExtended,
        tid,
        amount,
        orderId
    );
    String response = sendAndReceive(ip, port, request);
    return parseApprovalResponse(request, response);
  }

  @Override
  public Tl3800ProtocolResponse requestCancel(
      String ip,
      int port,
      BigDecimal amount,
      String originalApprovalNo,
      LocalDate originalApprovalDate,
      LocalTime originalApprovalTime,
      String tid
  ) {
    String dateStr = originalApprovalDate.toString().replace("-", ""); // YYYYMMDD
    String timeStr = String.format("%02d%02d%02d",
        originalApprovalTime.getHour(),
        originalApprovalTime.getMinute(),
        originalApprovalTime.getSecond()
    );

    String request = Tl3800PacketBuilder.buildCancelPacket(
        tid,
        amount,
        originalApprovalNo,
        dateStr,
        timeStr
    );
    String response = sendAndReceive(ip, port, request);
    return parseCancelResponse(request, response);
  }

  @Override
  public Tl3800ProtocolResponse requestLastApproval(String ip, int port, String tid) {
    String request = Tl3800PacketBuilder.buildLastApprovalPacket(tid);
    String response = sendAndReceive(ip, port, request);
    return parseLastApprovalResponse(request, response);
  }

  @Override
  public Tl3800ProtocolResponse requestStatus(String ip, int port) {
    String request = Tl3800PacketBuilder.buildStatusPacket();
    String response = sendAndReceive(ip, port, request);
    return parseStatusResponse(request, response);
  }

  private String sendAndReceive(String ip, int port, String request) {
    Socket socket = new Socket();
    try {
      socket.connect(
          new InetSocketAddress(ip, port),
          properties.getConnectTimeoutMillis()
      );
      socket.setSoTimeout(properties.getReadTimeoutMillis());

      BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII)
      );
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)
      );

      log.info("[TL3800] >> {}", request);
      writer.write(request);
      writer.flush();

      char[] buffer = new char[4096];
      int len = reader.read(buffer);
      if (len <= 0) {
        throw new CustomException(Tl3800ErrorCode.PROTOCOL_TIMEOUT);
      }
      String response = new String(buffer, 0, len);
      log.info("[TL3800] << {}", response);
      return response;

    } catch (IOException e) {
      throw new CustomException(Tl3800ErrorCode.PROTOCOL_IO_ERROR);
    } finally {
      try {
        socket.close();
      } catch (IOException ignored) {
      }
    }
  }

  // 아래 parse* 메서드는 실제 응답 전문(b, c, l, a) 포맷에 맞게 나중에 세부 구현
  private Tl3800ProtocolResponse parseApprovalResponse(String request, String response) {
    // TODO: PDF 기반으로 정확히 파싱
    return Tl3800ProtocolResponse.builder()
        .jobCode("b")
        .responseCode("0000")
        .responseMessage("승인 성공(가짜)")
        .approvalNo("000000")
        .approvalDate(LocalDate.now())
        .approvalTime(LocalTime.now())
        .amount(BigDecimal.ZERO)
        .cardNoMasked("************0000")
        .mediaType("IC")
        .rawRequest(request)
        .rawResponse(response)
        .build();
  }

  private Tl3800ProtocolResponse parseCancelResponse(String request, String response) {
    return parseApprovalResponse(request, response);
  }

  private Tl3800ProtocolResponse parseLastApprovalResponse(String request, String response) {
    return parseApprovalResponse(request, response);
  }

  private Tl3800ProtocolResponse parseStatusResponse(String request, String response) {
    return Tl3800ProtocolResponse.builder()
        .jobCode("a")
        .responseCode("0000")
        .responseMessage(response)
        .rawRequest(request)
        .rawResponse(response)
        .build();
  }
}