package no.ntnu.item.ttm4115.termassignment.taxiclient;

import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
import no.ntnu.item.arctis.runtime.Block;
import java.util.logging.Logger;

public class TaxiClient extends Block {

	public java.lang.String alias;
	private static Logger log = Logger.getLogger("TaxiClient");
	public static String getAlias(String alias){
		return alias;
	}
	/**
	 * Return the instance alias from message!
	 * */
	public static String getAlias(Message messageData) {
		String topicBase = "ntnu/item/ttm4115/group7/";
		String _alias = messageData.getTopic().substring(topicBase.length()+4).replaceAll("/", "");
		log.info("Message is Sent to TaxiClient: " + _alias);
        return _alias;
	}

	public String exit() {
		log.info("TaxiClient: " + alias +" Exists");
		return alias;
	}
	
}
