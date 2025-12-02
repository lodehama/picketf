package com.hama.picketf.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hama.picketf.dto.SubsRecommendForm;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.SubsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubsController {

  private final SubsService subsService;

  @PostMapping("/subs/recommended")
  public String addRecommended(
      @AuthenticationPrincipal CustomUser loginUser,
      @RequestBody List<SubsRecommendForm> forms) {
    Long userNum = loginUser.getUsNum(); // 로그인 유저 PK
    subsService.addRecommendedSubs(userNum, forms);
    return "OK";
  }
}
