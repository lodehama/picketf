package com.hama.picketf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

  @GetMapping
  public String portfolio() {
    return "portfolio";
  }

  @GetMapping("/add")
  public String add() {
    return "portfolio-add";
  }

  @PostMapping("/add")
  public String addSubmit() {
    // 로직 추가예정
    return "redirect:/portfolio";
  }

}
