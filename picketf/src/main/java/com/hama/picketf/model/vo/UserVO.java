package com.hama.picketf.model.vo;

import lombok.Data;

@Data
public class UserVO {
	int us_num;
	String us_id;
	String us_pw;
	String us_nickname;
	String us_email;
	String us_authority;
}
