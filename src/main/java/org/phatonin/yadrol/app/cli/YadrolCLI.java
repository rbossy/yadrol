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

import java.io.FileNotFoundException;

import org.phatonin.yadrol.app.YadrolResult;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.parser.ParseException;

public class YadrolCLI {
	public static void main(String args[]) throws ParseException, EvaluationException, FileNotFoundException {
		CLIOptions options = new CLIOptions();
		OptionParser.parse(options, args);
		if (options.isHelp()) {
			usage();
			return;
		}
		if (!options.hasExpressionString()) {
			throw new RuntimeException();
		}
		YadrolResult result = YadrolResult.createResult(options);
		DisplayManager displayManager = options.getDisplayManager();
		displayManager.display(options, result);
	}

	private static void usage() {
		//XXX
	}
}
