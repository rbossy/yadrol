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
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ContainerVisitor;
import org.phatonin.yadrol.core.values.Function;

/**
 * <code>count container</code>
 * 
 *
 */
public class Count extends AbstractIntegerExpression {
	private final Expression container;

	public Count(Location location, Expression container) {
		super(location);
		this.container = container;
	}

	public Expression getContainer() {
		return container;
	}

	@Override
	public AbstractIntegerExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Count(getLocation(), container.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return container.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Count(getLocation(), container.substituteVariables(scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object container = this.container.evaluateList(ctx, scope);
			return new CountVisitor().visit(container, null);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	private class CountVisitor extends ContainerVisitor<Long,Void,EvaluationException> {
		@Override
		protected Long visitScalar(Object value, Void param) throws RuntimeException {
			return 1L;
		}

		@Override
		public Long visitUndef(Void param) throws RuntimeException {
			return 0L;
		}

		@Override
		public Long visit(List<Object> value, Void param) throws RuntimeException {
			return (long) value.size();
		}

		@Override
		public Long visit(Map<String,Object> value, Void param) throws RuntimeException {
			return (long) value.size();
		}

		@Override
		public Long visit(Function value, Void param) throws EvaluationException {
			throw new EvaluationException(Count.this, "invalid count operand: " + value);
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((container == null) ? 0 : container.hashCode());
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
		Count other = (Count) obj;
		if (container == null) {
			if (other.container != null)
				return false;
		}
		else if (!container.equals(other.container))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		unaryOperator(sb, "count ", container, Precedence.SUBSCRIPT);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.UNARY;
	}
}
