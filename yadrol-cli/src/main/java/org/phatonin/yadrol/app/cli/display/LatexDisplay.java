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

public class LatexDisplay extends DisplayManager {
	@Override
	protected void writeMultiCounts(PrintStream out, EvaluationContext ctx, CLIOptions options, List<MultiCount> multiCounts) throws EvaluationException {
		List<SampleRecord> sampleRecords = ctx.getSampleRecords();
		TableSpecification tableSpec = buildTableSpecification(sampleRecords);
		String[][] data = buildTableData(ctx, options, multiCounts);
		openTabular(out, sampleRecords);
		tableSpec.print(out, data);
		closeTabular(out);
	}

	private static void openTabular(PrintStream out, List<SampleRecord> sampleRecords) {
		out.print("\\begin{tabular}{r");
		for (int i = 0; i < sampleRecords.size(); ++i) {
			out.print('r');
		}
		out.println("}");
	}
	
	private static void closeTabular(PrintStream out) {
		out.println("\\end{tabular}");
	}
	
	private static TableSpecification buildTableSpecification(List<SampleRecord> sampleRecords) {
		TableSpecification result = new TableSpecification();
		RowSpecification headerSpecification = new RowSpecification();
		RowSpecification dataSpecification = new RowSpecification();
		headerSpecification.appendColumnSpecification(DataColumnSpecification.RAW);
		dataSpecification.appendColumnSpecification(DataColumnSpecification.RAW);
		for (int i = 0; i < sampleRecords.size(); ++i) {
			headerSpecification.appendColumnSpecification(LATEX_HEADER);
			dataSpecification.appendColumnSpecification(DataColumnSpecification.RAW);
		}
		latexizeRowSpecification(headerSpecification);
		latexizeRowSpecification(dataSpecification);
		result.setDefaultRowSpecification(dataSpecification);
		result.setRowSpecification(0, headerSpecification);
		return result;
	}
	
	private static void latexizeRowSpecification(RowSpecification spec) {
		spec.insertSeparator(LATEX_SEPARATOR);
		spec.appendColumnSpecification(LATEX_END_LINE);
	}

	private static final ColumnSpecification LATEX_END_LINE = new ConstantColumnSpecification("\\\\");
	
	private static final ColumnSpecification LATEX_SEPARATOR = new ConstantColumnSpecification(" & ");
	
	private static final ColumnSpecification LATEX_HEADER = new ColumnSpecification() {
		@Override
		public void printColumn(PrintStream out, int maxDataWidth, String cell) {
			out.print("\\textbf{");
			out.print(cell);
			out.print("}");
		}
		
		@Override
		public boolean consumesDataCell() {
			return true;
		}
	};

	@Override
	protected void writeDiceRecords(PrintStream out, CLIOptions options, String rollName, List<DiceRecord> diceRecords) {
		throw new RuntimeException();
	}

	@Override
	protected void writeRollRecord(PrintStream out, CLIOptions options, RollRecord rollRecord) {
		throw new RuntimeException();
	}
}
