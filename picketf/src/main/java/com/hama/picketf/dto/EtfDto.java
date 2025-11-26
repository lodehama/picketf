package com.hama.picketf.dto;

import lombok.Data;

@Data
public class EtfDTO {
  private Long etfNum;
  private String etfCategory;
  private String etfCode;
  private String etfFullName;
  private String etfName;
  private String etfIssuer;
  private Double etfRealCost;
  private Double etfLeverage;
  private Boolean etfFxHedged;
}
