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

package org.phatonin.yadrol.app.cli.display;

import java.io.PrintStream;
import java.util.List;

import org.phatonin.yadrol.app.cli.CLIOptions;
import org.phatonin.yadrol.core.DiceRecord;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.MultiCount;
import org.phatonin.yadrol.core.RollRecord;
import org.phatonin.yadrol.core.SampleRecord;

public class DefaultDisplay extends DisplayManager {
	@Override
	protected void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
		TableSpecification tableSpec = buildTableSpecification(ctx.getSampleRecords());
		String[][] data = buildTableData(ctx, options, multiCounts);
		tableSpec.print(out, data);
	}

	private static TableSpecification buildTableSpecification(List<SampleRecord> sampleRecords) {
		TableSpecification result = new TableSpecification();
		RowSpecification rowSpec = new RowSpecification();
		rowSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
		for (int i = 0; i < sampleRecords.size(); ++i) {
			rowSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
		}
		rowSpec.insertSeparator("  ");
		result.setDefaultRowSpecification(rowSpec);
		return result;
	}
	
	@Override
	protected void writeRollRecord(PrintStream out, CLIOptions optiosn, RollRecord rollRecord) {
		out.println(rollRecord.getName());
		out.println(toString(rollRecord.getResult()));
	}

	@Override
	protected void writeDiceRecords(PrintStream out, CLIOptions options, String rollName, List<DiceRecord> diceRecords) {
		for (DiceRecord diceRecord : diceRecords) {
			displayDiceRecord(out, rollName, diceRecord);
		}
	}
	
	private static void displayDiceRecord(PrintStream out, String rollName, DiceRecord diceRecord) {
		List<Object> res = diceRecord.getResult();
		out.format("(%s) %dd%s: %s\n", rollName, res.size(), toString(diceRecord.getType()), toString(res));
	}
}
