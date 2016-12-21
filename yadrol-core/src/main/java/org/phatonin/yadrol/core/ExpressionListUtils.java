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

package org.phatonin.yadrol.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

public class ExpressionListUtils {
	private ExpressionListUtils() {}
	
	public static Expression[] reduce(Expression[] expressions) throws EvaluationException {
		for (int i = 0; i < expressions.length; ++i) {
			expressions[i] = expressions[i].reduce();
		}
		return expressions;
	}

	public static Expression evaluateButLast(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		final int last = expressions.length - 1;
		for (int i = 0; i < last; ++i) {
			Expression e = expressions[i];
			e.evaluate(ctx, scope);
		}
		return expressions[last];
	}

	public static Object evaluate(Expression[] expressions, EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluate(ctx, scope, type);
	}

	public static Object evaluate(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluate(ctx, scope);
	}

	public static void evaluateUndef(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateButLast(expressions, ctx, scope).evaluateUndef(ctx, scope);
	}

	public static boolean evaluateBoolean(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateBoolean(ctx, scope);
	}

	public static String evaluateString(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateString(ctx, scope);
	}

	public static long evaluateInteger(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateInteger(ctx, scope);
	}

	public static List<Object> evaluateList(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateList(ctx, scope);
	}

	public static Function evaluateFunction(Expression[] expressions, EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateButLast(expressions, ctx, scope).evaluateFunction(ctx, scope);
	}
	
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
