package com.hama.picketf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IsaTaxController {
  @GetMapping("/isa/tax")
  public String isatax() {
    return "isa-tax";
  }

}
