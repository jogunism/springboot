package com.range.shipon;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.range.shipon.mybatis.model.SeluvCategory;
import com.range.shipon.mybatis.service.SeluvHandlingService;
import com.range.shipon.util.SeluvProductXMLGenerator;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GodoZaraCrawlingTestController {

	static final String PATH = "/Users/jogun/Downloads/zara/";
	static final String FILE_LINK = "zara_link.txt";
	static final String FOLDER_RESOURCES = "resources/";
	static final String PRODUCT_CODE_PREFIX = "ZR";
	static final String PRODUCT_CODE = "001";
	static final String MALL_ID = "zara";

	private ArrayList<String> removedUrls = null;
	private ArrayList<String> products = null;
	private int codeSerial = 1;

	@Value("${domain.file}")
	private String staticWebDomain;

	@Autowired
	SeluvHandlingService service;
	
	@Before
	public void mockData() {
	}

	@Test
	public void getZaraProductList()  {

		long starttime = System.currentTimeMillis();

		try {
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

			System.out.println("-----------------------------------------------------------------------------------");
			System.out.println("RESULT ");
			System.out.println(" - total : "+ count);
			System.out.println(" - time : "+ new SimpleDateFormat("mm:ss:SSS").format(new Date(endtime - starttime)));
			System.out.println("-----------------------------------------------------------------------------------");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}


//	@Test
	public void crwaling() throws Exception {

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
			System.out.println("NO MORE URLs.");
			return;
		}

        int count = 0;
        this.removedUrls = new ArrayList<String>();
        this.products = new ArrayList<String>();

        for (String product : productList) {
			this.xmlGenerator(product.split(" ")[0], product.split(" ")[1], product.split(" ")[2]);
	    	count++;

	    	if (count > 5) break;	// TODO : delete
//	    	break;
        }

//        // result
//        // TODO - slack notification
//        System.out.println("-----------------------------------------------------------------------------------");
//        System.out.println("RESULT ");
//        System.out.println(" - total : "+ count);
//        System.out.println(" - removed : "+ this.removedUrls.size() +" / out of stock : "+ this.outOfStocks.size() +" / "+ this.products.size());
//        System.out.println(" - products : "+ (count - this.removedUrls.size() - this.outOfStocks.size()));
//        System.out.println("-----------------------------------------------------------------------------------");
	}


	@SuppressWarnings("unchecked")
	private void xmlGenerator(String categoryName, String categoryCode, String url) throws Exception {

//		System.out.println(categoryName);
//		System.out.println(categoryCode);
//		System.out.println(url);
		
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

		System.out.println(json);
		
//		// removed or moved view page
//		Gson gson = new Gson();
//		Map<String, Object> obj = gson.fromJson(json, Map.class);
//		if (obj == null) {
//			this.removedUrls.add(url);
//			System.out.println("- Removed or moved product : "+ url);
//			return;
//		}
//
//		String modelNumber = url.substring(url.lastIndexOf("-")+1, url.lastIndexOf(".html"));
//		String v1 = url.substring(url.indexOf("=") + 1);
//
//		Map<String, Object> objProduct = (Map<String, Object>) obj.get("product");
//		String productName = new StringBuilder((String) objProduct.get("name")).append(" (").append(v1).append(")").toString();	// ex: {product name} ({v1 code})
//
//		// pass a same product
//		if (this.products.contains(productName)) {
//			return;
//		} else {
//			this.products.add(productName);
//		}
//
//		Map<String, Object> objDetail = (Map<String, Object>) objProduct.get("detail");
//		Map<String, Object> objColor = (Map<String, Object>) ((ArrayList<Map<String, Object>>) objDetail.get("colors")).get(0);
//		ArrayList<Map<String, Object>> objSizes = (ArrayList<Map<String, Object>>) objColor.get("sizes");
//
//		ArrayList<String> optionList = new ArrayList<String>();
//		for (int i=0; i<objSizes.size(); i++) {
//			Map<String,Object> size = (Map<String,Object>) objSizes.get(i);
//			String serial = String.format("%05d", this.codeSerial);			// 5자리 serial : 00001~99999
//			StringBuilder option = new StringBuilder((String)size.get("name")).append("-").append(PRODUCT_CODE_PREFIX).append(serial).append("-");
//			if ("in_stock".equals((String)size.get("availability"))) {
//				option.append("y");
//			} else {
//				option.append("n");
//			}
//			optionList.add(option.toString());
//			this.codeSerial++;
//		}
//
//		String desc = (String) objProduct.get("description");
//		if (desc != null && !"".equals(desc)) {
//			desc = desc.replaceAll("\n", "<br />").replaceAll(",", ".");
//		}
//		Double price = (Double) objProduct.get("price");
//		if (price == null) {
//			price = (Double) objSizes.get(0).get("price");
//		}
//		price *= 0.01;
//
//		Double oldPrice = (Double) objProduct.get("oldPrice");
//		if (oldPrice != null) {
//			oldPrice *= 0.01;
//		} else {
//			oldPrice = 0.0;
//		}
//
//		String colorName = (String) objColor.get("name");
//		if (colorName != null && !"".equals(colorName)) {
//			colorName = colorName.replaceAll(" ","_").replaceAll("/", "_");
//		}
//
//		//images
//		ArrayList<String> imageList = new ArrayList<String>();
//		Elements imgs = doc.select("#main-images a");
//		for (Element el : imgs) {
//			imageList.add((String) el.attr("abs:href"));
//		}
//
//		Elements imgsDetail = doc.select("#detail-images a");
//		for (Element el : imgsDetail) {
//			imageList.add((String) el.attr("abs:href"));
//		}
//
//		Elements imgsPlain = doc.select("#plain-image a");
//		for (Element el : imgsPlain) {
//			imageList.add((String) el.attr("abs:href"));
//		}
//
//		// product Detail
//		StringBuilder productDetail = new StringBuilder("<div style=\"text-align:center;\">");
//		String imageName = "";
//		String mainImage = "";
//		for (int i=0; i<imageList.size(); i++) {
//			String imageUrl = imageList.get(i);
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
//				productDetail.append("<img src=\"").append(this.staticWebDomain).append("static/seluv/images/").append(imageName).append("\" /><br /><br />");
//				
//			} catch(IOException io) {
//				System.out.println(io.getMessage());
//			}
//		}
//		if (!"null".equals(desc)) {
//			productDetail.append("<p style=\"font-size: 13px; font-weight: bold; text-align: center; padding: 30px;\">").append(desc).append("<br /><br />").append("상품코드 : ").append(modelNumber.replaceAll("p", "")).append("</p>");
//			productDetail.append("");	//하단 안내 이미지.
//		}
//		productDetail.append("</div>");
//
//
//		// XML Generation for using godomall API.
//		try {
//			SeluvProductInsertXMLGenerator xml = new SeluvProductInsertXMLGenerator();
//			xml.setFilename(modelNumber +"_"+ v1);
//			xml.addCategoryData(categoryCode);
//			xml.addProductName(productName);
//			xml.addSearchWord(categoryName);
//			xml.addBrandCode(PRODUCT_CODE); // ZARA Code
//			xml.addModelNo(modelNumber);
//			xml.addProductPriceData(price, oldPrice);
//			xml.addProductPrice(price);
//			for (int i=0; i<optionList.size(); i++) {
//				String option = optionList.get(i);	// ex: S-ZR00001-y
//				xml.addSizeOption(i+1, option.split("-")[0], option.split("-")[1], option.split("-")[2]);
//			}
//			xml.addMainImage(this.staticWebDomain + "static/seluv/images/" + mainImage);
//			xml.addProductDetail(productDetail.toString());
//			xml.addAdminMemo(url);
//			xml.generate();
//
//			
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
}
