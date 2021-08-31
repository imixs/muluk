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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
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
 * The ObjectMonitor starts and executes the object timer jobs.
 * 
 * @author rsoika
 * @version 1.0
 */
@Startup
@Singleton
public class ObjectMonitor {

    @Inject
    LogService logService;

    @Resource
    javax.ejb.TimerService timerService;

    private XMLConfig config;

    /**
     * This method starts all jobs defined in the current monitor configuration.
     */
    public void start(XMLConfig config) {
        this.config = config;
        XMLObject[] allObjects = config.getMonitor().getObject();
        if (allObjects != null) {
            for (XMLObject obj : allObjects) {
                try {
                    initObjectTimer(obj);
                } catch (AccessDeniedException | ParseException e) {
                    logService.severe("Failed to start object monitoring: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a non-persistent calendar-based timer based on a Job Object.
     * 
     * 
     * @param configuration
     * @return
     * @throws AccessDeniedException
     * @throws ParseException
     */
    protected void initObjectTimer(XMLObject object) throws AccessDeniedException, ParseException {
        Timer timer = null;
        if (object == null)
            return;

        logService.info("......starting new object monitor (⚙ " + object.getInterval() + " sec ▸ '" + object.getTarget()
                + "') ...");
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
     * This is the method which monitors the object
     * 
     * @param timer
     */
    @Timeout
    protected void onTimeout(javax.ejb.Timer timer) {

        // reset message log
        logService.reset();
        XMLObject object = (XMLObject) timer.getInfo();

        if (object == null) {
            logService.severe("...invalid object configuration! Timer will be stopped...");
            timer.cancel();
            return;
        }

        logService.info("......monitor object - " + object.getTarget());

        String target = object.getTarget();
        if ("web".equalsIgnoreCase(object.getType())) {
            try {
                WebClient webClient = new WebClient(object.getAuth());
                String result = webClient.get(target);
                logService.info("......HTTP Result = " + webClient.getLastHTTPResult());

                if (200 == webClient.getLastHTTPResult()) {

                    // Pattern match?
                    if (object.getPattern() != null && !object.getPattern().isEmpty()) {
                        Pattern p = Pattern.compile(object.getPattern()); // the pattern to search for
                        Matcher m = p.matcher(result);
                        // now try to find at least one match
                        if (m.find()) {
                            logService.info("......OK");
                            object.setStatus(MonitorService.STATUS_OK);
                            object.setLastSuccess(new Date());
                            config.addObjectPing();

                        } else {
                            logService.info("......FAILED - HTTP response did not match the pattern: '" + object.getPattern());
                            // log page content
                            logService.info("");
                            logService.info("=== Page Content Start ===");
                            logService.info(result);
                            logService.info("=== Page Content End ===");
                            object.setStatus(MonitorService.STATUS_FAILED);
                            object.setLastFailure(new Date());
                            config.addObjectErrors();
                        }
                    } else {
                        // just http response 200
                        object.setStatus(MonitorService.STATUS_OK);
                        object.setLastSuccess(new Date());
                        config.addObjectPing();
                    }
                } else {
                    // BAD Reqeust!
                    logService.info("......FAILED - target not responding!");
                    object.setStatus(MonitorService.STATUS_FAILED);
                    object.setLastFailure(new Date());
                    config.addObjectErrors();
                }

            } catch (IOException e) {
                logService.severe("FAILED to request target - " + e.getMessage());
                object.setStatus(MonitorService.STATUS_FAILED);
                object.setLastFailure(new Date());
                config.addObjectErrors();
            }

        }

        // if status has changed than we send an email....
        if (!object.getStatus().equals(object.getLastStatus())) {
            try {
                if (MonitorService.STATUS_FAILED.equals(object.getStatus())) {
                    logService.sendMessageLog("[" + config.getCluster().getName()
                            + "] We have a problem - Service DOWN: " + object.getTarget(), config.getMail());
                    // invalidate the cached token in case of a form based authentication!
                    if ("FORM".equalsIgnoreCase(object.getAuth().getType())) {
                        object.getAuth().setToken(null);
                    }
                }
                if (MonitorService.STATUS_OK.equals(object.getStatus())) {
                    logService.sendMessageLog("[" + config.getCluster().getName() + "] Problem solved - Service UP: "
                            + object.getTarget(), config.getMail());
                }
                object.setLastStatus(object.getStatus());
            } catch (MessagingException e) {
                logService.severe("Failed to send Status Mail - " + e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
