package com.range.shipon.mybatis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductPrice {

	private Long productId;
	private int price;
	private int discountPrice;
	private String date;

}
