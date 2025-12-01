/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.product.dto.request;

import com.wit.payment.domain.product.entity.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "UpdateProductRequest", description = "상품 수정 요청 DTO")
public class UpdateProductRequest {

  @Schema(description = "상품 이름", example = "아이스 아메리카노")
  private String name;

  @Schema(description = "상품 부제목(최대 30자)", example = "시원한 여름 시그니처 메뉴")
  @Size(max = 30, message = "상품 한줄 설명은 최대 30자까지 입력 가능합니다.")
  private String subTitle;

  @Schema(description = "상품 가격", example = "4500")
  private Integer price;

  @Schema(description = "상품 설명(최대 200자)", example = "깊고 진한 에스프레소에 시원한 얼음을 더한 음료입니다.")
  @Size(max = 200, message = "상품 설명은 최대 200자까지 입력 가능합니다.")
  private String description;

  @Schema(description = "상품 상태", example = "ON_SALE")
  private ProductStatus status;

  @Schema(description = "상품을 노출할 키오스크 ID 목록", example = "[1, 2, 3]")
  private List<Long> kioskIds;
}
