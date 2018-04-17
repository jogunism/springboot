package com.range.shipon.mybatis.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.range.shipon.mybatis.mapper.ProductMapper;
import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;

@Service
public class MonitoringService {

	@Autowired
	private ProductMapper mapper;

	public int getProductsTotalCount(String mallId, String queryId, String queryName) throws Exception {
		return mapper.getProductsTotalcount(mallId, queryId, queryName);
	}

	public ArrayList<Product> getProductList(String mallId, String queryId, String queryName, int page, int count) 
			throws Exception {
		return mapper.getProductList(mallId, queryId, queryName, null, page, count);
	}
	
	public ArrayList<ProductPrice> getProductPriceList(long productId) throws Exception {
		return mapper.getProductPriceList(productId);
	}

	public Product getProductShippingDetail(long productId) throws Exception {
		return mapper.getProductShippingDetail(productId);
	}
}
