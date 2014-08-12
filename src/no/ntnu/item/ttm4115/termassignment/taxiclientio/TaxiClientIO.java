package no.ntnu.item.ttm4115.termassignment.taxiclientio;

import no.ntnu.item.arctis.runtime.Block;

import java.util.HashMap;

import com.bitreactive.library.mqtt.mqtt.MQTT.Message;

import no.ntnu.item.ttm4115.termassignment.support.*;
import no.ntnu.item.ttm4115.simulation.routeplanner.Journey;

public class TaxiClientIO extends Block {

	public java.lang.String alias;
	private String userName;
	private String pickLocation;
	private static final String topicToDispatcher = "ntnu/item/ttm4115/group7/dispatcher/";
	private static final String topicToUserBase = "ntnu/item/ttm4115/group7/user/";
	public java.lang.String location;
	public java.util.HashMap<java.lang.String,java.lang.String> message;
	public java.lang.String order;
		
	/**
	public HashMap<String,String> getPayload( Message in) {
       return io.getPayload(in);
	}
    
	public String getValue() {
		return message.get("message");
	}
	public String getType(HashMap <String,String> data) {
		return  data.get("type");
	}*/
	
	public Message ondutyM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","onDuty");
	}
	public Message offdutyM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","offDuty");

	}
	public Message availableM(String loca) {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","available",loca);
	}
	public Message unavailableM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","unAvailable");		
	}
	public Message confirmM() {
		return io.createMessage(topicToDispatcher,alias,"dispatcher","confirm");		
	}
	public Message send(String in) {
		return io.createMessage(topicToUserBase+userName+"/",alias,userName,"send",in);			
	}
	public Message doneM() {
		return io.createMessage(topicToUserBase+userName+"/",alias,userName,"done");			
	}
	
	public Journey cj1() {
		return new Journey(alias,location,pickLocation);
		
	}
	public Journey cj2(String dest) {
		return new Journey(alias, pickLocation,dest);
	}
    /**
	public Message toUser() {
		return io.createMessage(topicToUserBase+order.substring(0,order.indexOf("#"))+"/",alias,order.substring(0,order.indexOf("#")),"order",alias);			
	}*/

	public String getMessage(Message in) {
		HashMap<String,String> messageBody = io.getPayload(in);
		if (messageBody.get("type").equals("reply")){
			return "Dispatcher->"+messageBody.get("message");
		}
		if (messageBody.get("type").equals("send")){
			return userName+"->"+messageBody.get("message");
		}
		if (messageBody.get("type").equals("order")){
			int index = messageBody.get("message").indexOf("#");
			userName = messageBody.get("message").substring(0, index);
			pickLocation = messageBody.get("message").substring(index+1);
			return "Receive order:"+messageBody.get("message");
		}
		return "Received a spam message!";
	}
	public Message genOrder(String mess) {
		if (mess.contains("Receive order:")){
			String receiver = mess.substring(mess.indexOf(":")+1,mess.indexOf("#"));
			return io.createMessage(topicToUserBase+receiver+"/",alias,receiver,"order",alias);
		}
		else{
			return new Message("Not  order!".getBytes(),"nullpointer/");
		}
		
	}
}
