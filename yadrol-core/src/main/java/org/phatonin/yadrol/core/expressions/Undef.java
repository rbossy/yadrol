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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>undef</code>
 * 
 *
 */
public class Undef extends AbstractUndefExpression {
	public Undef(Location location) {
		super(location);
	}

	public Undef() {
		super();
	}

	@Override
	public AbstractUndefExpression reduce() {
		return this;
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return true;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return this;
	}

	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) {
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj instanceof Undef);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.litteral("undef");
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
