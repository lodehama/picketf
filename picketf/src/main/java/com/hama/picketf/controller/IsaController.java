package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hama.picketf.dto.IsaDTO;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.IsaService;

@Controller
@RequestMapping("/isa")
public class IsaController {

  @Autowired
  private IsaService isaService;

  // ✅ 인증 유저 usNum 뽑는 중복 제거
  private Integer getLoginUsNumOrNull() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null)
      return null;

    Object principal = auth.getPrincipal();
    if (!(principal instanceof CustomUser))
      return null;

    CustomUser user = (CustomUser) principal;
    return user.getUsNum().intValue();
  }

  @GetMapping
  public String isa(Model model) {
    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null)
      return "redirect:/login";

    IsaDTO isa = isaService.getIsaByUser(usNum);
    model.addAttribute("isa", isa);

    if (isa != null) {
      long lifetimeRemain = isaService.calcTotalRemain(isa); // 평생 납입 한도 남은 금액 계산
      model.addAttribute("lifetimeRemain", lifetimeRemain);

      long currentRemain = isaService.calcTotalRemainByRule(isa); // 누적 규칙(이월 포함)
      model.addAttribute("currentRemain", currentRemain);

    }
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

    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null)
      return "redirect:/login";

    isaService.createIsa(usNum, openedYear, accountType, initialDeposit);
    return "redirect:/isa";
  }

  // 예수금 추가: 화면
  @GetMapping("/cash")
  public String cashAddPage(Model model) {
    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null)
      return "redirect:/login";

    IsaDTO isa = isaService.getIsaByUser(usNum);
    if (isa == null) {
      // ISA 계좌 없으면 예수금 추가 불가 -> 계좌 만들기로 유도
      return "redirect:/isa";
    }

    model.addAttribute("isa", isa);
    return "isa-cash"; // ← 너가 만들 예수금 추가 페이지
  }

  // 예수금 추가: 처리
  @PostMapping("/cash")
  public String cashAddSubmit(@RequestParam("amount") long amount) {
    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null)
      return "redirect:/login";

    // 간단 검증 (원하면 서비스로 내려도 됨)
    if (amount <= 0) {
      return "redirect:/isa/cash";
    }

    isaService.addCash(usNum, amount); // 서비스에 구현 필요
    return "redirect:/isa";
  }
}
