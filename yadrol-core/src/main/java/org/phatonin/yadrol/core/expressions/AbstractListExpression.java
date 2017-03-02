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
 * Base class for list expressions.
 * 
 *
 */
public abstract class AbstractListExpression extends AbstractExpression {
	protected AbstractListExpression(Location location) {
		super(location);
	}

	protected AbstractListExpression() {
		super();
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return evaluateList(ctx, scope);
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateList(ctx, scope);
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return EvaluationContext.listToBoolean(evaluateList(ctx, scope));
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.listToString(scope, evaluateList(ctx, scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.listToInteger(scope, evaluateList(ctx, scope));
	}

	@Override
	public abstract List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException;

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return EvaluationContext.listToMap(evaluateList(ctx, scope));
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return EvaluationContext.listToFunction(evaluateList(ctx, scope));
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LIST;
	}

	@Override
	public abstract AbstractListExpression reduce() throws EvaluationException;
	
	@Override
	protected AbstractListExpression pureExpression() throws EvaluationException {
		List<Object> value = evaluateList(null, null);
		return EvaluationContext.listToExpression(value);
	}
}
