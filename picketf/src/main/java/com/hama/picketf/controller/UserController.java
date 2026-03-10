package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hama.picketf.model.vo.UserVO;
import com.hama.picketf.service.UserService;

@Controller
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  // 회원가입 처리
  @PostMapping("/register")
  public String register(UserVO userVO) {
    userService.register(userVO);
    return "redirect:/login";
  }
}
