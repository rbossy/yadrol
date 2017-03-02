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

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.RollRecord;

public enum RollRecordConverter implements JsonConverter<RollRecord> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(RollRecord value, WebOptions options) {
		JSONObject result = new JSONObject();
		result.put("name", value.getName());
		result.put("dice-records", ConverterUtil.convert(value.getDiceRecords(), DiceRecordConverter.INSTANCE, options));
		result.put("result", ValueConverter.INSTANCE.convert(value.getResult(), options));
		result.put("string-result", EvaluationContext.valueToExpression(value.getResult()).toString());
		return result;
	}
}
