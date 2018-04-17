package com.range.shipon.controller.rest;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.range.shipon.controller.DefaultRestController;
import com.range.shipon.controller.scheduler.SeluvZaraCrawlingController;
import com.range.shipon.mybatis.model.SeluvCategory;
import com.range.shipon.mybatis.service.SeluvHandlingService;

@RestController
@RequestMapping("/api/seluv")
public class SeluvController extends DefaultRestController {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SeluvController.class);

	@Autowired
	SeluvHandlingService service;
	
	@Autowired
	SeluvZaraCrawlingController rest;

	@RequestMapping(value = "/zara/category/list", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<SeluvCategory> getProductList(
			HttpServletRequest req) throws Exception {

		this.loginRequired(req);

		return service.getCategoryList("zara");
	}


	@RequestMapping(value = "/zara/category/update", method = RequestMethod.PUT)
	@ResponseBody
	public String upldateCategoryData(
			HttpServletRequest req, 
			@RequestParam(value = "code", required = true) String code,
			@RequestParam(value = "urls", required = true) String urls) throws Exception {

		this.loginRequired(req);

		this.service.updateCategory(code, urls);

		return urls;
	}


	@RequestMapping(value = "/zara/test/scheduler/link")
	@ResponseBody
	public Map<String, String> testLinkScheduler() throws Exception {
		return this.rest.getZaraProductList();
	}
}
