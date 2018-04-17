package com.range.shipon.component;

import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.range.shipon.mybatis.service.SeluvHandlingService;
import com.range.shipon.util.SeluvProductXMLGenerator;


@Component
public class GodomallZaraHandlingComponent {

	private static final Logger logger = LoggerFactory.getLogger(GodomallZaraHandlingComponent.class);

	static final String PATH = "/home/ubuntu/cron/zara/";
//	static final String PATH = "/Users/jogun/Downloads/zara/";
	static final String FOLDER_RESOURCES = "resources/";
	static final String STATIC_XML_PATH = "static/seluv/xml/";
	static final String PRODUCT_CODE_PREFIX = "ZR";
	static final String PRODUCT_CODE = "001";
	static final String MALL_ID = "zara";

	@Value("${domain.file}")
	private String staticWebDomain;

	@Value("${godo.openhub.insert}")
	private String godoOpenhubInsertUrl;
	
	@Value("${godo.openhub.update}")
	private String godoOpenhubUpdateUrl;

	@Value("${godo.auth.partner_key}")
	private String partnerKey;

	@Value("${godo.auth.key}")
	private String key;

	@Autowired
	SeluvHandlingService service;

	@Autowired
	HttpDispatcher dispatcher;
	
	@Autowired
	private AwsS3HandlingComponent aws;

	@Async
	public void process(String targetProductNo, 
					String modelNumber, String v1, 
					String categoryCode, String categoryName, String productName, 
					double price, double oldPrice, ArrayList<String> optionList, 
					String mainImage, String productDetail, 
					boolean isSoldout, String url) {

		try {
			boolean hasProduct = (targetProductNo != null && !"".equals(targetProductNo));
			String filename = new StringBuilder(hasProduct? "update_" : "insert_").append(modelNumber).append("_").append(v1).append(".xml").toString();

			// generate xml
			SeluvProductXMLGenerator xml = new SeluvProductXMLGenerator();
			if (hasProduct) {
				xml.addTargetProductNo(targetProductNo);
			}
			xml.setFilename(filename);
			xml.addCategoryData(categoryCode);
			xml.addProductName(productName);
			xml.addSearchWord(categoryName);
			xml.addBrandCode(PRODUCT_CODE);
			xml.addModelNo(modelNumber);
			xml.addProductPriceData(price, oldPrice);
			xml.addProductPrice(price);
			for (int i=0; i<optionList.size(); i++) {
				String option = optionList.get(i);	// ex: S-ZR00001-y
				xml.addSizeOption(i+1, option.split("-")[0], option.split("-")[1], option.split("-")[2]);
			}
//			xml.addMainImage(this.staticWebDomain + STATIC_IMAGE_PATH + mainImage);	// TODO : file.shipon.de 도메인에 위치한 images
			xml.addMainImage(mainImage);
			xml.addProductDetail(productDetail.toString());
			xml.addSoldoutFlag(isSoldout ? "y" : "n");
			xml.addAdminMemo(url);
			xml.generate();

			// aws upload
			this.aws.upload(PATH + FOLDER_RESOURCES + "xml/" + filename, STATIC_XML_PATH);

			//  godo API call
//			long apistart = System.currentTimeMillis();
			StringBuilder targetUrl = new StringBuilder();
			if (hasProduct) {
				targetUrl.append(this.godoOpenhubUpdateUrl);
			} else {
				targetUrl.append(this.godoOpenhubInsertUrl);
			}
			targetUrl.append("?partner_key=").append(this.partnerKey).append("&key=").append(this.key).append("&data_url=").append(this.staticWebDomain).append(STATIC_XML_PATH).append(filename);
			String resultXml = dispatcher.request(targetUrl.toString());
//			long apiend = System.currentTimeMillis();
//			logger.info(">> API : "+ new SimpleDateFormat("mm:ss:SSS").format(new Date(apiend - apistart)));

			// insert to db
			DOMParser parser = new DOMParser();
		    parser.parse(new InputSource(new java.io.StringReader(resultXml)));
		    org.w3c.dom.Document xmlDocument = parser.getDocument();
		    String productNo = xmlDocument.getChildNodes()
									.item(0).getChildNodes()	// data >
									.item(3).getChildNodes()	// return >
									.item(1).getChildNodes()	// data >
									.item(7).getChildNodes()	// goodsno
									.item(1).getTextContent();
		    String productCode = modelNumber +"_"+ v1;
		    int hasStock = isSoldout? 0 : 1;

		    if (hasProduct) {
		    	service.updateProduct(productCode, price, hasStock);
		    } else {		    	
		    	service.insertProduct(MALL_ID, productNo, productCode, price, hasStock);
		    }

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
	
}
