package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Param;
import com.hama.picketf.model.vo.UserVO;

public interface UserDAO {

	UserVO selectUser(@Param("username") String username);

	void insertUser(UserVO userVO);

	// 아이디 중복 체크를 위한 메서드 추가
	int countByUserId(String userId);

}