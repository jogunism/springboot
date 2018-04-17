package com.range.shipon.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRestController {

	private static final Logger logger = LoggerFactory.getLogger(DefaultRestController.class);
	
	private String token;

	public boolean hasLoginToken(HttpServletRequest req) {
		this.token = req.getHeader("Authorization");		
		return this.token != null;
	}

	public void loginRequired(HttpServletRequest req) throws Exception {
		if (!this.hasLoginToken(req)) {
			logger.warn("Unvalid request from no where.");
			throw new Exception("There's no session. Need to login.");
		}
	}

}
