package org.imixs.muluk.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Defines a mail configuration to send smtp mails
 * 
 * @author rsoika
 *
 */
@XmlRootElement(name = "mail")
public class XMLMail implements java.io.Serializable {

	@XmlTransient
	private static final long serialVersionUID = 1L;
	private int  port;
	private String user;
	private String password;
	private String host;
	private String from;
	private String recipients;

	public XMLMail() {
		super();
	}

	@XmlAttribute
	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		if (port<=0) {
			port=465;
		}
		return port;
	}
	
	@XmlAttribute
	public void setFrom(String from) {
		this.from = from;
	}


	public String getFrom() {
		return from;
	}

	@XmlAttribute
	public void setHost(String host) {
		this.host = host;
	}


	public String getHost() {
		return host;
	}

	@XmlAttribute
	public void setUser(String user) {
		this.user = user;
	}

	
	public String getUser() {
		return user;
	}

	@XmlAttribute
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Comma separated list of recipients
	 * @return
	 */
	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	
	

}
