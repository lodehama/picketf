package com.hama.picketf.dto;

import lombok.Data;

@Data
public class MinWageResultDTO {
  private int baseYear;
  private int compareYear;

  private int baseMinWage;
  private int compareMinWage;

  // 예: 0.095 -> 9.5%
  private double growthRate;

  // CAGR 예: 0.030 -> 3.0%
  private double cagrRate;
}
