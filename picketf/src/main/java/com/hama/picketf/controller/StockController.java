package com.hama.picketf.controller;

import com.hama.picketf.dto.NaverStockQuote;
import com.hama.picketf.service.NaverStockService;

import java.util.List;
import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class StockController {

  private final NaverStockService naver;

  public StockController(NaverStockService naver) {
    this.naver = naver;
  }

  // 화면용 (최초 진입)
  @GetMapping("/stocks/{code}")
  public String stockView(@PathVariable String code, Model model) {
    model.addAttribute("code", code);
    return "snp"; // templates/stocks/view.html
  }

  // AJAX 폴링용 API
  @GetMapping("/api/stocks/{code}")
  @ResponseBody
  public ResponseEntity<NaverStockQuote> stockApi(@PathVariable String code) {
    return ResponseEntity.ok(naver.fetchQuote(code));
  }

  // 여러 종목을 한 번에 가져오기 (ex: /api/stocks?codes=005930,000660,373220)
  @GetMapping("/api/stocks")
  @ResponseBody
  public ResponseEntity<List<NaverStockQuote>> stockApiBatch(
      @RequestParam(name = "codes") String codesCsv) {

    List<String> codes = Arrays.stream(codesCsv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();

    return ResponseEntity.ok(naver.fetchQuotes(codes));
  }

  // 여기까지 네이버

}
