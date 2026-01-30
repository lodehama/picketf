package com.hama.picketf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hama.picketf.dao.CpiDAO;
import com.hama.picketf.dto.CpiMinWageRow;
import com.hama.picketf.dto.MinWageResultDTO;

@Service
public class CpiService {

  @Autowired
  private CpiDAO cpiDao;

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
