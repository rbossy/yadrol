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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception raised during the evaluation.
 * 
 *
 */
@SuppressWarnings("serial")
public class EvaluationException extends Exception {
	private final List<Expression> expressionStack = new ArrayList<Expression>();
	
	public EvaluationException(Expression expression) {
		super();
		expressionStack.add(expression);
	}

	public EvaluationException(Expression expression, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		expressionStack.add(expression);
	}

	public EvaluationException(Expression expression, String message, Throwable cause) {
		super(message, cause);
		expressionStack.add(expression);
	}

	public EvaluationException(Expression expression, String message) {
		super(message);
		expressionStack.add(expression);
	}

	public EvaluationException(Expression expression, Throwable cause) {
		super(cause);
		expressionStack.add(expression);
	}

	/**
	 * Returns the stack of expressions from which this exception was raised.
	 * @return
	 */
	public List<Expression> getExpressionStack() {
		return Collections.unmodifiableList(expressionStack);
	}
	
	/**
	 * Appends the specified expression to the expression stack.
	 * @param expression
	 */
	public void appendToExpressionStack(Expression expression) {
		expressionStack.add(expression);
	}
}
