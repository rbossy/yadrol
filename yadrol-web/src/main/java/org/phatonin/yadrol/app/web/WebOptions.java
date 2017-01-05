package org.phatonin.yadrol.app.web;

import org.phatonin.yadrol.app.YadrolOptions;

public class WebOptions extends YadrolOptions {
	private double confidenceIntervalRisk = 0.2;
	
	public WebOptions(String source) {
		super(source);
	}

	public WebOptions() {
		this("web params");
	}

	public double getConfidenceIntervalRisk() {
		return confidenceIntervalRisk;
	}

	public void setConfidenceIntervalRisk(double confidenceIntervalRisk) {
		this.confidenceIntervalRisk = confidenceIntervalRisk;
	}
}
