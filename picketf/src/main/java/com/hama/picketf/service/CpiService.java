package com.hama.picketf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hama.picketf.dao.CpiDAO;
import com.hama.picketf.dto.CpiMinWageRow;
import com.hama.picketf.dto.CpiResultDTO;
import com.hama.picketf.dto.MinWageResultDTO;

@Service
public class CpiService {

  @Autowired
  private CpiDAO cpiDao;

  // CPI 계산 (비교년도 금액, 물가상승률, CAGR)
  public CpiResultDTO calcCpi(int baseYear, int compareYear, long baseAmount) {

    Double baseCpi = cpiDao.findCpiValueByYear(baseYear);
    Double compareCpi = cpiDao.findCpiValueByYear(compareYear);

    if (baseCpi == null || compareCpi == null) {
      throw new IllegalArgumentException("해당 연도의 CPI 데이터가 없습니다.");
    }
    if (baseCpi <= 0 || compareCpi <= 0) {
      throw new IllegalArgumentException("CPI 값이 올바르지 않습니다.");
    }

    // 1) 비교년도 금액(기준금액을 비교년도 가치로 환산)
    double compareAmountRaw = baseAmount * (compareCpi / baseCpi);
    long compareAmount = Math.round(compareAmountRaw);

    // 2) 물가 상승률(누적): (기준/비교) - 1
    double inflation = (baseCpi / compareCpi) - 1.0;

    // 3) CAGR
    int years = Math.abs(compareYear - baseYear);
    double cagr;
    if (years == 0) {
      cagr = 0.0;
    } else {
      double ratio = baseCpi / compareCpi;
      cagr = Math.pow(ratio, 1.0 / years) - 1.0;
    }

    CpiResultDTO dto = new CpiResultDTO();
    dto.setBaseYear(baseYear);
    dto.setCompareYear(compareYear);
    dto.setBaseAmount(baseAmount);
    dto.setCompareAmount(compareAmount);
    dto.setInflationRate(inflation);
    dto.setCagrRate(cagr);

    return dto;
  }

  // 최저시급 조회 (계산)
  public MinWageResultDTO calcMinWage(int baseYear, int compareYear) {

    CpiMinWageRow baseRow = cpiDao.findMinWageByYear(baseYear);
    CpiMinWageRow compareRow = cpiDao.findMinWageByYear(compareYear);

    if (baseRow == null || compareRow == null) {
      // 간단 처리: 없는 연도면 예외 (프론트에서 알림 처리)
      throw new IllegalArgumentException("해당 연도의 최저시급 데이터가 없습니다.");
    }

    int baseMin = baseRow.getCpiMinWage();
    int compareMin = compareRow.getCpiMinWage();

    // 상승률: (기준/비교) - 1
    double growth = (baseMin / (double) compareMin) - 1.0;

    // CAGR: (기준/비교)^(1/n) - 1
    int years = Math.abs(compareYear - baseYear);
    double cagr;

    if (years == 0) {
      cagr = 0.0;
    } else {
      double ratio = baseMin / (double) compareMin;
      cagr = Math.pow(ratio, 1.0 / years) - 1.0;
    }

    MinWageResultDTO dto = new MinWageResultDTO();
    dto.setBaseYear(baseYear);
    dto.setCompareYear(compareYear);
    dto.setBaseMinWage(baseMin);
    dto.setCompareMinWage(compareMin);
    dto.setGrowthRate(growth);
    dto.setCagrRate(cagr);

    return dto;
  }

}
