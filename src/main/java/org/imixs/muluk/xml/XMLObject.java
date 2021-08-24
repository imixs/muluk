package org.imixs.muluk.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.imixs.muluk.MonitorService;

/**
 *
 * 
 * @author rsoika
 *
 */
@XmlRootElement(name = "object")
public class XMLObject implements java.io.Serializable {

	@XmlTransient
	private static final long serialVersionUID = 1L;
	private String type;
	private long interval; // in seconds

	private String target;
	
	private String pattern;
	private String status;
	private String lastStatus;
	private Date lastSuccess;
	private Date lastFailure;
	
	private XMLAuth auth; // optional
	
	public XMLObject() {
		super();
	}

	@XmlAttribute
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * returns the type. Default ist 'web'
	 * @return
	 */
	public String getType() {
		if (type==null || type.isEmpty()) {
			type="web";// default
		}
		return type;
	}

	/**
	 * Timer Interval in seconds
	 * 
	 * @return
	 */
	@XmlAttribute
	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}



	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getLastStatus() {
		if (lastStatus==null || lastStatus.isEmpty()) {
			lastStatus=MonitorService.STATUS_OK;
		}
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Date getLastSuccess() {
		return lastSuccess;
	}

	public void setLastSuccess(Date lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

	public Date getLastFailure() {
		return lastFailure;
	}

	public void setLastFailure(Date lastFailure) {
		this.lastFailure = lastFailure;
	}

	public XMLAuth getAuth() {
		return auth;
	}

	public void setAuth(XMLAuth auth) {
		this.auth = auth;
	}
	
	

}
