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

import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Base class for function expressions.
 * 
 *
 */
public abstract class AbstractFunctionExpression extends AbstractExpression {
	protected AbstractFunctionExpression(Location location) {
		super(location);
	}

	protected AbstractFunctionExpression() {
		super();
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateFunction(ctx, scope);
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateFunction(ctx, scope);
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.functionToBoolean(scope, evaluateFunction(ctx, scope));
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.functionToString(scope, evaluateFunction(ctx, scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.functionToInteger(scope, evaluateFunction(ctx, scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.functionToList(scope, evaluateFunction(ctx, scope));
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.functionToMap(scope, evaluateFunction(ctx, scope));
	}

	@Override
	public abstract Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException;

	@Override
	public ValueType getReturnType() {
		return ValueType.FUNCTION;
	}

	@Override
	public abstract AbstractFunctionExpression reduce() throws EvaluationException;
}
