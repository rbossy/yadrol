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
