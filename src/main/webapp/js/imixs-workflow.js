/*******************************************************************************
 * imixs-xml.js Copyright (C) 2015, http://www.imixs.org
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You can receive a copy of the GNU General Public License at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Project:  http://www.imixs.org
 * 
 * Contributors: Ralph Soika - Software Developer
 ******************************************************************************/

/**
 * This library provides methods to control the state of a workitem and provide
 * model information
 * 
 * 
 * Version 3.0.0
 */

IMIXS.namespace("org.imixs.workflow");
IMIXS.org.imixs.workflow = (function() {

	if (!IMIXS.org.imixs.core) {
		console.error("ERROR - missing dependency: imixs-core.js");
	}

	if (!IMIXS.org.imixs.xml) {
		console.error("ERROR - missing dependency: imixs-xml.js");
	}

	// private properties
	var serviceURL, imixs = IMIXS.org.imixs.core, imixsXML = IMIXS.org.imixs.xml,

	// validates servcieURL
	getServiceURL = function() {
		var url = IMIXS.org.imixs.workflow.serviceURL;
		if (url.charAt(url.length - 1) != '/') {
			url = url + "/";
		}
		return url;
	},

	/**
	 * The method loads a single workitem from the service base URL.
	 * 
	 */
	loadWorkitem = function(options) {
		var url = getServiceURL();

		url = url + "workflow/workitem/" + options.uniqueID;
 		$.ajax({
			type : "GET",
			url : url,
			dataType : "xml",
			success : function(response) {
				var entity = imixsXML.xml2document(response);
				options.success(new imixs.ItemCollection(entity));
			},
			error : options.error
		});

	},

	/**
	 * The method loads a list of workitems from a given service URL.
	 * 
	 * option params:  service, count, start, items, sortorder
	 * 
	 */
	loadWorklist = function(options) {
		var items,url;
		
		url = getServiceURL();
		if (options.service.charAt(0) == '/') {
			options.service = options.service.substring(1);
		} 
		
		url = url + options.service;
		
		if (url.indexOf('?')==-1) {
			url=url+'?_';
		}
		if (isFinite(options.start)) {
			url=url+"&start="+options.start; 
		}
		if (isFinite(options.count)) {
			url=url+"&count="+options.count;
		}
		if (isFinite(options.sortorder)) {
			url=url+"&sortorder="+options.sortorder;
		}
		if (options.items) {
			items=options.items;
			if (!$.isArray(items)) {
				items = $.makeArray( items );
			}
			url=url+="&items=";
			$.each(items, function(index, aitem) {
				url=url+aitem+",";
			});
		}
		
		$.ajax({
			type : "GET",
			url : url,
			dataType : "xml",
			success : function(response) {
				options.success(imixsXML.xml2collection(response));
			},
			error : options.error
		});
	},

	/**
	 * This method process an workitem. After successful processing the method
	 * returns the updates workitem (ItemCollection). In case of an processing
	 * error the method returns the workiem with the attributes '$error_code'
	 * and '$error_message'
	 */
	processWorkitem = function(options) {
		var url = getServiceURL();

		// update $activityid
		if (options.activity) {
			options.workitem.setItem("$activityid", options.activity
					.getItem("numactivityid"), "xs:int");
		}

		
		
		var xmlData = imixsXML.json2xml(options.workitem);
		// console.debug(xmlData);
		console.debug("process workitem: '"
				+ options.workitem.getItem('$uniqueid') + "'...");

		url = url + "workflow/workitem/";

		$.ajax({
			type : "POST",
			url : url,
			data : xmlData,
			contentType : "text/xml",
			dataType : "xml",
			cache : false,
			error : function(jqXHR, error, errorThrown) {
				var message = errorThrown;
				var json = imixsXML.xml2json(jqXHR.responseXML);
				var workitem = new imixs.ItemCollection(json);
				var uniqueid = workitem.getItem('$uniqueid');
				var error_code = workitem.getItem('$error_code');
				var error_message = workitem.getItem('$error_message');
				console.debug(uniqueid + " : " + error_code + " - "
						+ error_message, true);

				options.error(workitem);
			},
			success : function(response) {
				// return processed workitem
				var entity = imixsXML.xml2document(response);
				options.success(new imixs.ItemCollection(entity));
			}
		});

	},

	/**
	 * This method loads the activity list for a workItem.
	 * 
	 * After the activityList was loaded the call-back-method
	 * success(activityList) will be called.
	 * 
	 * The method uses the browsers localStorage to cache objects.
	 * 
	 */
	loadActivities = function(options) {
		var activityList;
		var modelversion = options.workitem.getItem('$modelversion');
		var processid = options.workitem.getItem('$processid');

		// we first test if we still have the Entity in the local storage
		var currentProcessEntity = null;
		if (imixs.hasLocalStorage())
			activityList = localStorage
					.getItem("org.imixs.workflow.activities." + modelversion
							+ "." + processid);
		if (currentProcessEntity == null) {
			// now we need to load the model information from the rest
			// service...
			var url = getServiceURL();
			url = url + "model/" + modelversion + "/activities/" + processid;

			$.ajax({
				type : "GET",
				url : url,
				dataType : "xml",
				success : function(response) {
					activityList = imixsXML.xml2collection(response);

					if (imixs.hasLocalStorage()) {
						localStorage.setItem("org.imixs.workflow.activities."
								+ modelversion + "." + processid, JSON
								.stringify(activityList));
					}

					if ($.isFunction(options.success)) {
						options.success(activityList);
					}
				},
				error : options.error
			});

		} else {
			// already cached
			if ($.isFunction(options.success)) {
				options.success(activityList);
			}
		}

	}

	// public API
	return {
		serviceURL : serviceURL,
		loadWorkitem : loadWorkitem,
		loadWorklist : loadWorklist,
		processWorkitem : processWorkitem,
		loadActivities : loadActivities
	};

}());
