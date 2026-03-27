package com.hama.picketf.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hama.picketf.dao.UserDAO;
import com.hama.picketf.model.util.UserConst;
import com.hama.picketf.model.vo.UserVO;

@Service
public class UserService {

	private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{3,15}$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9!@#$%^&*]{8,20}$");
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣A-Za-z0-9]{2,8}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
		validateUserId(userId);

		if (existsByUserId(userId)) {
			throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
		}

		// 비밀번호 검증
		validatePassword(password);

		// 닉네임 검증
		validateNickname(nickname);

		if (isBlockedNickname(nickname)) {
			throw new IllegalArgumentException("사용할 수 없는 닉네임입니다.");
		}

		if (existsByNickname(nickname)) {
			throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
		}

		// 이메일 검증
		validateEmail(email);

		userVO.setUs_pw(passwordEncoder.encode(password));
		userVO.setUs_authority("USER");

		userDAO.insertUser(userVO);
	}

	public void updateNickname(int userNum, String newNickname) {
		String nickname = normalize(newNickname);

		validateNickname(nickname);

		if (isBlockedNickname(nickname)) {
			throw new IllegalArgumentException("사용할 수 없는 닉네임입니다.");
		}

		UserVO loginUser = userDAO.selectUserByNum(userNum);
		if (loginUser == null) {
			throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
		}

		String currentNickname = normalize(loginUser.getUs_nickname());

		if (currentNickname != null && currentNickname.equalsIgnoreCase(nickname)) {
			throw new IllegalArgumentException("현재 닉네임과 동일합니다.");
		}

		if (existsByNickname(nickname)) {
			throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
		}

		userDAO.updateNickname(userNum, nickname);
	}

	public String checkNicknameForUpdate(int userNum, String nickname) {
		String normalizedNickname = normalize(nickname);

		validateNickname(normalizedNickname);

		if (isBlockedNickname(normalizedNickname)) {
			throw new IllegalArgumentException("사용할 수 없는 닉네임입니다.");
		}

		UserVO loginUser = userDAO.selectUserByNum(userNum);
		if (loginUser == null) {
			throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
		}

		String currentNickname = normalize(loginUser.getUs_nickname());

		if (currentNickname != null && currentNickname.equalsIgnoreCase(normalizedNickname)) {
			return "current";
		}

		if (existsByNickname(normalizedNickname)) {
			throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
		}

		return "available";
	}

	public void validateUserId(String userId) {
		if (userId == null || userId.isEmpty()) {
			throw new IllegalArgumentException("아이디를 입력해주세요.");
		}

		if (!USER_ID_PATTERN.matcher(userId).matches()) {
			throw new IllegalArgumentException("아이디는 4~16자, 영문으로 시작해야 합니다.");
		}
	}

	public void validatePassword(String password) {
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("비밀번호를 입력해주세요.");
		}

		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new IllegalArgumentException("비밀번호는 8~20자, 영문과 숫자, 특수문자만 가능합니다.");
		}
	}

	public void validateNickname(String nickname) {
		if (nickname == null || nickname.isEmpty()) {
			throw new IllegalArgumentException("닉네임을 입력해주세요.");
		}

		if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
			throw new IllegalArgumentException("닉네임은 2~8자이며, 한글/영문/숫자만 가능합니다.");
		}
	}

	public void validateEmail(String email) {
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("이메일을 입력해주세요.");
		}

		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
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

	public boolean existsByNickname(String nickname) {
		String normalizedNickname = normalize(nickname);

		if (normalizedNickname == null || normalizedNickname.isEmpty()) {
			return false;
		}

		return userDAO.countByNickname(normalizedNickname) > 0;
	}

	public int getUserNum(String username) {
		String normalizedUsername = normalize(username);
		UserVO user = userDAO.selectUser(normalizedUsername);
		return user != null ? user.getUs_num() : 0;
	}

	public UserVO getUserByNum(int userNum) {
		return userDAO.selectUserByNum(userNum);
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	public void updatePassword(int userNum, String newPassword) {

		if (newPassword == null || newPassword.isBlank()) {
			throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
		}

		// 형식 검증
		validatePassword(newPassword);

		UserVO loginUser = userDAO.selectUserByNum(userNum);
		if (loginUser == null) {
			throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
		}

		// 기존 비밀번호와 동일한지 체크 (선택)
		if (passwordEncoder.matches(newPassword, loginUser.getUs_pw())) {
			throw new IllegalArgumentException("현재 비밀번호와 다른 비밀번호를 입력해주세요.");
		}

		String encodedPassword = passwordEncoder.encode(newPassword);
		userDAO.updatePassword(userNum, encodedPassword);
	}
}