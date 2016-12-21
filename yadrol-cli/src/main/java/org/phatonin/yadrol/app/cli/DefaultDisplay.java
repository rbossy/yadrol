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

package org.phatonin.yadrol.app.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.DiceRecord;
import org.phatonin.yadrol.core.MultiCount;
import org.phatonin.yadrol.core.RollRecord;

public class DefaultDisplay extends DisplayManager {
	@Override
	protected void writeMultiCounts(PrintStream out, CLIOptions options, Collection<MultiCount> multiCounts) {
		int maxValueWidth = getMultiCountMaxValueWidth(multiCounts);
		List<String> names = getMultiCountNames(multiCounts);

		String valueFormat = String.format("%%%ds", maxValueWidth);
		List<String> columnFormats = getColumnFormats(names);
		
		displayMultiCountsHeader(out, valueFormat, names, columnFormats);
		
		for (MultiCount multiCount : multiCounts) {
			displayMultiCountRow(out, multiCount, valueFormat, names, columnFormats);
		}
	}
	
	private static int getMultiCountMaxValueWidth(Collection<MultiCount> multiCounts) {
		int result = 0;
		for (MultiCount multiCount : multiCounts) {
			Object value = multiCount.getValue();
			String valueString = toString(value);
			result = Math.max(result, valueString.length());
		}
		return result;
	}
	
	private static List<String> getColumnFormats(List<String> names) {
		List<String> result = new ArrayList<String>();
		for (String name : names) {
			int width = Math.max(6, name.length());
			String fmt = String.format("   %%%ds", width);
			result.add(fmt);
		}
		return result;
	}
	
	private static void displayMultiCountsHeader(PrintStream out, String valueFormat, List<String> names, List<String> columnFormats) {
		out.format(valueFormat, "");
		for (int i = 0; i < names.size(); ++i) {
			String fmt = columnFormats.get(i);
			String name = names.get(i);
			out.format(fmt, name);
		}
		out.println();
	}
	
	private static void displayMultiCountRow(PrintStream out, MultiCount multiCount, String valueFormat, List<String> names, List<String> columnFormats) {
		displayMultiCountValue(out, multiCount, valueFormat);
		Map<String,Number> counts = multiCount.getCounts();
		for (int i = 0; i < names.size(); ++i) {
			String fmt = columnFormats.get(i);
			String name = names.get(i);
			displayMultiCountScores(out, counts, fmt, name);
		}
		out.println();
	}
	
	private static void displayMultiCountValue(PrintStream out, MultiCount multiCount, String valueFormat) {
		Object value = multiCount.getValue();
		String valueString = toString(value);
		out.format(valueFormat, valueString);
	}
	
	private static void displayMultiCountScores(PrintStream out, Map<String,Number> counts, String fmt, String name) {
		String cell = getMultiCountCell(counts, name);
		out.format(fmt, cell);
	}
	
	private static String getMultiCountCell(Map<String,Number> counts, String name) {
		if (counts.containsKey(name)) {
			Number n = counts.get(name);
			return String.format("%.3f", n);
		}
		return "";
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
