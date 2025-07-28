package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Param;
import com.hama.picketf.model.vo.UserVO;

public interface UserDAO {

	UserVO selectUser(@Param("username") String username);
	void insertUser(UserVO userVO);

}
