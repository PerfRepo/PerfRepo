/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.util;

import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;

import java.util.Map;

public class ReportUtils {

	/**
	 * Creates new ReportProperty object with given attributes. It's a helper method for creating
	 * these objects.
	 *
	 * @param name
	 * @param value
	 * @param report
	 * @return newly created {@link ReportProperty} object
	 */
	public static ReportProperty createReportProperty(String name, String value, Report report) {
		ReportProperty reportProperty = new ReportProperty();
		reportProperty.setName(name);
		reportProperty.setValue(value);
		reportProperty.setReport(report);

		return reportProperty;
	}

	/**
	 * Helper method. It performs an update in map of report properties, if the property already exists. If not,
	 * it creates new one. This method exists because if the ReportProperty already exists, we must not
	 * create a new object, because it would have new autogenerated ID. Instead, we modify the existing one.
	 *
	 * @param reportProperties
	 * @param name
	 * @param value
	 * @param report
	 */
	public static void createOrUpdateReportPropertyInMap(Map<String, ReportProperty> reportProperties, String name, String value, Report report) {
		if (reportProperties.containsKey(name)) {
			reportProperties.get(name).setValue(value);
		} else {
			reportProperties.put(name, ReportUtils.createReportProperty(name, value, report));
		}
	}
}
