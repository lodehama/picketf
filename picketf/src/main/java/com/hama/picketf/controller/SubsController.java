package com.hama.picketf.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hama.picketf.dto.SubsDTO;
import com.hama.picketf.dto.SubsRecommendForm;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.SubsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SubsController {

  private final SubsService subsService;

  @GetMapping("/subs")
  public String viewSubsPage(Model model, Authentication authentication) {
    System.out.println("===== SubsController /subs 진입함 =====");
    System.out.println("authentication = " + authentication);

    CustomUser customUser = (CustomUser) authentication.getPrincipal();
    Long userNum = customUser.getUsNum();
    System.out.println("DEBUG /subs userNum = " + userNum);

    List<SubsDTO> subsList = subsService.getSubsListByUser(userNum);
    System.out.println("DEBUG SubsService.getSubsListByUser size = " + subsList.size());

    model.addAttribute("subsList", subsList);
    model.addAttribute("subsCount", subsList.size());

    return "subs";
  }

  // 추천 구독 서비스 추가 (JSON/문자열로 응답)
  @PostMapping("/subs/recommended")
  @ResponseBody
  public String addRecommended(
      @AuthenticationPrincipal CustomUser loginUser,
      @RequestBody List<SubsRecommendForm> forms) {

    Long userNum = loginUser.getUsNum();
    subsService.addRecommendedSubs(userNum, forms);
    return "OK";
  }

  // 한 번에 구독 추가
  @PostMapping("/subs/add")
  @ResponseBody
  public String addSubs(@RequestBody List<SubsDTO> list,
      @AuthenticationPrincipal CustomUser user) {

    for (SubsDTO dto : list) {
      dto.setSubsUsNum(user.getUsNum());
      dto.setSubsStartDate(LocalDate.now());
      dto.setSubsActive(1);
    }

    subsService.insertSubsList(list);
    return "OK";
  }

  @GetMapping("/subs2")
  public String subs2() {
    return "subs2";
  }

}
