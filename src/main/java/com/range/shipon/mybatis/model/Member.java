package com.range.shipon.mybatis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {
	private Long seq;
	private String id;
	private String name;
	private String password;
	private String mobile;
	private String email;
	private String grade;
	private String birthDate;
	private String joinDate;
	private String team;
	private String company; 	// 0 : range international (def:0)
	private int status; 	// 0:사용중, -1:중지(퇴사)
}
