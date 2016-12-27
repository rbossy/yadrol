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
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>"value"</code>
 * 
 *
 */
public class StringConstant extends AbstractStringExpression {
	private final String value;

	public StringConstant(Location location, String value) {
		super(location);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public AbstractStringExpression reduce() {
		return this;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return this;
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringConstant other = (StringConstant) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.string(value);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
