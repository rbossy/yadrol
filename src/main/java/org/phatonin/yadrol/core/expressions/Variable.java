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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

public class Variable extends AbstractExpression {
	private final String name;

	public Variable(Location location, String name) {
		super(location);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Expression reduce() {
		return this;
	}

	@Override
	public boolean isPureConstant() {
		return false;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		if (scope.hasVariable(name)) {
			return EvaluationContext.valueToExpression(scope.getVariable(name));
		}
		return this;
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) {
		return scope.getVariable(name);
	}

	@Override
	public boolean isAssignable() {
		return true;
	}

	@Override
	public void assign(EvaluationContext ctx, Scope scope, Object value) {
		scope.setVariable(name, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		identifierToString(sb, name);
	}
	
	private boolean isCapitalized() {
		return Character.isUpperCase(name.charAt(0));
	}

	@Override
	public boolean requiresSpaceAsDiceNumber() {
		return !(name.length() == 1 && isCapitalized());
	}

	@Override
	public boolean requiresSpaceAsDiceType() {
		return !isCapitalized();
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
