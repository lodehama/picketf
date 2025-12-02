package com.hama.picketf.dto;

import lombok.Data;

@Data
public class SubsRecommendForm {
  private String name; // 구독 이름 (넷플릭스, 통신비 ...)
  private Integer price; // 사용자가 수정한 가격
  private String type; // 지출 타입 (생활/콘텐츠/음악 등)
}
