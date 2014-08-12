package no.ntnu.item.ttm4115.termassignment.simplemqtt;

import no.ntnu.item.arctis.runtime.Block;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
import com.bitreactive.library.mqtt.MQTTConfigParam;
import java.util.logging.Logger;

/**
 * SimpleMQTT block which packages MQTT to provide utility for TTM4115 project.
 * Known problem: not possible to set QoS.
 * 
 * @author Tong Yang <tongy@stud.ntnu.no>
 * @copyright 2014 Tong Yang <tongy@stud.ntnu.no>
 */
public class SimpleMQTT extends Block {

	// Instance parameter. Edit only in overview page.
	public final int group;
		/**
		 * Base topic without group number;
		 * group number as integer, followed by slash, must be appended!
		 */
		private static final String BASE_TOPIC = "ntnu/item/ttm4115/group";
		
		/** Effective group number, either from UI or AdvancedConfiguration */
		public int effectiveGroup;
		
		private static Logger log = Logger.getLogger("SimpleMQTT");

		
		/**
		 * Generate a MQTT configuration object from instance variables.
		 * 
		 * @copyright 2014 Tong Yang <tongy@stud.ntnu.no>
		 * @return Generated MQTTConfigParam instance
		 */
		public MQTTConfigParam generateConfig(String subscribeTopics) {
			if (effectiveGroup == 0) {
				effectiveGroup = group;
			}
			if (effectiveGroup == 0) {
				throw new IllegalStateException("Rightclick the TaxiMQTT block and select \"Parameters and Generics\"");
			}
			String topicBase = BASE_TOPIC+effectiveGroup;
			String clientId = MQTTConfigParam.generateUUID(); // not UUID, UID.
			MQTTConfigParam config = new MQTTConfigParam("dev.bitreactive.com", 1883, clientId);
			String[] topics = subscribeTopics.split(",");
			for(String topic : topics) {
				logInfo("Started listening on topic "+topicBase+'/'+topic);
				if (!topic.equals("")) {
					config.addSubscribeTopic(topicBase+"/"+topic);
				}
			}
			// Default topic should never be used, as ESM prohibits it.
			config.setDefaultPublishTopic(topicBase+"/");
			log.info("New Configuration Instance Is Generated!");
			return config;
		}

		
	public Message createMessage(String payload) {
		
		log.info("MapUpdate Information Is Sent via SimpleMQTT!");
		return new Message(payload.getBytes(),"ntnu/item/ttm4115/group7/map/");
	}
		
	// Do not edit this constructor.
	public SimpleMQTT(int group) {
	    this.group = group;
	}
}
