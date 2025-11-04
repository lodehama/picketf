package com.hama.picketf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hama.picketf.service.EtfService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EtfController {

  private final EtfService etfService;

  // 예: /api/etf/real-cost?code=379800
  @GetMapping("/api/etf/real-cost")
  public Double getEtfRealCost(@RequestParam String code) {
    return etfService.getEtfRealCostByCode(code);
  }
}
