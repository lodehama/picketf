package com.hama.picketf.dto;

import lombok.Data;

@Data
public class EtfMetaDTO {
  private String etfCode;
  private Double etfLeverage;
  private Integer etfFxHedged; // 0/1
}
