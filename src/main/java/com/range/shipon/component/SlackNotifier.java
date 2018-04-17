package com.range.shipon.component;

import org.springframework.stereotype.Component;

//@Slf4j
@Component
public class SlackNotifier {

//	@Autowired
//	private RestTemplate restTemplate;
//
//	public enum SlackTarget { 
//		CH_INCOMING("https://hooks.slack.com/services/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", "incoming");
//
//		String webHookUrl;
//		String channel;
//		SlackTarget(String webHookUrl, String channel) {
//			this.webHookUrl = webHookUrl;
//			this.channel = channel;
//		} 
//	}
//
//	@Data
//	@AllArgsConstructor
//	@NoArgsConstructor
//	@Builder
//	public static class SlackMessageAttachement {
//		private String color;
//		private String pretext;
//		private String title;
//		private String title_link;
//		private String text;
//	}
//
//	@Data
//	@AllArgsConstructor
//	@NoArgsConstructor
//	@Builder
//	public static class SlackMessage {
//		private String text;
//		private String channel;
//		private List<SlackMessageAttachement> attachments;
//
//		void addAttachment(SlackMessageAttachement attachement) {
//			if (this.attachments == null) {
//				this.attachments = Lists.newArrayList();
//			}
//			this.attachments.add(attachement);
//		} 
//	}
//
//	public boolean notify(SlackTarget target, SlackMessageAttachement message) {
//		log.debug("Notify[target: {}, message: {}]", target, message); 
//		SlackMessage slackMessage = SlackMessage.builder()
//											.channel(target.channel)
//											.attachments(Lists.newArrayList(message))
//											.build();
//		try {
//			restTemplate.postForEntity(target.webHookUrl, slackMessage, String.class); 
//			return true; 
//		} catch (Exception e) {
//			log.error("Occur Exception: {}", e); 
//			return false;
//		}
//	}
	
}
