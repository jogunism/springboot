package com.range.shipon.controller.rest;

import java.util.ArrayList;

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
import com.range.shipon.mybatis.model.Comparing;
import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.service.ComparingService;

@RestController
@RequestMapping("/api/comparing")
public class ComparingController extends DefaultRestController {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ComparingController.class);

	@Autowired
	private ComparingService service;

	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Comparing> getComparingList(
			HttpServletRequest req) throws Exception {

		this.loginRequired(req);

		return service.getComparingList();
	}


	@RequestMapping(value = "/item", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Product> getComparingItem(
			HttpServletRequest req,
			@RequestParam(value = "seq", required = true) long seq
			) throws Exception {

		this.loginRequired(req);

		return service.getComparingItem(seq);
	}

	@RequestMapping(value="/itemTitle", method = RequestMethod.PUT)
	@ResponseBody
	public void updateComparingItemTitle(
			HttpServletRequest req,
			@RequestParam(value = "seq", required = true) long seq,
			@RequestParam(value = "title", required = true) String title
			) throws Exception {

		this.loginRequired(req);
		
		service.updateComparingItemTitle(seq, title);
	}
	
	@RequestMapping(value="/item", method = RequestMethod.DELETE)
	@ResponseBody
	public void removeComparingItem(
			HttpServletRequest req,
			@RequestParam(value = "seq", required = true) long seq
			) throws Exception {

		this.loginRequired(req);
		
		service.removeComparingItem(seq);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Product> getProductList(
			HttpServletRequest req,
			@RequestParam(value = "queryString", required = false) String queryString
			) throws Exception {

		this.loginRequired(req);

		String queryId = "";
		String queryName = "";
		try {
			queryId = String.valueOf(Integer.parseInt(queryString));
		} catch(Exception e) {
			queryName = queryString;
		}

		return service.getSearchResult(queryId, queryName);
	}

	@RequestMapping(value = "/item", method = RequestMethod.POST)
	@ResponseBody
	public Comparing addComparingItem(
			HttpServletRequest req,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "products[]", required = true) long[] products
			) throws Exception {

		this.loginRequired(req);

		service.addComparing(title, products);

		return service.getLastComparing();
	}
	
}
