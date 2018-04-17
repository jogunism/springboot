package com.range.shipon.mybatis.mapper;

import org.apache.ibatis.jdbc.SQL;

public class SeluvQuery {

	public String getCategories() {
		return new SQL() {{
			SELECT("*");
			FROM("slvCategories");
			WHERE("mallId = #{mallId}");
			ORDER_BY("categoryCode");
		}}.toString();
	}

	public String updateCategory() {
		return new SQL() {{
			UPDATE("slvCategories");
			SET("urls = #{urls}");
			WHERE("categoryCode = #{code}");
		}}.toString();
	}
	
	public String insertProductBasic() {
		return new SQL() {{
			INSERT_INTO("slvProductsBasic");
			VALUES("mallId", "#{mallId}");
			VALUES("productCode", "#{productCode}");
			VALUES("productNo", "#{productNo}");
		}}.toString();
	}

	public String updateProductBasic() {
		return new SQL() {{
			UPDATE("slvProductsBasic");
			SET("updateDate = NOW()");
			WHERE("productCode = #{productCode}");
		}}.toString();
	}

	public String insertProductAdditional() {
		return new SQL() {{
			INSERT_INTO("slvProductsAdditional");
			VALUES("productCode", "#{productCode}");
			VALUES("lastPrice", "#{lastPrice}");
			VALUES("hasStock", "#{hasStock}");
		}}.toString();
	}

	public String getProductList() {
		return new SQL() {{
			SELECT("*");
			FROM("slvProductsBasic");
			WHERE("mallId = #{mallId}");
		}}.toString();
	}

}
