package com.range.shipon.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SeluvProductXMLGenerator {

	static final String PATH = "/home/ubuntu/cron/zara/resources/xml/";
//	static final String PATH = "/Users/jogun/Downloads/zara/resources/xml/";
	static final double CURRENCY = 1350;
	static final double MARGIN_RATE = 1.15;

	private Document document;
	private Element goodsData;
	private String filename;

	private double ceilPrice(double price) {
		return Math.ceil(price / 100) * 100;
	}

	public SeluvProductXMLGenerator() throws Exception {
		root();
		addBasicData();
	}

	private void root() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.document = builder.newDocument();

		Element data = this.document.createElement("data");
		this.document.appendChild(data);

		this.goodsData = this.document.createElement("goods_data");
		this.goodsData.setAttribute("idx", "1");
		data.appendChild(goodsData);
	}

	private void addBasicData() {
		Element goodsDisplayFl = this.document.createElement("goodsDisplayFl");	// PC쇼핑몰 노출상태(y/n)
		goodsDisplayFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(goodsDisplayFl);

		Element goodsDisplayMobileFl = this.document.createElement("goodsDisplayMobileFl");	// 모바일쇼핑몰 노출상태(y/n)
		goodsDisplayMobileFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(goodsDisplayMobileFl);

		Element goodsSellFl = this.document.createElement("goodsSellFl");		// PC쇼핑몰 판매상태 (y/n)
		goodsSellFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(goodsSellFl);

		Element goodsSellMobileFl = this.document.createElement("goodsSellMobileFl");	// 모바일쇼핑몰 판매상태 (y/n)
		goodsSellMobileFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(goodsSellMobileFl);	

		Element goodsNmFl = this.document.createElement("goodsNmFl");			// 상품명 타입 (d=기본 상품명, e=확장 상품명)
		goodsNmFl.appendChild(this.document.createTextNode("d"));
		this.goodsData.appendChild(goodsNmFl);

		Element goodsState = this.document.createElement("goodsState");			// 상품상태 (n=새상품, u=중고상품, r=반품/재고상품)
		goodsState.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(goodsState);
		
		Element payLimitFl = this.document.createElement("payLimitFl");			// 결제수단 설정 (n=통합설정, y=개별설정)
		payLimitFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(payLimitFl);

		Element payLimit = this.document.createElement("payLimit");				// 결제수단 개별설정, 구분자 : ^|^, (gb=무통장, pg=PG결제, gm=마일리지, gd=예치금, payco=페이코)
		payLimit.appendChild(this.document.createCDATASection("gb^|^pg^|^gm^|^gd^|^payco"));
		this.goodsData.appendChild(payLimit);

		Element goodsPermission = this.document.createElement("goodsPermission");	// 구매가능 회원등급 (all=전체(회원+비회원), member=회원전용(비회원제외), group=특정회원등급)
		goodsPermission.appendChild(this.document.createTextNode("all"));
		this.goodsData.appendChild(goodsPermission);

		Element onlyAdultFl = this.document.createElement("onlyAdultFl");		// 성인인증 사용여부 (y=사용함, n=사용안함)
		onlyAdultFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(onlyAdultFl);

		Element taxFreeFl = this.document.createElement("taxFreeFl");			// 과세/면세 (t=과세, f=면세)
		taxFreeFl.appendChild(this.document.createTextNode("t"));
		this.goodsData.appendChild(taxFreeFl);

		Element taxPercent = this.document.createElement("taxPercent");			// 과세율 * 과세율은 10으로 고정됩니다.
		taxPercent.appendChild(this.document.createCDATASection("10.0"));
		this.goodsData.appendChild(taxPercent);

		Element stockFl = this.document.createElement("stockFl");				// 판매재고 (n=무한정판매, y=재고량에 따름)
		stockFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(stockFl);

		Element mileageFl = this.document.createElement("mileageFl");			// 마일리지 지급방법 (c=통합설정, g=개별설정)
		mileageFl.appendChild(this.document.createTextNode("g"));
		this.goodsData.appendChild(mileageFl);

		Element mileageGoods = this.document.createElement("mileageGoods");		// 과세율 * 과세율은 10으로 고정됩니다.
		mileageGoods.appendChild(this.document.createCDATASection("0.0"));
		this.goodsData.appendChild(mileageGoods);

		Element mileageGoodsUnit = this.document.createElement("mileageGoodsUnit");		// 마일리지 개별설정 지급단위 (percent=%, mileage=원)
		mileageGoodsUnit.appendChild(this.document.createTextNode("percent"));
		this.goodsData.appendChild(mileageGoodsUnit);

		Element goodsDiscountFl = this.document.createElement("goodsDiscountFl");		// 상품 할인 설정 (y=사용함, n=사용안함)
		goodsDiscountFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(goodsDiscountFl);

		Element optionFl = this.document.createElement("optionFl");				// 옵션 사용여부 (y=사용함, n=사용안함)
		optionFl.appendChild(this.document.createTextNode("y"));
		this.goodsData.appendChild(optionFl);

		Element optionDisplayFl = this.document.createElement("optionDisplayFl");		// 옵션 노출타입 (s=일체형, d=분리형)
		optionDisplayFl.appendChild(this.document.createTextNode("d"));
		this.goodsData.appendChild(optionDisplayFl);

		Element optionName = this.document.createElement("optionName");			// 옵션명 (*구분자 ^|^)
		optionName.appendChild(this.document.createCDATASection("사이즈"));
		this.goodsData.appendChild(optionName);

		Element optionTextFl = this.document.createElement("optionTextFl");		// 텍스트옵션여부 (y=사용함, n=사용안함)
		optionTextFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(optionTextFl);

		Element addGoodsFl = this.document.createElement("addGoodsFl");			// 추가상품여부 (y=사용함, n=사용안함)
		addGoodsFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(addGoodsFl);
		
		Element deliverySno = this.document.createElement("deliverySno");		// 배송비 코드
		deliverySno.appendChild(this.document.createTextNode("2"));
		this.goodsData.appendChild(deliverySno);

		Element imgDetailViewFl = this.document.createElement("imgDetailViewFl");		// 상품이미지 돋보기 (y=사용함, n=사용안함)
		imgDetailViewFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(imgDetailViewFl);

		Element externalVideoFl = this.document.createElement("externalVideoFl");		// 외부 동영상 등록여부 (y=사용함, n=사용안함)
		externalVideoFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(externalVideoFl);
		
		Element detailInfoDelivery = this.document.createElement("detailInfoDelivery");	// 배송안내 (0=사용안함) *코드값 사용
		detailInfoDelivery.appendChild(this.document.createTextNode("002001"));
		this.goodsData.appendChild(detailInfoDelivery);

		Element detailInfoAS = this.document.createElement("detailInfoAS");				// AS안내 (0=사용안함) *코드값 사용
		detailInfoAS.appendChild(this.document.createTextNode("003001"));
		this.goodsData.appendChild(detailInfoAS);
		
		Element detailInfoRefund = this.document.createElement("detailInfoRefund");		// 환불안내 (0=사용안함) *코드값 사용
		externalVideoFl.appendChild(this.document.createTextNode("004001"));
		this.goodsData.appendChild(detailInfoRefund);
		
		Element detailInfoExchange = this.document.createElement("detailInfoExchange");	// 교환안내 (0=사용안함) *코드값 사용
		detailInfoExchange.appendChild(this.document.createTextNode("005001"));
		this.goodsData.appendChild(detailInfoExchange);

		Element scmNo = this.document.createElement("scmNo");					// 공급사번호 (1=본사, 그외 공급사)
		scmNo.appendChild(this.document.createTextNode("1"));
		this.goodsData.appendChild(scmNo);

		Element restockFl = this.document.createElement("restockFl");
		restockFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(restockFl);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void addCategoryData(String category) throws Exception {
		Element cateCd = this.document.createElement("cateCd");
		cateCd.appendChild(this.document.createTextNode(category));
		this.goodsData.appendChild(cateCd);

		StringBuilder code = new StringBuilder(category.substring(0,3));
		if (category.length() > 3) {
			code.append("|").append(category.substring(0,6));
		}
		if (category.length() > 6) {
			code.append("|").append(category);
		}
		Element allCateCd = this.document.createElement("allCateCd");
		allCateCd.appendChild(this.document.createCDATASection(code.toString()));
		this.goodsData.appendChild(allCateCd);
	}

	public void addProductName(String name) throws Exception {
		Element goodsNm = this.document.createElement("goodsNm");
		goodsNm.appendChild(this.document.createCDATASection(name));
		this.goodsData.appendChild(goodsNm);
	}

	public void addSearchWord(String word) throws Exception {
		Element goodsSearchWord = this.document.createElement("goodsSearchWord");
		goodsSearchWord.appendChild(this.document.createCDATASection(word));
		this.goodsData.appendChild(goodsSearchWord);
	}
	
	public void addBrandCode(String code) throws Exception {
		Element brandCd = this.document.createElement("brandCd");				// 브랜드코드
		brandCd.appendChild(this.document.createCDATASection(code));
		this.goodsData.appendChild(brandCd);
	}

	public void addModelNo(String no) throws Exception {
		Element goodsModelNo = this.document.createElement("goodsModelNo");		// 모델번호
		goodsModelNo.appendChild(this.document.createCDATASection(no));
		this.goodsData.appendChild(goodsModelNo);
	}

	public void addDiscountPercentage(double rate) throws Exception {
		Element goodsDiscount = this.document.createElement("goodsDiscount");	// 상품 할인값
		goodsDiscount.appendChild(this.document.createCDATASection(String.valueOf(rate)));
		this.goodsData.appendChild(goodsDiscount);

		Element goodsDiscountUnit = this.document.createElement("goodsDiscountUnit");	// 상품 할인 단위 (percent=%, price=원)
		goodsDiscountUnit.appendChild(this.document.createTextNode("percent"));
		this.goodsData.appendChild(goodsDiscountUnit);
	}

	public void addProductPriceData(double price, double orgPrice) throws Exception {
		Element fixedPrice = this.document.createElement("fixedPrice");			// 정가
		fixedPrice.appendChild(this.document.createCDATASection(String.valueOf(ceilPrice(orgPrice * CURRENCY))));
		this.goodsData.appendChild(fixedPrice);

		Element costPrice = this.document.createElement("costPrice");			// 매입가 : EURO
		costPrice.appendChild(this.document.createCDATASection(String.valueOf(Math.ceil(price))));
		this.goodsData.appendChild(costPrice);
		
		// 원래가격이 있고, 판매가가 원래가격보다 적으면 "세일"아이콘.
		if (orgPrice > 0 && ceilPrice(price * CURRENCY) < ceilPrice(orgPrice * CURRENCY)) {
			Element goodsIconCd = this.document.createElement("goodsIconCd");	// 아이콘 기간무제한용 (icon0006 = 세일상품)
			goodsIconCd.appendChild(this.document.createTextNode("icon0006"));
			this.goodsData.appendChild(goodsIconCd);
		}
	}

	public void addProductPrice(double price) throws Exception {
		double sellPrice = ceilPrice(price * CURRENCY * MARGIN_RATE);
		Element goodsPrice = this.document.createElement("goodsPrice");			// 판매가
		goodsPrice.appendChild(this.document.createCDATASection(String.valueOf(sellPrice)));
		this.goodsData.appendChild(goodsPrice);
	}

	public void addSizeOption(int idx, String value, String code, String soldoutFlag) throws Exception {
		Element optionData = this.document.createElement("optionData");
		optionData.setAttribute("idx", String.valueOf(idx));
		this.goodsData.appendChild(optionData);

		// optionData sub elements
		Element optionNo = this.document.createElement("optionNo");
		optionNo.appendChild(this.document.createTextNode(String.valueOf(idx)));
		optionData.appendChild(optionNo);

		Element optionValue1 = this.document.createElement("optionValue1");
		optionValue1.appendChild(this.document.createCDATASection(value));
		optionData.appendChild(optionValue1);

		Element optionViewFl = this.document.createElement("optionViewFl");
		optionViewFl.appendChild(this.document.createTextNode("y"));
		optionData.appendChild(optionViewFl);

		Element optionSellFl = this.document.createElement("optionSellFl");		// 품절여부 (y=판매함, n=판매안함)
		optionSellFl.appendChild(this.document.createTextNode(soldoutFlag));
		optionData.appendChild(optionSellFl);

		Element optionCode = this.document.createElement("optionCode");
		optionCode.appendChild(this.document.createCDATASection(code));
		optionData.appendChild(optionCode);

		Element stockCnt = this.document.createElement("stockCnt");
		stockCnt.appendChild(this.document.createTextNode("999"));
		optionData.appendChild(stockCnt);		
	}
	
	public void addMainImage(String url) throws Exception {
		Element imageStorage = this.document.createElement("imageStorage");
		imageStorage.appendChild(this.document.createTextNode("url"));
		this.goodsData.appendChild(imageStorage);

		Element detailImageData = this.document.createElement("detailImageData");
		detailImageData.setAttribute("idx", "1");
		detailImageData.appendChild(this.document.createCDATASection(url));
		this.goodsData.appendChild(detailImageData);

		Element listImageData = this.document.createElement("listImageData");
		listImageData.setAttribute("idx", "1");
		listImageData.appendChild(this.document.createCDATASection(url));
		this.goodsData.appendChild(listImageData);

		Element mainImageData = this.document.createElement("mainImageData");
		mainImageData.setAttribute("idx", "1");
		mainImageData.appendChild(this.document.createCDATASection(url));
		this.goodsData.appendChild(mainImageData);
	}

	public void addProductDetail(String text) throws Exception {
		Element goodsDescription = this.document.createElement("goodsDescription");
		goodsDescription.appendChild(this.document.createCDATASection(text));
		this.goodsData.appendChild(goodsDescription);

		Element goodsDescriptionMobile = this.document.createElement("goodsDescriptionMobile");
		goodsDescriptionMobile.appendChild(this.document.createCDATASection(text));
		this.goodsData.appendChild(goodsDescriptionMobile);
	}
	
	public void addRelatedProduct(boolean hasRelatedProduct, String code) throws Exception {
		String flag = hasRelatedProduct ? "m" : "n";
		Element relationFl = this.document.createElement("relationFl");				// 관련상품 설정 (n=사용안함, a=자동(동일카테고리 상품 무작위), m=직접선택)
		relationFl.appendChild(this.document.createTextNode(flag));
		this.goodsData.appendChild(relationFl);

		Element relationSameFl = this.document.createElement("relationSameFl");		// 관련상품 직접선택 서로등록여부 (y=사용함, n=사용안함)
		relationSameFl.appendChild(this.document.createTextNode("n"));
		this.goodsData.appendChild(relationSameFl);

		Element relationGoodsNo = this.document.createElement("relationGoodsNo");	// 관련상품코드 (구분자 : ||)
		relationGoodsNo.appendChild(this.document.createCDATASection(code));
		this.goodsData.appendChild(relationGoodsNo);
	}

	public void addSoldoutFlag(String flag) throws Exception {
		Element soldOutFl = this.document.createElement("soldOutFl");			// 품절상태 (n=정상, y=품절(수동))
		soldOutFl.appendChild(this.document.createTextNode(flag));
		this.goodsData.appendChild(soldOutFl);
	}
	
	public void addAdminMemo(String text) throws Exception {
		Element memo = this.document.createElement("memo");						// 관리자 메모
		memo.appendChild(this.document.createCDATASection(text));
		this.goodsData.appendChild(memo);
	}
	
	// UPDATE only
	public void addTargetProductNo(String productNo) {
		Element goodsNo = this.document.createElement("goodsNo");
		goodsNo.appendChild(this.document.createTextNode(productNo));
		this.goodsData.appendChild(goodsNo);
	}

	public void generate() throws Exception {
		if (this.filename == null || "".equals(this.filename)) {
			throw new Exception("The file name required.");
		}

		if (!this.filename.contains(".xml")) {
			throw new Exception("This is not a valid filename.");
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(PATH + this.filename));
		transformer.transform(source, result);
	}

}
