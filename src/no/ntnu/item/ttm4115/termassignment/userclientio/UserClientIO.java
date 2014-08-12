package no.ntnu.item.ttm4115.termassignment.userclientio;

import no.ntnu.item.arctis.runtime.Block;
import no.ntnu.item.ttm4115.termassignment.support.*;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
import java.util.HashMap;

public class UserClientIO extends Block {
	
	public java.lang.String alias;
	private String taxiName;
	private static final String topicToDispatcher = "ntnu/item/ttm4115/group7/dispatcher/";
	private static final String topicToTaxiBase = "ntnu/item/ttm4115/group7/taxi/";
	
	
	/**Generate request*/
	public Message requestM(String request) {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","request",request);
	}
	
	/**Generate confirm message*/
	public Message confirmM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","confirm");
	}
	
	/**Generate cancel message*/
	public Message cancelM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","cancel");
	}
	/**Generate send message*/
	public Message sendM(String mess) {
		return io.createMessage(topicToTaxiBase+taxiName+"/",alias,taxiName,"send",mess);
	}

	public String getMessage(Message in) {
		HashMap<String,String> messageBody = io.getPayload(in);
		if (messageBody.get("type").equals("reply")){
			return "Dispatcher->"+messageBody.get("message");
		}
		if (messageBody.get("type").equals("send")){
			return taxiName+"->"+messageBody.get("message");
		}
		if (messageBody.get("type").equals("order")){
			taxiName = messageBody.get("message");
			return "Receive order:"+taxiName;
		}
		if (messageBody.get("type").equals("done")){
			return "Trip Completed,Good Luck!";
		}
		return "Received a spam message!";
	}
	
    /**Get the message body from message
	public HashMap<String,String> getPayload(Message in) {
		return io.getPayload(in);
	}*/
	/**
	public String getValue() {
		return messageData.get("message");
	}*/
	
	/**
	public String getMessageType(HashMap <String,String> data) {
		return  data.get("type");
	}*/

}

