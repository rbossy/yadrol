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
