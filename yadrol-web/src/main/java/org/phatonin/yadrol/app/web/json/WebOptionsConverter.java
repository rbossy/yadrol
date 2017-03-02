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
import org.phatonin.yadrol.app.web.WebParamDispatcher;

public enum WebOptionsConverter implements JsonConverter<WebOptions> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(WebOptions options, WebOptions duh) {
		JSONObject result = new JSONObject();
		for (WebParamDispatcher wpd : WebParamDispatcher.values()) {
			String value = wpd.getParam(options);
			result.put(wpd.name, value);
		}
		return result;
	}
}
