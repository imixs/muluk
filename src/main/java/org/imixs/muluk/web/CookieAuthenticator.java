package org.imixs.muluk.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Client request Filter for Cookies.
 * <p>
 * This ClientRequestFilter sets a cookie in each request
 * 
 * @author rsoika
 *
 */
public class CookieAuthenticator implements RequestFilter {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(CookieAuthenticator.class.getName());

	private String cookieString;

	public CookieAuthenticator(String cookieString) {
		super();
		this.cookieString = cookieString;
	}

	/**
	 * add cookie
	 */
	@Override
	public void filter(HttpURLConnection connection) throws IOException {
		List<Object> cookies = new ArrayList<>();
		cookies.add(this.cookieString);
		
		connection.setRequestProperty("Cookie", cookieString); 
	
	}


}
