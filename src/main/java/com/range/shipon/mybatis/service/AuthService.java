package com.range.shipon.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.range.shipon.mybatis.mapper.MemberMapper;
import com.range.shipon.mybatis.model.Member;

@Service
public class AuthService {

	@Autowired
	private MemberMapper mapper;
	
	public String getCurrentDataTime() {
		return mapper.getCurrentDateTime();
	}

	public Member getMember(String id, String password) throws Exception {
		return mapper.getMember(id, password);
	}

	public Member getMemberByEmail(String email) throws Exception {
		return mapper.getMemberByEmail(email);
	}

//	public void addMember(Member member) throws Exception {
//		mapper.addMember(member);
//	}

}
