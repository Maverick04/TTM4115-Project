package no.ntnu.item.ttm4115.termassignment.dispatcherSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class distance {
	public static Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}
	public static String getResponse(String start, String end) throws IOException {
		    String url = "http://maps.googleapis.com/maps/api/distancematrix/xml?origins="+start+"&destinations="+end+"&sensor=false";
		    URL oracle = new URL(url.replaceAll(" ", "%20"));
	        URLConnection yc = oracle.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    yc.getInputStream()));
	        String inputLine;
	        String sum = "";
	        while ((inputLine = in.readLine()) != null) 
	            sum += inputLine;
	        in.close();
	        return sum;
	    }
	   
}
