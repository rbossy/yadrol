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

import java.util.Map;

import org.phatonin.yadrol.core.expressions.Undef;

public abstract class ExpressionStringer {
	private final StringBuilder sb = new StringBuilder();

	public ExpressionStringer leftParen() {
		leftParen(sb);
		return this;
	}
	
	protected abstract void leftParen(StringBuilder sb);
	
	public ExpressionStringer rightParen() {
		rightParen(sb);
		return this;
	}
	
	protected abstract void rightParen(StringBuilder sb);
	
	public ExpressionStringer leftCurly() {
		leftCurly(sb);
		return this;
	}
	
	protected abstract void leftCurly(StringBuilder sb);
	
	public ExpressionStringer rightCurly() {
		rightCurly(sb);
		return this;
	}
	
	protected abstract void rightCurly(StringBuilder sb);
	
	public ExpressionStringer leftBracket() {
		leftBracket(sb);
		return this;
	}
	
	protected abstract void leftBracket(StringBuilder sb);
	
	public ExpressionStringer rightBracket() {
		rightBracket(sb);
		return this;
	}
	
	protected abstract void rightBracket(StringBuilder sb);
	
	public ExpressionStringer space() {
		space(sb);
		return this;
	}
	
	protected abstract void space(StringBuilder sb);
	
	public ExpressionStringer comma() {
		comma(sb);
		return this;
	}
	
	protected abstract void comma(StringBuilder sb);
	
	public ExpressionStringer colon() {
		colon(sb);
		return this;
	}
	
	protected abstract void colon(StringBuilder sb);

	public ExpressionStringer operator(String op) {
		operator(sb, op);
		return this;
	}
	
	protected abstract void operator(StringBuilder sb, String op);

	public ExpressionStringer keyword(String kw) {
		keyword(sb, kw);
		return this;
	}
	
	protected abstract void keyword(StringBuilder sb, String kw);

	public ExpressionStringer litteral(String value) {
		litteral(sb, value);
		return this;
	}
	
	protected abstract void litteral(StringBuilder sb, String value);

	public ExpressionStringer string(String str) {
		string(sb, str);
		return this;
	}
	
	protected abstract void string(StringBuilder sb, String str);

	public ExpressionStringer identifier(String var) {
		identifier(sb, var);
		return this;
	}
	
	protected abstract void identifier(StringBuilder sb, String var);

	public ExpressionStringer expression(Expression expr, Precedence prec) {
		expr.toString(this, prec);
		return this;
	}

	public ExpressionStringer unaryOperator(String op, Expression operand, Precedence prec) {
		return operator(op)
				.expression(operand, prec);
	}
	
	public ExpressionStringer binaryOperator(String op, Expression left, Expression right, Precedence prec) {
		return expression(left, prec)
				.operator(op)
				.expression(right, prec);
	}

	public ExpressionStringer nAryOperator(String op, Expression[] operands, Precedence prec) {
		boolean first = true;
		for (Expression e : operands) {
			if (first) {
				first = false;
			}
			else {
				operator(op);
			}
			expression(e, prec);
		}
		return this;
	}

	public ExpressionStringer expressionList(Expression[] exprs) {
		boolean first = true;
		for (Expression e : exprs) {
			if (first) {
				first = false;
			}
			else {
				comma().space();
			}
			expression(e, Precedence.SEQUENCE);
		}
		return this;		
	}

	public ExpressionStringer expressionMap(Map<String,Expression> exprs, boolean args) {
		boolean first = true;
		for (Map.Entry<String,Expression> e : exprs.entrySet()) {
			if (first) {
				first = false;
			}
			else {
				comma().space();
			}
			identifier(e.getKey());
			Expression value = e.getValue();
			if (args) {
				if (new Undef(Location.NONE).equals(value)) {
					continue;
				}
				args = false;
			}
			colon().space();
			expression(value, Precedence.SEQUENCE);
		}
		return this;
	}
	
	protected abstract void escapeAndAppend(StringBuilder sb, String str);
	
	protected ExpressionStringer append(String str) {
		escapeAndAppend(sb, str);
		return this;
	}
	
	protected abstract void escapeAndAppend(StringBuilder sb, char c);
	
	protected ExpressionStringer append(char c) {
		escapeAndAppend(sb, c);
		return this;
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
	public ExpressionStringer clear() {
		sb.setLength(0);
		return this;
	}
}
