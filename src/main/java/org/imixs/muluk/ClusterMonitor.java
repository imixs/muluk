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

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.imixs.muluk.web.WebClient;
import org.imixs.muluk.xml.XMLConfig;
import org.imixs.muluk.xml.XMLObject;

/**
 * The ClusterMonitor starts and executes the cluster node timer jobs.
 * 
 * @author rsoika
 * @version 1.0
 */
@Startup
@Singleton
public class ClusterMonitor {

	@Inject
	LogService logService;

	@Resource
	javax.ejb.TimerService timerService;

	private XMLConfig config;

	/**
	 * This method starts all jobs defined in the current monitor configuration.
	 * <p>
	 * In addition the method also starts a scheduled timer for the daily message
	 * based on the overall configuration.
	 */
	public void start(XMLConfig config) {
		this.config = config;

		// first we start for each cluster object a separate timer
		XMLObject[] allObjects = config.getCluster().getNode();
		if (allObjects != null) {
			for (XMLObject obj : allObjects) {
				try {
					initNodeTimer(obj);
				} catch (AccessDeniedException | ParseException e) {
					logService.severe("Failed to start cluster monitoring: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		// finally we start a scheduled timer for the whole object. This timer is used
		// for the daily message.
		initDailyTimer();
	}

	/**
	 * Create a non-persistent calendar-based timer based on a Node Object.
	 * 
	 * 
	 * @param configuration
	 * @return
	 * @throws AccessDeniedException
	 * @throws ParseException
	 */
	protected void initNodeTimer(XMLObject object) throws AccessDeniedException, ParseException {
		Timer timer = null;
		if (object == null)
			return;

		logService.info("......starting new cluster monitor (⚙ " + object.getInterval() + " sec ▸ '"
				+ object.getTarget() + "') ...");
		// create a new non persistent timer object
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(object);
		timerConfig.setPersistent(false);

		long interval = object.getInterval();
		if (interval <= 0) {
			// default interval = 60 seconds
			interval = MonitorService.DEFAULT_INTERVAL;
		}
		// intial delay 10 seconds
		timer = timerService.createIntervalTimer(MonitorService.INITIAL_DELAY, interval * 1000, timerConfig);
		if (timer == null) {
			logService.warning("...failed to start new monitoring job!");
		}

	}

	/**
	 * Starts a daily timer running on a fixed time every day.
	 * 
	 * @param config
	 */
	protected void initDailyTimer() {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo("DAILY_MESSAGE");
		timerConfig.setPersistent(false);

		ScheduleExpression scheduerExpression = new ScheduleExpression();
		scheduerExpression.minute("42");
		scheduerExpression.hour(6);
		Timer timer = timerService.createCalendarTimer(scheduerExpression, timerConfig);
		if (timer == null) {
			logService.warning("...failed to start new monitoring job!");
		}
	}

	/**
	 * This is the method which monitors the cluster node
	 * <p>
	 * We are just interested in HTTP result 200
	 * <p>
	 * The method also handles the scheduled daily timer which is sending an overall
	 * report.
	 * 
	 * @param timer
	 */
	@Timeout
	protected void onTimeout(javax.ejb.Timer timer) {

		// reset message log
		logService.reset();

		/*
		 * Verify cluster node object
		 */
		if (timer.getInfo() instanceof XMLObject) {

			XMLObject object = (XMLObject) timer.getInfo();

			if (object == null) {
				logService.severe("...invalid object configuration! Timer will be stopped...");
				timer.cancel();
				return;
			}

			logService.info("......monitor cluster node - " + object.getTarget());

			String target = object.getTarget();
			// just check http status
			try {
				WebClient webClient = new WebClient(object.getAuth());
				@SuppressWarnings("unused")
				String result = webClient.get(target);

				if (webClient.getLastHTTPResult() == 200) {
					logService.info("......OK");
					object.setStatus("OK");
					object.setLastSuccess(new Date());
					config.addClusterPing();
				} else {
					logService.info("......FAILED - pattern not found!");
					object.setStatus("FAILED");
					object.setLastFailure(new Date());
					config.addClusterErrors();
				}
			} catch (IOException e) {
				logService.severe("FAILED to request target - " + e.getMessage());
				object.setStatus("FAILED");
				object.setLastFailure(new Date());
				config.addClusterErrors();
			}

			// if status has changed than we send an email....
			if (!object.getStatus().equals(object.getLastStatus())) {
				try {
					if (MonitorService.STATUS_FAILED.equals(object.getStatus())) {
						logService.sendMessageLog("[" + config.getCluster().getName() + "] We have a problem - Service DOWN: " + object.getTarget(),
								config.getMail());
					}
					if (MonitorService.STATUS_OK.equals(object.getStatus())) {
						logService.sendMessageLog("[" + config.getCluster().getName() + "] Problem solved - Service UP: " + object.getTarget(),
								config.getMail());
					}
					object.setLastStatus(object.getStatus());
				} catch (MessagingException e) {
					logService.severe("Failed to send Status Mail - " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		/*
		 * Daily Message
		 */

		if ("DAILY_MESSAGE".equals(timer.getInfo())) {

			logService.logHeaderToMail();
			
			String overallStatus = MonitorService.STATUS_OK;

			logService.info("Cluster: " + config.getCluster().getName());
			logService.info("------------------------------------------");

			for (XMLObject node : config.getCluster().getNode()) {
				if (MonitorService.STATUS_OK.equals(node.getStatus())) {
					logService.info(node.getStatus() + " \t " + node.getLastSuccess() + "\t" + node.getTarget());
				} else {
					overallStatus = MonitorService.STATUS_FAILED;
					logService.info(node.getStatus() + " \t " + node.getLastFailure() + "\t" + node.getTarget());
				}
			}
			logService.info("");
			logService.info("Objects: ");
			logService.info("------------------------------------------");
			for (XMLObject node : config.getMonitor().getObject()) {
				if (MonitorService.STATUS_OK.equals(node.getStatus())) {
					logService.info(node.getStatus() + " \t " + node.getLastSuccess() + "\t" + node.getTarget());
				} else {
					overallStatus = MonitorService.STATUS_FAILED;
					logService.info(node.getStatus() + " \t " + node.getLastFailure() + "\t" + node.getTarget());
				}
			}

			// send an email....
			try {
				if (MonitorService.STATUS_OK.equals(overallStatus)) {
					logService.sendMessageLog(
							"[" + config.getCluster().getName() + "] Good Morning :-) - everything is up and running. ",
							config.getMail());
				} else {
					logService.sendMessageLog(
							"[" + config.getCluster().getName() + "] Good Morning :-/ - we have a problem! ",
							config.getMail());

				}
				
			} catch (MessagingException e) {
				logService.severe("Failed to send Status Mail - " + e.getMessage());
				e.printStackTrace();
			}

		}

	}
}
