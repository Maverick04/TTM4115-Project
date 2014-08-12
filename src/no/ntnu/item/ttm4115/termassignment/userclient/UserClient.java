package no.ntnu.item.ttm4115.termassignment.userclient;

import no.ntnu.item.arctis.runtime.Block;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
import java.util.logging.Logger;

public class UserClient extends Block {
	
	
	public java.lang.String alias;
	private static Logger log = Logger.getLogger("UserClient");
	/**
	 * Extract the topic from a Message and truncate away the prefix
	 * The subtopic is also the alias of instance
	 * 
	 * @copyright 2014 Tong Yang <tongy@stud.ntnu.no>
	 * 
	 * @param message The message
	 * @return String short topic if prefix matches, long topic if prefix doesn't match
	 * @throws Exception 
	 */
	public static String getAlias(Message messageData) {
		String topicBase = "ntnu/item/ttm4115/group7/user/";
		String _alias = messageData.getTopic().substring(topicBase.length()).replaceAll("/", "");
		log.info("Message Send To User: " + _alias);
		return _alias;
	}

	public static String getAlias(int number){   
		return "user"+number;
	}
	public static String getAlias(String alias){
		return alias;
	}

	public String exit() {
		log.info("UserClient: " + alias +" Exists");
		return alias;
	}
}
