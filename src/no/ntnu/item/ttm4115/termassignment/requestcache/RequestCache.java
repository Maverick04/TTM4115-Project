package no.ntnu.item.ttm4115.termassignment.requestcache;

import no.ntnu.item.arctis.runtime.Block;
import com.bitreactive.library.mqtt.mqtt.MQTT.Message;
public class RequestCache extends Block {

	public com.bitreactive.library.mqtt.mqtt.MQTT.Message message;

	public String getRequestType(Message in) {
		return "f";
	}

	public void init() {
	}

}
