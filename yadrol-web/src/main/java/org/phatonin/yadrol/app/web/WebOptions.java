/**
   Copyright 2016, Robert Bossy

   This file is part of Yadrol.

   Yadrol is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Yadrol is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Yadrol.  If not, see <http://www.gnu.org/licenses/>.
**/

package org.phatonin.yadrol.app.web;

import org.phatonin.yadrol.app.YadrolOptions;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.importManagers.JavaResourceImportParser;
import org.phatonin.yadrol.core.importManagers.URLImportManager;

public class WebOptions extends YadrolOptions {
	private double confidenceIntervalRisk = 0.2;

	public WebOptions(String source) {
		super(source);
	}

	public WebOptions() {
		this("web params");
		setJavaResourceImportParser(JavaResourceImportParser.withStandardSearchLocation(WebOptions.class.getClassLoader()));
		addImport(Location.NONE, "std");
		setUrlImportManager(new URLImportManager());
	}

	public double getConfidenceIntervalRisk() {
		return confidenceIntervalRisk;
	}

	public void setConfidenceIntervalRisk(double confidenceIntervalRisk) {
		this.confidenceIntervalRisk = confidenceIntervalRisk;
	}
}
