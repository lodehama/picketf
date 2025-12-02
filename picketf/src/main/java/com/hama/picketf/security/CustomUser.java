package com.hama.picketf.security;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.hama.picketf.model.vo.UserVO;

import lombok.Data;

@Data
public class CustomUser extends User {

	private UserVO member;

	public CustomUser(UserVO vo) {
		super(
				vo.getUs_id(),
				vo.getUs_pw(),
				Arrays.asList(new SimpleGrantedAuthority(vo.getUs_authority())));
		this.member = vo;
	}

	public UserVO getUser() {
		return member;
	}

	// 편의 메서드 (선택)
	public Long getUsNum() {
		return (long) member.getUs_num();
	}

}
