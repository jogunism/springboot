package com.range.shipon.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;
import com.range.shipon.mybatis.service.MonitoringService;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

	private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);

	@Autowired
	MonitoringService service;

	@RequestMapping(value = "/productList", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getProductList(
			HttpServletResponse response,
			@RequestParam(value = "mallId", required = true) String mallId,
			@RequestParam(value = "queryString", required = false) String queryString,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "count", required = true) int count
			) throws Exception {

		// TODO - login check
		
		String queryId = "";
		String queryName = "";
		try {
			queryId = String.valueOf(Integer.parseInt(queryString));
		} catch(Exception e) {
			queryName = queryString;
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put( "totalCount", service.getProductsTotalCount(mallId, queryId, queryName) );
		result.put( "list", service.getProductList(mallId, queryId, queryName, (page-1)*count, count) );
		return result;
	}


	@RequestMapping(value = "/productPriceList", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<ProductPrice> getProductPriceList(
			@RequestParam(value = "productId") long productId) throws Exception{
		
		// TODO - login check

		return service.getProductPriceList(productId);
	}
	
	
	@RequestMapping(value = "/productShippingDetail", method = RequestMethod.GET)
	@ResponseBody
	public Product getProductShippingDetail(
			HttpServletResponse response,
			@RequestParam(value = "productId") long productId
			) throws Exception {

		// TODO = login check

		return service.getProductShippingDetail(productId);
	}
	
}
