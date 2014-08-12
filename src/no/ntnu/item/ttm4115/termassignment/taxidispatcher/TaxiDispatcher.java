package no.ntnu.item.ttm4115.termassignment.taxidispatcher;

import no.ntnu.item.arctis.runtime.Block;

import com.bitreactive.library.mqtt.mqtt.MQTT.Message;

import no.ntnu.item.ttm4115.termassignment.dispatcherSupport.*;
import no.ntnu.item.ttm4115.termassignment.support.io;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TaxiDispatcher extends Block {
	private List<String> onDutyTaxi = new ArrayList<String>();
    private HashMap<String,Taxi> TaxiQ  = new HashMap<String,Taxi>();
    private HashMap<String,Order> OrderQ = new HashMap<String,Order>();
    private HashMap<String,request> WaitQ = new HashMap<String,request>();
    
	private String userTopicBase = "ntnu/item/ttm4115/group7/user/";
	private String taxiTopicBase = "ntnu/item/ttm4115/group7/taxi/";
	private static Logger log = Logger.getLogger("Dispatcher");
	
	/**
	 * Wrap different fields of a message return a list!
	 * @param messageBody The message sent from taxi
	 * @param topicBase The topicBase for the receiver
	 * @return List which contains all the basic info for a message!
	 * @author Tong Yang <tongy@stud.ntnu.no>
	 * */
	private List<String> messOverhead(HashMap<String,String> messageBody,String topicBase){
		String topic = topicBase+messageBody.get("sender")+"/";
    	String sender = "dispatcher";
    	String receiver = messageBody.get("sender");
    	String type = "reply";
    	List<String> head = new ArrayList<String>();
    	head.add(topic);
    	head.add(sender);
    	head.add(receiver);
    	head.add(type);
		return head;
	}
	
	/**
	 * Receive an onDuty message from taxi and register it on the dispatcher.
	 * @param messageBody The message sent from taxi
	 * @return Message tells the taxi whether it registers succeed or fail.
	 * @author Tong Yang <tongy@stud.ntnu.no>
	 * */
    private Message onDuty(HashMap<String,String> messageBody){
    	List<String> head = messOverhead(messageBody,taxiTopicBase);
        if (onDutyTaxi.add(messageBody.get("sender"))){
        	log.info("Register Successful! -> " +head.get(2));
        	return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"Register Successful!");
        }    	
        else{
        	log.info("Register Failed! -> " +head.get(2));
        	return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"Register Failed!");
        }
        	
    }
    /**
	 * Receive an offDuty message from taxi and unregister it on the dispatcher.
	 * @param messageBody The message sent from taxi
	 * @return Message tells the taxi whether it unregisters succeed or fail.
	 * @author Tong Yang <tongy@stud.ntnu.no>
	 * */
    private Message offDuty(HashMap<String,String> messageBody){
        List<String> head = messOverhead(messageBody,taxiTopicBase);
        if (onDutyTaxi.remove(messageBody.get("sender"))){
        	log.info("UnRegister Successful! -> " + head.get(2));
        	return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"UnRegister Successful!");
        }    	
        else{
        	log.info("UnRegister Failed! -> " + head.get(2));
        	return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"UnRegister Failed!");
        }
        	
    }
    /**
   	 * Receive an available message from taxi and put this taxi into TaxiQ.
   	 * @param messageBody The message sent from taxi
   	 * @return Message tells the taxi whether it comes into TaxiQ succeed or fail.
   	 * @author Tong Yang <tongy@stud.ntnu.no>
   	 * */
    private Message available(HashMap<String,String> messageBody){
    	List<String> head = messOverhead(messageBody,taxiTopicBase);
    	long availableTime = Calendar.getInstance().getTimeInMillis()/1000L;
    	if (onDutyTaxi.contains(messageBody.get("sender"))){
    		Taxi taxi = new Taxi(messageBody.get("location"),availableTime);
    		TaxiQ.put(messageBody.get("sender"), taxi);
    		log.info("Available Successful! -> " + head.get(2));
    		return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"Available Successful!");
    	}
    	else{
    		log.info("Not in the onDuty List! s-> " + head.get(2));
    		return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"Not in the onDuty List!");
    	} 	
    }
    /**
   	 * Receive an Unavailable message from taxi and put this taxi into TaxiQ. 
   	 * @param messageBody The message sent from taxi
   	 * @return Message tells the taxi whether it drops from TaxiQ succeed or fail.
   	 * @author Tong Yang <tongy@stud.ntnu.no>
   	 * */
    private Message unAvailable(HashMap<String,String> messageBody){
    	List<String> head = messOverhead(messageBody,taxiTopicBase);
    	if (TaxiQ.remove(messageBody.get("sender")) != null){
    		
    		log.info("UnAvailable Successful! -> " + head.get(2));
    		return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"UnAvailable Successful!");
    	}
    	else{
    		log.info("Not in the TaxiQ List! -> " + head.get(2));
    		return io.createMessage(head.get(0),head.get(1),head.get(2),head.get(3),"Not in the TaxiQ List!");
    	} 	
    }
    
    /**
   	 * Receive a request from user, Handle this request. If it's prebook request, put it in to waiting queue.
   	 * Else handle this request, if a taxi is available, send an order, else put it into waiting queue!
   	 * @param messageBody The message sent from taxi
   	 * @return Message tells the taxi whether it drops from TaxiQ succeed or fail.
   	 * @author Tong Yang <tongy@stud.ntnu.no>
   	 * */
    
    private Message request(HashMap<String,String> messageBody) {
    	Set<String> availTaxi = new HashSet<String>();
    	
    	String receiver = messageBody.get("sender");
    	String topicUser = userTopicBase+receiver+"/";
    	String sender = "dispatcher";
    	String startAddress = messageBody.get("location");
    	if (messageBody.get("cartype").startsWith("Pre")){
    		request r = new request(messageBody.get("location"),(long)Integer.parseInt(messageBody.get("time")),messageBody.get("cartype"));
    		WaitQ.put(receiver, r);
    		log.info("Received a prebook order:" + receiver);
   			return io.createMessage(topicUser,sender,receiver,"reply","Your order is received!");
    	}
    	else{
    		String prefix = messageBody.get("cartype").contains("Maxi") ? "Maxi" : "Cargo";
    		for(String taxiName : TaxiQ.keySet()){
        		if (taxiName.startsWith(prefix)){
        			availTaxi.add(taxiName);
        		}
        	}
    		if (availTaxi.size() >= 1){
        		String dTaxi =  availTaxi.size() == 1 ? availTaxi.toArray()[0].toString() : findTaxi(availTaxi,startAddress);
       			Order  nOrder = new Order(messageBody.get("sender"));
       			OrderQ.put(dTaxi, nOrder);
       			log.info("Assign Taxi:" + dTaxi + " To User: " + receiver);
       			return io.createMessage(taxiTopicBase+dTaxi+"/",sender,dTaxi,"order",receiver+"#"+startAddress);
       		}
        	else{
        		request r = new request(messageBody.get("location"),(long)Integer.parseInt(messageBody.get("time")),messageBody.get("cartype"));
       			WaitQ.put(receiver, r);
       			log.info(WaitQ.size()+" clients are in the waiting queue");
       			return io.createMessage(topicUser,sender,receiver,"reply",WaitQ.size()+" clients are in front of you!");
        		}
    	}
    }
    /**
   	 * Find the most suitable Taxi for the User
   	 * @param availTaxi The set for available taxis
   	 * @param startAddr The start address of the user
   	 * @return taxiName The name of most suitable taxi
   	 * @author Tong Yang <tongy@stud.ntnu.no>
   	 * */
    private String findTaxi(Set<String> availTaxi ,String startAddr ) {
    	String dest = "";
    	for (String taxi : availTaxi){
    		dest = dest + " | " + TaxiQ.get(taxi).getLocation();
    	}
    	dest = dest.substring(3);
       
		Document dist = null;
		try {
			dist = distance.loadXMLFromString(distance.getResponse(startAddr, dest));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dist != null){
			NodeList list = dist.getElementsByTagName("element");
			List<re> result = new ArrayList<re>();
			int Count = 0;
			for (String taxi : availTaxi ){
				if(list.item(Count).getChildNodes().item(1).getTextContent().equals("OK")){
					int distance = Integer.parseInt(list.item(Count).getChildNodes().item(5).getChildNodes().item(1).getTextContent());
		    		int duration = (int)(Calendar.getInstance().getTimeInMillis()/1000 - TaxiQ.get(taxi).getAvailableTimeStamp());
		    		result.add(new re(taxi,distance,duration));
				}
				Count ++;
			}
	        Compare c = new Compare();
	        Collections.sort(result,c);
	    	return result.get(0).getName();
		}
		else{
			return null;
		}
    }
    /**
     * Update the order state, 
     * @param String which indicates the name of taxi or user
     * @return void Update the order's status
     * @author Tong Yang <tongy@stud.ntnu.no>
     * */   
    private boolean updateOrder(String key){
    	if(key.startsWith("user")){
    	 for(String taxi : OrderQ.keySet()){
    		 if (OrderQ.get(taxi).getUserAlias().equals(key)){
    			 if (OrderQ.get(taxi).update()){
    			 return true;
    			 }
    		 }
    	 }
    	}
    	else{
    		if (OrderQ.get(key).update()){
    		return true;
    		}
    	}
    	return false;
    }
    /**
     * Confirm the order(from user and taxi side both!), 
     * @param Message which comes from taxi or user
     * @return Message which indicate update success or not
     * @author Tong Yang <tongy@stud.ntnu.no>
     * */  
    private Message confirm(HashMap<String,String> messageBody){
    	String topic = "";
    	if (messageBody.get("sender").startsWith("user")){
    		topic = userTopicBase+messageBody.get("sender")+"/";
    	}
    	else{
    		topic = taxiTopicBase+messageBody.get("sender")+"/";
    	}
    	String sender = "dispatcher";
    	String receiver = messageBody.get("sender");
    	String type = "reply";
         if (updateOrder(receiver)){
        	log.info("Order Confirmed! -> " + receiver);
         	return io.createMessage(topic,sender,receiver,type,"Order Confirmed!");
         }    	
         else{
        	 log.info("Order Confirm Failed! -> " + receiver);
        	 return io.createMessage(topic,sender,receiver,type,"Order Confirm Failed!");
         }
    }
    /**
     * Cancel the order from the user side 
     * @param Message which comes from the ser
     * @return Message which indicate cancel success or not
     * @author Tong Yang <tongy@stud.ntnu.no>
     * */  
    private Message cancel(HashMap<String,String> messageBody){
    	String topic = userTopicBase+messageBody.get("sender")+"/";
    	String sender = "dispatcher";
    	String receiver = messageBody.get("sender");
    	String type = "reply";
    	for(String taxi :OrderQ.keySet()){
    		if (OrderQ.get(taxi).getUserAlias().equals(receiver)){
    		    if (OrderQ.remove(taxi) != null){
    		    	log.info("Order Canceled! -> " + receiver);
    		    	return io.createMessage(topic,sender,receiver,type,"Order Canceled!");
    		    }
    		}
    		else{
    			log.info("Order Cancel Failed! -> " + receiver);
    			return io.createMessage(topic,sender,receiver,type,"Order Cancel Failed!");
    		}
    	}
    	log.info("Order Cancel Failed! -> " + receiver);
    	return io.createMessage(topic,sender,receiver,type,"Order Cancel Failed!");
    }
	
    /**
     * Periodical action which searches the WaitQ, if there are waiting requests, try to reDispatch them!
     * @param Message which comes from the ser
     * @return Message which indicate cancel success or not
     * @author Tong Yang <tongy@stud.ntnu.no>
     * */  
	public Message reDispatch() {
	    if(WaitQ.size()==0){
	    	return new Message("Nothing to do".getBytes(),"nullpointer/");
	    }
	    else {
	    	int diff = 0;
	    	String name = null;
	    	long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
	    	for(String r : WaitQ.keySet()){
	    		int p = (int)(currentTime - WaitQ.get(r).getTime() + 600);
	    		if ( p > diff){
	    			diff = p;
	    			name = r;
	    		}
	    	}
	    	if (name != null){
	    		String prefix = WaitQ.get(name).getType().contains("Maxi") ? "Maxi" : "Cargo";
	    		Set<String> availTaxi = new HashSet<String>();  	
	        	String sender = "dispatcher";
	        	String startAddress = WaitQ.get(name).getLocation();
	    		
	    		for(String taxiName : TaxiQ.keySet()){
	        		if (taxiName.startsWith(prefix)){
	        			availTaxi.add(taxiName);
	        		}
	        	}
	    		if (availTaxi.size() >= 1){
	        		String dTaxi =  availTaxi.size() == 1 ? availTaxi.toArray()[0].toString() : findTaxi(availTaxi,startAddress);
	       			Order  nOrder = new Order(name);
	       			OrderQ.put(dTaxi, nOrder);
	       			WaitQ.remove(name);
	       			log.info("(reDispatch)Assign Taxi:" + dTaxi + " To User: " + name);
	       			return io.createMessage(taxiTopicBase+dTaxi+"/",sender,dTaxi,"order",name+"#"+startAddress);
	       		}
	        	else{
	        		return new Message("No Available Taxi".getBytes(),"nullpointer/");
	        		}	
	    	}
	    	else{
	    		return  new Message("No Suitable Request!".getBytes(),"nullpointer/");
	    	}
	    	
	    }
	}
	
	/**
     * Call the function based on different input!
     * @param Message coming message
     * @return Message reply to the sender, if fails to call return nonsense!
     * @author Tong Yang <tongy@stud.ntnu.no>
     * */  
	public Message Handler(Message message) {
		
		HashMap<String,String> messageBody = io.getPayload(message);
		String type = messageBody.get("type");
		try{
			Method  m = this.getClass().getDeclaredMethod(type, new Class[]{HashMap.class});
		    return (Message) m.invoke(this,new Object[]{messageBody});
		}
		catch(Exception x){
			x.printStackTrace();
		}
        log.info("Call Failed! " + type);
	    return new Message("Call Failed!".getBytes(),"nullpointer/");
	}
}
