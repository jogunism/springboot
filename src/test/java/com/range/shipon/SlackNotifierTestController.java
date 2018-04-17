package com.range.shipon;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.range.shipon.enums.ZaraCategory;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SlackNotifierTestController {

	static final String CSV = "zara_products.csv";
	static final String ZIP = "zara_images.zip";
	
	@Value("${slack.webhook.url}")
	private String webhook;
	
	@Test
	public void notification() {

//		SlackMessage msg = new SlackMessage();
////		msg.setText("https://console.shipon.de\nCrawling *도이치몰* complete");
//		msg.setText("file");
//		msg.setChannel("slacktest");
//
//		SlackAttachment attach = new SlackAttachment();
////		attach.setAuthorName("jogun");
////		attach.setImageUrl("http://scontent.cdninstagram.com/t51.2885-15/s320x320/e35/20393896_1469811343103222_8091284701341286400_n.jpg");
//		attach.setText("http://file.shipon.de/link.txt");
//		attach.setFallback("fallback");
//		ArrayList<SlackAttachment> attachlist = new ArrayList<SlackAttachment>();
//		attachlist.add(attach);
//		msg.setAttachments(attachlist);
//
//		SlackApi slack = new SlackApi(this.webhook);
//		slack.call(msg);
		

//		StringBuilder result = new StringBuilder()
//				.append("ZARA product crawling results\n")
//				.append(" - total : ").append(152).append("\n")
//				.append(" - removed : ").append(0).append(" / out of stock : ").append(7).append("\n")
//				.append(" - products : ").append(145);
//
//		SlackMessage msg = new SlackMessage();
//		msg.setText(result.toString());
//
//		SlackAttachment csv = new SlackAttachment();
//		csv.setText("http://file.shipon.de/static/zara/"+ CSV);
//		csv.setFallback("csv");
//		SlackAttachment zip = new SlackAttachment();
//		zip.setText("http://file.shipon.de/static/zara/"+ ZIP);
//		zip.setFallback("zip");
//
//		ArrayList<SlackAttachment> attachlist = new ArrayList<SlackAttachment>();
//		attachlist.add(csv);
//		attachlist.add(zip);
//		msg.setAttachments(attachlist);
//
//		SlackApi slack = new SlackApi(this.webhook);
//		slack.call(msg);
		
		SlackMessage msg = new SlackMessage();
		msg.setText("Exception : test");
		msg.setChannel("error_report");
		SlackApi slack = new SlackApi(this.webhook);
		slack.call(msg);
	}
	
}
