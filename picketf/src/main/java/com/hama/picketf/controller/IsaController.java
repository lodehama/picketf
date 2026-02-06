package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.IsaService;

@Controller
@RequestMapping("/isa")
public class IsaController {

  @Autowired
  private IsaService isaService;

  @GetMapping
  public String isa() {
    return "isa";
  }

  @GetMapping("/add")
  public String add() {
    return "isa-add";
  }

  @PostMapping("/add")
  public String addSubmit(
      @RequestParam("opened_year") int openedYear,
      @RequestParam("accountType") String accountType,
      @RequestParam("initialDeposit") long initialDeposit
  ) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    Object principal = auth.getPrincipal();
    if (!(principal instanceof CustomUser)) {
      return "redirect:/login";
    }

    CustomUser user = (CustomUser) principal;
    int usNum = user.getUsNum().intValue();

    isaService.createIsa(usNum, openedYear, accountType, initialDeposit);

    return "redirect:/isa";
  }
}
