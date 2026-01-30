package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hama.picketf.dto.MinWageResultDTO;
import com.hama.picketf.service.CpiService;

@Controller
public class CpiController {

  @Autowired
  private CpiService cpiService;

  @GetMapping("/cpi")
  public String cpi() {
    return "cpi";
  }

  @ResponseBody
  @GetMapping("/cpi/minwage")
  public MinWageResultDTO minwage(
      @RequestParam int baseYear,
      @RequestParam int compareYear
  ) {
    return cpiService.calcMinWage(baseYear, compareYear);
  }
}
