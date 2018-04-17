package com.range.shipon.mybatis.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.range.shipon.mybatis.model.SeluvCategory;
import com.range.shipon.mybatis.model.SeluvProductBasic;

@Mapper
public interface SeluvMapper {

	@SelectProvider(type = SeluvQuery.class, method = "getCategories")
	public ArrayList<SeluvCategory> getCategories(@Param("mallId") String mallId) throws Exception;

	@SelectProvider(type = SeluvQuery.class, method = "getProductList")
	public ArrayList<SeluvProductBasic> getProductList(@Param("mallId") String mallId) throws Exception;

	@UpdateProvider(type = SeluvQuery.class, method = "updateCategory")
	public void updateCategory(@Param("code") String code, @Param("urls") String urls) throws Exception;

	@InsertProvider(type = SeluvQuery.class, method = "insertProductBasic")
	public void insertProductBasic(@Param("mallId") String mallId, 
								@Param("productCode") String productCode, 
								@Param("productNo") String productNo) throws Exception;
	
	@UpdateProvider(type = SeluvQuery.class, method = "updateProductBasic")
	public void updateProductBasic(@Param("productCode") String productCode) throws Exception;

	@InsertProvider(type = SeluvQuery.class, method = "insertProductAdditional")
	public void insertProductAdditional(@Param("productCode") String productCode,
								@Param("lastPrice") double lastPrice, 
								@Param("hasStock") int hasStock) throws Exception;

}
