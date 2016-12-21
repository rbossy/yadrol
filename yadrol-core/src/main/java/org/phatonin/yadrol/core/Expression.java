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


public interface Expression {
	Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException;
	void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException;
	boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException;
	String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException;
	long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException;
	List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException;
	Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException;
	Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException;
	Object evaluate(EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException;
	ValueType getReturnType();
	boolean isAssignable();
	void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException;
	@Override int hashCode();
	@Override boolean equals(Object obj);
	boolean isPureConstant() throws EvaluationException;
	Expression reduce() throws EvaluationException;
	Expression substituteVariables(Scope scope);
	void toString(StringBuilder sb, Precedence prec);
	boolean requiresSpaceAsDiceNumber();
	boolean requiresSpaceAsDiceType();
	Location getLocation();
}
