package org.phatonin.yadrol.app.web.json;

import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.Expression;

public enum ExpressionConverter implements JsonConverter<Expression> {
	INSTANCE;

	@Override
	public Object convert(Expression value, WebOptions options) {
		return value.toString();
	}
}
