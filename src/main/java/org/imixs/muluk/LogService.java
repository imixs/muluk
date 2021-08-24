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

package org.imixs.muluk;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.imixs.muluk.xml.XMLMail;

/**
 * The LogService provides methods to print messages to the server log file and
 * optional write messages into a message buffer which can be used to send log
 * entries via mail.
 * 
 * @author rsoika
 * @version 1.0
 */
@Singleton
public class LogService {

	private static Logger logger = Logger.getLogger(LogService.class.getName());

	StringBuffer log;

	/**
	 * This method start the system setup during deployment
	 * 
	 * @throws AccessDeniedException
	 */
	@PostConstruct
	public void reset() {
		log = new StringBuffer();
	}

	/**
	 * Writes a message to the server console and into the message log.
	 * 
	 * @return
	 */
	public void info(String message) {
		logger.info(message);
		log.append(message + "\n");
	}

	public void warning(String message) {
		logger.warning(message);
		log.append(message + "\n");
	}

	public void severe(String message) {
		logger.severe(message);
		log.append(message + "\n");
	}

	/**
	 * Returns the current log messages
	 * 
	 * @return
	 */
	public String getLog() {
		return log.toString();

	}

	/**
	 * Returns the current log messages and reset the log.
	 * 
	 * @return
	 */
	public String flush() {
		String result = log.toString();
		reset();
		return result;
	}

	
	/**
	 * Sends the message log via email.
	 * @throws MessagingException 
	 * 
	 */
	public void sendMessageLog(String subject,XMLMail config) throws MessagingException {

		if (config==null || config.getHost()==null || config.getHost().isEmpty()) {
			return;
		}
        // Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtps.port", config.getPort()); 
    	props.put("mail.smtps.starttls.enable", "true");
    	
    	if (config.getPassword()!=null && !config.getPassword().isEmpty()) {
    	props.put("mail.smtps.auth", "true");
    	}

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(config.getFrom()));
        String[] receipients = config.getRecipients().split(",");
        List<InternetAddress> receipientAdresses=new ArrayList<InternetAddress>();
        for (String adr: receipients) {
        	receipientAdresses.add(new InternetAddress(adr));
        }
        
        InternetAddress[] adrArray = new InternetAddress[receipientAdresses.size()];
        receipientAdresses.toArray(adrArray); // fill the array
        msg.setRecipients(Message.RecipientType.TO, adrArray);
     
        msg.setSubject(subject);
        msg.setContent(getLog(),"text/text");
        
       // Create a transport.
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            logger.info("...sending email message...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(config.getHost(), config.getUser(), config.getPassword());
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            
        }
        catch (Exception ex) {
            logger.severe("Failed to send Mail: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    
	}
	
}
