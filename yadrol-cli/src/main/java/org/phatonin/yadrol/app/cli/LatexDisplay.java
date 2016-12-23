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
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.DiceRecord;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.MultiCount;
import org.phatonin.yadrol.core.RollRecord;

public class LatexDisplay extends DisplayManager {
	@Override
	protected void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) {
		List<String> names = getMultiCountNames(multiCounts);

		out.print("\\begin{tabular}{r");
		for (int i = 0; i < names.size(); ++i) {
			out.print('c');
		}
		out.println('}');
		
		for (String name : names) {
			out.print(" & \\textbf{");
			out.print(name);
			out.print('}');
		}
		out.println("\\\\");
		
		for (MultiCount multiCount : multiCounts) {
			Object value = multiCount.getValue();
			String valueString = toString(value);
			out.print(valueString);
			Map<String,Number> counts = multiCount.getCounts();
			for (String name : names) {
				out.print(" & ");
				if (counts.containsKey(name)) {
					Number n = counts.get(name);
					out.format("%.3f", n);
				}
			}
			out.println("\\\\");
		}
		
		out.println("\\end{tabular}");
	}

	@Override
	protected void writeDiceRecords(PrintStream out, CLIOptions options, String rollName, List<DiceRecord> diceRecords) {
		throw new RuntimeException();
	}

	@Override
	protected void writeRollRecord(PrintStream out, CLIOptions options, RollRecord rollRecord) {
		throw new RuntimeException();
	}
}
