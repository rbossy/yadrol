package org.phatonin.yadrol.core;

public interface DistributionScore {
	Object compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException;
	String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException;
	String getName();
}
