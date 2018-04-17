package com.range.shipon.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CategoryComponent {

	private static Map<String, String> CATEGORY;

	public Map<String, String> dokhan() {
		CATEGORY = new HashMap<String, String>();
		CATEGORY.put("생활건강","50000008");
		CATEGORY.put("식품","50000006");
		CATEGORY.put("화장품/미용","50000002");
		CATEGORY.put("출산/육아","50000005");
		CATEGORY.put("디지털/가전","50000003");
		return CATEGORY;
	}

	public Map<String, String> doichiMall() {
		CATEGORY = new HashMap<String, String>();
		CATEGORY.put("식품&건강", "68e9450358fd4e068517b8b9d59ef9b0");
		CATEGORY.put("화장품", "5bf5f54272cf4ce8a2d230a3faeb8348");
		CATEGORY.put("메이크업", "2cc24f15d00c45798d481ca8b85a8946");
		CATEGORY.put("유아화장품", "ec5d9b33447d49648ba27276630e6e04");
		CATEGORY.put("분유&유산균", "d5f0154ad4bb4837a47b4ef5569622e4");
		CATEGORY.put("주방용품", "110d36e516894ccca08eede017ad2a15");
		CATEGORY.put("조리도구", "40adb3ae3f714dc3aed4978fa68ecaaa");
		CATEGORY.put("그릇&식기", "a878382f68b74b7e8a11f88f6fbba63a");
		CATEGORY.put("생활용품", "780f9bc4eab34095a35e310b6e37c95b");
		CATEGORY.put("인테리어소품", "d12771f3d35d48be8625d9b7da8ba585");
		CATEGORY.put("패션&잡화", "75ef0f455420409d827d9b4360262005");
		return CATEGORY;
	}

	public Map<String, String> hieuro() {
		CATEGORY = new HashMap<String, String>();
		CATEGORY.put("핫딜", "50a2661877df41519a15b35f49409f55");
		CATEGORY.put("유럽분유", "9de032a2e7a34a94b8519deefbe061f7");
		CATEGORY.put("미국상품", "1719202f7827440499ae2c13e7407e61");
		CATEGORY.put("리모와캐리어", "0440f4a915f94c3da6737b0f4b6cf5e0");
		CATEGORY.put("피쉬&브리타&USB", "54c3420b350c41e29fd4890ce6baa36d");
		CATEGORY.put("커피&차&시럽", "5367fc8ce58d495884c505aec905192a");
		CATEGORY.put("콤벨", "9cadc3c5cdcf4ff2a6d7e36db724a63e");
		CATEGORY.put("주방용품", "279c8efc835742cfa8e34da229b9c316");
		CATEGORY.put("건강기능식품", "ea5038cbf6324880af635c62d0de371c");
		CATEGORY.put("슈퍼마켓", "df1c746e51614377b80d5df5a7d941a5");
		CATEGORY.put("일리캡슐커피", "100ab97773e545c1a9970b198d0d64cf");
		CATEGORY.put("써니라이프", "697e82880cb54990958f704d2e3565fe");
		CATEGORY.put("유럽패션", "b3320686bbc140278df818f0843e7429");
		CATEGORY.put("폴로&타미스웨터", "b472c734ba9447abbf34d735ed41ff97");
		return CATEGORY;
	}

	public Map<String, String> euromoms() {
		CATEGORY = new HashMap<String, String>();
		CATEGORY.put("주방용품", "49a76ea62b5948b49191bc2a420ec8b9");
		CATEGORY.put("조리도구", "0d97f466264b4851a44126a283c14474");
		CATEGORY.put("그릇", "12caa3aa07914524809b0907d99cc1b3");
		CATEGORY.put("커트러리", "515ab68a3dfc46b8945f1ead6c040043");
		CATEGORY.put("식품/건강", "070a23dad3c14208b482ac3c8b5cc5c8");
		CATEGORY.put("화장품I", "acfa22d6d0304841aa92fa03e87e5431");
		CATEGORY.put("화장품II", "08c03e12f5bd49e19c88d1bfcc877e52");
		CATEGORY.put("화장품III", "8156359960fe40e9a4d0b4309c8c23b8");
		CATEGORY.put("화장품IV", "c05cf9346c1a40399ecc4da5bb551d56");
		CATEGORY.put("유아용화장품", "b44dfbdfa0c44f0baf1c2be6763b3fa1");
		CATEGORY.put("분유/유산균", "e3f97f1720794d868baa07df612e868a");
		CATEGORY.put("유아용품", "41489134a70c4c27b15b8dd06f5f4eba");
		CATEGORY.put("생활용품", "2a8bd51e476449bbb5d27b91a78cf045");
		CATEGORY.put("인테리어소품", "3fbc9cfd837e41d486d757127e14c331");
		return CATEGORY;
	}

	public Map<String, String> euroexpress() {
		CATEGORY = new HashMap<String, String>();
		CATEGORY.put("리모와","c2bc53aa07ff4fcd8017fda49da82f8c");
		CATEGORY.put("몽클레어","d62b4b954b77402f862809a7a034083a");
		CATEGORY.put("해외명품","c4fe53f76f8249bf8f6520917816d25e");
		CATEGORY.put("주방명품","e896c13480de4f05aae63e4b9b5ffa93");
		CATEGORY.put("명품가전","b76c14b478ac482b844b3143edc7d880");
		CATEGORY.put("생활용품","4c86505ead7e46e882b5529d64f0417c");
		CATEGORY.put("육아용품","b6ed75236d3646bb98e838e3dd628bf1");
		CATEGORY.put("코스메틱","7c0ecb0aa8e24151b7b420095ced2d33");
		return CATEGORY;
	}
}
