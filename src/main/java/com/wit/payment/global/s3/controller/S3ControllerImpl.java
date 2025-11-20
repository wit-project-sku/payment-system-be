/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.s3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wit.payment.global.response.BaseResponse;
import com.wit.payment.global.s3.dto.S3Response;
import com.wit.payment.global.s3.entity.PathName;
import com.wit.payment.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class S3ControllerImpl implements S3Controller {

  private final S3Service s3Service;

  @Override
  public ResponseEntity<BaseResponse<S3Response>> uploadImage(
      PathName pathName, MultipartFile file) {

    S3Response s3Response = s3Service.uploadImage(pathName, file);
    return ResponseEntity.ok(BaseResponse.success("이미지 업로드에 성공했습니다.", s3Response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<S3Response>>> listFiles(PathName pathName) {
    List<S3Response> files = s3Service.getAllFiles(pathName);
    return ResponseEntity.ok(BaseResponse.success(files));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteByUrl(String url) {
    s3Service.deleteByUrl(url);
    return ResponseEntity.ok(BaseResponse.success("파일 삭제에 성공했습니다."));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteFile(PathName pathName, String fileName) {
    s3Service.deleteFile(pathName, fileName);
    return ResponseEntity.ok(BaseResponse.success("파일 삭제에 성공했습니다."));
  }
}
