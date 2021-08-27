/*******************************************************************************
 *  Imixs Workflow Technology
 *  Copyright (C) 2003, 2008 Imixs Software Solutions GmbH,  
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
 *  Contributors:  
 *  	Imixs Software Solutions GmbH - initial API and implementation
 *  	Ralph Soika
 *  
 *******************************************************************************/
package org.imixs.muluk;

import java.io.Serializable;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.imixs.muluk.xml.XMLConfig;
import org.imixs.muluk.xml.XMLObject;

/**
 * This backing bean provides front end data about the MontorService.
 * 
 * @author rsoika
 * @version 1.0
 */
@Named
@SessionScoped
public class MonitorController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(MonitorController.class.getName());

	@Inject
	MonitorService monitorService;

	public MonitorController() {
		super();

	}

	/**
	 * This method start the system setup during deployment
	 * 
	 * @throws AccessDeniedException
	 */
	@PostConstruct
	public void init() {
		logger.info("...started");

		logger.info("app start=" + monitorService.getStarted());

	}

	public Date getStarted() {
		return monitorService.getStarted();
	}

	/**
	 * Returns the object availiblity
	 * @return
	 */
	public double getAvailability() {
		double pings = getConfig().getObjectPings();
		double errors = getConfig().getObjectErrors();
		double total = pings + errors;
		double result = 0;
		if (total > 0) {
			result = pings / total * 100;
		}
		return result;
	}

	/**
	 * Returns the cluster availability
	 */
	public double getClusterAvailability() {
		double pings = getConfig().getClusterPings();
		double errors = getConfig().getClusterErrors();
		double total = pings + errors;
		double result = 0;
		if (total > 0) {
			result = pings / total * 100;
		}
		return result;
	}

	
	/**
	 * Returns the objects.
	 * 
	 * @return
	 */
	public XMLConfig getConfig() {
		return monitorService.getConfig();
	}
	
	public String getName() {
	    return monitorService.getConfig().getCluster().getName();
	}

	/**
	 * Returns the objects.
	 * 
	 * @return
	 */
	public XMLObject[] getObjects() {
		return monitorService.getConfig().getMonitor().getObject();
	}

	public int getObjectsTotal() {
		return monitorService.getConfig().getMonitor().getObject().length;
	}

	public int getObjectsUp() {
		int result = 0;
		XMLObject[] objects = monitorService.getConfig().getMonitor().getObject();
		for (XMLObject obj : objects) {
			if ("OK".equals(obj.getStatus())) {
				result++;
			}
		}
		return result;
	}

	public String getUptime() {

		long different = System.currentTimeMillis() - monitorService.getStarted().getTime();
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		String uptime = "";

		if (elapsedDays > 0) {
			uptime = uptime + elapsedDays + " days, ";
		}
		if (elapsedHours > 0) {
			uptime = uptime + elapsedHours + " hours, ";
		}

		if (elapsedMinutes > 0) {
			uptime = uptime + elapsedMinutes + " minutes, ";
		}

		uptime = uptime + elapsedSeconds + " seconds";

		return uptime;

	}

	public int getClusterSize() {
		return monitorService.getConfig().getCluster().getNode().length;
	}

	public XMLObject[] getClusterNodes() {
		return monitorService.getConfig().getCluster().getNode();
	}

}
