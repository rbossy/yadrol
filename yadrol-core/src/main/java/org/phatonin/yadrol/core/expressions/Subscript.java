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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueVisitor;

/**
 * <code>list[subscript]</code>
 * <code>list.subscript</code>
 * 
 *
 */
public class Subscript extends AbstractExpression {
	private final Expression list;
	private final Expression subscript;
	
	public Subscript(Expression list, Expression subscript) {
		super(list.getLocation());
		this.list = list;
		this.subscript = subscript;
	}

	public Expression getList() {
		return list;
	}

	public Expression getSubscript() {
		return subscript;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Subscript(list.reduce(), subscript.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(list, subscript);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Subscript(list, subscript.substituteVariables(scope));
	}

	public Object getElement(List<Object> list, int index) throws EvaluationException {
		try {
			return list.get(index - 1);
		}
		catch (IndexOutOfBoundsException e) {
			throw indexOutOfBounds(list, index);
		}
	}
	
	private EvaluationException indexOutOfBounds(List<Object> list, long index) {
		return new EvaluationException(this, String.format("length: %d, index: %d", list.size(), index));
	}

	public Object getElement(Object container, Object subscript) throws EvaluationException {
		Object result = new GetSubscript().visit(subscript, container);
		Function fun = EvaluationContext.asFunction(result);
		if (fun == null) {
			return result;
		}
		return fun.reassignOwner(container);
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object list = this.list.evaluate(ctx, scope);
			Object subscript = this.subscript.evaluate(ctx, scope);
			return getElement(list, subscript);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	private class GetSubscript extends ValueVisitor<Object,Object,EvaluationException> {
		@Override
		public Object visitUndef(Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: undef");
		}

		@Override
		public Object visit(String value, Object param) throws EvaluationException {
			Map<String,Object> map = EvaluationContext.asMap(param);
			if (map == null) {
				throw new EvaluationException(Subscript.this, "invalid container for string subscript");
			}
			return map.get(value);
		}

		@Override
		public Object visit(boolean value, Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
		}

		@Override
		public Object visit(long value, Object param) throws EvaluationException {
			List<Object> list = EvaluationContext.asList(param);
			if (list == null) {
				throw new EvaluationException(Subscript.this, "invalid container for integer subscript");
			}
			return getElement(list, (int) value);
		}

		@Override
		public Object visit(List<Object> value, Object param) throws EvaluationException {
			List<Object> result = new ArrayList<Object>(value.size());
			for (Object v : value) {
				Object sv = visit(v, param);
				result.add(sv);
			}
			return result;
		}

		@Override
		public Object visit(Map<String,Object> value, Object param) throws EvaluationException {
			Map<String,Object> result = new HashMap<String,Object>();
			for (Map.Entry<String,Object> e : value.entrySet()) {
				Object v = e.getValue();
				Object sv = visit(v, param);
				result.put(e.getKey(), sv);
			}
			return result;
		}

		@Override
		public Object visit(Function value, Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
		}
	};

	@Override
	public boolean isAssignable() {
		return true;
	}

	@Override
	public void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException {
		try {
			Object container = list.evaluate(ctx, scope);
			SubscriptAssignVisitor visitor = new SubscriptAssignVisitor(ctx, scope, container);
			Object subscript = this.subscript.evaluate(ctx, scope);
			visitor.visit(subscript, value);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	private class SubscriptAssignVisitor extends ValueVisitor<Void,Object,EvaluationException> {
		private final EvaluationContext ctx;
		private final Scope scope;
		private final Object container;
	
		private SubscriptAssignVisitor(EvaluationContext ctx, Scope scope, Object container) {
			super();
			this.ctx = ctx;
			this.scope = scope;
			this.container = container;
		}

		@Override
		public Void visitUndef(Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: undef");
		}

		@Override
		public Void visit(String value, Object param) throws EvaluationException {
			Map<String,Object> map = EvaluationContext.asMap(container);
			if (map == null) {
				throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
			}
			map.put(value, param);
			return null;
		}

		@Override
		public Void visit(boolean value, Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
		}

		@Override
		public Void visit(long value, Object param) throws EvaluationException {
			List<Object> list = EvaluationContext.asList(container);
			if (list == null) {
				throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
			}
			try {
				list.set((int) value - 1, param);
			}
			catch (IndexOutOfBoundsException e) {
				throw indexOutOfBounds(list, value);
			}
			return null;
		}

		@Override
		public Void visit(List<Object> value, Object param) throws EvaluationException {
			List<Object> list = ctx.valueToList(scope, param);
			for (int i = 0; i < value.size(); ++i) {
				Object v = value.get(i);
				Object p = i < list.size() ? list.get(i) : null;
				visit(v, p);
			}
			return null;
		}

		@Override
		public Void visit(Map<String,Object> value, Object param) throws EvaluationException {
			Map<String,Object> map = ctx.valueToMap(scope, param);
			for (Map.Entry<String,Object> e : value.entrySet()) {
				Object v = e.getValue();
				Object p = map.get(e.getKey());
				visit(v, p);
			}
			return null;
		}

		@Override
		public Void visit(Function value, Object param) throws EvaluationException {
			throw new EvaluationException(Subscript.this, "invalid subscript: " + value);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((subscript == null) ? 0 : subscript.hashCode());
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
		Subscript other = (Subscript) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		}
		else if (!list.equals(other.list))
			return false;
		if (subscript == null) {
			if (other.subscript != null)
				return false;
		}
		else if (!subscript.equals(other.subscript))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		list.toString(sb, Precedence.SUBSCRIPT);
		if (subscript instanceof StringConstant) {
			String name = ((StringConstant) subscript).getValue();
			sb.append('.');
			identifierToString(sb, name);
		}
		else {
			sb.append('[');
			subscript.toString(sb, Precedence.SEQUENCE);
			sb.append(']');
		}
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.SUBSCRIPT;
	}
}
