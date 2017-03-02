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

package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.Count;

public enum CountConverter implements JsonConverter<Count> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Count value, WebOptions options) {
		JSONObject result = new JSONObject();
		Object o = value.getValue();
		result.put("value", ValueConverter.INSTANCE.convert(o, options));
		result.put("frequency", value.getFrequency());
		result.put("at-least-frequency", value.getAtLeastFrequency());
		result.put("at-most-frequency", value.getAtMostFrequency());
		result.put("relative-frequency", value.getRelativeFrequency());
		result.put("relative-at-least-frequency", value.getRelativeAtLeastFrequency());
		result.put("relative-at-most-frequency", value.getRelativeAtMostFrequency());
		return result;
	}
}
