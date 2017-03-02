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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.parser.ParseException;
import org.phatonin.yadrol.core.parser.YadrolParser;

/**
 * <code>"originalString"</code>
 * 
 *
 */
public class StringInterpolation extends AbstractStringExpression {
	private static interface Generator {
		void generate(EvaluationContext ctx, Scope scope, StringBuilder sb) throws EvaluationException;
		String generate(EvaluationContext ctx, Scope scope) throws EvaluationException;
		boolean isPureConstant() throws EvaluationException;
		Generator reduce() throws EvaluationException;
		Generator substituteVariables(Scope scope);
	}

	private static class Litteral implements Generator {
		private final String value;

		private Litteral(String value) {
			super();
			this.value = value;
		}

		@Override
		public void generate(EvaluationContext ctx, Scope scope, StringBuilder sb) throws EvaluationException {
			sb.append(value);
		}

		@Override
		public String generate(EvaluationContext ctx, Scope scope) throws EvaluationException {
			return value;
		}

		@Override
		public boolean isPureConstant() {
			return true;
		}

		@Override
		public Generator reduce() throws EvaluationException {
			return this;
		}

		@Override
		public Generator substituteVariables(Scope scope) {
			return this;
		}
	}
	
	private static abstract class NonConstantGenerator implements Generator {
		@Override
		public String generate(EvaluationContext ctx, Scope scope) throws EvaluationException {
			StringBuilder sb = new StringBuilder();
			generate(ctx, scope, sb);
			return sb.toString();
		}
	}
	
	private static class Replacement extends NonConstantGenerator {
		private final Expression expr;

		private Replacement(Expression expr) {
			super();
			this.expr = expr;
		}

		@Override
		public Generator substituteVariables(Scope scope) {
			return new Replacement(expr.substituteVariables(scope));
		}

		@Override
		public void generate(EvaluationContext ctx, Scope scope, StringBuilder sb) throws EvaluationException {
			String value = expr.evaluateString(ctx, scope);
			sb.append(value);
		}

		@Override
		public boolean isPureConstant() throws EvaluationException {
			return expr.isPureConstant();
		}

		@Override
		public Generator reduce() throws EvaluationException {
			if (expr.isPureConstant()) {
				String value = expr.evaluateString(null, null);
				return new Litteral(value);
			}
			return new Replacement(expr.reduce());
		}
	}
	
	private static class GeneratorSequence extends NonConstantGenerator {
		private final List<Generator> generators;

		private GeneratorSequence(List<Generator> generators) {
			super();
			this.generators = generators;
		}

		@Override
		public Generator substituteVariables(Scope scope) {
			List<Generator> generators = new ArrayList<Generator>(this.generators.size());
			for (Generator gen : this.generators) {
				generators.add(gen.substituteVariables(scope));
			}
			return new GeneratorSequence(generators);
		}

		@Override
		public void generate(EvaluationContext ctx, Scope scope, StringBuilder sb) throws EvaluationException {
			for (Generator gen : generators) {
				gen.generate(ctx, scope, sb);
			}
		}
	
		@Override
		public boolean isPureConstant() throws EvaluationException {
			for (Generator gen : generators) {
				if (!gen.isPureConstant()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Generator reduce() throws EvaluationException {
			ListIterator<Generator> lit = generators.listIterator();
			while (lit.hasNext()) {
				Generator gen = lit.next();
				lit.set(gen.reduce());
			}
			return this;
		}
	}
	
	private final String originalString;
	private final Generator generator;

	private StringInterpolation(Location location, String originalString, Generator generator) {
		super(location);
		this.originalString = originalString;
		this.generator = generator;
	}
	
	public StringInterpolation(Location location, String originalString) throws ParseException {
		this(location, originalString, createGenerator(location, originalString));
	}
	
	public StringInterpolation(Expression expression) {
		this(expression.getLocation(), expression.toString(), new Litteral(expression.toString()));
	}
	
	private static Generator createGenerator(Location location, String originalString) throws ParseException {
		YadrolParser parser = new YadrolParser((Reader) null);
		List<Generator> generators = new ArrayList<Generator>();
		StringBuilder buf = new StringBuilder();
		boolean escape = false;
		boolean inReplacement = false;
		for (int i = 0; i < originalString.length(); ++i) {
			char c = originalString.charAt(i);
			if (escape) {
				buf.append(escapedChar(c));
				continue;
			}
			if (c == '\\') {
				escape = true;
				continue;
			}
			if (c == '$') {
				if (inReplacement) {
					finishReplacement(location, generators, buf, parser);
					inReplacement = false;
					continue;
				}
				finishLitteral(generators, buf);
				inReplacement = true;
				continue;
			}
			buf.append(c);
		}
		finishLitteral(generators, buf);
		switch (generators.size()) {
			case 0: return new Litteral("");
			case 1: return generators.get(0);
			default: return new GeneratorSequence(generators);
		}
	}
	
	private static char escapedChar(char c) {
		switch (c) {
			case 'n': return '\n';
			case 't': return '\t';
			case 'r': return '\r';
			default: return c;
		}
	}
	
	private static void finishReplacement(Location location, List<Generator> generators, StringBuilder buf, YadrolParser parser) throws ParseException {
		Reader reader = new StringReader(buf.toString());
		parser.ReInit(reader);
		Expression expr = parser.expression(location.getSource(), location.getColumn());
		generators.add(new Replacement(expr));
		buf.setLength(0);
	}
	
	private static void finishLitteral(List<Generator> generators, StringBuilder buf) {
		if (buf.length() > 0) {
			generators.add(new Litteral(buf.toString()));
			buf.setLength(0);
		}
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return generator.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new StringInterpolation(getLocation(), originalString, generator.substituteVariables(scope));
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		return generator.generate(ctx, scope);
	}


	@Override
	public AbstractStringExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new StringInterpolation(getLocation(), originalString, generator.reduce());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((originalString == null) ? 0 : originalString.hashCode());
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
		StringInterpolation other = (StringInterpolation) obj;
		if (originalString == null) {
			if (other.originalString != null)
				return false;
		}
		else if (!originalString.equals(other.originalString))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.litteral("\"")
		.litteral(originalString)
		.litteral("\"");
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
