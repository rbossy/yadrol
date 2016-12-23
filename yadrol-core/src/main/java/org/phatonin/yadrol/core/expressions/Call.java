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

package org.phatonin.yadrol.core.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * function(parg1, parg2, ..., name1: narg1, name2: narg2, ...)
 * 
 *
 */
public class Call extends AbstractExpression {
	private final Expression function;
	private final Expression[] positionalArgs;
	private final Map<String,Expression> namedArgs;
	
	public Call(Expression function, List<Expression> positionalArgs, Map<String,Expression> namedArgs) {
		super(function.getLocation());
		this.function = function;
		this.positionalArgs = positionalArgs.toArray(new Expression[positionalArgs.size()]);
		this.namedArgs = namedArgs;
	}
	
	private Call(Expression function, Expression[] positionalArgs, Map<String,Expression> namedArgs) {
		super(function.getLocation());
		this.function = function;
		this.positionalArgs = positionalArgs;
		this.namedArgs = namedArgs;
	}

	public Expression getFunction() {
		return function;
	}

	public Expression[] getPositionalArgs() {
		return positionalArgs;
	}

	public Map<String,Expression> getNamedArgs() {
		return namedArgs;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Call(function.reduce(), ExpressionListUtils.reduce(positionalArgs), reduce(namedArgs));
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return function.isPureConstant() && isPureConstant() && isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Call(function, substituteVariables(positionalArgs, scope), substituteVariables(namedArgs, scope));
	}

	private List<Object> evaluatePositionalArgs(EvaluationContext ctx, Scope scope) throws EvaluationException {
		List<Object> result = new ArrayList<Object>(positionalArgs.length);
		for (Expression e : positionalArgs) {
			Object v = e.evaluate(ctx, scope);
			result.add(v);
		}
		return result;
	}
	
	private Map<String,Object> evaluateNamedArgs(EvaluationContext ctx, Scope scope) throws EvaluationException {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		for (Map.Entry<String,Expression> e : namedArgs.entrySet()) {
			result.put(e.getKey(), e.getValue().evaluate(ctx, scope));
		}
		return result;
	}
	
	private Function getFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		if (scope.getDepth() >= ctx.getMaxCallDepth()) {
			throw new EvaluationException(this, "max call depth");
		}
		Object result = function.evaluate(ctx, scope);
		Function fun = EvaluationContext.asFunction(result);
		if (fun == null) {
			throw new EvaluationException(this, "not a function: " + result);
		}
		return fun;
	}
	
	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.call(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			fun.callUndef(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callBoolean(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callString(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callInteger(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callList(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callMap(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Function fun = getFunction(ctx, scope);
			List<Object> positionalArgs = evaluatePositionalArgs(ctx, scope);
			Map<String,Object> namedArgs = evaluateNamedArgs(ctx, scope);
			return fun.callFunction(this, ctx, positionalArgs, namedArgs, scope.getDepth());
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.ANY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + ((namedArgs == null) ? 0 : namedArgs.hashCode());
		result = prime * result + Arrays.hashCode(positionalArgs);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Call other = (Call) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		}
		else if (!function.equals(other.function))
			return false;
		if (namedArgs == null) {
			if (other.namedArgs != null)
				return false;
		}
		else if (!namedArgs.equals(other.namedArgs))
			return false;
		if (!Arrays.equals(positionalArgs, other.positionalArgs))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		function.toString(sb, Precedence.SUBSCRIPT);
		sb.append('(');
		expressionListToString(sb, positionalArgs);
		if (positionalArgs.length != 0 && !namedArgs.isEmpty()) {
			sb.append(", ");
		}
		expressionMapToString(sb, namedArgs, false);
		sb.append(')');
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.expression(function, Precedence.SUBSCRIPT)
		.leftParen()
		.expressionList(positionalArgs);
		if (positionalArgs.length != 0 && !namedArgs.isEmpty()) {
			stringer.comma().space();
		}
		stringer.expressionMap(namedArgs, false)
		.rightParen();
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.SUBSCRIPT;
	}
}
