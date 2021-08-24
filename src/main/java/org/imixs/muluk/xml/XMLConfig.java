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

package org.imixs.muluk.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The XMLDataCollection represents a list of XMLItemCollections. This root
 * element is used by JAXB api
 * 
 * @author rsoika
 * @version 0.0.1
 */
@XmlRootElement(name = "config")
public class XMLConfig implements java.io.Serializable {

	@XmlTransient
	private static final long serialVersionUID = 1L;

	private XMLCluster cluster;
	private XMLMonitor monitor;
	private XMLMail mail;
	private long objectPings = 0;
	private long objectErrors = 0;
	private long clusterPings = 0;
	private long clusterErrors = 0;

	public XMLConfig() {
		super();
	}

	public XMLCluster getCluster() {
		return cluster;
	}

	public void setCluster(XMLCluster cluster) {
		this.cluster = cluster;
	}

	public XMLMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(XMLMonitor monitor) {
		this.monitor = monitor;
	}

	
	public XMLMail getMail() {
		return mail;
	}

	public void setMail(XMLMail mail) {
		this.mail = mail;
	}

	

	public long getObjectPings() {
		return objectPings;
	}

	public void setObjectPings(long objectPings) {
		this.objectPings = objectPings;
	}

	public long getObjectErrors() {
		return objectErrors;
	}

	public void setObjectErrors(long objectErrors) {
		this.objectErrors = objectErrors;
	}

	public long getClusterPings() {
		return clusterPings;
	}

	public void setClusterPings(long clusterPings) {
		this.clusterPings = clusterPings;
	}

	public long getClusterErrors() {
		return clusterErrors;
	}

	public void setClusterErrors(long clusterErrors) {
		this.clusterErrors = clusterErrors;
	}

	public void addObjectPing() {
		objectPings++;
	}

	public void addObjectErrors() {
		objectErrors++;
	}
	
	public void addClusterPing() {
		clusterPings++;
	}

	public void addClusterErrors() {
		clusterErrors++;
	}

}
