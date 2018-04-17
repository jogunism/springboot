package com.range.shipon;

import org.apache.xerces.parsers.DOMParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.range.shipon.component.HttpDispatcher;
import com.range.shipon.mybatis.service.SeluvHandlingService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpDispatchTestController {

	@Autowired
	HttpDispatcher dispatcher;

	@Autowired
	SeluvHandlingService service;

	@Test
	public void request() {
		
		try {
			String url = "https://openhub.godo.co.kr/godomall5/common/Code_Search.php?"
					+ "partner_key=JUIyJTdCJUZDJUIza1olMDZl&"
					+ "key=JTA3SyVERWElRkQlQzAlOUYxJUUwJTgyJTAzJTI1JUJDJUQ4JTE4JUI2JThDNE0lRjklMEFjJUY2JTIyJUVDbCU4MWElRkMlMDQlQzYuJTE3JUQ3JUI0JThC&"
					+ "code_type=delivery"; 
	
			for (int i=0; i<100; i++) {
				dispatchGodoApi(url, i);
				if (i % 9 == 0) {
					System.out.println("sleep "+ i);
					Thread.sleep(2000);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void dispatchGodoApi(final String url, final int index) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String xml = dispatcher.request(url);
				System.out.println(index);
				System.out.println(xml);
			}
		}).start();

//		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
//					"<data>\n" + 
//						"<header>\n" + 
//							"<code>000</code>\n" + 
//							"<msg>\n" + 
//								"<![CDATA[ 성공 ]]>\n" + 
//							"</msg>\n" + 
//						"</header>\n" + 
//						"<return>\n" + 
//							"<goods_data idx=\"1\">\n" + 
//								"<code>000</code>\n" + 
//								"<idx>1</idx>\n" + 
//								"<msg>\n" + 
//									"<![CDATA[ 성공 ]]>\n" + 
//								"</msg>\n" + 
//								"<data>\n" + 
//									"<goodsno>1000000042</goodsno>\n" + 
//								"</data>\n" + 
//							"</goods_data>\n" + 
//						"</return>\n" + 
//					"</data>";

//		DOMParser parser = new DOMParser();
//	    parser.parse(new InputSource(new java.io.StringReader(xml)));
//	    Document doc = parser.getDocument();
//	    String goodsNo = doc.getChildNodes()
//								.item(0).getChildNodes()	// data >
//								.item(3).getChildNodes()	// return >
//								.item(1).getChildNodes()	// data >
//								.item(7).getChildNodes()	// goodsno
//								.item(1).getTextContent();
//
//	    System.out.println(goodsNo);
////
//	    service.insertProductBasic("zara", goodsNo, "p00021007", 25.90, 1);
	}

}
