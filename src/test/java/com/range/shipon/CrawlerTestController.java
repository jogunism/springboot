package com.range.shipon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;
import com.range.shipon.mybatis.service.CrawlingService;
import com.range.shipon.util.StringUtil;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerTestController {

	@Autowired
	public CrawlingService service;
	
	@Test
	public void crawling() throws Exception {

//		Document doc = Jsoup.connect("http://smartstore.naver.com/doichmall").get();
		
		int page = 1;
		while (true) {

			String url = "http://smartstore.naver.com/doichmall/category/d5f0154ad4bb4837a47b4ef5569622e4?page="+ page +"&st=POPULAR&dt=IMAGE&size=80&free=false&cp=1";
			Document doc = Jsoup.connect(url).get();
			Elements contents = doc.select(".sec_dis_img ul li");

			if (contents.size() < 1) {
				break;
			}

			for (Element el : contents) {

				Element elInfo = el.select(".info dt a").first();
				Elements elPrice = el.select(".price .thm");

				String pageUrl = elInfo.attr("abs:href");
				long productId = Long.parseLong(pageUrl.substring(pageUrl.lastIndexOf("/") + 1));
				String productName = elInfo.ownText();
				String thumbnail = el.select(".img_center img").attr("src");
				int price = Integer.parseInt(StringUtil.replaceExtCharacter(elPrice.get(0).ownText()));
				int discountPrice = 0;
				if (elPrice.size() > 1) {
					discountPrice = Integer.parseInt(StringUtil.replaceExtCharacter(elPrice.get(1).ownText()));
				}

				// detail
				Document detail = Jsoup.connect(pageUrl).get();
				String shippingFees = detail.select(".delivery ._deliveryBaseFeeArea span").get(0).ownText();	// 배송료

				Elements element = detail.select("._delivery_leadtime_area ");
				String shippingAverage = element.select(".wrap_time h3").get(0).ownText();			// 배송기간 평균 : n일 이상
				String shippingWithin3Days = element.select("table td i").get(0).ownText();			// 배송기간 3 일이내
				String shippingWithin3DaysRates = element.select("table td em").get(0).ownText();		// 배송기간 3 일이내 Percentage
				String shippingWithin4Days = element.select("table td i").get(1).ownText();			// 배송기간 4 일이내
				String shippingWithin4DaysRates = element.select("table td em").get(1).ownText();		// 배송기간 4 일이내 Percentage
				String shippingWithinWeek = element.select("table td i").get(2).ownText();			// 배송기간 5~6 일
				String shippingWithinWeekRates = element.select("table td em").get(2).ownText();		// 배송기간 5~6 일 Percentage
				String shippingWeekOver = element.select("table td i").get(3).ownText();				// 배송기간 6 일이상
				String shippingWeekOverRates = element.select("table td em").get(3).ownText();		// 배송기간 6 일이상 Percentage

				String evaluationCount = detail.select(".detail_view .tab_floatable li").get(1).select("a span").get(0).ownText();		// 구매평수

				// Insert or Update
				Product product = new Product();
				product.setProductId(productId);
				product.setCategory("");
				product.setName(productName);
				product.setUrl(pageUrl);
				product.setThumbnail(thumbnail);
				product.setUrl(pageUrl);

				product.setOrgPrice(price);
				product.setDcPrice(discountPrice);
				product.setShippingFees(Integer.parseInt(StringUtil.getNumberOnly(shippingFees)));

				product.setShippingAverage(Integer.parseInt(StringUtil.getNumberOnly(shippingAverage)));
				product.setShippingWithin3Days(Integer.parseInt(shippingWithin3Days));
				product.setShippingWithin3DaysRates(Integer.parseInt(StringUtil.getNumberOnly(shippingWithin3DaysRates))); 
				product.setShippingWithin4Days(Integer.parseInt(shippingWithin4Days));
				product.setShippingWithin4DaysRates(Integer.parseInt(StringUtil.getNumberOnly(shippingWithin4DaysRates)));
				product.setShippingWithinWeek(Integer.parseInt(shippingWithinWeek));
				product.setShippingWithinWeekRates(Integer.parseInt(StringUtil.getNumberOnly(shippingWithinWeekRates)));
				product.setShippingWeekOver(Integer.parseInt(shippingWeekOver));
				product.setShippingWeekOverRates(Integer.parseInt(StringUtil.getNumberOnly(shippingWeekOverRates)));

				product.setEvaluationCount(Integer.parseInt(evaluationCount));

				service.addProduct(product);

				ProductPrice productprice = new ProductPrice();
				productprice.setProductId(productId);
				productprice.setPrice(price);
				productprice.setDiscountPrice(discountPrice);
				service.addPrice(productprice);

//				System.out.println(pageUrl);			// url
//				System.out.println(productId); 		// productId
//				System.out.println(thumbnail);		// thumbnail
//				System.out.println(productName);		// product name
//				System.out.println(price);			// price
//				System.out.println(discountPrice);	// discount price

				System.out.println(StringUtil.getNumberOnly(shippingFees));
				System.out.println(StringUtil.getNumberOnly(shippingAverage));
				System.out.println(shippingWithin3Days);
				System.out.println(StringUtil.getNumberOnly(shippingWithin3DaysRates));
				System.out.println(shippingWithin4Days);
				System.out.println(StringUtil.getNumberOnly(shippingWithin4DaysRates));
				System.out.println(shippingWithinWeek);
				System.out.println(StringUtil.getNumberOnly(shippingWithinWeekRates));
				System.out.println(shippingWeekOver);
				System.out.println(StringUtil.getNumberOnly(shippingWeekOverRates));
				System.out.println(StringUtil.getNumberOnly(evaluationCount));
				
				break;
			}
			page++;
		}
		

			
	}
	
}
