package com.range.shipon.mybatis.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.range.shipon.mybatis.model.Comparing;
import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;

@Mapper
public interface ProductMapper {

	@SelectProvider(type = ProductQuery.class, method = "getProduct")
	public int getProduct(@Param("productId") long productId) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getProductsTotalCount")
	public int getProductsTotalcount(
			@Param("mallId") String mallId, 
			@Param("queryId") String queryId,
			@Param("queryName") String queryName) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getProductList")
	public ArrayList<Product> getProductList(
			@Param("mallId") String mallId, 
			@Param("queryId") String queryId,
			@Param("queryName") String queryName, 
			@Param("order") String order,
			@Param("page") int page, 
			@Param("count") int count) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getDatediff")
	public int getDatediff(@Param("productId") long productId) throws Exception;

	@InsertProvider(type = ProductQuery.class, method = "addProduct")
	public void addProduct(
			@Param("mallId") String mallId,
			@Param("productId") long productId,
			@Param("category") String category,
			@Param("name") String name, 
			@Param("thumbnail") String thumbnail,
			@Param("url") String url, 
			@Param("orgPrice") int orgPrice, 
			@Param("dcPrice") int dcPrice,
			@Param("shippingFees") int shippingFees,
			@Param("shippingAverage") int shippingAverage,
			@Param("shippingWithin3Days") int shippingWithin3Days,
			@Param("shippingWithin3DaysRates") int shippingWithin3DaysRates,
			@Param("shippingWithin4Days") int shippingWithin4Days,
			@Param("shippingWithin4DaysRates") int shippingWithin4DaysRates,
			@Param("shippingWithinWeek") int shippingWithinWeek,
			@Param("shippingWithinWeekRates") int shippingWithinWeekRates,
			@Param("shippingWeekOver") int shippingWeekOver,
			@Param("shippingWeekOverRates") int shippingWeekOverRates,
			@Param("evaluationCount") int evaluationCount,
			@Param("isNew") String isNew) throws Exception;

	@UpdateProvider(type = ProductQuery.class, method = "updateProduct")
	public void updateProduct(
			@Param("orgPrice") int orgPrice,
			@Param("dcPrice") int dcPrice,
			@Param("shippingFees") int shippingFees,
			@Param("shippingAverage") int shippingAverage,
			@Param("shippingWithin3Days") int shippingWithin3Days,
			@Param("shippingWithin3DaysRates") int shippingWithin3DaysRates,
			@Param("shippingWithin4Days") int shippingWithin4Days,
			@Param("shippingWithin4DaysRates") int shippingWithin4DaysRates,
			@Param("shippingWithinWeek") int shippingWithinWeek,
			@Param("shippingWithinWeekRates") int shippingWithinWeekRates,
			@Param("shippingWeekOver") int shippingWeekOver,
			@Param("shippingWeekOverRates") int shippingWeekOverRates,
			@Param("evaluationCount") int evaluationCount,
			@Param("isNew") String isNew, 
			@Param("productId") long productId) throws Exception;

	@InsertProvider(type = ProductQuery.class, method = "addPrice")
	public void addPrice(
			@Param("productId") long productId, 
			@Param("price") int price,
			@Param("discountPrice") int discountPrice ) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getProductPriceList")
	public ArrayList<ProductPrice> getProductPriceList(@Param("productId") long productId) throws Exception;
	
	@SelectProvider(type = ProductQuery.class, method = "getProductShippingDetail")
	public Product getProductShippingDetail(@Param("productId") long productId) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getComparingList")
	public ArrayList<Comparing> getComparingList() throws Exception;
	
	@SelectProvider(type = ProductQuery.class, method = "getComparingItem")
	public ArrayList<Product> getComparingItem(@Param("seq") long seq) throws Exception; 
	
	@UpdateProvider(type = ProductQuery.class, method = "updateComparingItemTitle")
	public void updateComparingItemTitle(
			@Param("seq") long seq, 
			@Param("title") String title) throws Exception;

	@DeleteProvider(type = ProductQuery.class, method = "removeComparing")
	public void removeComparing(@Param("seq") long seq) throws Exception;

	@DeleteProvider(type = ProductQuery.class, method = "removeComparingItem")
	public void removeComparingItem(@Param("seq") long seq) throws Exception;

	@InsertProvider(type = ProductQuery.class, method = "addComparing")
	public void addComparing(@Param("title") String title) throws Exception;

	@InsertProvider(type = ProductQuery.class, method = "addComparingItem")
	public void addComparingItem(@Param("productId") long productId) throws Exception;

	@SelectProvider(type = ProductQuery.class, method = "getLastComparing")
	public Comparing getLastComparing() throws Exception;

}
