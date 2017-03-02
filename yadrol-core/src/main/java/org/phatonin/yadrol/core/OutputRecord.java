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

package org.phatonin.yadrol.core;

import org.phatonin.yadrol.core.values.ValueType;

/**
 * Base class for outputs required during the evaluation of expressions.
 * 
 *
 */
public abstract class OutputRecord {
	private final String name;
	private final Expression expression;
	private final ValueType evaluationType;
	
	protected OutputRecord(String name, Expression expression, ValueType evaluationType) {
		super();
		this.name = name;
		this.expression = expression;
		this.evaluationType = evaluationType;
	}

	/**
	 * Returns the name of the output.
	 * By default the name is a string representation of the expression that produced the output.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the expression that produced this output.
	 * @return
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Returns the type of the evaluation that produced this output.
	 * @return
	 */
	public ValueType getEvaluationType() {
		return evaluationType;
	}
}
