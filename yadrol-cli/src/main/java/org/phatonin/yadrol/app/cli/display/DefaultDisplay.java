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
	public static final String COLOR_RESET = "\u001B[0m";
	public static final String COLOR_NORMAL = "\u001B[37;1m";
	public static final String COLOR_BRIGHT = "\u001B[37;1m";
	public static final String COLOR_HIGHLIGHT = "\u001B[31m;1";
	public static final String COLOR_BRIGHT_HIGHLIGHT = "\u001B[31m";

	private final boolean color;
	
	public DefaultDisplay(boolean color) {
		super();
		this.color = color;
	}

	@Override
	protected void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
		TableSpecification tableSpec = buildTableSpecification(options, ctx.getSampleRecords());
		String[][] data = buildTableData(ctx, options, multiCounts);
		tableSpec.print(out, data);
	}

	private TableSpecification buildTableSpecification(CLIOptions options, List<SampleRecord> sampleRecords) {
		TableSpecification result = new TableSpecification();
		RowSpecification rowSpec = new RowSpecification();
		RowSpecification highlightSpec = new RowSpecification();
		if (color) {
			rowSpec.appendColumnSpecification(new ConstantColumnSpecification(COLOR_BRIGHT));
			highlightSpec.appendColumnSpecification(new ConstantColumnSpecification(COLOR_BRIGHT_HIGHLIGHT));
		}
		rowSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
		highlightSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
		if (color) {
			rowSpec.appendColumnSpecification(new ConstantColumnSpecification(COLOR_RESET));
			highlightSpec.appendColumnSpecification(new ConstantColumnSpecification(COLOR_RESET + COLOR_HIGHLIGHT));
		}
		for (int i = 0; i < sampleRecords.size(); ++i) {
			rowSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
			highlightSpec.appendColumnSpecification(DataColumnSpecification.MAX_RIGHT);
		}
		if (color) {
			highlightSpec.appendColumnSpecification(new ConstantColumnSpecification(COLOR_RESET));
		}
		rowSpec.insertSeparator("  ");
		highlightSpec.insertSeparator("  ");
		result.setDefaultRowSpecification(rowSpec);
		for (int i = 0; i < options.getDistributionScores().size(); ++i) {
			result.setRowSpecification(-1-i, highlightSpec);
		}
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
