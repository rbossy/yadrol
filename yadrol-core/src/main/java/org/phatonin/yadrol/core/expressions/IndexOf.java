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
import java.util.ListIterator;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ContainerVisitor;
import org.phatonin.yadrol.core.values.ValueComparator;

/**
 * <code>element in container</code>
 * 
 *
 */
public class IndexOf extends AbstractExpression {
	private final Expression element;
	private final Expression container;
	
	public IndexOf(Expression element, Expression container) {
		super(element.getLocation());
		this.element = element;
		this.container = container;
	}

	public Expression getElement() {
		return element;
	}

	public Expression getContainer() {
		return container;
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		Object element = this.element.evaluate(ctx, scope);
		Object container = this.container.evaluate(ctx, scope);
		return new IndexOfVisitor().visit(container, element);
	}

	private static boolean equalValues(Object a, Object b) {
		return ValueComparator.INSTANCE.compare(a, b) == 0;
	}
	
	private static class IndexOfVisitor extends ContainerVisitor<Object,Object,RuntimeException> {
		@Override
		protected Object visitScalar(Object value, Object param) throws RuntimeException {
			return equalValues(value, param);
		}

		@Override
		public Object visitUndef(Object param) throws RuntimeException {
			return null;
		}

		@Override
		public Object visit(List<Object> value, Object param) throws RuntimeException {
			ListIterator<Object> lit = value.listIterator();
			while (lit.hasNext()) {
				Object v = lit.next();
				if (equalValues(param, v)) {
					return lit.previousIndex();
				}
			}
			return null;
		}

		@Override
		public Object visit(Map<String,Object> value, Object param) throws RuntimeException {
			for (Map.Entry<String,Object> e : value.entrySet()) {
				Object v = e.getValue();
				if (equalValues(v, param)) {
					return e.getKey();
				}
			}
			return null;
		}
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(element, container);
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new IndexOf(element.reduce(), container.reduce());
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new IndexOf(element.substituteVariables(scope), container.substituteVariables(scope));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((container == null) ? 0 : container.hashCode());
		result = prime * result + ((element == null) ? 0 : element.hashCode());
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
		IndexOf other = (IndexOf) obj;
		if (container == null) {
			if (other.container != null)
				return false;
		}
		else if (!container.equals(other.container))
			return false;
		if (element == null) {
			if (other.element != null)
				return false;
		}
		else if (!element.equals(other.element))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		binaryOperator(sb, " in ", element, container, Precedence.APPEND);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.expression(element, Precedence.APPEND)
		.keyword(" in ")
		.expression(container, Precedence.APPEND);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.INDEXOF;
	}
}
