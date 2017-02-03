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

package org.phatonin.yadrol.app;

import java.io.Reader;
import java.io.StringReader;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.expressions.Import;
import org.phatonin.yadrol.core.expressions.Output;
import org.phatonin.yadrol.core.parser.ParseException;
import org.phatonin.yadrol.core.parser.YadrolParser;

/**
 * Application result.
 * 
 *
 */
public class YadrolResult {
	private final Expression[] expressions;
	private final EvaluationContext evaluationContext;
	
	private YadrolResult(Expression[] expressions, EvaluationContext evaluationContext) {
		super();
		this.expressions = expressions;
		this.evaluationContext = evaluationContext;
	}

	/**
	 * Returns the expressions evaluated.
	 * @return
	 */
	public Expression[] getExpressions() {
		return expressions;
	}

	/**
	 * Returns the evaluation context.
	 * @return
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * Honors the specified options and returns the result.
	 * This method does the following:
	 * <ul>
	 * <li>creates an evaluation context wrt to the specified options;</li>
	 * <li>resolves all imports;</li>
	 * <li>parses the expression string;</li>
	 * <li>evaluates all expressions.</li>
	 * @param options
	 * @return
	 * @throws EvaluationException
	 * @throws ParseException
	 */
	public static YadrolResult createResult(YadrolOptions options) throws EvaluationException, ParseException {
		EvaluationContext evaluationContext = createEvaluationContext(options);
		Scope scope = evaluationContext.getGlobalScope();
		Expression[] expressions = parseExpression(options);
		Expression last = ExpressionListUtils.evaluateButLast(expressions, evaluationContext, scope);
		last.evaluate(evaluationContext, scope);
		if (needsOutput(options, evaluationContext)) {
			last = forceOutput(options, last);
			evaluationContext.clearRecords();
			last.evaluate(evaluationContext, scope);
		}
		return new YadrolResult(expressions, evaluationContext);
	}

	private static EvaluationContext createEvaluationContext(YadrolOptions options) throws EvaluationException {
		EvaluationContext result = new EvaluationContext();
		result.setSeed(options.getSeed());
		result.setDefaultEvaluationType(options.getDefaultEvaluationType());
		result.setSampleSize(options.getSampleSize());
		result.setImportManager(options.getImportManager());
		Scope globalScope = result.getGlobalScope();
		for (Import imp : options.getImports()) {
			imp.evaluateUndef(result, globalScope);
		}
		return result;
	}

	private static Expression[] parseExpression(YadrolOptions options) throws ParseException, EvaluationException {
		Reader r = new StringReader(options.getExpressionString());
		YadrolParser parser = new YadrolParser(r);
		Expression[] result = parser.parse(options.getSource(), 0);
		if (options.isReduce()) {
			ExpressionListUtils.reduce(result);
		}
		return result;
	}

	private static boolean needsOutput(YadrolOptions options, EvaluationContext ctx) {
		if (!options.isForceOutput()) {
			return false;
		}
		switch (options.getOutputMode()) {
			case ROLL: return !ctx.hasOutputRecord();
			case SAMPLE: return !ctx.hasSampleRecord();
			case DEFAULT: throw new RuntimeException();
		}
		throw new RuntimeException();
	}
	
	private static Expression forceOutput(YadrolOptions options, Expression expr) {
		return new Output(expr.getLocation(), null, expr, options.getDefaultEvaluationType(), options.getOutputMode());
	}
}
