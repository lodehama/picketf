package com.hama.picketf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/isa/tax")
public class IsaTaxController {

  @GetMapping
  public String page() {
    return "isa-tax";
  }

}
