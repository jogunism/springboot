package com.range.shipon.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.range.shipon.component.CacheWrapper;
import com.range.shipon.mybatis.model.Member;
import com.range.shipon.mybatis.service.AuthService;
import com.range.shipon.util.StringUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private static final String TOKEN_POST_PIX = "_token";
	private static final String KEEP_POST_PIX = "_keep";
	private static final int EXPIRATION = 60 * 60 * 24;	 // one day.

	@Autowired
	private AuthService service;
	
	@Autowired
	private CacheWrapper cache;


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Boolean> login(
			HttpServletResponse response, 
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "rememberEmail", required = false) boolean rememberEmail
			) throws Exception {

		Member member = service.getMember(email, password);
		if (member == null) {
			throw new Exception("Check your Email or Password again.");
		}

		String token = UUID.randomUUID().toString();
		String id = StringUtil.getUserId(email);

		// add session for server
		cache.open();
		cache.put(token, email);
		cache.put(id + TOKEN_POST_PIX, token);
//		cache.put(id, member);
		cache.close();
		logger.info("make session => key : "+ (id + TOKEN_POST_PIX) +" / value : "+ token);

		// add cookie for client
		Cookie cookieEmail = new Cookie("user_email", email);		// user_email={email}
		cookieEmail.setMaxAge(EXPIRATION);
		cookieEmail.setPath("/");
		cookieEmail.setSecure(true);
		response.addCookie(cookieEmail);

		Cookie cookieId = new Cookie("user_id", id);				// user_id={id}
		cookieId.setMaxAge(EXPIRATION);
		cookieId.setPath("/");
		cookieId.setSecure(true);
		response.addCookie(cookieId);

		Cookie cookieToken = new Cookie(id + TOKEN_POST_PIX, token);		// {id}_token={token}
		cookieToken.setMaxAge(EXPIRATION);
		cookieToken.setPath("/");
		cookieToken.setSecure(true);
		response.addCookie(cookieToken);
		logger.info("make cookie => key : "+ id + TOKEN_POST_PIX +" / value : "+ token);

		Cookie cookieKeepEmail = new Cookie(id + KEEP_POST_PIX, "1");		// {id}_keep=1
		cookieKeepEmail.setPath("/");
		cookieKeepEmail.setSecure(true);
		if (rememberEmail) {
			cookieKeepEmail.setMaxAge(EXPIRATION);
		} else {
			cookieKeepEmail.setMaxAge(0);
		}
		response.addCookie(cookieKeepEmail);


		Map<String, Boolean> result = new HashMap<String, Boolean>();
		result.put("result", true);
		return result;
	}
	
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public void logout(
			HttpServletResponse response, 
			@RequestParam(value = "email", required = true) String email
			) throws Exception {

		String id = StringUtil.getUserId(email);

		cache.open();
		cache.evict(id + TOKEN_POST_PIX);
		cache.close();

		Cookie cookieToken = new Cookie(id + TOKEN_POST_PIX, null);	// {id}_token={token}
		cookieToken.setMaxAge(0);
		cookieToken.setPath("/");
		cookieToken.setSecure(true);
		response.addCookie(cookieToken);

		Cookie cookieId = new Cookie("user_id", id);			// user_id={id}
		cookieId.setMaxAge(0);
		cookieId.setPath("/");
		cookieId.setSecure(true);
		response.addCookie(cookieId);

		logger.info("removed session / cookie => "+ cookieId.getName());
	}

	@RequestMapping(value = "/getSession", method = RequestMethod.GET)
	@ResponseBody
	public Member checkSession(
//			HttpServletResponse response, 
			@RequestParam(value = "email", required = true) String email, 
			@RequestParam(value = "token", required = true) String token
			) throws Exception {

		String id = StringUtil.getUserId(email);

		cache.open();
		String sessionToken = (String) cache.get(id + TOKEN_POST_PIX);
		cache.close();
		logger.info("sessionToken ("+ id + TOKEN_POST_PIX +") => "+ sessionToken);

		// token이 없음 : 로그인 되어있지 않음.
		if (sessionToken == null) {
			logger.warn("Unhealthy connection => "+ id);
			throw new Exception("There's no token. You need to login.");
		} else {
			logger.info("token : "+ token +" / session token : "+ sessionToken);
			logger.info(token.equals(sessionToken)?"YES":"NO");

			// 서버의 token과 client의 token이 다름 : 비정상적인 로그아웃 or 다른 브라우져 로그인. => 다시 로그인필요.
			if (!token.equals(sessionToken)) { 
				String message = "Abnormally logout or Tried login at another browser. need to re-login.";
				logger.info(message +" => "+ email);
				throw new Exception(message);
			} else {
//				 token 있고, token도 동일 : do nothing.
			}

			Member member = service.getMemberByEmail(email); 
			if (member == null) {
				throw new Exception("There's no member data.");
			}

			return member;
		}
	}	
}
