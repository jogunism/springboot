package com.range.shipon.mybatis.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.range.shipon.mybatis.mapper.SeluvMapper;
import com.range.shipon.mybatis.model.SeluvCategory;
import com.range.shipon.mybatis.model.SeluvProductBasic;

@Service
public class SeluvHandlingService {

	@Autowired
	private SeluvMapper mapper;

	public ArrayList<SeluvCategory> getCategoryList(String mallId) throws Exception {
		return this.mapper.getCategories(mallId);
	}
	
	public ArrayList<SeluvProductBasic> getProductList(String mallId) throws Exception {
		return this.mapper.getProductList(mallId);
	}

	public void updateCategory(String code, String urls) throws Exception {
		this.mapper.updateCategory(code, urls);
	}
	
	public void insertProduct(String mallId, String productNo, String productCode, double lastPrice, int hasStock) throws Exception {
		this.mapper.insertProductBasic(mallId, productCode, productNo);
		this.mapper.insertProductAdditional(productCode, lastPrice, hasStock);
	}

	public void updateProduct(String productCode, double lastPrice, int hasStock) throws Exception {
		this.mapper.updateProductBasic(productCode);
		this.mapper.insertProductAdditional(productCode, lastPrice, hasStock);
	}
	
}
