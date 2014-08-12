package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;

public class re{
	private String name;
	private int distance;
	private int duration;
	public re(String _name, int _distance, int _duration){
		this.name = _name;
		this.distance = _distance;
		this.duration = _duration;
	}
	public String getName(){
		return name;
	}
	public void setName(String _name){
		this.name = _name;
	}
	
	public int getDistance(){
		return distance;
	}
	public void setDistance(int _distance){
		this.distance = _distance;
	}
	
	public int getDuration(){
		return duration;
	}
	public void setDuration(int _duration){
		this.duration = _duration;
	}
	
}