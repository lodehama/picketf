package com.hama.picketf.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.hama.picketf.dao.EtfDAO;

@Service
@RequiredArgsConstructor
public class EtfService {

  private final EtfDAO etfDAO;

  public Double getEtfTerByCode(String code) {
    return etfDAO.selectEtfTerByCode(code);
  }
}
