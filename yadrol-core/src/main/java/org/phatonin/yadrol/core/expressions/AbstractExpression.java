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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Base class for expressions.
 * 
 *
 */
public abstract class AbstractExpression implements Expression {
	private Location location;
	
	protected AbstractExpression() {
		super();
	}

	protected AbstractExpression(Location location) {
		super();
		this.location = location;
	}
	
	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		evaluate(ctx, scope);
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.valueToBoolean(scope, evaluate(ctx, scope));
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.valueToString(evaluate(ctx, scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.valueToInteger(scope, evaluate(ctx, scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.valueToList(scope, evaluate(ctx, scope));
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return ctx.valueToMap(scope, evaluate(ctx, scope));
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return EvaluationContext.valueToFunction(evaluate(ctx, scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException {
		switch (type) {
			case BOOLEAN: return evaluateBoolean(ctx, scope);
			case FUNCTION: return evaluateFunction(ctx, scope);
			case INTEGER: return evaluateInteger(ctx, scope);
			case LIST: return evaluateList(ctx, scope);
			case MAP: return evaluateMap(ctx, scope);
			case STRING: return evaluateString(ctx, scope);
			case UNDEF: evaluateUndef(ctx, scope); return null;
			case ANY: return evaluate(ctx, scope);
			case DEFAULT: return evaluate(ctx, scope, ctx.getDefaultEvaluationType());
		}
		throw new RuntimeException();
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.ANY;
	}

	@Override
	public boolean isAssignable() {
		return false;
	}

	@Override
	public void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException {
		throw new EvaluationException(this, "not assignable");
	}

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	/**
	 * Reduce each expression in the specified map.
	 * @param map
	 * @return
	 * @throws EvaluationException
	 */
	protected static Map<String,Expression> reduce(Map<String,Expression> map) throws EvaluationException {
		for (Map.Entry<String,Expression> e : map.entrySet()) {
			e.setValue(e.getValue().reduce());
		}
		return map;
	}
	
	/**
	 * Either all expressions in the specified collection are pure constants.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static boolean isPureConstant(Collection<Expression> expressions) throws EvaluationException {
		for (Expression e : expressions) {
			if (!e.isPureConstant()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Either all expressions in the specified map are pure constants.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static boolean isPureConstant(Map<String,Expression> expressions) throws EvaluationException {
		for (Expression e : expressions.values()) {
			if (!e.isPureConstant()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Either all expressions in the specified array are pure constants.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static boolean isPureConstant(Expression... expressions) throws EvaluationException {
		return isPureConstant(Arrays.asList(expressions));
	}
	
	/**
	 * Calls {@link Expression#substituteVariables(Scope)} on each element of the specified list.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static List<Expression> substituteVariables(List<Expression> expressions, Scope scope) {
		List<Expression> result = new ArrayList<Expression>(expressions.size());
		for (Expression e : expressions) {
			result.add(e.substituteVariables(scope));
		}
		return result;
	}
	
	/**
	 * Calls {@link Expression#substituteVariables(Scope)} on each element of the specified array.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static List<Expression> substituteVariables(Expression[] expressions, Scope scope) {
		return substituteVariables(Arrays.asList(expressions), scope);
	}
	
	/**
	 * Calls {@link Expression#substituteVariables(Scope)} on each value of the specified map.
	 * @param expressions
	 * @return
	 * @throws EvaluationException
	 */
	protected static Map<String,Expression> substituteVariables(Map<String,Expression> map, Scope scope) {
		Map<String,Expression> result = new HashMap<String,Expression>();
		for (Map.Entry<String,Expression> e : map.entrySet()) {
			result.put(e.getKey(), e.getValue().substituteVariables(scope));
		}
		return result;
	}
	
	/**
	 * Evaluates this expression and converts the result back to a constant or a constructor.
	 * @return
	 * @throws EvaluationException if this expression is not a pure constant.
	 */
	protected Expression pureExpression() throws EvaluationException {
		Object value = evaluate(null, null);
		return EvaluationContext.valueToExpression(value);
	}
	
	/**
	 * Returns either this expression needs to be enclosed with parentheses wrt the specified precedence.
	 * @param prec
	 * @return
	 */
	protected boolean requiresParentheses(Precedence prec) {
		return prec.compareTo(getPrecedence()) > 0;
	}

	@Override
	public void toString(StringBuilder sb, Precedence prec) {
		if (requiresParentheses(prec)) {
			sb.append('(');
			toStringWithoutParen(sb);
			sb.append(')');
		}
		else {
			toStringWithoutParen(sb);
		}
	}
	
	@Override
	public boolean requiresSpaceAsDiceNumber() {
		return !requiresParentheses(Precedence.UNARY);
	}
	
	@Override
	public boolean requiresSpaceAsDiceType() {
		return !requiresParentheses(Precedence.UNARY);
	}

	/**
	 * Converts a binary operator to string.
	 * @param sb
	 * @param operator
	 * @param left
	 * @param right
	 * @param prec
	 */
	protected static void binaryOperator(StringBuilder sb, CharSequence operator, Expression left, Expression right, Precedence prec) {
		left.toString(sb, prec);
		sb.append(operator);
		right.toString(sb, prec);
	}
	
	/**
	 * Converts an unary operator to string.
	 * @param sb
	 * @param operator
	 * @param expr
	 * @param prec
	 */
	protected static void unaryOperator(StringBuilder sb, String operator, Expression expr, Precedence prec) {
		sb.append(operator);
		expr.toString(sb, prec);
	}
	
	/**
	 * Converts a n-ary operator to string.
	 * @param sb
	 * @param operator
	 * @param expressions
	 * @param prec
	 */
	protected static void nAryOperator(StringBuilder sb, String operator, Expression[] expressions, Precedence prec) {
		boolean first = true;
		for (Expression e : expressions) {
			if (first) {
				first = false;
			}
			else {
				sb.append(operator);
			}
			e.toString(sb, prec);
		}
	}
	
	/**
	 * Converts the specified expression array to string (separated by commas).
	 * @param sb
	 * @param expressions
	 */
	protected static void expressionListToString(StringBuilder sb, Expression[] expressions) {
		nAryOperator(sb, ", ", expressions, Precedence.SEQUENCE);
	}
	
	private static final Pattern UNQUOTED_IDENTIFIER = Pattern.compile("[A-Z_a-z][0-9A-Z_a-z]*");
	
	/**
	 * Converts the specified variable identifier to string.
	 * @param sb
	 * @param name
	 */
	protected static void identifierToString(StringBuilder sb, String name) {
		Matcher m = UNQUOTED_IDENTIFIER.matcher(name);
		if (m.matches()) {
			sb.append(name);
		}
		else {
			sb.append('\'');
			for (int i = 0; i < name.length(); ++i) {
				char c = name.charAt(i);
				switch (c) {
					case '\'':
						sb.append("\\'");
						break;
					case '\n':
						sb.append("\\n");
						break;
					case '\t':
						sb.append("\\t");
						break;
					case '\r':
						sb.append("\\r");
						break;
					default:
						sb.append(c);
				}
			}
			sb.append('\'');
		}
	}
	
	/**
	 * Convert the specified string constant into a string litteral.
	 * @param sb
	 * @param value
	 */
	protected static void stringConstant(StringBuilder sb, String value) {
		sb.append('"');
		for (int i = 0; i < value.length(); ++i) {
			char c = value.charAt(i);
			switch (c) {
				case '"':
					sb.append("\\\"");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					sb.append(c);
			}
		}
		sb.append('"');
	}
	
	/**
	 * Convert the specified expression map to string.
	 * @param sb
	 * @param map
	 * @param args
	 */
	protected static void expressionMapToString(StringBuilder sb, Map<String,Expression> map, boolean args) {
		boolean first = true;
		for (Map.Entry<String,Expression> e : map.entrySet()) {
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}
			identifierToString(sb, e.getKey());
			Expression value = e.getValue();
			if (args) {
				if (new Undef(Location.NONE).equals(value)) {
					continue;
				}
				args = false;
			}
			sb.append(": ");
			e.getValue().toString(sb, Precedence.SEQUENCE);
		}
	}
	
	/**
	 * Convert this expression to string.
	 * @param sb
	 */
	protected abstract void toStringWithoutParen(StringBuilder sb);
	
	/**
	 * Returns the precedence of the operator used to build this expression.
	 * @return
	 */
	protected abstract Precedence getPrecedence();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, Precedence.SEQUENCE);
		return sb.toString();
	}

	@Override
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Set this expression location.
	 * @param location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * Set this expression location as the same as the specified expression.
	 * @param expr
	 */
	protected void infixLocation(Expression expr) {
		setLocation(expr.getLocation());
	}
	
	/**
	 * Set this expression location as the same as the first expression in the specified array.
	 * @param expressions
	 */
	protected void infixLocation(Expression[] expressions) {
		setLocation(expressions.length == 0 ? Location.NONE : expressions[0].getLocation());
	}
	
	/**
	 * Complete the expression stack of the specified evaluation exception.
	 * @param e
	 * @return
	 */
	protected EvaluationException completeStack(EvaluationException e) {
		e.appendToExpressionStack(this);
		return e;
	}
}
