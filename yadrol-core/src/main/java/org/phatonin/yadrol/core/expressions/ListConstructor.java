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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>[ elt1, elt2, ... ]</code>
 * 
 *
 */
public class ListConstructor extends AbstractListExpression {
	private final Expression[] elements;

	public ListConstructor(Location location, List<Expression> elements) {
		super(location);
		this.elements = elements.toArray(new Expression[elements.size()]);
	}

	private ListConstructor(Location location, Expression[] elements) {
		super(location);
		this.elements = elements;
	}

	public Expression[] getElements() {
		return elements;
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		return new ListConstructor(getLocation(), ExpressionListUtils.reduce(elements));
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(elements);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new ListConstructor(getLocation(), substituteVariables(elements, scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			List<Object> result = new ArrayList<Object>(elements.length);
			for (Expression e : elements) {
				Object v = e.evaluate(ctx, scope);
				result.add(v);
			}
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean isAssignable() {
		for (Expression e : elements) {
			if (!e.isAssignable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException {
		try {
			List<Object> list = ctx.valueToList(scope, value);
			for (int i = 0; i < elements.length; ++i) {
				Expression e = elements[i];
				Object v = i < list.size() ? list.get(i) : null;
				e.assign(ctx, scope, v);
			}
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
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
		ListConstructor other = (ListConstructor) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.leftBracket()
		.expressionList(elements)
		.rightBracket();
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
