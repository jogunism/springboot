package com.range.shipon.controller.scheduler;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.range.shipon.component.CategoryComponent;
import com.range.shipon.mybatis.model.Product;
import com.range.shipon.mybatis.model.ProductPrice;
import com.range.shipon.mybatis.service.CrawlingService;
import com.range.shipon.util.StringUtil;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;


@RestController
@RequestMapping("/batch/crawling/naver")
public class NaverSmartStoreCrawlingController {

	private static final Logger logger = LoggerFactory.getLogger(NaverSmartStoreCrawlingController.class);
	private static final int ITEM_COUNT_PER_PAGE = 80;

	@Value("${slack.webhook.url}")
	private String webhook;

	@Autowired
	private CategoryComponent category;

	@Autowired
	public CrawlingService service;

	@RequestMapping(value = "/dokhan")
	public String dokhan() {
		process("dokhan", category.dokhan());
		return "done";
	}

	@RequestMapping(value = "/doichi")
	public String doichi() {
		process("doichmall", category.doichiMall());
		return "done";
	}

	@RequestMapping(value = "/hieuro")
	public String hieuro() {
		process("hieuro", category.hieuro());
		return "done";
	}

	@RequestMapping(value = "/euromoms")
	public String euromoms() {
		process("euromoms", category.euromoms());
		return "done";
	}

	@RequestMapping(value = "/euroexpress")
	public String euroexpress() throws Exception {
		process("euroexpress", category.euroexpress());
		return "done";
	}

	private void process(String mallId, Map<String, String> category) {

		int totalcount = 0;
		for (String categoryName : category.keySet()) {
			int page = 1;
			String url = "";
			while(true) {
				try {
					url = "http://smartstore.naver.com/"+ mallId +"/category/"+ category.get(categoryName) +"?page="+ page +"&st=POPULAR&dt=IMAGE&size="+ ITEM_COUNT_PER_PAGE +"&free=false&cp=1";
					Document doc = Jsoup.connect(url).get();
					Elements contents = doc.select(".sec_dis_img ul li");
					if (contents == null || contents.size() < 1) {
						contents = doc.select(".module_list_product_default ul li");
					}
	//				System.out.println(contents);
					totalcount += contents.size();
	
					logger.info("-----------------------------------------------------------------------------------------");
					logger.info(" CATEGORY : "+ categoryName +" URL : "+ url );
					logger.info(" CONTENT SIZE : "+ contents.size() );
					logger.info("-----------------------------------------------------------------------------------------");
	
					for (Element el : contents) {
						Element elInfo = el.select(".info dt a").first();
						if (elInfo == null) {
							elInfo = el.select("a").first();
						}
	//					System.out.println(elInfo);
	
						Elements elPrice = el.select(".price .thm");
						if (elPrice == null || elPrice.size() < 1) {
							elPrice = el.select(".area_price .number");
						}
	//					System.out.println(elPrice);
	
						String pageUrl = elInfo.attr("abs:href");
						long productId = Long.parseLong(pageUrl.substring(pageUrl.lastIndexOf("/") + 1));
						String thumbnail = el.select(".img_center img").attr("src");
						if (thumbnail == null || "".equals(thumbnail)) {
							thumbnail = el.select(".thumbnail img").attr("data-src");
						}
	
						int price = Integer.parseInt(StringUtil.getNumberOnly(elPrice.get(0).ownText()));
						int discountPrice = 0;
						if (elPrice.size() > 1) {
							discountPrice = Integer.parseInt(StringUtil.getNumberOnly(elPrice.get(1).ownText()));
						}
	
						// detail
						Document detail = Jsoup.connect(pageUrl).get();
						String productName = detail.select("._copyable .prd_name strong").get(0).ownText();
						String shippingInfo = detail.select(".delivery ._deliveryBaseFeeArea span").get(0).ownText();
						int shippingFees = 0;
						if (!shippingInfo.equals("무료")) {
							shippingFees = Integer.parseInt(StringUtil.getNumberOnly(shippingInfo));
						}
						int evaluationCount = Integer.parseInt(StringUtil.getNumberOnly(detail.select(".detail_view .tab_floatable li").get(1).select("a span").get(0).ownText()));		// 구매평수
	
						Elements element = detail.select("._delivery_leadtime_area ");
						int shippingAverage = 0;					// 배송기간 평균 : n일 이상
						int shippingWithin3Days = 0;				// 배송기간 3 일이내
						int shippingWithin3DaysRates = 0;		// 배송기간 3 일이내 Percentage
						int shippingWithin4Days = 0;				// 배송기간 4 일이내
						int shippingWithin4DaysRates = 0;		// 배송기간 4 일이내 Percentage
						int shippingWithinWeek = 0;				// 배송기간 5~6 일
						int shippingWithinWeekRates = 0;			// 배송기간 5~6 일 Percentage
						int shippingWeekOver = 0;				// 배송기간 6 일이상
						int shippingWeekOverRates = 0;			// 배송기간 6 일이상 Percentage
						if (element.size() > 0) {
							shippingAverage = Integer.parseInt(StringUtil.getNumberOnly(element.select(".wrap_time h3").get(0).ownText()));
							shippingWithin3Days = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td i").get(0).ownText()));
							shippingWithin3DaysRates = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td em").get(0).ownText()));
							shippingWithin4Days = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td i").get(1).ownText()));
							shippingWithin4DaysRates = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td em").get(1).ownText()));
							shippingWithinWeek = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td i").get(2).ownText()));
							shippingWithinWeekRates = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td em").get(2).ownText()));		
							shippingWeekOver = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td i").get(3).ownText()));
							shippingWeekOverRates = Integer.parseInt(StringUtil.getNumberOnly(element.select("table td em").get(3).ownText()));
						}
	
						Product product = null;
						ProductPrice productprice = null;
						try {
							
							String id = mallId;
							if ("doichmall".equals(mallId)) {
								id = "doichi";
							}
							product = new Product();
							product.setMallId(id);
							product.setProductId(productId);
							product.setCategory(categoryName);
							product.setName(productName);
							product.setUrl(pageUrl);
							product.setThumbnail(thumbnail);
							product.setUrl(pageUrl);
							product.setOrgPrice(price);
							product.setDcPrice(discountPrice);
							product.setShippingFees(shippingFees);
							product.setShippingAverage(shippingAverage);
							product.setShippingWithin3Days(shippingWithin3Days);
							product.setShippingWithin3DaysRates(shippingWithin3DaysRates); 
							product.setShippingWithin4Days(shippingWithin4Days);
							product.setShippingWithin4DaysRates(shippingWithin4DaysRates);
							product.setShippingWithinWeek(shippingWithinWeek);
							product.setShippingWithinWeekRates(shippingWithinWeekRates);
							product.setShippingWeekOver(shippingWeekOver);
							product.setShippingWeekOverRates(shippingWeekOverRates);
							product.setEvaluationCount(evaluationCount);
							service.addProduct(product);
	
							productprice = new ProductPrice();
							productprice.setProductId(productId);
							productprice.setPrice(price);
							productprice.setDiscountPrice(discountPrice);
							service.addPrice(productprice);
	
						} catch(Exception e) {
							logger.warn("ProductId : "+ product.getProductId());
							logger.warn(e.getMessage());
							e.printStackTrace();
						}
					}
	
					if (contents.size() < ITEM_COUNT_PER_PAGE) {
						break;
					}
	
					page++;

				} catch (IOException ioe) {
					logger.warn(url);
					ioe.getStackTrace();

					SlackMessage msg = new SlackMessage();
					msg.setText("NaverSmartStoreCrawlingController / IOException : "+ ioe.getMessage());
					msg.setChannel("error_report");
					SlackApi slack = new SlackApi(this.webhook);
					slack.call(msg);

				} catch (Exception e) {
					logger.warn(url);
					e.getStackTrace();

					SlackMessage msg = new SlackMessage();
					msg.setText("NaverSmartStoreCrawlingController / Exception : "+ e.getMessage());
					msg.setChannel("error_report");
					SlackApi slack = new SlackApi(this.webhook);
					slack.call(msg);
				}
			}
		}

		logger.info("--------------------------------------------------------------------------------------------------------------------");
		logger.info(" Crawling process has done.");
		logger.info("- Mall ID : " + mallId);
		logger.info("- Total deal count : " + totalcount);
		logger.info("--------------------------------------------------------------------------------------------------------------------");

		SlackApi slack = new SlackApi(this.webhook);
		slack.call(new SlackMessage("https://console.shipon.de\n Crawling *"+ mallId +"* complete"));		
	}

}
