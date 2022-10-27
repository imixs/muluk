package org.imixs.muluk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.imixs.muluk.xml.XMLCluster;
import org.imixs.muluk.xml.XMLConfig;
import org.imixs.muluk.xml.XMLMonitor;
import org.imixs.muluk.xml.XMLObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class test some typical regular expressions
 * 
 * 
 * @author rsoika
 */
public class TestRegex {

	/**
	 * test 
	 */
	@Test
	public void testSimple() {
		String result = " Administrator ";

		Pattern p = Pattern.compile("Administrator"); // the pattern to search for
		Matcher m = p.matcher(result);
		// now try to find at least one match
		if (m.find()) {
			System.out.println("......OK");
		} else {
			Assert.fail();
		}
	}

	
	/**
	 * test "..."
	 */
	@Test
	public void testQuotation() {
		String result = "{\"engine.version\":\"5.2.15\",\"model.versions\":9,\"model.groups\":11,\"database.status\":\"ok\",\"index.status\":\"ok\"}";

		// includes whitespaces
		String regex="\"index.status\":(\\s*)\"ok\"";
		
		Pattern p = Pattern.compile(regex); // the pattern to search for
		Matcher m = p.matcher(result);
		// now try to find at least one match
		if (m.find()) {
			System.out.println("......OK");
		} else {
			Assert.fail();
		}
	}
	
	
	   @Test
	    public void testQuotation2() {
	        String result = "{\"status\":\"UP\",\"checks\":[{\"name\":\"imixs-workflow\",\"status\":\"UP\",\"data\":{\"engine.version\":\"5.2.16-SNAPSHOT\",\"model.groups\":12,\"model.versions\":8,\"index.status\":\"ok\",\"database.status\":\"ok\"}},{\"name\":\"ready-deployment.office-krieger-app.war\",\"status\":\"UP\"}";

	        // includes whitespaces
	        String regex=".*(?=.*(\"index.status\":(\\s*)\"ok\"))(?=.*(\"database.status\":(\\s*)\"ok\")).*";
	        
	        Pattern p = Pattern.compile(regex); // the pattern to search for
	        Matcher m = p.matcher(result);
	        // now try to find at least one match
	        if (m.find()) {
	            System.out.println("......OK");
	        } else {
	            Assert.fail();
	        }
	    }

}
