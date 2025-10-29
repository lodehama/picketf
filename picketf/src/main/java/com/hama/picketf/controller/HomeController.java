package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hama.picketf.model.vo.UserVO;
import com.hama.picketf.service.UserService;

@Controller
public class HomeController {

  @Autowired
  UserService userService;

  
  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }
  
  @PostMapping("/register")
  public String register(UserVO userVO) {
    userService.register(userVO);
    return "redirect:/login";
  }
  @GetMapping("/")
  public String home() {
    return "index";
  }

  @GetMapping("/snp")
  public String snp() {
    return "snp";
  }

  @GetMapping("/qqq")
  public String qqq() {
    return "qqq";
  }

  @GetMapping("/m7")
  public String m7() {
    return "m7";
  }

  @GetMapping("/updates")
  public String updates() {
    return "updates";
  }

}
