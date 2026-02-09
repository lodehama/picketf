package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hama.picketf.dto.IsaDTO;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.IsaService;

@Controller
@RequestMapping("/isa")
public class IsaController {

  @Autowired
  private IsaService isaService;

  @GetMapping
  public String isa(Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUser user = (CustomUser) auth.getPrincipal();
    int usNum = user.getUsNum().intValue();

    IsaDTO isa = isaService.getIsaByUser(usNum); // DB 조회

    model.addAttribute("isa", isa); // thymeleaf에서 ${isa...}로 사용

    // 총 납입 가능한 금액(1억 - 누적납입)
    if (isa != null) {
      long totalRemain = Math.max(0, 100_000_000L - isa.getIsaTotalAmount());
      model.addAttribute("totalRemain", totalRemain);
    }

    // 올해 납입 가능 금액은 현재 DB에 "올해 납입액"이 없으니 임시로 2천만 풀로 표시
    model.addAttribute("yearRemain", 20_000_000L);

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
      @RequestParam("initialDeposit") long initialDeposit) {

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
