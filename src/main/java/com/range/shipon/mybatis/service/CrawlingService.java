package com.range.shipon.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.range.shipon.mybatis.mapper.ProductMapper;
import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;

@Service
public class CrawlingService {

	private static final int MIN_DATE = 1;	// TODO : 7일로 변경할것.

	@Autowired
	private ProductMapper mapper;

	public void addProduct(Product product) throws Exception {
		if (mapper.getProduct(product.getProductId()) < 1) {
			product.setIsNew("new");
			mapper.addProduct(
					product.getMallId(), product.getProductId(), product.getCategory(), product.getName(), product.getThumbnail(), product.getUrl(), 
					product.getOrgPrice(), product.getDcPrice(), product.getShippingFees(), 
					product.getShippingAverage(), product.getShippingWithin3Days(), product.getShippingWithin3DaysRates(), product.getShippingWithin4Days(), product.getShippingWithin4DaysRates(), 
					product.getShippingWithinWeek(), product.getShippingWithinWeekRates(), product.getShippingWeekOver(), product.getShippingWeekOverRates(), 
					product.getEvaluationCount(), product.getIsNew());
		} else {
			int datediff = mapper.getDatediff(product.getProductId());
			if (datediff > 0) {	// 중복등록 방지.
				if (datediff < MIN_DATE) {	// 등록된지 {MIN_DATE}일 이내의 product에 new태그.
					product.setIsNew("new");
				} else {
					product.setIsNew("");
				}
				mapper.updateProduct(
						product.getOrgPrice(), product.getDcPrice(), product.getShippingFees(),
						product.getShippingAverage(), product.getShippingWithin3Days(), product.getShippingWithin3DaysRates(), product.getShippingWithin4Days(), product.getShippingWithin4DaysRates(), 
						product.getShippingWithinWeek(), product.getShippingWithinWeekRates(), product.getShippingWeekOver(), product.getShippingWeekOverRates(), 
						product.getEvaluationCount(), product.getIsNew(), product.getProductId());				
			}
		}
	}

	public void addPrice(ProductPrice price) throws Exception {
		// 오늘 등록된 productId는 무시.
		if (mapper.getDatediff(price.getProductId()) > 0) {
			mapper.addPrice(price.getProductId(), price.getPrice(), price.getDiscountPrice());
		}
	}

}
