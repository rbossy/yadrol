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
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ContainerVisitor;

/**
 * <code>target &lt;&lt; source</code>
 * 
 *
 */
public class Append extends AbstractExpression {
	private final Expression target;
	private final Expression source;
	
	public Append(Expression target, Expression source) {
		super(target.getLocation());
		this.target = target;
		this.source = source;
	}

	public Expression getTarget() {
		return target;
	}

	public Expression getSource() {
		return source;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Append(target.reduce(), source.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(target, source);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Append(target.substituteVariables(scope), source.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object target = this.target.evaluate(ctx, scope);
			Object source = this.source.evaluate(ctx, scope);
			return new AppendVisitor(ctx, scope).visit(target, source);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	private class AppendVisitor extends ContainerVisitor<Object,Object,EvaluationException> {
		private final EvaluationContext ctx;
		private final Scope scope;
		
		private AppendVisitor(EvaluationContext ctx, Scope scope) {
			super();
			this.ctx = ctx;
			this.scope = scope;
		}

		@Override
		protected Object visitScalar(Object value, Object param) throws EvaluationException {
			throw new EvaluationException(Append.this, "invalid left operand for '<<': " + value);
		}

		@Override
		public Object visitUndef(Object param) throws EvaluationException {
			throw new EvaluationException(Append.this, "invalid left operand for '<<': undef");
		}

		@Override
		public Object visit(List<Object> value, Object param) throws EvaluationException {
			value.addAll(ctx.valueToList(scope, param));
			return value;
		}

		@Override
		public Object visit(Map<String,Object> value, Object param) throws EvaluationException {
			Map<String,Object> map = EvaluationContext.asMap(param);
			if (map == null) {
				throw new EvaluationException(Append.this, "invalid right operand for '<<', expected map");
			}
			value.putAll(map);
			return value;
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		Append other = (Append) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		}
		else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		}
		else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		binaryOperator(sb, " << ", target, source, Precedence.RANGE);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.binaryOperator(" << ", target, source, Precedence.RANGE);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.APPEND;
	}
}
