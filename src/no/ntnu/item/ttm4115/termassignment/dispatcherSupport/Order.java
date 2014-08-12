package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;

/*Enum type which indicates the order status*/
enum orderStatus {
   ISSUED,ONECONFIRMED,TWOCONFIRMED;
   } 

public class Order{

	private String userAlias;
	private orderStatus status;

	public Order (String _userAlias){
		this.userAlias = _userAlias;
		this.status = orderStatus.ISSUED;
	}
	
	public void setUserAlias(String _userAlias){
		this.userAlias=_userAlias;
	}
	public String getUserAlias(){
		return userAlias;
	}
	
	public void setSataus(orderStatus _status){
		this.status = _status;
	}
	public orderStatus getStatus(){
		return status;
	}
	
	public boolean update(){
		switch(status){
		case ISSUED:
			this.setSataus(orderStatus.ONECONFIRMED);
			return true;
		case ONECONFIRMED:
			this.setSataus(orderStatus.TWOCONFIRMED);
			return true;
		case TWOCONFIRMED:
			break;
		}
		return false;
	}
}
