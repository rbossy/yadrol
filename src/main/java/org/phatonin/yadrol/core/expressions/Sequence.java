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
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

public class Sequence extends AbstractExpression {
	private final Expression[] expressions;

	public Sequence(List<Expression> expressions) {
		super(expressions.get(0).getLocation());
		this.expressions = expressions.toArray(new Expression[expressions.size()]);
	}

	public Expression[] getExpressions() {
		return expressions;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Sequence(substituteVariables(expressions, scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluate(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			ExpressionListUtils.evaluateUndef(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluateBoolean(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluateString(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluateInteger(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluateList(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return ExpressionListUtils.evaluateFunction(expressions, ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public ValueType getReturnType() {
		return expressions[expressions.length - 1].getReturnType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(expressions);
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
		Sequence other = (Sequence) obj;
		if (!Arrays.equals(expressions, other.expressions))
			return false;
		return true;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (this.expressions.length == 0) {
			return new Undef(Location.NONE);
		}
		if (isPureConstant()) {
			return pureExpression();
		}
		List<Expression> expressions = new ArrayList<Expression>(this.expressions.length);
		final int last = this.expressions.length - 1;
		for (int i = 0; i < last; ++i) {
			Expression e = this.expressions[i];
			if (!e.isPureConstant()) {
				expressions.add(e.reduce());
			}
		}
		Expression lastE = this.expressions[last].reduce();
		if (expressions.isEmpty()) {
			return lastE;
		}
		expressions.add(lastE);
		return new Sequence(expressions);
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(expressions);
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		nAryOperator(sb, "; ", expressions, Precedence.OUTPUT); 
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.SEQUENCE;
	}
}
