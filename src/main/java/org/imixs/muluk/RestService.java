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
import java.io.OutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * The RestService provides the api to get the current config
 *
 * @author rsoika
 * 
 */
@Path("/")
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
@Stateless
public class RestService {

    @Inject
    MonitorService monitorService;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public StreamingOutput getRoot() {

        return new StreamingOutput() {
            public void write(OutputStream out) throws IOException, WebApplicationException {

                out.write("<div class=\"root\">".getBytes());
                out.write("<a href=\"/config\" type=\"application/xml\" rel=\"documents\"/>".getBytes());
                out.write("</div>".getBytes());
            }
        };
    }

    /**
     * Method to invalidate the current user session
     * <p>
     * Should be called by a client
     * 
     * @return
     */
    @GET
    @Path("/config")
    public Response convertResultList() {
        return Response.ok(monitorService.getConfig()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML)
                .build();
    }

}
