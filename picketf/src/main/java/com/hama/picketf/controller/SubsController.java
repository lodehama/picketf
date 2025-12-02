package com.hama.picketf.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hama.picketf.dto.SubsDTO;
import com.hama.picketf.dto.SubsRecommendForm;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.SubsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubsController {

  private final SubsService subsService;

  // 추천 구독 서비스 추가
  @PostMapping("/subs/recommended")
  public String addRecommended(
      @AuthenticationPrincipal CustomUser loginUser,
      @RequestBody List<SubsRecommendForm> forms) {
    Long userNum = loginUser.getUsNum(); // 로그인 유저 PK
    subsService.addRecommendedSubs(userNum, forms);
    return "OK";
  }

  // 추천 구독 서비스에 이미지 아이콘도 함께 저장
  @PostMapping("/subs/add")
  public String addSubs(@RequestBody List<SubsDTO> list,
      @AuthenticationPrincipal CustomUser user) {

    for (SubsDTO dto : list) {
      dto.setSubsUsNum(user.getUsNum());
      dto.setSubsStartDate(LocalDate.now());
      dto.setSubsActive(1); // Integer인 거 기억
    }

    subsService.insertSubsList(list); // ★ 한 번에 INSERT
    return "ok";
  }

}
