package com.hama.picketf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CpiController {

  @GetMapping("/cpi")
  public String cpi() {
    return "cpi";
  }

}
