package com.hama.picketf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hama.picketf.dao.UserDAO;
import com.hama.picketf.model.util.UserConst;
import com.hama.picketf.model.vo.UserVO;

@Service
public class UserService {

  @Autowired
  UserDAO userDAO;

  @Autowired
  PasswordEncoder passwordEncoder;

  public void register(UserVO userVO) {
    String nickname = userVO.getUs_nickname();

    if (nickname != null) {
      nickname = nickname.trim();
      userVO.setUs_nickname(nickname);
    }

    if (nickname == null || nickname.isEmpty()) {
      throw new IllegalArgumentException("닉네임을 입력해주세요.");
    }

    if (UserConst.BLOCKED_NICKNAMES.contains(nickname.toLowerCase())) {
      throw new IllegalArgumentException("사용할 수 없는 닉네임입니다.");
    }

    userVO.setUs_pw(passwordEncoder.encode(userVO.getUs_pw()));
    userVO.setUs_authority("USER");

    userDAO.insertUser(userVO);
  }

  public boolean isBlockedNickname(String nickname) {
    if (nickname == null || nickname.trim().isEmpty()) {
      return false;
    }

    return UserConst.BLOCKED_NICKNAMES.contains(nickname.trim().toLowerCase());
  }

  public boolean isDuplicate(String us_id) {
    return userDAO.selectUser(us_id) != null;
  }

  public int getUserNum(String username) {
    UserVO user = userDAO.selectUser(username);
    return user != null ? user.getUs_num() : 0;
  }
}