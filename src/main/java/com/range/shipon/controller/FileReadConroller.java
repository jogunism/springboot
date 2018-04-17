package com.range.shipon.controller;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVReader;


@RestController
public class FileReadConroller {

	
	@RequestMapping(value = "/file")
	public void readCSVFile() {
	    try {
	        InputStreamReader is = new InputStreamReader(new FileInputStream("/Users/jogun/Downloads/members.csv"), "UTF-8");
	        CSVReader reader = new CSVReader(is);
	        List<String[]> list = reader.readAll();

	        for(String[] str : list){
	            System.out.println();
	            	for (int i=0; i<str.length; i++) {

	                System.out.print(i +" / "+ str[i] + " ");
	            }
	        }

	    } catch(Exception e) {
	    		e.printStackTrace();
	    }
	}
}
