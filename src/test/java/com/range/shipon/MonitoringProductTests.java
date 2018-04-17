package com.range.shipon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.service.MonitoringService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitoringProductTests {

	@Autowired
	MonitoringService service;

	private String mallId;
	private String queryString;
	private int page;
	private int count;
	private long productId;
	
	
	@Before
	public void mockData() {
		this.mallId = "doichi";
		this.queryString = "포트";
		this.page = 1;
		this.count = 50;
		this.productId = 591894644;
	}

//	@Test
//	public void getList() throws Exception {
//		
//		String queryId = "";
//		String queryName = "";
//		
//		try {
//			queryId = String.valueOf(Integer.parseInt(queryString));
//		} catch(Exception e) {
//			queryName = queryString;
//		}
//		
//		ArrayList<Product> list = service.getProductList(this.mallId, queryId, queryName, this.page, this.count);
//		System.out.println(list.size());
//	}
	
	@Test 
	public void getProductShippingDetail() throws Exception {
		
		Product product = service.getProductShippingDetail(this.productId);
		
		
		System.out.println(product);
	}
}
