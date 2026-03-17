package com.hama.picketf.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hama.picketf.model.util.UserConst;
import com.hama.picketf.model.vo.UserVO;
import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.UserService;

@Controller
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("blockedNicknames", UserConst.BLOCKED_NICKNAMES);
    return "signup";
  }

  // 아이디 형식 + 중복 체크
  @GetMapping("/id/check")
  @ResponseBody
  public Map<String, Object> checkId(@RequestParam("userId") String userId) {
    Map<String, Object> result = new HashMap<>();

    String normalizedId = userId == null ? "" : userId.trim();

    if (normalizedId.isEmpty()) {
      result.put("valid", false);
      result.put("duplicated", false);
      result.put("message", "");
      return result;
    }

    try {
      userService.validateUserId(normalizedId);
    } catch (IllegalArgumentException e) {
      result.put("valid", false);
      result.put("duplicated", false);
      result.put("message", e.getMessage());
      return result;
    }

    boolean duplicated = userService.existsByUserId(normalizedId);

    result.put("valid", true);
    result.put("duplicated", duplicated);
    result.put("message", duplicated ? "이미 사용중인 아이디입니다." : "사용 가능한 아이디입니다.");

    return result;
  }

  // 닉네임 형식 + 차단 여부 체크
  @GetMapping("/nickname/check")
  @ResponseBody
  public Map<String, Object> checkNickname(@RequestParam("nickname") String nickname) {
    Map<String, Object> result = new HashMap<>();

    String normalizedNickname = nickname == null ? "" : nickname.trim();

    if (normalizedNickname.isEmpty()) {
      result.put("valid", false);
      result.put("blocked", false);
      result.put("message", "");
      return result;
    }

    try {
      userService.validateNickname(normalizedNickname);
    } catch (IllegalArgumentException e) {
      result.put("valid", false);
      result.put("blocked", false);
      result.put("message", e.getMessage());
      return result;
    }

    boolean blocked = userService.isBlockedNickname(normalizedNickname);

    result.put("valid", !blocked);
    result.put("blocked", blocked);
    result.put("message", blocked ? "사용할 수 없는 닉네임입니다." : "사용 가능한 닉네임입니다.");

    return result;
  }

  // 회원가입 처리
  @PostMapping("/register")
  public String register(UserVO userVO, Model model) {
    try {
      userService.register(userVO);
      return "redirect:/login";
    } catch (IllegalArgumentException e) {
      model.addAttribute("registerError", e.getMessage());
      model.addAttribute("user", userVO);
      model.addAttribute("blockedNicknames", UserConst.BLOCKED_NICKNAMES);
      return "signup";
    }
  }

  @GetMapping("/mypage")
  public String mypage(Authentication authentication, Model model) {
    CustomUser customUser = (CustomUser) authentication.getPrincipal();
    UserVO loginUser = customUser.getMember();

    model.addAttribute("loginUser", loginUser);
    return "mypage";
  }
}