package com.hama.picketf.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.hama.picketf.dao.EtfDAO;
import com.hama.picketf.dto.EtfMetaDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtfService {

  private final EtfDAO etfDAO;

  public Double getEtfRealCostByCode(String code) {
    return etfDAO.selectEtfRealCostByCode(code);
  }

  // 테이블 필터 기능
  public Map<String, Map<String, Object>> getEtfMetaMapByCodes(List<String> codes) {
    List<EtfMetaDTO> list = etfDAO.selectEtfMetaByCodes(codes);

    // 응답을 { code: { etf_leverage: x, etf_fx_hedged: y } } 형태로 변환
    Map<String, Map<String, Object>> result = new LinkedHashMap<>();
    for (EtfMetaDTO m : list) {
      Map<String, Object> v = new LinkedHashMap<>();
      v.put("etf_full_name", m.getEtfFullName());
      v.put("etf_name", m.getEtfName());
      v.put("etf_issuer", m.getEtfIssuer());
      v.put("etf_leverage", m.getEtfLeverage());
      v.put("etf_fx_hedged", m.getEtfFxHedged());
      v.put("etf_real_cost", m.getEtfRealCost());
      v.put("etf_listed_date", m.getEtfListedDate());
      v.put("etf_url", m.getEtfUrl());
      result.put(m.getEtfCode(), v);
    }
    return result;
  }
}
