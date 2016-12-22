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
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>repeat expression if condition</code>
 * <code>repeat expression while condition [limit limit]</code>
 * <code>while condition repeat expression [limit limit]</code>
 *
 */
public class Repeat extends AbstractListExpression {
	public static final String DEFAULT_VARIABLE_ASSIGN = "_";
	
	private final Expression expression;
	private final Expression condition;
	private final boolean preCondition;
	private final long limit;

	public Repeat(Location location, Expression expression, Expression condition, boolean preCondition, long limit) {
		super(location);
		this.expression = expression;
		this.condition = condition;
		this.preCondition = preCondition;
		this.limit = limit;
	}

	public Expression getExpression() {
		return expression;
	}

	public Expression getCondition() {
		return condition;
	}

	public long getLimit() {
		return limit;
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Repeat(getLocation(), expression.reduce(), condition.reduce(), preCondition, limit);
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(expression, condition);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Repeat(getLocation(), expression.substituteVariables(scope), condition.substituteVariables(scope), preCondition, limit);
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			final long limit = Math.min(this.limit, ctx.getMaxReroll());
			Scope loopScope = new Scope(scope);
			List<Object> result = new ArrayList<Object>();
			if (preCondition) {
				whileDo(ctx, loopScope, limit, result);
			}
			else {
				doWhile(ctx, loopScope, limit, result);
			}
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	private void doWhile(EvaluationContext ctx, Scope loopScope, long limit, List<Object> result) throws EvaluationException {
		do {
			Object v = expression.evaluate(ctx, loopScope);
			result.add(v);
		}
		while ((result.size() <= limit) && condition.evaluateBoolean(ctx, loopScope));
	}
	
	private void whileDo(EvaluationContext ctx, Scope loopScope, long limit, List<Object> result) throws EvaluationException {
		while ((result.size() < limit) && condition.evaluateBoolean(ctx, loopScope)) {
			Object v = expression.evaluate(ctx, loopScope);
			result.add(v);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + (int) (limit ^ (limit >>> 32));
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
		Repeat other = (Repeat) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		if (limit != other.limit)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append("repeat ");
		expression.toString(sb, Precedence.OR);
		sb.append(limit == 1 ? " if " : " while ");
		condition.toString(sb, Precedence.OR);
		if (limit != 1 && limit != Long.MAX_VALUE) {
			sb.append(" limit ");
			sb.append(limit);
		}
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.CONTROL;
	}
}
