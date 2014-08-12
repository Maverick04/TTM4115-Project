package no.ntnu.item.ttm4115.termassignment.multitimmer;

import no.ntnu.item.arctis.runtime.Block;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
import no.ntnu.item.ttm4115.termassignment.support.io;


public class MultiTimmer extends Block {
	private static int count =0;
	public com.bitreactive.library.mqtt.mqtt.MQTT.Message request;
	public static String getAlias (Message in){
		count ++;
		return "Timer-"+count;
	}
    // Return the time for timer: 15 minutes before the pre book time!
	public int getTime(Message in) {
		return Integer.parseInt(io.getPayload(in).get("time")) - 900;
	}

}
