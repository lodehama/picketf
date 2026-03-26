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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  // 회원가입용 닉네임 형식 + 차단 여부 체크
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

  // 마이페이지용 닉네임 검사
  @GetMapping("/mypage/nickname/check")
  @ResponseBody
  public Map<String, Object> checkNicknameForMypage(
      @RequestParam("nickname") String nickname,
      Authentication authentication) {

    Map<String, Object> result = new HashMap<>();

    String normalizedNickname = nickname == null ? "" : nickname.trim();

    if (normalizedNickname.isEmpty()) {
      result.put("valid", false);
      result.put("same", false);
      result.put("message", "");
      return result;
    }

    try {
      CustomUser customUser = (CustomUser) authentication.getPrincipal();
      UserVO loginUser = customUser.getMember();

      String currentNickname = loginUser.getUs_nickname() == null ? "" : loginUser.getUs_nickname().trim();

      userService.validateNickname(normalizedNickname);

      if (userService.isBlockedNickname(normalizedNickname)) {
        result.put("valid", false);
        result.put("same", false);
        result.put("message", "사용할 수 없는 닉네임입니다.");
        return result;
      }

      if (currentNickname.equalsIgnoreCase(normalizedNickname)) {
        result.put("valid", false);
        result.put("same", true);
        result.put("message", "현재 닉네임과 동일합니다.");
        return result;
      }

      if (userService.existsByNickname(normalizedNickname)) {
        result.put("valid", false);
        result.put("same", false);
        result.put("message", "이미 사용중인 닉네임입니다.");
        return result;
      }

      result.put("valid", true);
      result.put("same", false);
      result.put("message", "사용 가능한 닉네임입니다.");

    } catch (IllegalArgumentException e) {
      result.put("valid", false);
      result.put("same", false);
      result.put("message", e.getMessage());
    }

    return result;
  }

  // 마이페이지 닉네임 변경 처리
  @PostMapping("/mypage/update")
  public String updateNickname(
      @RequestParam("us_nickname") String nickname,
      Authentication authentication,
      Model model) {

    CustomUser customUser = (CustomUser) authentication.getPrincipal();
    UserVO loginUser = customUser.getMember();

    try {
      userService.updateNickname(loginUser.getUs_num(), nickname);

      // DB 반영 후 세션 사용자 정보도 같이 갱신
      loginUser.setUs_nickname(nickname == null ? "" : nickname.trim());
      customUser.setMember(loginUser);

      model.addAttribute("loginUser", loginUser);
      model.addAttribute("nicknameSuccess", "닉네임이 변경되었습니다.");
      return "mypage";

    } catch (IllegalArgumentException e) {
      model.addAttribute("loginUser", loginUser);
      model.addAttribute("nicknameError", e.getMessage());
      return "mypage";
    }
  }

  @PostMapping("/mypage/password")
  public String updatePassword(@RequestParam("currentPassword") String currentPassword,
      @RequestParam("newPassword") String newPassword,
      @RequestParam("newPasswordConfirm") String newPasswordConfirm,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    CustomUser customUser = (CustomUser) authentication.getPrincipal();
    UserVO loginUser = customUser.getMember();

    try {
      userService.updatePassword(
          loginUser.getUs_num(),
          currentPassword,
          newPassword,
          newPasswordConfirm);

      redirectAttributes.addFlashAttribute("passwordSuccess", "비밀번호가 변경되었습니다.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
    }

    return "redirect:/mypage";
  }
}