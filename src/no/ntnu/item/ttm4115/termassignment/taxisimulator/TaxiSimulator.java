package no.ntnu.item.ttm4115.termassignment.taxisimulator;

import no.ntnu.item.arctis.runtime.Block;
import no.ntnu.item.ttm4115.simulation.routeplanner.Journey;

public class TaxiSimulator extends Block {
	public java.lang.String alias;
	public String le() {
		return "Local Error in Routeplaner!";
	}
	public static String getAlias(Journey in ){
		return in.taxiAlias;
	}
}
