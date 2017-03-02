/**
   Copyright 2016-2017, Robert Bossy

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

package org.phatonin.yadrol.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Utility class for 
 * 
 *
 */
public class ExpressionListUtils {
	private ExpressionListUtils() {}
	
	/**
	 * Reduce each expression in the specified array.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	public static Expression[] reduce(Expression[] expressions) throws EvaluationException {
		for (int i = 0; i < expressions.length; ++i) {
			expressions[i] = expressions[i].reduce();
		}
		return expressions;
	}

	/**
	 * Evaluates all expressions in the specified array but the last.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static Expression evaluateButLast(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		final int last = expressions.length - 1;
		for (int i = 0; i < last; ++i) {
			Expression e = expressions[i];
			e.evaluate(ctx, scope);
		}
		return expressions[last];
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as the specified type.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @param type
	 * @return
	 * @throws EvaluationException
	 */
	public static Object evaluate(Expression[] expressions, EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluate(ctx, scope, type);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @param type
	 * @return
	 * @throws EvaluationException
	 */
	public static Object evaluate(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluate(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as undef.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @throws EvaluationException
	 */
	public static void evaluateUndef(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateButLast(expressions, ctx, scope).evaluateUndef(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as a boolean.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static boolean evaluateBoolean(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateBoolean(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as a string.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static String evaluateString(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateString(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as an integer.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static long evaluateInteger(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateInteger(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as a list.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static List<Object> evaluateList(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateList(ctx, scope);
	}

	/**
	 * Evaluates all expressions in the specified array but the last, then evaluates the last expression as a function.
	 * @param expressions
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static Function evaluateFunction(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateFunction(ctx, scope);
	}

	/**
	 * Evaluates each expression in the specified map and returns a map with the same keys mapped to the corresponding evaluation results.
	 * @param map
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	public static Map<String,Object> evaluate(Map<String,Expression> map, EvaluationContext ctx, Scope scope) throws EvaluationException {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		for (Map.Entry<String,Expression> e : map.entrySet()) {
			String name = e.getKey();
			Expression expr = e.getValue();
			Object value = expr.evaluate(ctx, scope);
			result.put(name, value);
		}
		return result;
	}

}
