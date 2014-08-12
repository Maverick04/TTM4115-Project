package no.ntnu.item.ttm4115.termassignment.userclientgui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import no.ntnu.item.arctis.runtime.Block;
import no.ntnu.item.ttm4115.termassignment.clientframe.ClientFrame;
import no.ntnu.item.ttm4115.termassignment.clientframe.LogTable;
import no.ntnu.item.ttm4115.termassignment.clientframe.PlaceholderTextField;

/**
 * Block for the UserClientGUI. By default, the GUI contains a text field
 * where the user can give his location, and two buttons.
 * Students are encouraged to make changes to this class
 * if they want to add features for extra credit.
 */
public class UserClientGUI extends Block {

	/**
	 * Event that indicates that the user requests a taxi.
	 * This request is accompanied by the current location of the user.
	 * The current location is entered in {@link #address} and passed via {@link #taxiRequest}
	 */
	public final String REQUEST = "REQUEST";
	/**
	 * Event that indicates that the user confirm his order.
	 */
	public final String CONFIRM = "CONFIRM";
	/**
	 * Event that indicates that the user is no longer
	 * interested in a taxi. Any standing requests must be cancelled.
	 */
	public final String CANCEL = "CANCEL";
	/**
	 * Event that indicates that the user sends a message to the taxi.
	 */
	public final String SEND = "SEND";
	
	
	/** Swing GUI */
	private ClientFrame frame;
	/** Panel which contains the text field and the buttons */
	private JPanel controls;
	/* The buttons */
	private JButton request, confirm, cancel, send;
	
	/**ComboBox which type of taxi and request will be used */
	private String type;
	/**Spinner what time will be used when sending a  pre-booking request*/
	private JSpinner time;
	/** Input field where the user enters his location */
	private JTextField address;
	/** Input field where the user enters his message to the taxi */
	private JTextField dialog;
	/** Label that indicates the current state */
	//private JLabel stateLabel;
	/**
	 * The log
	 * use {@link LogTable#addLogEntry(no.ntnu.item.ttm4115.termassignment.clientframe.LogTable.Direction, String)}
	 */
	private LogTable messages;
	
	/** Object for retrieving the requested address */
	private Object taxiRequest = new Object(){
		public String toString() {
			String requestTime;
			if (type.startsWith("Normal")){
				requestTime = String.valueOf((Calendar.getInstance().getTimeInMillis()/1000));
			}
			else {
				Date value = ((SpinnerDateModel)time.getModel()).getDate();
				requestTime = String.valueOf(value.getTime()/1000);
			}
			return type+"#"+requestTime+"#"+address.getText();
		}
	};
	
	private Object dialogText = new Object(){
		public String toString(){
			return dialog.getText();
		}
	};
	/** Need to set request type, taxi type, time*/
	
	/**
	 * Initialize and show the GUI. Should only be called *once* per user
	 * @param alias	Alias for this user
	 */
	//@SuppressWarnings("unchecked")
	public void show(String alias) {
		frame = new ClientFrame(this, alias);
		controls = new JPanel(new GridBagLayout());
	//	stateLabel = new JLabel("<html>Location: <i>unknown");
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		
		address = new PlaceholderTextField("Type address");
		c.gridy = 0;
		controls.add(address, c);
		
		c.gridwidth = 2;
		dialog = new PlaceholderTextField("Type message");
		c.gridy = 1;
		controls.add(dialog, c);
		
		/**Get the taxi and request type*/
	    String [] typeContent = {"Choose..","NormalMaxi","NormalCargo","PreMaxi","PreCargo"};
	    JComboBox<String> typeSelector = new JComboBox<String>(typeContent);
	    typeSelector.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == ItemEvent.SELECTED){
				    type=e.getItem().toString();
				}
			}
	    });
	    
		c.gridy = 2;
		c.gridwidth = 1;
		controls.add(typeSelector,c);
	
		
		/**Get the chosen time is the user book a taxi in advance*/
		Calendar init = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		end.add(Calendar.DATE, 1);
		start.add(Calendar.MINUTE, -1);
		//Remember to use different calendar!!! start should < init!
		SpinnerDateModel sm = new SpinnerDateModel(init.getTime(),start.getTime(),end.getTime(),Calendar.MINUTE);
		time = new JSpinner(sm);
		c.gridwidth =1;
		controls.add(time,c);
		
		
		request = frame.createEventButton(REQUEST, "Request taxi", taxiRequest);
		c.gridy = 3;
		c.gridx = 0;
		controls.add(request, c);
		
		confirm = frame.createEventButton(CONFIRM, "Confirm taxi");
		c.gridx = 1;
		controls.add(confirm, c);
		
		cancel = frame.createEventButton(CANCEL, "Cancel");
		c.gridy = 4;
		c.gridx = 0;
		controls.add(cancel, c);
		
		send = frame.createEventButton(SEND, "Send message",dialogText);
		c.gridx = 1;
		controls.add(send, c);
			

		frame.add(controls, BorderLayout.SOUTH);
		//frame.add(stateLabel, BorderLayout.NORTH);
		messages = frame.addLogTable();
		
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Close the GUI. Once closed, it cannot be reliably re-opened.
	 */
	public void close() {
		if (frame != null) {
			frame.dispose();
		}
	}

	/**
	 * Add an incoming message to the log
	 * @param message	The message received
	 */
	public void displayMessage(String message) {
		messages.addLogEntry(LogTable.Direction.IN, message);
	}

	/**
	 * Display a location in the GUI
	 * @param location	String representation of the location
	 
	public void displayLocation(String location) {
		//stateLabel.setText("<html>Location: <b>"+location.replace("<", "&lt;").replace("&", "&amp;"));
	}
	*/
    /** Kill these two functions in polish process!
	public void tripDone() {
		messages.addLogEntry(LogTable.Direction.IN, "Reach the destination");
	}

	public void displayOrder(String order) {
		messages.addLogEntry(LogTable.Direction.IN, order);
	}*/
}
