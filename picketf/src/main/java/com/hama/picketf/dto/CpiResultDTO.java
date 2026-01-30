package com.hama.picketf.dto;

import lombok.Data;

@Data
public class CpiResultDTO {

  private int baseYear;
  private int compareYear;

  private long baseAmount;      // 입력값 (기준년도 금액)
  private long compareAmount;   // 계산값 (비교년도 금액)

  private double inflationRate; // 물가 상승률(누적)
  private double cagrRate;      // 물가 연평균 상승률(CAGR)

}
