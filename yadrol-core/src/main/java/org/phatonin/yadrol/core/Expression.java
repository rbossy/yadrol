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

package org.phatonin.yadrol.core;

import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Yadrol expression.
 * 
 *
 */
public interface Expression {
	/**
	 * Evaluates this expression and returns the result.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression.
	 * @param ctx
	 * @param scope
	 * @throws EvaluationException
	 */
	void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as a boolean.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as a string.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as an integer.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as a list.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as a map.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as a function.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException;
	
	/**
	 * Evaluates this expression as the specified type.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
	Object evaluate(EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException;

	/**
	 * Returns the native type of this expression.
	 * @return
	 */
	ValueType getReturnType();
	
	/**
	 * Returns either this expression can be a left value in an assignment.
	 * @return
	 */
	boolean isAssignable();
	
	/**
	 * Assign the specified value to this expression.
	 * @param ctx
	 * @param scope
	 * @param value
	 * @throws EvaluationException if this expression cannot be assigned.
	 */
	void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException;
	
	@Override int hashCode();
	
	@Override boolean equals(Object obj);
	
	/**
	 * Returns either this expression evaluation does not depend on the scope and the evaluation context.
	 * @return
	 * @throws EvaluationException 
	 */
	boolean isPureConstant() throws EvaluationException;
	
	/**
	 * Reduces this expression.
	 * Reduction simplifies expressions for nstance by removing singleton in compound expressions.
	 * @return
	 * @throws EvaluationException
	 */
	Expression reduce() throws EvaluationException;
	
	/**
	 * Substitute variables in this expression and sub-expressions with the variable values in the specified scope converted to constant or constructor expression.
	 * @param scope
	 * @return
	 */
	Expression substituteVariables(Scope scope);
	
	/**
	 * Converts this expressio into a string.
	 * The string can be re-parsed back into an equivalent expression.
	 * @param sb
	 * @param prec precedence of the expression that contains this expression.
	 */
	void toString(StringBuilder sb, Precedence prec);
	
	/**
	 * Returns either there needs to be a space between this expression and the dice operator (<code>'d'</code).
	 * @return
	 */
	boolean requiresSpaceAsDiceNumber();
	
	/**
	 * Returns either there needs to be a space between the dice operator (<code>'d'</code) and this expression.
	 * @return
	 */
	boolean requiresSpaceAsDiceType();
	
	/**
	 * Returns the location of this expression.
	 * @return
	 */
	Location getLocation();
}
