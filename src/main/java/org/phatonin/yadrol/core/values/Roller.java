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

package org.phatonin.yadrol.core.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;

public class Roller {
	public static final String DICE_FUNCTION_NUMBER_ARGUMENT = "N";
	
	public static Object roll(Expression expression, EvaluationContext ctx, Object type) throws EvaluationException {
		return new RollVisitor(expression).visit(type, ctx);
	}
	
	public static List<Object> roll(Expression expression, EvaluationContext ctx, long n, Object type) throws EvaluationException {
		Function fun = EvaluationContext.asFunction(type);
		if (fun != null && isDiceFunction(fun)) {
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = new LinkedHashMap<String,Object>();
			namedArgs.put(DICE_FUNCTION_NUMBER_ARGUMENT, n);
			return fun.callList(expression, ctx, positionalArgs, namedArgs, 0);
		}
		return rollMultiple(expression, ctx, n, type);
	}
	
	private static boolean isDiceFunction(Function fun) {
		return fun.getPositionalArgs().contains(DICE_FUNCTION_NUMBER_ARGUMENT) || fun.getNamedArgs().containsKey(DICE_FUNCTION_NUMBER_ARGUMENT);
	}
	
	private static List<Object> rollMultiple(Expression expression, EvaluationContext ctx, long n, Object type) throws EvaluationException {
		List<Object> result = new ArrayList<Object>((int) n);
		for (int i = 0; i < n; ++i) {
			Object d = Roller.roll(expression, ctx, type);
			result.add(d);
		}
		return result;
	}
	
	public static long rollInteger(EvaluationContext ctx, long type) {
		Random rnd = ctx.getRandom();
		long n = rnd.nextLong();
		long an = n == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(n);
		return (an % type) + 1;
	}
	
	public static Object rollList(EvaluationContext ctx, List<Object> type) {
		if (type.isEmpty()) {
			return null;
		}
		Random rnd = ctx.getRandom();
		return type.get(rnd.nextInt(type.size()));
	}
	
	public static Map<String,Object> rollMap(EvaluationContext ctx, Map<String,Object> type) {
		if (type.isEmpty()) {
			return null;
		}
		Random rnd = ctx.getRandom();
		int n = rnd.nextInt(type.size());
		Iterator<Map.Entry<String,Object>> it = type.entrySet().iterator();
		for (int i = 0; i < n; ++i) {
			it.next();
		}
		Map.Entry<String,Object> e = it.next();
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		result.put(e.getKey(), e.getValue());
		return result;
	}
	
	public static Object rollFunction(Expression expression, EvaluationContext ctx, Function type) throws EvaluationException {
		List<Object> positionalArgs = Collections.emptyList();
		Map<String,Object> namedArgs = Collections.emptyMap();
		return type.call(expression, ctx, positionalArgs, namedArgs, 0);
	}
	
	private static final class RollVisitor extends ValueVisitor<Object,EvaluationContext,EvaluationException> {
		private final Expression expression;
		
		private RollVisitor(Expression expression) {
			super();
			this.expression = expression;
		}

		@Override
		public Object visitUndef(EvaluationContext param) throws EvaluationException {
			throw new EvaluationException(expression, "invalid dice type: undef");
		}

		@Override
		public Object visit(String value, EvaluationContext param) throws EvaluationException {
			throw new EvaluationException(expression, "invalid dice type: " + value);
		}

		@Override
		public Object visit(boolean value, EvaluationContext param) throws EvaluationException {
			throw new EvaluationException(expression, "invalid dice type: " + value);
		}

		@Override
		public Object visit(long value, EvaluationContext param) {
			return rollInteger(param, value);
		}

		@Override
		public Object visit(List<Object> value, EvaluationContext param) {
			return rollList(param, value);
		}

		@Override
		public Object visit(Map<String,Object> value, EvaluationContext param) {
			return rollMap(param, value);
		}

		@Override
		public Object visit(Function value, EvaluationContext param) throws EvaluationException {
			return rollFunction(expression, param, value);
		}
	};
}
