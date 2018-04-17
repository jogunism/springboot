package com.range.shipon.util;

public class StringUtil {

	public static String getUserId(String email) {
		String s = "";
		if (email.indexOf("@") > -1) {
			s = email.substring(0, email.indexOf("@"));
		}
		return replaceExtCharacter(s);
	}

	public static String replaceExtCharacter(String s){      
		StringBuffer sb = new StringBuffer();
		for(int i=0 ; i< s .length(); i++) {
			if(Character.isLetterOrDigit(s .charAt(i)))
			sb.append(s .charAt(i));
		}
		return sb.toString();
	}
	
	public static String getNumberOnly(String s) {
		return s.replaceAll("[^\\d]", "");
	}
}
