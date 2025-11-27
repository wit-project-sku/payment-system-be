/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wit.payment.global.exception.CustomException;
import com.wit.payment.global.s3.S3Config;
import com.wit.payment.global.s3.dto.S3Response;
import com.wit.payment.global.s3.entity.PathName;
import com.wit.payment.global.s3.exception.S3ErrorCode;
import com.wit.payment.global.s3.mapper.S3Mapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${app.file.max-size-bytes}")
  private long maxFileSizeBytes;

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;
  private final S3Mapper s3Mapper;

  /**
   * Multipart 이미지 파일을 업로드하고, S3Response를 반환합니다.
   */
  public S3Response uploadImage(PathName pathName, MultipartFile file) {

    String keyName = uploadFile(pathName, file);
    S3Response response = s3Mapper.toResponse(keyName);

    log.info(
        "이미지 업로드 성공 - pathName: {}, keyName: {}, imageUrl: {}",
        pathName,
        keyName,
        response.getImageUrl());

    return response;
  }

  /**
   * Multipart 파일을 지정한 PathName 경로에 업로드하고 S3 객체 keyName을 반환합니다.
   */
  public String uploadFile(PathName pathName, MultipartFile file) {

    validateFile(file);

    String extension = getFileExtension(file.getOriginalFilename());
    String keyName = createKeyName(pathName, extension);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));

      log.info("파일 업로드 성공 - bucket: {}, keyName: {}", s3Config.getBucket(), keyName);

      return keyName;
    } catch (AmazonS3Exception e) {
      log.error(
          "S3 업로드 중 AmazonS3Exception 발생 - bucket: {}, keyName: {}, message: {}",
          s3Config.getBucket(),
          keyName,
          e.getMessage(),
          e);
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    } catch (IOException e) {
      log.error(
          "S3 업로드 중 IO 예외 발생 - bucket: {}, keyName: {}, message: {}",
          s3Config.getBucket(),
          keyName,
          e.getMessage(),
          e);
      throw new CustomException(S3ErrorCode.IO_EXCEPTION);
    }
  }

  /**
   * keyName으로 S3에서 특정 파일을 삭제합니다.
   */
  public void deleteFile(String keyName) {

    assertFileExists(keyName);

    try {
      amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), keyName));
      log.info("파일 삭제 성공 - bucket: {}, keyName: {}", s3Config.getBucket(), keyName);
    } catch (AmazonS3Exception e) {
      log.error(
          "S3 삭제 중 AmazonS3Exception 발생 - bucket: {}, keyName: {}, message: {}",
          s3Config.getBucket(),
          keyName,
          e.getMessage(),
          e);
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    }
  }

  /**
   * 지정된 PathName 경로의 모든 파일 목록을 조회합니다.
   */
  public List<S3Response> getAllFiles(PathName pathName) {

    String prefix = getPrefix(pathName);

    try {
      ListObjectsV2Result result =
          amazonS3.listObjectsV2(
              new ListObjectsV2Request().withBucketName(s3Config.getBucket()).withPrefix(prefix));

      List<S3Response> responses = s3Mapper.toResponseList(result.getObjectSummaries());

      log.info(
          "파일 목록 조회 성공 - bucket: {}, pathName: {}, prefix: {}, count: {}",
          s3Config.getBucket(),
          pathName,
          prefix,
          responses.size());

      return responses;
    } catch (AmazonS3Exception e) {
      log.error(
          "S3 파일 목록 조회 중 AmazonS3Exception 발생 - bucket: {}, prefix: {}, message: {}",
          s3Config.getBucket(),
          prefix,
          e.getMessage(),
          e);
      throw new CustomException(S3ErrorCode.S3_CONNECTION_FAILED);
    }
  }

  /**
   * PathName + 파일명으로 파일을 삭제합니다.
   */
  public void deleteFile(PathName pathName, String fileName) {

    String keyName = getPrefix(pathName) + "/" + fileName;

    log.info("파일 삭제 요청 - pathName: {}, fileName: {}, keyName: {}", pathName, fileName, keyName);

    deleteFile(keyName);
  }

  /**
   * 이미지 URL에서 keyName(path + fileName)을 추출하여 파일을 삭제합니다.
   */
  public void deleteByUrl(String url) {

    log.info("URL 기반 파일 삭제 요청 - url: {}", url);

    String keyName = extractKeyNameFromUrl(url);

    deleteFile(keyName);
  }

  /* ===================== 검증/헬퍼 메서드 ===================== */

  private void validateFile(MultipartFile file) {

    if (file.getSize() > maxFileSizeBytes) {
      log.warn(
          "파일 사이즈 초과 - size: {} bytes, limit: {} bytes, originalFilename: {}",
          file.getSize(),
          maxFileSizeBytes,
          file.getOriginalFilename());
      throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      log.warn(
          "허용되지 않는 파일 타입 - contentType: {}, originalFilename: {}",
          contentType,
          file.getOriginalFilename());
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
  }

  private String createKeyName(PathName pathName, String extension) {
    return getPrefix(pathName) + "/" + UUID.randomUUID() + extension;
  }

  private void assertFileExists(String keyName) {

    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      log.warn("S3 객체 미존재 - bucket: {}, keyName: {}", s3Config.getBucket(), keyName);
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }
  }

  private String extractKeyNameFromUrl(String url) {

    String bucketUrlPrefix = getBucketUrlPrefix();

    if (!url.startsWith(bucketUrlPrefix)) {
      log.warn("S3 URL 형식 불일치 - expectedPrefix: {}, actualUrl: {}", bucketUrlPrefix, url);
      throw new CustomException(S3ErrorCode.FILE_NAME_MISSING);
    }

    return url.substring(bucketUrlPrefix.length());
  }

  private String getBucketUrlPrefix() {
    return "https://" + s3Config.getBucket() + ".s3." + s3Config.getRegion() + ".amazonaws.com/";
  }

  private String getPrefix(PathName pathName) {
    return switch (pathName) {
      case PRODUCT -> s3Config.getProductFolder();
    };
  }

  private String getFileExtension(String originalName) {

    if (originalName == null || !originalName.contains(".")) {
      log.warn("유효하지 않은 파일명 - originalName: {}", originalName);
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }

    return originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
  }
}
