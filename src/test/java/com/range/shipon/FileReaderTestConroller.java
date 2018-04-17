package com.range.shipon;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.opencsv.CSVReader;
import com.range.shipon.mybatis.model.Member;

public class FileReaderTestConroller {

	private String path;
	
	@Before
	public void mockData() {
		this.path = "/Users/jogun/Downloads/members.csv";
	}

	@Test
	public void readCSVFile() {
	    try {
	        InputStreamReader is = new InputStreamReader(new FileInputStream(this.path), "UTF-8");
	        CSVReader reader = new CSVReader(is);
	        List<String[]> list = reader.readAll();

	        for(String[] str : list){
	        		Member member = new Member();
	        		member.setStatus(0);
	        		System.out.println();
	            	for (int i=0; i<str.length; i++) {
	                System.out.print(i +":"+ str[i]);
	                if (i < str.length-1) {
	                		System.out.println(",");
	                }

	            		switch(i) {
//	            		case 0: member.setName(str[i]); break;
//	            		case 1: member.setId(str[i]); break;
//	            		case 2: member.setPhoneMobile(str[i]); break;
//	            		case 3: member.setSubEmail(str[i]); break;
//	            		case 4: member.setCompany(str[i]); break;
//	            		case 5: member.setLocation(str[i]); break;
//	            		case 6: member.setRank(str[i]); break;
//	            		case 7: member.setLanguage(str[i]); break;
//	            		case 8: member.setBirthday(str[i]); break;
//	            		case 9: member.setJoinDate(str[i]); break;
//	            		case 10: member.setTeam(str[i]); break;
	            		}
	            }
	            	System.out.println("");
	        }
	        reader.close();
	    } catch(Exception e) {
	    		e.printStackTrace();
	    }
	}
	
	
}
