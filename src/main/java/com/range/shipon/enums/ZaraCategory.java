package com.range.shipon.enums;

public enum ZaraCategory {

	DAMEN(616506),
	TRF(616514),
	HERREN(616518);

	private int code;

	ZaraCategory(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public static String getCategory(int categoryId) {
		switch (categoryId) {
			case 616506: 
				return "DAMEN";
			case 616514: 
				return "TRF";
			case 616518: 
				return "HERREN";
			default : 
				return "-";
		}
	}
}
