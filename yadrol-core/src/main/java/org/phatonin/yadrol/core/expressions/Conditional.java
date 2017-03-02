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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * <code>if condition then trueBranch else falseBranch</code>
 * 
 *
 */
public class Conditional extends AbstractExpression {
	private final Expression condition;
	private final Expression trueBranch;
	private final Expression falseBranch;
	
	public Conditional(Location location, Expression condition, Expression trueBranch, Expression falseBranch) {
		super(location);
		this.condition = condition;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	public Expression getCondition() {
		return condition;
	}

	public Expression getTrueBranch() {
		return trueBranch;
	}

	public Expression getFalseBranch() {
		return falseBranch;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		if (condition.isPureConstant()) {
			if (condition.evaluateBoolean(null, null)) {
				return trueBranch.reduce();
			}
			return falseBranch.reduce();
		}
		return new Conditional(getLocation(), condition.reduce(), trueBranch.reduce(), falseBranch.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(condition, trueBranch, falseBranch);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Conditional(getLocation(), condition.substituteVariables(scope), trueBranch.substituteVariables(scope), falseBranch.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluate(ctx, scope);
			}
			return falseBranch.evaluate(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				trueBranch.evaluateUndef(ctx, scope);
			}
			falseBranch.evaluateUndef(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluateBoolean(ctx, scope);
			}
			return falseBranch.evaluateBoolean(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluateString(ctx, scope);
			}
			return falseBranch.evaluateString(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluateInteger(ctx, scope);
			}
			return falseBranch.evaluateInteger(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluateList(ctx, scope);
			}
			return falseBranch.evaluateList(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			if (condition.evaluateBoolean(ctx, scope)) {
				return trueBranch.evaluateFunction(ctx, scope);
			}
			return falseBranch.evaluateFunction(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public ValueType getReturnType() {
		return trueBranch.getReturnType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((falseBranch == null) ? 0 : falseBranch.hashCode());
		result = prime * result + ((trueBranch == null) ? 0 : trueBranch.hashCode());
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
		Conditional other = (Conditional) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (falseBranch == null) {
			if (other.falseBranch != null)
				return false;
		}
		else if (!falseBranch.equals(other.falseBranch))
			return false;
		if (trueBranch == null) {
			if (other.trueBranch != null)
				return false;
		}
		else if (!trueBranch.equals(other.trueBranch))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword("if ")
		.expression(condition, Precedence.OR)
		.keyword(" then ")
		.expression(trueBranch, Precedence.OR)
		.keyword(" else ")
		.expression(falseBranch, Precedence.OR);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.CONTROL;
	}
}
