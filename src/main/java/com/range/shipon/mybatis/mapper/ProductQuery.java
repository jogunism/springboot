package com.range.shipon.mybatis.mapper;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

public class ProductQuery {

	public String getProduct() {
		return new SQL() {{
			SELECT("count(*)");
			FROM("products");
			WHERE("productId = #{productId}");
		}}.toString();
	}

	public String getProductsTotalCount(Map<String, Object> params) {

		String mallId = (String)params.get("mallId");
		String queryId = (String)params.get("queryId");
		String queryName = (String)params.get("queryName");

		return new StringBuilder()
				.append("SELECT count(*) ")
				.append("FROM products ")
				.append(getProductListWherePhrase(mallId, queryId, queryName)).toString();
	}
	
	public String getProductList(Map<String, Object> params) {

		String mallId = (String)params.get("mallId");
		String queryId = (String)params.get("queryId");
		String queryName = (String)params.get("queryName");
		String order = (String)params.get("order");
		int count = (int)params.get("count");

		StringBuilder query = new StringBuilder()
				.append("SELECT * ")
				.append("FROM products ")
				.append(getProductListWherePhrase(mallId, queryId, queryName));
		if (order != null && !"".equals(order)) {
			query.append("ORDER BY "+ order +" ");
		} else {
			query.append("ORDER BY isNew DESC, category, productId ");			
		}

		if (count > 0) {
			query.append("LIMIT #{page}, #{count}");
		}

		return query.toString();
	}

	private String getProductListWherePhrase(String mallId, String queryId, String queryName) {
		StringBuilder query = new StringBuilder("WHERE 1=1 ");

		if (mallId != null && !"".equals(mallId)) {
			query.append("AND mallId = #{mallId} ");
		}

		if (queryId != null && !"".equals(queryId)) {
			query.append("AND productId = #{queryId} ");
		}

		if (queryName != null && !"".equals(queryName)) {
			query.append("AND name LIKE CONCAT('%', #{queryName}, '%') ");
		}

		return query.toString();
	}
	
	
	public String addProduct() {
		return new SQL() {{
			INSERT_INTO ("products");
			VALUES("mallId, productId, category, name, thumbnail, url, orgPrice, dcPrice", "#{mallId}, #{productId}, #{category}, #{name}, #{thumbnail}, #{url}, #{orgPrice}, #{dcPrice}");
			VALUES("shippingFees, shippingAverage, shippingWithin3Days, shippingWithin3DaysRates", "#{shippingFees}, #{shippingAverage}, #{shippingWithin3Days}, #{shippingWithin3DaysRates}");
			VALUES("shippingWithin4Days, shippingWithin4DaysRates, shippingWithinWeek, shippingWithinWeekRates", "#{shippingWithin4Days}, #{shippingWithin4DaysRates}, #{shippingWithinWeek}, #{shippingWithinWeekRates}");
			VALUES("shippingWeekOver, shippingWeekOverRates", "#{shippingWeekOver}, #{shippingWeekOverRates}");
			VALUES("evaluationCount, isNew", "#{evaluationCount}, #{isNew}");
		}}.toString();
	}

	public String updateProduct() {
		return new SQL() {{
			UPDATE("products");
				SET("orgPrice = #{orgPrice}");
				SET("dcPrice = #{dcPrice}");
				SET("shippingFees = #{shippingFees}");
				SET("shippingAverage = #{shippingAverage}");
				SET("shippingWithin3Days = #{shippingWithin3Days}");
				SET("shippingWithin3DaysRates = #{shippingWithin3DaysRates}");
				SET("shippingWithin4Days = #{shippingWithin4Days}");
				SET("shippingWithin4DaysRates = #{shippingWithin4DaysRates}");
				SET("shippingWithinWeek = #{shippingWithinWeek}");
				SET("shippingWithinWeekRates = #{shippingWithinWeekRates}");
				SET("shippingWeekOver = #{shippingWeekOver}");
				SET("shippingWeekOverRates = #{shippingWeekOverRates}");
				SET("evaluationCount = #{evaluationCount}");
				SET("isNew = #{isNew}");
			WHERE("productId = #{productId}");
		}}.toString();
	}

	public String getDatediff() {
		return new SQL() {{
			// 오늘과 productId의 처음 등록된 날짜비교. 
			SELECT("COALESCE(DATEDIFF(NOW(), (SELECT date FROM productsPrice WHERE productId = #{productId} ORDER BY date DESC LIMIT 1)), 999)");
		}}.toString();
	}

	public String addPrice() {
		return new SQL() {{
			INSERT_INTO("productsPrice");
			VALUES("productId, price, discountPrice", "#{productId}, #{price}, #{discountPrice}");
		}}.toString();
	}

	public String getProductPriceList() {
		return new SQL() {{
			SELECT("*");
			FROM("productsPrice");
			WHERE("productId = #{productId}");
		}}.toString();
	}

	public String getProductShippingDetail() {
		return new SQL() {{
			SELECT("shippingAverage, shippingWithin3Days, shippingWithin3DaysRates, shippingWithin4Days, shippingWithin4DaysRates");
			SELECT("shippingWithinWeek, shippingWithinWeekRates, shippingWeekOver, shippingWeekOverRates");
			FROM("products");
			WHERE("productId = #{productId}");
		}}.toString();
	}

	public String getComparingList() {
		return new SQL() {{
			SELECT("*");
			FROM("productsComparing");
			ORDER_BY("seq");
		}}.toString();
	}

	public String getComparingItem() {
		StringBuilder builder = new StringBuilder()
			.append("SELECT ")
				.append("P.mallId, P.productId, P.name, P.orgPrice, P.dcPrice, P.shippingFees, P.url, ")
				.append("IF(P.dcPrice < 1, P.orgPrice+P.shippingFees, P.dcPrice+P.shippingFees) AS totalPrice ")
			.append("FROM products P ")
				.append("JOIN ( ")
					.append("SELECT productId ")
					.append("FROM productsComparingItems A JOIN productsComparing B ON A.seq = B.seq ")
					.append("WHERE B.seq = #{seq}) AS T ")
				.append("ON P.productId = T.productId ")
			.append("ORDER BY totalPrice, dcPrice, orgPrice, mallId");
		return builder.toString();
	}

	public String updateComparingItemTitle() {
		return new SQL() {{
			UPDATE("productsComparing");
			SET("title = #{title}");
			WHERE("seq = #{seq}");
		}}.toString();
	}
	
	public String removeComparing() {
		return new SQL() {{
			DELETE_FROM("productsComparing");
			WHERE("seq = #{seq}");
		}}.toString();
	}
	
	public String removeComparingItem() {
		return new SQL() {{
			DELETE_FROM("productsComparingItems");
			WHERE("seq = #{seq}");
		}}.toString();
	}
	
	public String addComparing() {
		return new SQL() {{
			INSERT_INTO("productsComparing");
			VALUES("title", "#{title}");
		}}.toString();
	}

	public String addComparingItem() {
		return new SQL() {{
			INSERT_INTO("productsComparingItems");
			VALUES("seq, productId", "LAST_INSERT_ID(), #{productId}");
		}}.toString();
	}

	public String getLastComparing() {
		return new SQL() {{
			SELECT("*");
			FROM("productsComparing");
			WHERE("seq = LAST_INSERT_ID()");
		}}.toString();
	}
}
