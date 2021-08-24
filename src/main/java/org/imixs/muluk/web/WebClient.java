/*  
 *  Imixs-Workflow 
 *  
 *  Copyright (C) 2001-2020 Imixs Software Solutions GmbH,  
 *  http://www.imixs.com
 *  
 *  This program is free software; you can redistribute it and/or 
 *  modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You can receive a copy of the GNU General Public
 *  License at http://www.gnu.org/licenses/gpl.html
 *  
 *  Project: 
 *      https://www.imixs.org
 *      https://github.com/imixs/imixs-workflow
 *  
 *  Contributors:  
 *      Imixs Software Solutions GmbH - Project Management
 *      Ralph Soika - Software Developer
 */

package org.imixs.muluk.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.imixs.muluk.xml.XMLAuth;

/**
 * The WebClient is a helper class for a http communication.
 * 
 * @author Ralph Soika
 */
public class WebClient {

	private Map<String, String> requestProperties = null;
	private String encoding = "UTF-8";
	private int iLastHTTPResult = 0;
	private XMLAuth xmlAuth;

	private final static Logger logger = Logger.getLogger(WebClient.class.getName());

	public WebClient() {
		super();
	}

	/**
	 * Creates a new Web Client with an optional XMLAuth Object
	 * 
	 * @param xmlAuth
	 */
	public WebClient(XMLAuth xmlAuth) {
		super();
		this.xmlAuth = xmlAuth;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String aEncoding) {
		encoding = aEncoding;
	}

	/**
	 * Set a single header request property
	 */
	public void setRequestProperty(String key, String value) {
		if (requestProperties == null) {
			requestProperties = new HashMap<String, String>();
		}
		requestProperties.put(key, value);
	}

	/**
	 * This method returns the last HTTP Result
	 * 
	 * @return
	 */
	public int getLastHTTPResult() {
		return iLastHTTPResult;
	}

	/**
	 * Gets the content of a GET request from a Rest Service URI Endpoint. I case of
	 * an error the method throws a RestAPIException.
	 * 
	 * @param uri - Rest Endpoint RUI
	 * @return - content or null if no content is available.
	 * @throws IOException
	 */
	public String get(String uri) throws IOException {

		HttpURLConnection urlConnection = (HttpURLConnection) new URL(uri).openConnection();

		// optional default is GET
		urlConnection.setRequestMethod("GET");

		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setAllowUserInteraction(false);

		if (requestProperties != null) {
			for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if (xmlAuth != null) {
			RequestFilter authFilter = null;
			if ("BASIC".equalsIgnoreCase(xmlAuth.getType())) {
				authFilter = new BasicAuthenticator(xmlAuth.getUser(), xmlAuth.getPassword());
			}
			if ("FORM".equalsIgnoreCase(xmlAuth.getType())) {
				// compute base url
				String baseURL=uri;
				//String path=urlConnection.getURL().getPath();
				String host=urlConnection.getURL().getHost();
				baseURL=baseURL.substring(0,baseURL.indexOf(host)+host.length());
				
				logger.info("form based auth - base URI="+baseURL);
				
				authFilter = new FormAuthenticator(baseURL,xmlAuth.getUser(), xmlAuth.getPassword());
			}
			if ("JWT".equalsIgnoreCase(xmlAuth.getType())) {
				authFilter = new JWTAuthenticator( xmlAuth.getPassword());
			}

			if (authFilter != null) {
				authFilter.filter(urlConnection);
			}
		}

		iLastHTTPResult = urlConnection.getResponseCode();
		logger.finest("......Sending 'GET' request to URL : " + uri);
		logger.finest("......Response Code : " + iLastHTTPResult);
		// read response if response was successful
		if (iLastHTTPResult >= 200 && iLastHTTPResult <= 299) {
			return readResponse(urlConnection);
		} else {
			String error = "Error " + iLastHTTPResult + " - failed GET request from '" + uri + "'";
			logger.warning(error);
			throw new IOException(error);
		}

	}

	/**
	 * Reads the response from a http request.
	 * 
	 * @param urlConnection
	 * @throws IOException
	 */
	private String readResponse(URLConnection urlConnection) throws IOException {
		// get content of result
		logger.finest("......readResponse....");
		StringWriter writer = new StringWriter();
		BufferedReader in = null;
		try {
			// test if content encoding is provided
			String sContentEncoding = urlConnection.getContentEncoding();
			if (sContentEncoding == null || sContentEncoding.isEmpty()) {
				// no so lets see if the client has defined an encoding..
				if (encoding != null && !encoding.isEmpty())
					sContentEncoding = encoding;
			}

			// if an encoding is provided read stream with encoding.....
			if (sContentEncoding != null && !sContentEncoding.isEmpty())
				in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), sContentEncoding));
			else
				in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				logger.finest("......" + inputLine);
				writer.write(inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}

		return writer.toString();

	}

}
