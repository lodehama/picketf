package com.hama.picketf.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hama.picketf.model.util.UserConst;
import com.hama.picketf.model.vo.UserVO;
import com.hama.picketf.service.UserService;

@Controller
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("blockedNicknames", UserConst.BLOCKED_NICKNAMES);
    return "signup";
  }

  // 아이디 중복 체크
  @GetMapping("/id/check")
  @ResponseBody
  public Map<String, Object> checkId(@RequestParam("userId") String userId) {
    boolean duplicated = userService.existsByUserId(userId);

    Map<String, Object> result = new HashMap<>();
    result.put("duplicated", duplicated);
    return result;
  }

  // 닉네임 차단 여부 체크
  @GetMapping("/nickname/check")
  @ResponseBody
  public Map<String, Object> checkNickname(@RequestParam("nickname") String nickname) {
    boolean blocked = userService.isBlockedNickname(nickname);

    Map<String, Object> result = new HashMap<>();
    result.put("blocked", blocked);
    return result;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/register")
  public String register(UserVO userVO, Model model) {
    try {
      userService.register(userVO);
      return "redirect:/login";
    } catch (IllegalArgumentException e) {
      model.addAttribute("nicknameError", e.getMessage());
      model.addAttribute("user", userVO);
      model.addAttribute("blockedNicknames", UserConst.BLOCKED_NICKNAMES);
      return "signup";
    }
  }
}