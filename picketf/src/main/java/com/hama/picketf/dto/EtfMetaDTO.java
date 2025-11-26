package com.hama.picketf.dto;

import lombok.Data;

@Data
public class EtfMetaDTO {
  private String etfCode;

  // 모달/화면에서 쓰는 정보들
  private String etfFullName;
  private String etfName;
  private String etfIssuer;

  private Double etfLeverage;
  private Integer etfFxHedged; // 0/1
  private Double etfRealCost; // 모달2 추가 이후 페이지 로딩 시간이 너무 길어서 여기에 추가

}
