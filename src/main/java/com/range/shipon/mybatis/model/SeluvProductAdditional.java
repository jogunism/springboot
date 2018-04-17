package com.range.shipon.mybatis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SeluvProductAdditional {

	private String productCode;
	private double lastPrice;
	private int hasStock;
	private int status;
	private String updateDate;

}
