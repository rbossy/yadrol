package org.phatonin.yadrol.core;

import java.util.Map;

public interface ExpressionStringer {
	ExpressionStringer leftParen();
	ExpressionStringer rightParen();
	ExpressionStringer leftCurly();
	ExpressionStringer rightCurly();
	ExpressionStringer leftBracket();
	ExpressionStringer rightBracket();
	ExpressionStringer space();
	ExpressionStringer comma();
	ExpressionStringer colon();
	ExpressionStringer operator(String op);
	ExpressionStringer keyword(String kw);
	ExpressionStringer litteral(String value);
	ExpressionStringer string(String str);
	ExpressionStringer identifier(String var);
	ExpressionStringer expression(Expression expr, Precedence prec);
	ExpressionStringer unaryOperator(String op, Expression operand, Precedence prec);
	ExpressionStringer binaryOperator(String op, Expression left, Expression right, Precedence prec);
	ExpressionStringer nAryOperator(String op, Expression[] operands, Precedence prec);
	ExpressionStringer expressionList(Expression[] exprs);
	ExpressionStringer expressionMap(Map<String,Expression> exprs, boolean args);
}
