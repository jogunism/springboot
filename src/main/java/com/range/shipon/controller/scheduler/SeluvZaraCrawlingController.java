package com.range.shipon.controller.scheduler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.range.shipon.component.GodomallZaraHandlingComponent;
import com.range.shipon.mybatis.model.SeluvCategory;
import com.range.shipon.mybatis.model.SeluvProductBasic;
import com.range.shipon.mybatis.service.SeluvHandlingService;


@RestController
@RequestMapping("/batch/crawling/seluv/zara")
public class SeluvZaraCrawlingController {

	private static final Logger logger = LoggerFactory.getLogger(SeluvZaraCrawlingController.class);
	
	static final String PATH = "/home/ubuntu/cron/zara/";
//	static final String PATH = "/Users/jogun/Downloads/zara/";
	static final String FILE_LINK = "zara_link.txt";
	static final String FOLDER_RESOURCES = "resources/";
	static final String STATIC_IMAGE_PATH = "static/seluv/image/";
	static final String STATIC_XML_PATH = "static/seluv/xml/";

	static final String PRODUCT_CODE_PREFIX = "ZR";
	static final String PRODUCT_CODE = "001";
	static final String MALL_ID = "zara";

	private ArrayList<String> products = null;
	private int codeSerial = 1;
	private ArrayList<SeluvProductBasic> storedProductList = null;


	@Autowired
	SeluvHandlingService service;

	@Autowired
	GodomallZaraHandlingComponent thread;

	@RequestMapping(value = "/link")
	public Map<String, String> getZaraProductList() throws Exception  {

		Map<String, String> result = new HashMap<String, String>();

		try {
			long starttime = System.currentTimeMillis();
			
			// get product urls
			ArrayList<String> productList = new ArrayList<String>();

			for (SeluvCategory category : service.getCategoryList(MALL_ID)) {
				if (category == null || category.getUrls() == null || "".equals(category.getUrls())) {
					continue;
				}
				String[] urls = category.getUrls().replaceAll(" ", "").replaceAll("\n", "").split(",");
				for (String url : urls) {
					if (url == null || "".equals(url)) {
						continue;
					}
					
					Document linkDocument = Jsoup.connect(url).get();
					Elements productElement = linkDocument.select("._productList li");

					if (productElement.size() < 1) {
						continue;
					}

					for (Element el : productElement) {
						String productUrl = el.select("a").attr("abs:href");
						if (productUrl == null || "".equals(productUrl)) {
							continue;
						}
						
		        		if (productUrl.contains("&")) {	// http://www.zara.com/de/{product_name}.html?v1={code}
		        			productUrl = productUrl.substring(0, productUrl.lastIndexOf("&"));
		        		}

						productList.add(category.getCategoryName() +" "+ category.getCategoryCode() +"_"+ productUrl);
					}
				}
			}

			// Makes a File without duplication
			ArrayList<String> temp = new ArrayList<String>();
			int count = 0;
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH + FILE_LINK), "utf-8"))) {
	        	for (String product : productList) {
	        		String category = product.split("_")[0];
	        		String url = product.split("_")[1];
	    			if(!temp.contains(url)) {
	                    temp.add(url);
	                    writer.write(category +" "+ url + "\n");
	                    count++;
	    			}
	        	}
			}
			long endtime = System.currentTimeMillis();
			String spentTime = new SimpleDateFormat("mm:ss:SSS").format(new Date(endtime - starttime));

			logger.info("-----------------------------------------------------------------------------------");
			logger.info("RESULT ");
			logger.info(" - total : "+ count);
			logger.info(" - time : "+ spentTime);
			logger.info("-----------------------------------------------------------------------------------");

			result.put("total", String.valueOf(count));
			result.put("time", spentTime);

		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return result;
	}


	@RequestMapping(value = "/process")
	public void crwaling() throws Exception {

		this.storedProductList = service.getProductList(MALL_ID);
		ArrayList<String> productList = new ArrayList<String>();

		Path path = Paths.get(PATH + FILE_LINK);
		ArrayList<String> fileContent = new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));

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
			logger.warn("NO MORE URLs.");
			return;
		}

        int count = 0;
        this.products = new ArrayList<String>();
        for (String product : productList) {
        	try {
				this.zaraProductCrawling(product.split(" ")[0], product.split(" ")[1], product.split(" ")[2]);
				count++;
            } catch(Exception e) {
            	logger.warn("error : "+ product.split(" ")[2]);
            	e.printStackTrace();
            	continue;
            }

			if (count % 8 == 0) {
				Thread.sleep(1000);
			}
        }

        // result
        // TODO - slack notification
        logger.info("-----------------------------------------------------------------------------------");
        logger.info("RESULT ");
        logger.info(" - total : "+ count);
        logger.info(" - products : "+ this.products.size());
        logger.info("-----------------------------------------------------------------------------------");
	}


	@SuppressWarnings("unchecked")
	private void zaraProductCrawling(String categoryName, String categoryCode, String url) throws Exception {

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

		// removed or moved view page
		Gson gson = new Gson();
		Map<String, Object> obj = gson.fromJson(json, Map.class);
		if (obj == null) {
			logger.info("Removed or moved product : "+ url);
			return;
		}

		String modelNumber = url.substring(url.lastIndexOf("-")+1, url.lastIndexOf(".html"));
		String v1 = url.substring(url.indexOf("=") + 1);

		Map<String, Object> objProduct = (Map<String, Object>) obj.get("product");
		String productName = new StringBuilder((String) objProduct.get("name")).append(" (").append(v1).append(")").toString();	// ex: {product name} ({v1 code})

		// pass a same product
		if (this.products.contains(productName)) {
			return;
		} else {
			this.products.add(productName);
		}

		Map<String, Object> objDetail = (Map<String, Object>) objProduct.get("detail");
		Map<String, Object> objColor = (Map<String, Object>) ((ArrayList<Map<String, Object>>) objDetail.get("colors")).get(0);
		ArrayList<Map<String, Object>> objSizes = (ArrayList<Map<String, Object>>) objColor.get("sizes");

		ArrayList<String> optionList = new ArrayList<String>();
		boolean isSoldout = true;
		for (int i=0; i<objSizes.size(); i++) {
			Map<String,Object> size = (Map<String,Object>) objSizes.get(i);
			String serial = String.format("%05d", this.codeSerial);			// 5자리 serial : 00001~99999
			StringBuilder option = new StringBuilder((String)size.get("name")).append("-").append(PRODUCT_CODE_PREFIX).append(serial).append("-");
			if ("in_stock".equals((String)size.get("availability"))) {
				option.append("y");
				isSoldout = false;
			} else {
				option.append("n");
			}
			optionList.add(option.toString());
			this.codeSerial++;
		}

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

		String colorName = (String) objColor.get("name");
		if (colorName != null && !"".equals(colorName)) {
			colorName = colorName.replaceAll(" ","_").replaceAll("/", "_");
		}

		//images
		ArrayList<String> imageList = new ArrayList<String>();
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

		// product Detail (and download images)
		StringBuilder productDetail = new StringBuilder("<div style=\"text-align:center;\">");
//		String imageName = "";
//		String mainImage = "";
		for (int i=0; i<imageList.size(); i++) {
			String imageUrl = imageList.get(i);
			// TODO : image downloading and upload to aws
//			try(InputStream in = new URL(imageUrl).openStream()){
//				switch(i) {
//					case 0:
//						imageName = modelNumber +"_"+ v1 +"_main.jpg";
//						mainImage = imageName;
//						break;
//					default:
//						imageName = modelNumber +"_"+ v1 +"_sub_"+ i +".jpg";
//				}
//
//				Files.copy(in, Paths.get(PATH + FOLDER_RESOURCES + "image/" + imageName));
//			} catch(IOException io) {
//				logger.warn("Already exists file : "+ io.getMessage());
//			}
//			awsUploader.upload(PATH + FOLDER_RESOURCES + "image/" + imageName, STATIC_IMAGE_PATH);
//			productDetail.append("<img src=\"").append(this.staticWebDomain).append(STATIC_IMAGE_PATH).append(imageName).append("\" /><br /><br />");
			productDetail.append("<img src=\"").append(imageUrl).append("\" /><br /><br />");
		}

		if (!"null".equals(desc)) {
			productDetail.append("<p style=\"font-size: 13px; font-weight: bold; text-align: center; padding: 30px;\">").append(desc).append("<br /><br />").append("상품코드 : ").append(modelNumber.replaceAll("p", "")).append("</p>");
			productDetail.append("");	//하단 안내 이미지.
		}
		productDetail.append("</div>");

		// process
		String targetProductNo = null;
		for (SeluvProductBasic product : this.storedProductList) {
			if (product.getProductCode().equals(modelNumber+"_"+v1)) {
				targetProductNo = product.getProductNo();
				break;
			}
		}

		thread.process(targetProductNo, modelNumber, v1, categoryCode, categoryName, productName, price, oldPrice, optionList, imageList.get(0), productDetail.toString(), isSoldout, url);
	}

}
