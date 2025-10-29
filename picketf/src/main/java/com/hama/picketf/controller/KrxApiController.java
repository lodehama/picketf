package com.hama.picketf.controller;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hama.picketf.dto.KrxEtfDTO;
import com.hama.picketf.service.KrxApiService;

import lombok.RequiredArgsConstructor;

// KrxApiController.java
@RestController
@RequestMapping("/api/krx")
@RequiredArgsConstructor
public class KrxApiController {

  private final KrxApiService krxApiService;

  @GetMapping("/{isuCd}")
  public ResponseEntity<?> getOne(@PathVariable String isuCd,
      @RequestParam(required = false) String basDd) {
    try {
      KrxEtfDTO dto = krxApiService.getEtf(isuCd, basDd);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return ResponseEntity.status(502).body(Map.of(
          "error", "upstream_call_failed",
          "message", e.getMessage()));
    }
  }

  @GetMapping
  public ResponseEntity<?> getMany(@RequestParam String codes,
      @RequestParam(required = false) String basDd) {
    try {
      List<String> list = Arrays.stream(codes.split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .toList();
      Map<String, KrxEtfDTO> map = krxApiService.getEtfs(list, basDd);
      return ResponseEntity.ok(map);
    } catch (Exception e) {
      return ResponseEntity.status(502).body(Map.of(
          "error", "upstream_call_failed",
          "message", e.getMessage()));
    }
  }

  // 프론트 분리 테스트용
  @GetMapping("/_dummy")
  public Map<String, KrxEtfDTO> dummy() {
    return Map.of("379800", new KrxEtfDTO(), "433330", new KrxEtfDTO());
  }
}
