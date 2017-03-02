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
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>draw n from list</code>
 * 
 *
 */
public class DrawMultiple extends AbstractListExpression {
	private final Expression n;
	private final Expression list;
	
	public DrawMultiple(Location location, Expression n, Expression list) {
		super(location);
		this.n = n;
		this.list = list;
	}

	public Expression getN() {
		return n;
	}

	public Expression getList() {
		return list;
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(n, list);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new DrawMultiple(getLocation(), n.substituteVariables(scope), list.substituteVariables(scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			long n = this.n.evaluateInteger(ctx, scope);
			List<Object> list = this.list.evaluateList(ctx, scope);
			if (n < 0) {
				throw new EvaluationException(this, "cannot draw negative");
			}
			n = Math.min(n, list.size());
			List<Object> head = list.subList(0, (int) n);
			List<Object> result = new ArrayList<Object>(head);
			head.clear();
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new DrawMultiple(getLocation(), n.reduce(), list.reduce());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((n == null) ? 0 : n.hashCode());
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
		DrawMultiple other = (DrawMultiple) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		}
		else if (!list.equals(other.list))
			return false;
		if (n == null) {
			if (other.n != null)
				return false;
		}
		else if (!n.equals(other.n))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword("draw ")
		.expression(n, Precedence.ASSIGN)
		.keyword(" from ")
		.expression(list, Precedence.DICE);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.DRAW;
	}
}
