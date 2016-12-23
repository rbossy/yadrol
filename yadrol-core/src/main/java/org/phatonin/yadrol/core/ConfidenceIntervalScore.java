package org.phatonin.yadrol.core;

public class ConfidenceIntervalScore implements DistributionScore {
	private final double risk;

	public ConfidenceIntervalScore(double risk) {
		super();
		this.risk = risk;
	}

	@Override
	public Object compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
		return dist.confidenceInterval(risk);
	}

	@Override
	public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
		Count[] interval = dist.confidenceInterval(risk);
		String loString = EvaluationContext.valueString(interval[0].getValue());
		String hiString = EvaluationContext.valueString(interval[1].getValue());
		return String.format("%s - %s", loString, hiString);
	}

	@Override
	public String getName() {
		return String.format("interval (r=%.2f)", risk);
	}
}
