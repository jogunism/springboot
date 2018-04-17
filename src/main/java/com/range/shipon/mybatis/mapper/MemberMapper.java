package com.range.shipon.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.range.shipon.mybatis.model.Member;

@Mapper
public interface MemberMapper {

	@Select("SELECT NOW()")
	public String getCurrentDateTime();

	@SelectProvider(type = MemberQuery.class, method = "getMember")
	public Member getMember(@Param("id") String id, @Param("password") String password) throws Exception;

	@SelectProvider(type = MemberQuery.class, method = "getMemberById")
	public Member getMemberByEmail(@Param("id") String id) throws Exception;

}
