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
		String userId = normalize(userVO.getUs_id());
		String nickname = normalize(userVO.getUs_nickname());
		String email = normalize(userVO.getUs_email());
		String password = userVO.getUs_pw();

		userVO.setUs_id(userId);
		userVO.setUs_nickname(nickname);
		userVO.setUs_email(email);

		// 아이디 검증
		if (userId == null || userId.isEmpty()) {
			throw new IllegalArgumentException("아이디를 입력해주세요.");
		}

		if (existsByUserId(userId)) {
			throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
		}

		// 닉네임 검증
		validateNickname(nickname);

		if (isBlockedNickname(nickname)) {
			throw new IllegalArgumentException("사용할 수 없는 닉네임입니다.");
		}

		// 이메일 검증
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("이메일을 입력해주세요.");
		}

		// 비밀번호 검증
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("비밀번호를 입력해주세요.");
		}

		// 비밀번호 암호화 및 권한 설정
		userVO.setUs_pw(passwordEncoder.encode(password));
		userVO.setUs_authority("USER");

		userDAO.insertUser(userVO);
	}

	private void validateNickname(String nickname) {

		if (nickname == null || nickname.isEmpty()) {
			throw new IllegalArgumentException("닉네임을 입력해주세요.");
		}

		int koreanCount = 0;
		int englishCount = 0;
		int numberCount = 0;

		for (char c : nickname.toCharArray()) {

			if (c >= '가' && c <= '힣') {
				koreanCount++;
			} else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				englishCount++;
			} else if (c >= '0' && c <= '9') {
				numberCount++;
			} else {
				throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.");
			}
		}

		boolean valid = koreanCount >= 2 ||
				englishCount >= 3 ||
				numberCount >= 4;

		if (!valid) {
			throw new IllegalArgumentException(
					"닉네임은 한글 2자 이상, 영문 3자 이상, 숫자 4자 이상 중 하나를 만족해야 합니다.");
		}
	}

	public boolean isBlockedNickname(String nickname) {
		String normalizedNickname = normalize(nickname);

		if (normalizedNickname == null || normalizedNickname.isEmpty()) {
			return false;
		}

		return UserConst.BLOCKED_NICKNAMES.contains(normalizedNickname.toLowerCase());
	}

	public boolean existsByUserId(String us_id) {
		String normalizedId = normalize(us_id);

		if (normalizedId == null || normalizedId.isEmpty()) {
			return false;
		}

		return userDAO.countByUserId(normalizedId) > 0;
	}

	public int getUserNum(String username) {
		String normalizedUsername = normalize(username);
		UserVO user = userDAO.selectUser(normalizedUsername);
		return user != null ? user.getUs_num() : 0;
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}
}