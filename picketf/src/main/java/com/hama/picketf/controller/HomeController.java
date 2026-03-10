package com.hama.picketf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.hama.picketf.service.UserService;

@Controller
public class HomeController {

  @Autowired
  UserService userService;

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

  @GetMapping("/devlog")
  public String devlog() {
    return "devlog";
  }

  // @GetMapping("/fees")
  // public String fees() {
  // return "fees";
  // }

}
