package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;

public class Taxi {
    
    	private String location;
    	private long availbleTimeStamp;
    
    	public Taxi (String _location, long _availableTimeStamp){
    		this.location = _location;
    		this.availbleTimeStamp = _availableTimeStamp;
    	}
  	
    	public void setLocation(String _location){
    		this.location = _location;
    	}
    	
    	public String getLocation(){
    		return location;
    	}
    	
    	public void setAvailableTimeStamp(long _availbleTimeStamp){
    		this.availbleTimeStamp = _availbleTimeStamp;
    	}
    	
    	public long getAvailableTimeStamp(){
    		return availbleTimeStamp;
    	}
    	
    }
