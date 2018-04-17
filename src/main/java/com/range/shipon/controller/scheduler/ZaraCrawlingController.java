package com.range.shipon.controller.scheduler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import com.google.gson.Gson;
import com.range.shipon.component.AwsS3HandlingComponent;
import com.range.shipon.enums.ZaraCategory;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;

@RestController
@RequestMapping("/batch/crawling/zara")
public class ZaraCrawlingController {

	private static final Logger logger = LoggerFactory.getLogger(ZaraCrawlingController.class);

	static final String BASE_PATH = "/home/ubuntu/cron/"; 
	static final String PATH = BASE_PATH + "zara/";
	static final String FOLDER_TEMPORARY = "temp/";
	static final String FOLDER_PRODUCTS = "products/";

	static final String FILE_LINK = "zara_link.txt";
	static final String FILE_CSV_PRODUCTS = "_products.csv";
	static final String FILE_CSV_CODES = "_codes.csv";
	static final String FILE_PRODUCTS = "zara_products";
	static final int CURRENCY = 1400;	// TODO

	private ArrayList<String> removedUrls = null;
	private ArrayList<String> outOfStocks = null;
	private ArrayList<String> products = null;
	private ArrayList<String> codes = null;
	private int codeSerial = 1;


	@Value("${domain.file}")
	private String staticWebDomain;

	@Value("${slack.webhook.url}")
	private String slackWebHook;

	@Autowired
	private AwsS3HandlingComponent uploader;

	@RequestMapping(value = "/link")
	public void getZaraProductList()  {

		ArrayList<String> categories = new ArrayList<String>();

		try {
			long starttime = System.currentTimeMillis();

			Document doc = Jsoup.connect("https://www.zara.com/de/").get();
			Elements element = doc.select("#menu ul li");

			// Categories
			for (Element el : element) {
				Integer categoryId = Integer.parseInt(el.attr("data-categoryid"));
				if (categoryId == ZaraCategory.HERREN.getCode()) {
					String parentCategory = "";
					for (Element li : (Elements)el.select("ul li")) {
						// add link url
						Element link = li.select("a").get(0);
						String subCategory = link.ownText();
						String url = link.attr("abs:href");
						String v = link.attr("data-extraquery");
						String currentLink = new StringBuilder(url).append("?").append(v).toString();

						// TODO : HERREN > BASIC, SCHUHE 하위 카테고리.
						if ("BASICS".equals(subCategory) || "SCHUHE".equals(subCategory)) {
							parentCategory = subCategory;
						}
						
						if (!"".equals(parentCategory)) {
							subCategory = subCategory.replaceAll("-", "_").replaceAll(" ", "_");
							if ("BASICS".equals(parentCategory) && 
									(("Jacken".equals(subCategory) || "Pullover".equals(subCategory) || "T_Shirts".equals(subCategory)))) {
								categories.add(ZaraCategory.getCategory(categoryId) +"/"+ subCategory +" : "+ currentLink);
//								System.out.println(parentCategory +" - "+ ZaraCategory.getCategory(categoryId) +"/"+ subCategory +" : "+ currentLink );
							}

							if ("SCHUHE".equals(parentCategory) && "Alles_sehen".equals(subCategory)) {
								subCategory = "Sneaker";
								categories.add(ZaraCategory.getCategory(categoryId) +"/"+ subCategory +" : "+ currentLink);								
//								System.out.println(parentCategory +" - "+ ZaraCategory.getCategory(categoryId) +"/"+ subCategory +" : "+ currentLink );
							}							
						}
						if ("MÄNTEL".equals(subCategory) || "TASCHEN".equals(subCategory)) {
							parentCategory = "";
						}
					}
				}
			}

			// Products
			ArrayList<String> productList = new ArrayList<String>();
			for (String text : categories) {
				String category = text.substring(0, text.indexOf(":")-1);
				String link = text.substring(text.indexOf(":") + 2);

				Document linkDocument = Jsoup.connect(link).get();
				Elements productElement = linkDocument.select("._productList li");

				if (productElement.size() < 1) {
					continue;
				}

				for (Element el : productElement) {
					String url = el.select("a").attr("abs:href");
					if (url == null || "".equals(url)) {
						continue;
					}
					productList.add(category +" : "+ url);
				}
			}

			// Makes a File
			ProductListWriteWrapper wrapper = null;
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH + FILE_LINK), "utf-8"))) {
				wrapper = new ProductListWriteWrapper(writer);
		        	for (String product : productList) {
		        		String category = product.substring(0, product.indexOf(":")-1);
		        		String url = "";
		        		if (product.contains("&")) {	// http://www.zara.com/de/{product_name}.html?v1={code}
		        			url = product.substring(product.indexOf(":")+2, product.lastIndexOf("&"));
		        		} else {
		        			url = product.substring(product.indexOf(":")+2);		        			
		        		}
		        		String code = url.substring(url.lastIndexOf("-")+1, url.lastIndexOf(".html"));

		        		// code가 "p"로 시작하지않는-하위depth가 더 있는 page는 productList li를 한번 더 찾는다.
		        		if (!"p".equals(code.substring(0, 1))) {
		    				Document linkDocument = Jsoup.connect(url).get();
		    				Elements productElement = linkDocument.select("._productList li");
		    				if (productElement.size() < 1) {
		    					continue;
		    				}

		    				for (Element el : productElement) {
		    					url = el.select("a").attr("abs:href");	// http://www.zara.com/de/{product_name}.html?v1={code}		    					
		    					if (url != null && !"".equals(url) && url.lastIndexOf("&") > 0) {
		    		        			url = url.substring(0, url.lastIndexOf("&"));
		    		        		}
		    		        		wrapper.write(category, url);
	    					}
		        		} else {
		        			wrapper.write(category, url);
		        		}
				}
			}
			long endtime = System.currentTimeMillis();

			// Result
			logger.info("ZARA product urls");
			logger.info(" - total : "+ wrapper.getCount());
			logger.info(" - time : "+ new SimpleDateFormat("mm:ss:SSS").format(new Date(endtime - starttime)));

		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/crwaling")
	public void crwaling() throws Exception {

		logger.info("-----------------------------------------------------------------------------------");
		logger.info("ZARA CRAWLING START.");
		long starttime = System.currentTimeMillis();

		ArrayList<String> productList = new ArrayList<String>();

		Path path = Paths.get(PATH + FILE_LINK);
    		List<String> fileContent = new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));

    		for (int i=0; i<fileContent.size(); i++) {
    			String line = fileContent.get(i);
    			if ("-".equals(line)) {
    				continue;
    			}
    			productList.add(line);
    			fileContent.set(i, "-");
    		}
    		Files.write(path, fileContent, StandardCharsets.UTF_8);

    		if (productList.size() < 1) {
    			logger.warn("No more urls.");
    			logger.info("-----------------------------------------------------------------------------------");
    			return;
    		}

        this.removedUrls = new ArrayList<String>();
        this.outOfStocks = new ArrayList<String>();
        this.products = new ArrayList<String>();
        this.codes = new ArrayList<String>();

        // CSV write : _product
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH + FOLDER_TEMPORARY + FILE_CSV_PRODUCTS), "utf-8"))) {
        		// Header
        		writer.write("NAVER 카테고리ID\tZARA 카테고리\tZARA 서브카테고리\t상품정보제공고시 모델명\t코드\t상품명\t가격(EURO)\t가격 (환율"+ CURRENCY +"원 기준)\t즉시할인값\t대표 이미지 파일명\t상품 상세정보\t옵션값\n");

	        for (String product : productList) {
	        		String category = product.substring(0, product.indexOf("/"));
	        		String subCategory = product.substring(product.indexOf("/")+1, product.indexOf("-"));
	        		String url = product.substring(product.indexOf("-") + 1);

	        		// CSV row
	        		this.addWriteProductDetailRow(category, subCategory, url, writer);
	        }
        }

        // CSV write : _codes
        if (this.codes.size() > 0) {
	        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH + FOLDER_TEMPORARY + FILE_CSV_CODES), "utf-8"))) {
	        		// Header
		    		writer.write("코드\t옵션\t상품명\t매입가(EURO)\t링크\n");

		        for (String code : this.codes) {
		        		// CSV row
		        		writer.write(code +"\n");
		        }
		    }
        }

        // products folder to ZIP file
        String zipFilename = FILE_PRODUCTS + "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) +".zip";
        pack(PATH + FOLDER_TEMPORARY, PATH + FOLDER_PRODUCTS + zipFilename);

        long endtime = System.currentTimeMillis();

        // result log
        logger.info(" Results : "+ this.products.size());
        logger.info(" Time : "+ new SimpleDateFormat("mm:ss:SSS").format(new Date(endtime - starttime)));
        logger.info("-----------------------------------------------------------------------------------");

        // AWS S3 upload
        uploader.upload(PATH + FOLDER_PRODUCTS + zipFilename);

		// Slack Notification
		StringBuilder result = new StringBuilder()
				.append(" Zara product result : ").append(this.products.size()).append("\n")
				.append(" Time : ").append(new SimpleDateFormat("mm:ss:SSS").format(new Date(endtime - starttime)));

		SlackMessage msg = new SlackMessage();
		msg.setText(result.toString());

		SlackAttachment zip = new SlackAttachment();
		zip.setText(staticWebDomain + "/static/zara/"+ zipFilename);
		zip.setFallback("zip");

		ArrayList<SlackAttachment> attachlist = new ArrayList<SlackAttachment>();
		attachlist.add(zip);
		msg.setAttachments(attachlist);

		SlackApi slack = new SlackApi(this.slackWebHook);
		slack.call(msg);
	}


	@SuppressWarnings("unchecked")
	private void addWriteProductDetailRow(String category, String subCategory, String url, Writer writer) throws Exception {

		Document doc = Jsoup.connect(url).followRedirects(false).get();

		Elements elScript = doc.select("script");
		String json = "";
		for (Element script : elScript) {	
			if (script.data().contains("window.zara.dataLayer")) {				
				String txt = script.data();
				json = txt.substring(txt.indexOf("window.zara.dataLayer")+24, txt.lastIndexOf("}")+1);
				break;
			}
		}
//			System.out.println(json);

		// removed or moved view page
		Gson gson = new Gson();
		Map<String, Object> obj = gson.fromJson(json, Map.class);
		if (obj == null) {
			this.removedUrls.add(url);
			logger.info("Removed or moved page : "+ url);
			return;
		}

		Map<String, Object> objProduct = (Map<String, Object>) obj.get("product");
		Map<String, Object> objDetail = (Map<String, Object>) objProduct.get("detail");
		Map<String, Object> objColor = (Map<String, Object>) ((ArrayList<Map<String, Object>>) objDetail.get("colors")).get(0);
		ArrayList<Map<String, Object>> objSizes = (ArrayList<Map<String, Object>>) objColor.get("sizes");
		ArrayList<String> optionList = new ArrayList<String>();
		for (int i=0; i<objSizes.size(); i++) {
			Map<String,Object> size = (Map<String,Object>) objSizes.get(i);
			if ("in_stock".equals((String)size.get("availability"))) {
				optionList.add((String)size.get("name"));
			}
		}

		// out of stocks.
		if (optionList.size() < 1) {
			this.outOfStocks.add(url);
			logger.info("Out of stocks : " + url);
			return;
		}

		// process
		String code = url.substring(url.lastIndexOf("-")+1, url.lastIndexOf(".html"));
		String name = (String) objProduct.get("name");
		String desc = (String) objProduct.get("description");
		if (desc != null && !"".equals(desc)) {
			desc = desc.replaceAll("\n", "<br />").replaceAll(",", ".");
		}
		Double price = (Double) objProduct.get("price");
		if (price == null) {
			price = (Double) objSizes.get(0).get("price");
		}
		price *= 0.01;

		Double oldPrice = (Double) objProduct.get("oldPrice");
		if (oldPrice != null) {
			oldPrice *= 0.01;
		} else {
			oldPrice = 0.0;
		}
		Double discountPrice = 0.0;
		if (oldPrice > 0) {
			discountPrice = oldPrice - price;
		}

		String colorName = (String) objColor.get("name");
		if (colorName != null && !"".equals(colorName)) {
			colorName = colorName.replaceAll(" ","_").replaceAll("/", "_");
		}
		ArrayList<String> imageList = new ArrayList<String>();

		//images
		Elements imgs = doc.select("#main-images a");
		for (Element el : imgs) {
			imageList.add((String) el.attr("abs:href"));
		}

		Elements imgsDetail = doc.select("#detail-images a");
		for (Element el : imgsDetail) {
			imageList.add((String) el.attr("abs:href"));
		}

		Elements imgsPlain = doc.select("#plain-image a");
		for (Element el : imgsPlain) {
			imageList.add((String) el.attr("abs:href"));
		}

		// product Detail
		StringBuilder productDetail = new StringBuilder();
		String mainImage = "";
		productDetail.append("<div style=\"text-align:center;\">");
		for (int i=0; i<imageList.size(); i++) {
			String imageUrl = imageList.get(i);
			switch(i) {
				case 0:	// download the first image for main. 
					mainImage = code +"_"+ colorName +".jpg";
					try(InputStream in = new URL(imageUrl).openStream()){
						Files.copy(in, Paths.get(PATH + FOLDER_TEMPORARY + mainImage));
					} catch(IOException io) {
//						System.out.println(io.getMessage());
					}
					break;
				default:
					productDetail.append("<img src=\"").append(imageUrl).append("\" /><br /><br />");
			}
		}
		if (!"null".equals(desc)) {
			productDetail.append("<p style=\"font-size: 13px; font-weight: bold; text-align: center; padding: 30px;\">").append(desc).append("<br /><br />").append("상품코드 : ").append(code.replaceAll("p", "")).append("</p>");
			productDetail.append("<img src=\"https://shop-phinf.pstatic.net/20180219_198/500223749_1519041380407vJ5R9_PNG/image.png\" width=\"690\" height=\"4223\" alt=\"\">");	//하단 안내 이미지.
		}
		productDetail.append("</div>");

		ArrayList<String> productinfo = productInfo(category, subCategory);
		String productCode = productinfo.get(0);
		String productName = "유럽 자라 "+ productinfo.get(1) +" "+ name +" "+ colorName;

		// option bundle
		StringBuilder optionBundle = new StringBuilder();
		StringBuilder stockBundle = new StringBuilder();
		for (int i=0; i<optionList.size(); i++) {
			// option
			String serial = String.format("%05d", this.codeSerial);	// 5자리 serial : 00001~99999
			String option = optionList.get(i);
			optionBundle.append(option)
						.append(" {EZ").append(serial).append("}");
			
			// stock
			stockBundle.append("300");

			if (i < optionList.size()-1) {
				optionBundle.append(", ");
				stockBundle.append(", ");
			}

			// Add data for codes
			String data = new StringBuilder("EZ").append(serial).append("\t")
									.append(option).append("\t")
									.append(productName).append("\t")
									.append(price).append("\t")
									.append(url)
									.toString();
			this.codes.add(data);
			this.codeSerial++;
		}

		// duplication filter by product name
        if (!this.products.contains(productName)) {
	        	// CSV add row : 상품코드, 카테고리, 서브카테고리, 상품정보제공고시 모델명, 상품정보제공고시 모델명(p 제외), 상품명, 매입원가, 가격, 즉시할인값, 대표 이미지 파일명, 상품 상세정보, 옵션값, 재고, 상품URL
            writer.write(productCode +"\t"+ category +"\t"+ subCategory +"\t"+ code +"\t"+ code.replaceAll("p","") +"\t"+ productName +"\t"+ price +"\t"+ Math.round((price * CURRENCY)/1000)*1000 +"\t"+ Math.round((discountPrice * CURRENCY)/1000)*1000 +"\t"+ mainImage +"\t"+ productDetail +"\t"+ optionBundle.toString() +"\t"+ stockBundle.toString() +"\t"+ url +"\n");
            this.products.add(productName);
        }
	}

	private void pack(String target, String file) throws IOException {
		Path p = Files.createFile(Paths.get(file));
		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
			Path pp = Paths.get(target);
			Files.walk(pp)
				.filter(path -> !Files.isDirectory(path))
				.forEach(path -> {
					ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
					try {
						zs.putNextEntry(zipEntry);
						Files.copy(path, zs);
						zs.closeEntry();
					} catch (IOException e) {
						System.err.println(e);
					}
				});
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> productInfo(String category, String subCategory) {
		String json = "{"
				+ "\"HERREN\": {"
					+ "\"Jacken\":[\"50000838\", \"재킷\"],"		// 재킷
					+ "\"Pullover\":[\"50000831\", \"니트\"],"		// 니트/스웨터
					+ "\"T_Shirts\":[\"50000830\", \"티셔츠\"],"	// 티셔츠
					+ "\"Sneaker\":[\"50000788\", \"스니커즈\"]"	// 스니커즈
				+ "}"
			+ "}";

		Gson gson = new Gson();
		Map<String, Map<String, ArrayList<String>>> jsonObject = gson.fromJson(json, Map.class);
		Map<String, ArrayList<String>> obj = (Map<String, ArrayList<String>>) jsonObject.get(category);

		return (ArrayList<String>) obj.get(subCategory);
	}


	class ProductListWriteWrapper {
		private Writer writer;
		private ArrayList<String> list;
		private int count = 0;

		public ProductListWriteWrapper(Writer writer) {
			this.writer = writer;
			this.list = new ArrayList<String>();
		}

		public void  write(String category, String url) throws Exception {
			// 신발 카테고리에서 sneaker만 골라냄.
			if ("HERREN/Sneaker".equals(category) && !url.contains("sneaker")) {
				return;
			}
			// Remove duplications
			if(!this.list.contains(url)) {
                this.list.add(url);
                this.writer.write(category +"-"+ url + "\n");
                this.count++;
			}
		}
		public int getCount() {
			return this.count;
		}
	}
	
}
