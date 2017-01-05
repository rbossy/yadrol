package org.phatonin.yadrol.app.web.json;

import org.phatonin.yadrol.app.web.WebOptions;

public interface JsonConverter<T> {
	Object convert(T value, WebOptions options);
}
