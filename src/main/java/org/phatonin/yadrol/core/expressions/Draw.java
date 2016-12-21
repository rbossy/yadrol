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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

public class Draw extends AbstractExpression {
	private final Expression list;

	public Draw(Location location, Expression list) {
		super(location);
		this.list = list;
	}

	public Expression getList() {
		return list;
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			List<Object> list = this.list.evaluateList(ctx, scope);
			if (list.isEmpty()) {
				return null;
			}
			return list.remove(0);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return list.isPureConstant();
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Draw(getLocation(), list.reduce());
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Draw(getLocation(), list.substituteVariables(scope));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
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
		Draw other = (Draw) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		}
		else if (!list.equals(other.list))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append("draw from ");
		list.toString(sb, Precedence.DICE);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.DRAW;
	}
}
