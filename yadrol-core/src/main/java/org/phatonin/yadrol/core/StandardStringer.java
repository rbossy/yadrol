package org.phatonin.yadrol.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StandardStringer extends ExpressionStringer {
	@Override
	protected void leftParen(StringBuilder sb) {
		escapeAndAppend(sb, '(');
	}

	@Override
	protected void rightParen(StringBuilder sb) {
		escapeAndAppend(sb, ')');
	}

	@Override
	protected void leftCurly(StringBuilder sb) {
		escapeAndAppend(sb, '{');
	}

	@Override
	protected void rightCurly(StringBuilder sb) {
		escapeAndAppend(sb, '}');
	}

	@Override
	protected void leftBracket(StringBuilder sb) {
		escapeAndAppend(sb, '[');
	}

	@Override
	protected void rightBracket(StringBuilder sb) {
		escapeAndAppend(sb, ']');
	}

	@Override
	protected void space(StringBuilder sb) {
		escapeAndAppend(sb, ' ');
	}

	@Override
	protected void comma(StringBuilder sb) {
		escapeAndAppend(sb, ',');
	}

	@Override
	protected void colon(StringBuilder sb) {
		escapeAndAppend(sb, ':');
	}

	@Override
	protected void operator(StringBuilder sb, String op) {
		escapeAndAppend(sb, op);		
	}

	@Override
	protected void keyword(StringBuilder sb, String kw) {
		escapeAndAppend(sb, kw);
	}

	@Override
	protected void litteral(StringBuilder sb, String value) {
		escapeAndAppend(sb, value);
	}

	@Override
	protected void string(StringBuilder sb, String str) {
		escapeAndAppend(sb, '"');
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			switch (c) {
				case '"':
					escapeAndAppend(sb, "\\\"");
					break;
				case '\n':
					escapeAndAppend(sb, "\\n");
					break;
				case '\t':
					escapeAndAppend(sb, "\\t");
					break;
				case '\r':
					escapeAndAppend(sb, "\\r");
					break;
				default:
					escapeAndAppend(sb, c);
			}
		}
		escapeAndAppend(sb, '"');
	}

	private static final Pattern UNQUOTED_IDENTIFIER = Pattern.compile("[A-Z_a-z][0-9A-Z_a-z]*");

	@Override
	protected void identifier(StringBuilder sb, String var) {
		Matcher m = UNQUOTED_IDENTIFIER.matcher(var);
		if (m.matches()) {
			escapeAndAppend(sb, var);
		}
		else {
			escapeAndAppend(sb, '\'');
			for (int i = 0; i < var.length(); ++i) {
				char c = var.charAt(i);
				switch (c) {
					case '\'':
						escapeAndAppend(sb, "\\'");
						break;
					case '\n':
						escapeAndAppend(sb, "\\n");
						break;
					case '\t':
						escapeAndAppend(sb, "\\t");
						break;
					case '\r':
						escapeAndAppend(sb, "\\r");
						break;
					default:
						escapeAndAppend(sb, c);
				}
			}
			escapeAndAppend(sb, '\'');
		}
	}
}
