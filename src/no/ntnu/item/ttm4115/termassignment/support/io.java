package no.ntnu.item.ttm4115.termassignment.support;

import java.nio.charset.Charset;
import java.util.HashMap;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;

public class io {
    
	/** Char set constant for encoding/decoding MQTT messages */
	private static final Charset UTF8;
	public java.util.HashMap<String,String> messageData;
	static {
		Charset utf8 = null;
		try {
			utf8 = Charset.forName("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (utf8 == null) {
			utf8 = Charset.defaultCharset();
		}
		UTF8 = utf8;
	}
	/**
	 * Create a message for IO block. 
	 * @param String... args topic,sender,receiver,type,....
	 * @return Message
	 * @author TongYang <tongy@stud.ntnu.no>
	 * */
	public  static Message createMessage(String... args){
		HashMap<String,String> payload = new HashMap<String, String>();
		payload.put("sender", args[1]);
		payload.put("receiver",args[2]);
		payload.put("type",args[3]);
		
		if(args[3].equals("request")){
			String [] content = args[4].split("#");
			payload.put("cartype",content[0]);
			payload.put("time", content[1]);
			payload.put("location", content[2]);
		}
		
	    if(args[3].equals("send") || args[3].equals("reply") || args[3].equals("order")){
			payload.put("message", args[4]);
			
		}
	    
	    if(args[3].equals("available")){
			payload.put("location", args[4]);
		}
	
	    return new Message(payload.toString().getBytes(UTF8), args[0]);
	
	}
	
	/**
	 * Reconstruct the message body from incoming message. 
	 * @param Message  the incoming message
	 * @return HashMap the message body
	 * @author TongYang <tongy@stud.ntnu.no> 
	 * */
	
	public static HashMap<String,String> getPayload(Message in){
        
		HashMap<String, String> myMap = new HashMap<String, String>();
		String payload = new String(in.getPayload(),UTF8);
		payload=payload.substring(1,payload.length()-1);
		String[] pairs = payload.split(",");
		for (int i=0;i<pairs.length;i++) {
		    String pair = pairs[i];
		    String[] keyValue = pair.split("=");
		    myMap.put(keyValue[0].trim(), keyValue[1].trim());
		}
			return myMap;
	}
}
