package com.range.shipon.mybatis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {

	private String mallId;
	private Long productId;
	private String category;
	private String name;
	private String thumbnail;
	private String url;

	// price
	private int orgPrice;
	private int dcPrice;
	private int shippingFees;
	private int totalPrice;

	// shipping
	private int shippingAverage;
	private int shippingWithin3Days;
	private int shippingWithin3DaysRates;
	private int shippingWithin4Days;
	private int shippingWithin4DaysRates;
	private int shippingWithinWeek;
	private int shippingWithinWeekRates;
	private int shippingWeekOver;
	private int shippingWeekOverRates;

	private int evaluationCount;
	private String isNew;

}
