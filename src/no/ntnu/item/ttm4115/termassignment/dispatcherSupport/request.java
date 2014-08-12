package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;

public class request {

	private String location;
	private long time;
	private String type;
	
    public void setLocation(String _location){
    	this.location = _location;
    }
    public String getLocation(){
    	return location;
    }
    
    public void setTime(long _time){
    	this.time = _time;
    }
    public long getTime(){
    	return time;
    }
    
    public void setType(String _type){
    	this.type = _type;
    }

    public String getType(){
    	return type;
    }
    
    public request(String _location,long _time, String _type){
    	this.location = _location;
    	this.time = _time;
    	this.type = _type;
    }
}
