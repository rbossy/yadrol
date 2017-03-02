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

public class TaggingStringer extends ExpressionStringer {
	private final ExpressionStringer delegate;

	private String beforeSymbol = "";
	private String afterSymbol = "";
	private String beforeOperator = "";
	private String afterOperator = "";
	private String beforeKeyword = "";
	private String afterKeyword = "";
	private String beforeLitteral = "";
	private String afterLitteral = "";
	private String beforeIdentifier = "";
	private String afterIdentifier = "";

	public TaggingStringer(ExpressionStringer delegate) {
		super();
		this.delegate = delegate;
	}

	public ExpressionStringer getDelegate() {
		return delegate;
	}

	public String getBeforeSymbol() {
		return beforeSymbol;
	}

	public String getAfterSymbol() {
		return afterSymbol;
	}

	public String getBeforeOperator() {
		return beforeOperator;
	}

	public String getAfterOperator() {
		return afterOperator;
	}

	public String getBeforeKeyword() {
		return beforeKeyword;
	}

	public String getAfterKeyword() {
		return afterKeyword;
	}

	public String getBeforeLitteral() {
		return beforeLitteral;
	}

	public String getAfterLitteral() {
		return afterLitteral;
	}

	public String getBeforeIdentifier() {
		return beforeIdentifier;
	}

	public String getAfterIdentifier() {
		return afterIdentifier;
	}

	public void setBeforeSymbol(String beforeSymbol) {
		this.beforeSymbol = beforeSymbol;
	}

	public void setAfterSymbol(String afterSymbol) {
		this.afterSymbol = afterSymbol;
	}

	public void setBeforeOperator(String beforeOperator) {
		this.beforeOperator = beforeOperator;
	}

	public void setAfterOperator(String afterOperator) {
		this.afterOperator = afterOperator;
	}

	public void setBeforeKeyword(String beforeKeyword) {
		this.beforeKeyword = beforeKeyword;
	}

	public void setAfterKeyword(String afterKeyword) {
		this.afterKeyword = afterKeyword;
	}

	public void setBeforeLitteral(String beforeLitteral) {
		this.beforeLitteral = beforeLitteral;
	}

	public void setAfterLitteral(String afterLitteral) {
		this.afterLitteral = afterLitteral;
	}

	public void setBeforeIdentifier(String beforeIdentifier) {
		this.beforeIdentifier = beforeIdentifier;
	}

	public void setAfterIdentifier(String afterIdentifier) {
		this.afterIdentifier = afterIdentifier;
	}

	@Override
	protected void leftParen(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.leftParen(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void rightParen(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.rightParen(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void leftCurly(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.leftCurly(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void rightCurly(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.rightCurly(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void leftBracket(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.leftBracket(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void rightBracket(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.rightBracket(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void space(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.space(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void comma(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.comma(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void colon(StringBuilder sb) {
		sb.append(beforeSymbol);
		delegate.colon(sb);
		sb.append(afterSymbol);
	}

	@Override
	protected void operator(StringBuilder sb, String op) {
		sb.append(beforeOperator);
		delegate.operator(sb, op);
		sb.append(afterOperator);
	}

	@Override
	protected void keyword(StringBuilder sb, String kw) {
		sb.append(beforeKeyword);
		delegate.keyword(sb, kw);
		sb.append(afterKeyword);
	}

	@Override
	protected void litteral(StringBuilder sb, String value) {
		sb.append(beforeLitteral);
		delegate.litteral(sb, value);
		sb.append(afterLitteral);
	}

	@Override
	protected void string(StringBuilder sb, String str) {
		sb.append(beforeLitteral);
		delegate.string(sb, str);
		sb.append(afterLitteral);
	}

	@Override
	protected void identifier(StringBuilder sb, String var) {
		sb.append(beforeIdentifier);
		delegate.identifier(var);
		sb.append(afterIdentifier);
	}

	@Override
	protected void escapeAndAppend(StringBuilder sb, String str) {
		delegate.escapeAndAppend(sb, str);
	}

	@Override
	protected void escapeAndAppend(StringBuilder sb, char c) {
		delegate.escapeAndAppend(sb, c);
	}
}
