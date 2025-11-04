package com.hama.picketf.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hama.picketf.service.EtfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EtfController {

  private final EtfService etfService;

  // /api/etf/real-cost?code=379800
  @GetMapping("/api/etf/real-cost")
  public Double getEtfRealCost(@RequestParam String code) {
    return etfService.getEtfRealCostByCode(code);
  }

  // 테이블 필터 기능
  // /api/etf/meta?codes=360750,379800,360200
  @GetMapping("/api/etf/meta")
  public Map<String, Map<String, Object>> getEtfMeta(@RequestParam String codes) {
    // 콤마 분리 + 공백 제거 + 비어있는 항목 제거
    List<String> codeList = Arrays.stream(codes.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

    if (codeList.isEmpty()) {
      return Collections.emptyMap();
    }

    return etfService.getEtfMetaMapByCodes(codeList);
  }
}
