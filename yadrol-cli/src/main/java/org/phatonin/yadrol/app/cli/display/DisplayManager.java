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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.app.YadrolResult;
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

public abstract class DisplayManager {
	public void display(CLIOptions options, YadrolResult result) throws FileNotFoundException, EvaluationException {
		writeSeedFile(options, result);
		writeRollRecords(options, result);
		writeSampleRecords(options, result);
	}

	private void writeSampleRecords(CLIOptions options, YadrolResult result) throws FileNotFoundException, EvaluationException {
		EvaluationContext ctx = result.getEvaluationContext();
		List<MultiCount> multiCounts = ctx.getMultiCounts(options.getCountSelector().getRelative());
		File sampleRecordsFile = options.getSampleRecordsFile();
		if (sampleRecordsFile == null) {
			writeMultiCounts(System.out, result.getEvaluationContext(), options, multiCounts);
		}
		else {
			try (PrintStream out = new PrintStream(sampleRecordsFile)) {
				System.err.println("writing samples in " + sampleRecordsFile.getAbsolutePath());
				writeMultiCounts(out, result.getEvaluationContext(), options, multiCounts);
			}
		}
	}

	protected abstract void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException;

	private void writeRollRecords(CLIOptions options, YadrolResult result) throws FileNotFoundException {
		File rollRecordsFile = options.getRollRecordsFile();
		if (rollRecordsFile == null) {
			writeRollRecords(System.out, options, result);
		}
		else {
			try (PrintStream out = new PrintStream(rollRecordsFile)) {
				System.err.println("writing rolls in " + rollRecordsFile.getAbsolutePath());
				writeRollRecords(out, options, result);
			}
		}
	}
	
	private void writeRollRecords(PrintStream out, CLIOptions options, YadrolResult result) {
		EvaluationContext ctx = result.getEvaluationContext();
		for (RollRecord rollRecord : ctx.getRollRecords()) {
			if (options.isWriteDiceRecords()) {
				String rollName = rollRecord.getName();
				writeDiceRecords(System.err, options, rollName, rollRecord.getDiceRecords());
			}
			writeRollRecord(out, options, rollRecord);
		}
	}

	protected abstract void writeDiceRecords(PrintStream out, CLIOptions options, String rollName, List<DiceRecord> diceRecords);
	protected abstract void writeRollRecord(PrintStream out, CLIOptions options, RollRecord rollRecord);

	private static void writeSeedFile(CLIOptions options, YadrolResult result) throws FileNotFoundException {
		File seedFile = options.getSeedFile();
		if (seedFile == null) {
			return;
		}
		if (seedFile.exists()) {
			return;
		}
		try (PrintStream out = new PrintStream(seedFile)) {
			EvaluationContext ctx = result.getEvaluationContext();
			long seed = ctx.getSeed();
			out.print(seed);
		}
	}

	protected static String toString(Object value) {
		return EvaluationContext.valueToExpression(value).toString();
	}
	
	protected static List<String> getMultiCountNames(Collection<MultiCount> multiCounts) {
		Collection<String> names = new LinkedHashSet<String>();
		for (MultiCount multiCount : multiCounts) {
			Map<String,Number> counts = multiCount.getCounts();
			Collection<String> countNames = counts.keySet();
			names.addAll(countNames);
		}
		return new ArrayList<String>(names);
	}
	
	protected static String[][] buildTableData(EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
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
}
