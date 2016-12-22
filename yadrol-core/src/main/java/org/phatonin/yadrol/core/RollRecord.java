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

package org.phatonin.yadrol.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.phatonin.yadrol.core.values.ValueType;

/**
 * A roll record stores the final result of the evaluation of an expression.
 * 
 *
 */
public class RollRecord extends OutputRecord {
	private final List<DiceRecord> diceRecords = new ArrayList<DiceRecord>();
	private Object result;
	
	RollRecord(String name, Expression expression, ValueType evaluationType) {
		super(name, expression, evaluationType);
	}

	/**
	 * Returns the result recorded.
	 * @return
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * Returns the dice records stored during the evaluation.
	 * @return
	 */
	public List<DiceRecord> getDiceRecords() {
		return Collections.unmodifiableList(diceRecords);
	}

	void setResult(Object result) {
		this.result = result;
	}

	void addDiceRecord(DiceRecord rec) {
		diceRecords.add(rec);
	}
}
