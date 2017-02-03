package org.phatonin.yadrol.app.web;

import org.phatonin.yadrol.app.YadrolOptions;
import org.phatonin.yadrol.core.importManagers.ImportManagers;
import org.phatonin.yadrol.core.importManagers.JavaResourceImportParser;
import org.phatonin.yadrol.core.importManagers.URLImportManager;

public class WebOptions extends YadrolOptions {
	private double confidenceIntervalRisk = 0.2;
	
	public WebOptions(String source) {
		super(source);
	}

	public WebOptions() {
		this("web params");
		URLImportManager urlImportManager = new URLImportManager();
		JavaResourceImportParser resImportManager = new JavaResourceImportParser(WebOptions.class.getClassLoader());
		resImportManager.addSearchPaths("org/phatonin/yadrol/lib");
		ImportManagers importManagers = new ImportManagers();
		importManagers.addImportManager(resImportManager);
		importManagers.addImportManager(urlImportManager);
		setImportManager(importManagers);
	}

	public double getConfidenceIntervalRisk() {
		return confidenceIntervalRisk;
	}

	public void setConfidenceIntervalRisk(double confidenceIntervalRisk) {
		this.confidenceIntervalRisk = confidenceIntervalRisk;
	}
}
