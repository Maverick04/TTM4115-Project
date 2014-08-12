package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;
import java.util.*;
public class Compare implements Comparator<re> {
	
	public int compare(re arg0, re arg1){
		if (arg0.getDistance() < arg1.getDistance()){
			return -1;
		}
		
		if (arg0.getDistance() == arg1.getDistance()){
			if(arg0.getDuration() ==arg1.getDuration()){
				return 0;
			}
			if(arg0.getDuration() > arg1.getDuration()){
				return -1;	
			}
			if(arg0.getDuration() < arg1.getDuration()){
					return 1;	
		    }
		}
		return 1;
	}
}
