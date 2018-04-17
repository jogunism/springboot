package com.range.shipon.mybatis.mapper;

import org.apache.ibatis.jdbc.SQL;

import com.range.shipon.mybatis.model.Member;

public class MemberQuery {

	public String getMember() {
		return new SQL() {{
			SELECT("*");
			FROM("members");
			WHERE("id = #{id} AND password = #{password}");
		}}.toString();
	}

	public String getMemberById() {
		return new SQL() {{
			SELECT("*");
			FROM("members");
			WHERE("id = #{id}");			
		}}.toString();
	}

	public String addMember(final Member member) {
		return new SQL() {{
			INSERT_INTO("members");
			VALUES("id, name, password, joinDate", "#{id}, #{name}, #{password}, #{joinDate}");
		}}.toString();
	}

	public String getMemberList() {
		SQL query = new SQL() {{
			SELECT("*");
			FROM("members");
		}};
		// TODO
//		if (!id.equals("") || id != null) {
//			query.WHERE("id", id.toString());
//		}
		return query.toString();
	}
}
