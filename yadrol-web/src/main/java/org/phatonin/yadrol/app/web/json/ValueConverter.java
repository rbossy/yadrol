package org.phatonin.yadrol.app.web.json;

import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueVisitor;

public class ValueConverter extends ValueVisitor<Object,WebOptions,RuntimeException> implements JsonConverter<Object> {
	public static final ValueConverter INSTANCE = new ValueConverter();
	
	private ValueConverter() {
		super();
	}

	@Override
	public Object convert(Object value, WebOptions options) {
		return visit(value, options);
	}

	@Override
	public Object visitUndef(WebOptions param) throws RuntimeException {
		return null;
	}

	@Override
	public Object visit(String value, WebOptions param) throws RuntimeException {
		return value;
	}

	@Override
	public Object visit(boolean value, WebOptions param) throws RuntimeException {
		return value;
	}

	@Override
	public Object visit(long value, WebOptions param) throws RuntimeException {
		return value;
	}

	@Override
	public Object visit(List<Object> value, WebOptions param) throws RuntimeException {
		return ConverterUtil.convert(value, this, param);
	}

	@Override
	public Object visit(Map<String,Object> value, WebOptions param) throws RuntimeException {
		return ConverterUtil.convert(value, this, param);
	}

	@Override
	public Object visit(Function value, WebOptions param) throws RuntimeException {
		return "<<FUN>>";
	}
}
