package com.hama.picketf.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

  // 인증 유저 usNum 뽑는 중복 제거
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
      long lifetimeRemain = isaService.calcTotalRemain(isa);
      model.addAttribute("lifetimeRemain", lifetimeRemain);

      long currentRemain = isaService.calcTotalRemainByRule(isa);
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

  // 예수금 추가 AJAX 처리(JSON 응답)
  @PostMapping("/cash")
  @ResponseBody
  public ResponseEntity<?> cashAddAjax(@RequestParam("amount") long amount) {
    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null) {
      return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다"));
    }

    try {
      isaService.addCash(usNum, amount);

      // 업데이트된 값 다시 조회
      IsaDTO isa = isaService.getIsaByUser(usNum);

      long lifetimeRemain = isaService.calcTotalRemain(isa);
      long currentRemain = isaService.calcTotalRemainByRule(isa);

      Map<String, Object> res = new HashMap<>();
      res.put("isaTotalAmount", isa.getIsaTotalAmount());
      res.put("lifetimeRemain", lifetimeRemain);
      res.put("currentRemain", currentRemain);

      return ResponseEntity.ok(res);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
    }
  }

  // ISA 계좌 삭제 AJAX 처리(JSON 응답)
  @PostMapping("/delete")
  @ResponseBody
  public ResponseEntity<?> deleteIsa() {

    Integer usNum = getLoginUsNumOrNull();
    if (usNum == null) {
      return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다"));
    }

    try {
      boolean deleted = isaService.deleteIsaByUser(usNum);

      if (!deleted) {
        // ISA가 없거나 이미 삭제된 상태
        return ResponseEntity.status(404).body(Map.of("message", "삭제할 ISA 계좌가 없습니다"));
      }

      return ResponseEntity.ok(Map.of("ok", true));

    } catch (IllegalStateException e) {
      // 예: 보유 데이터 때문에 삭제 불가 같은 정책을 넣었을 때
      return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
    }
  }

}
