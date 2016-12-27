package org.phatonin.yadrol.core;

public class RegularStringer extends StandardStringer {
	@Override
	protected void escapeAndAppend(StringBuilder sb, String str) {
		sb.append(str);
	}

	@Override
	protected void escapeAndAppend(StringBuilder sb, char c) {
		sb.append(c);
	}
}
