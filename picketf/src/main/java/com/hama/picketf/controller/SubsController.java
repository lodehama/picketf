package com.hama.picketf.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hama.picketf.dto.SubsDTO;
import com.hama.picketf.dto.SubsRecommendForm;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.SubsService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SubsController {

  private final SubsService subsService;

  @GetMapping("/subs")
  public String viewSubsPage(
      @AuthenticationPrincipal CustomUser user,
      @RequestParam(defaultValue = "date") String sort, // date 또는 price
      @RequestParam(defaultValue = "desc") String dir, // asc 또는 desc
      Model model) {

    Long userNum = user.getUsNum();

    // 방어 로직 (이상한 값 들어와도 기본값으로 맞춰줌)
    if (!"price".equals(sort)) {
      sort = "date"; // 기본: 구독일 기준
    }
    if (!"asc".equals(dir) && !"desc".equals(dir)) {
      dir = "desc"; // 기본: 최신순(내림차순)
    }

    // 새로 만들 정렬 지원 메서드 호출
    List<SubsDTO> subsList = subsService.getSubsListByUserSorted(userNum, sort, dir);

    model.addAttribute("subsList", subsList);
    model.addAttribute("subsCount", subsList.size());
    model.addAttribute("sort", sort);
    model.addAttribute("dir", dir);

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

  // 구독 활성/비활성 변경
  @PostMapping("/subs/{subsNum}/active")
  @ResponseBody
  public Map<String, Object> updateSubsActive(
      @PathVariable Long subsNum,
      @RequestBody Map<String, Integer> body,
      @AuthenticationPrincipal CustomUser user) {

    Long userNum = user.getUsNum(); // 세션 대신 시큐리티 사용

    int active = body.getOrDefault("active", 0);
    subsService.updateSubsActive(userNum, subsNum, active);

    Map<String, Object> res = new HashMap<>();
    res.put("success", true);
    res.put("active", active);
    return res;
  }

  @GetMapping("/subs2")
  public String subs2() {
    return "subs2";
  }

}
