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
import java.util.Map;

import org.phatonin.yadrol.app.cli.CLIOptions;
import org.phatonin.yadrol.core.DiceRecord;
import org.phatonin.yadrol.core.Distribution;
import org.phatonin.yadrol.core.DistributionScore;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.MultiCount;
import org.phatonin.yadrol.core.RollRecord;
import org.phatonin.yadrol.core.SampleRecord;
import org.phatonin.yadrol.core.Scope;

public class DefaultDisplay extends DisplayManager {
	@Override
	protected void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
		String[][] table = buildTable(ctx, options, multiCounts);
		writeTable(out, table, "  ");
	}
	
	private static String[][] buildTable(EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
		List<DistributionScore> distScores = options.getDistributionScores();
		List<SampleRecord> sampleRecords = ctx.getSampleRecords();
		int nRows = multiCounts.size() + distScores.size() + 1;
		int nCols = sampleRecords.size() + 1;
		String[][] result = new String[nRows][];
		result[0] = getHeaderRow(sampleRecords, nCols);
		for (int i = 0; i < multiCounts.size(); ++i) {
			result[i + 1] = getMultiCountRow(sampleRecords, multiCounts.get(i), nCols);
		}
		for (int i = 0; i < distScores.size(); ++i) {
			result[i + 1 + multiCounts.size()] = getDistributionScoreRow(ctx, sampleRecords, distScores.get(i), nCols);
		}
		return result;
	}

	private static String[] getDistributionScoreRow(EvaluationContext ctx, List<SampleRecord> sampleRecords, DistributionScore distScore, int nCols) throws EvaluationException {
		String[] result = new String[nCols];
		result[0] = distScore.getName();
		Scope scope = ctx.getGlobalScope();
		for (int i = 1; i < nCols; ++i) {
			SampleRecord sampleRecord = sampleRecords.get(i - 1);
			Distribution dist = sampleRecord.getDistribution();
			result[i] = distScore.computeAsString(ctx, scope, dist, "%.3f");
		}
		return result;
	}

	private static String[] getMultiCountRow(List<SampleRecord> sampleRecords, MultiCount multiCount, int nCols) {
		Object value = multiCount.getValue();
		Map<String,Number> counts = multiCount.getCounts();
		String[] result = new String[nCols];
		result[0] = EvaluationContext.valueString(value);
		for (int i = 1; i < nCols; ++i) {
			SampleRecord sampleRecord = sampleRecords.get(i - 1);
			String name = sampleRecord.getName();
			Number n = counts.get(name);
			result[i] = String.format("%.3f", n.doubleValue());
		}
		return result;
	}

	private static String[] getHeaderRow(List<SampleRecord> sampleRecords, int nCols) {
		String[] result = new String[nCols];
		result[0] = "";
		for (int i = 1; i < nCols; ++i) {
			SampleRecord sampleRecord = sampleRecords.get(i - 1);
			result[i] = sampleRecord.getName();
		}
		return result;
	}
	
	private static void writeTable(PrintStream out, String[][] table, String separator) {
		String[] header = table[0];
		final int nCols = header.length;
		String[] colsFmt = getColumnsFormat(table, nCols);
		for (String[] row : table) {
			for (int i = 0; i < nCols; ++i) {
				if (i > 0) {
					out.print(separator);
				}
				out.format(colsFmt[i], row[i]);
			}
			out.println();
		}
	}
	
	private static String[] getColumnsFormat(String[][] table, int nCols) {
		int[] colsWidth = new int[nCols];
		for (int i = 0; i < table.length; ++i) {
			String[] row = table[i];
			for (int j = 0; j < nCols; ++j) {
				colsWidth[j] = Math.max(colsWidth[j], row[j].length());
			}
		}
		String[] result = new String[nCols];
		for (int i = 0; i < nCols; ++i) {
			result[i] = String.format("%%%ds", colsWidth[i]);
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
