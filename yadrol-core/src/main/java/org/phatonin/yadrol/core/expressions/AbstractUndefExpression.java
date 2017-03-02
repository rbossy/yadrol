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
 * Base class for undef expressions.
 * 
 *
 */
public abstract class AbstractUndefExpression extends AbstractExpression {
	protected AbstractUndefExpression(Location location) {
		super(location);
	}

	protected AbstractUndefExpression() {
		super();
	}

	@Override
	public abstract void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException;

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return null;
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToBoolean();
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToString();
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToInteger();
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToList();
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToMap();
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluateUndef(ctx, scope);
		return EvaluationContext.undefToFunction();
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.UNDEF;
	}

	@Override
	public abstract AbstractUndefExpression reduce() throws EvaluationException;
}
