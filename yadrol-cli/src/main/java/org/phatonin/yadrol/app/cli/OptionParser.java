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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.ConfidenceIntervalScore;
import org.phatonin.yadrol.core.CountSelector;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.OutputMode;
import org.phatonin.yadrol.core.StandardDistributionScore;
import org.phatonin.yadrol.core.importManagers.FileSystemImportManager;
import org.phatonin.yadrol.core.values.ValueType;

enum OptionParser {
	HELP("-help", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setHelp(true);
		}
	},

	ROLL("-roll", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setOutputMode(OutputMode.ROLL);
		}
	},
	
	NATIVE_TYPE("-native", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setDefaultEvaluationType(ValueType.ANY);
		}
	},
	
	SAMPLE_SIZE("-size", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			long sampleSize = toLong(args[0]);
			if (sampleSize <= 0) {
				throw new RuntimeException();
			}
			options.setSampleSize(sampleSize);
		}
	},
	
	SEED("-seed", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setSeed(toLong(args[0]));
		}
	},
	
	FREQUENCY("-frequency", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setCountSelector(CountSelector.RELATIVE_FREQUENCY);
		}
	},
	
	AT_MOST("-atmost", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setCountSelector(CountSelector.RELATIVE_AT_MOST);
		}
	},
	
	LATEX("-latex", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setDisplayManager(new LatexDisplay());
		}
	},
	
	SEED_FILE("-seedfile", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			File seedFile = new File(args[0]);
			options.setSeedFile(seedFile);
			if (seedFile.exists()) {
				try (Reader r = new FileReader(seedFile)) {
					char[] buffer = new char[1024];
					r.read(buffer); // XXX
					String seedString = new String(buffer);
					long seed = Long.parseLong(seedString);
					options.setSeed(seed);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	},
	
	ROLL_RECORDS_FILE("-rollsfile", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			File rollRecordsFile = new File(args[0]);
			options.setRollRecordsFile(rollRecordsFile);
		}
	},
	
	SAMPLE_RECORDS_FILE("-samplesfile", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			File sampleRecordsFile = new File(args[0]);
			options.setSampleRecordsFile(sampleRecordsFile);
		}
	},
	
	WRITE_DICE_RECORDS("-writedice", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.setWriteDiceRecords(true);
		}
	},
	
	IMPORT_PATHS("-paths", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			FileSystemImportManager fsImportManager = options.getFsImportManager();
			fsImportManager.addSearchPaths(args[0]);
		}
	},
	
	IMPORT_FILE("-import", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			Location location = new Location(options.getSource(), 0, 0);
			options.addImport(location, args[0]);
		}
	},
	
	SCRIPT("-script", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			Location location = new Location(options.getSource(), 0, 0);
			options.addImport(location, null);
		}
	},
	
	MEAN("-mean", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.addDistributionScore(StandardDistributionScore.MEAN);
		}
	},
	
	MODE("-mode", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.addDistributionScore(StandardDistributionScore.MODE);
		}
	},
	
	MEDIAN("-median", 0) {
		@Override
		public void process(CLIOptions options, String[] args) {
			options.addDistributionScore(StandardDistributionScore.MEDIAN_SUP);
		}
	},
	
	CONFIDENCE_INTERVAL("-interval", 1) {
		@Override
		public void process(CLIOptions options, String[] args) {
			double risk = Double.parseDouble(args[0]);
			options.addDistributionScore(new ConfidenceIntervalScore(risk));
		}
	}
	;

	private final String trigger;
	private final int requiredArgumentsCount;

	private OptionParser(String trigger, int requiredArgumentsCount) {
		this.trigger = trigger;
		this.requiredArgumentsCount = requiredArgumentsCount;
	}

	public abstract void process(CLIOptions options, String[] args);
	
	public String getTrigger() {
		return trigger;
	}

	public int getRequiredArgumentsCount() {
		return requiredArgumentsCount;
	}

	private static long toLong(String s) {
		return Long.parseLong(s);
	}

	public static void parse(CLIOptions options, String[] args) {
		List<String> argList = Arrays.asList(args);
		Iterator<String> argIt = argList.iterator();
		Map<String,OptionParser> optionMap = createOptionMap();
		parse(options, optionMap, argIt);
	}

	private static Map<String,OptionParser> createOptionMap() {
		Map<String,OptionParser> result = new HashMap<String,OptionParser>();
		for (OptionParser opt : OptionParser.values()) {
			String trigger = opt.getTrigger();
			result.put(trigger, opt);
		}
		return result;
	}
		
	private static void parse(CLIOptions options, Map<String,OptionParser> optionMap, Iterator<String> argIt) {
		while (argIt.hasNext()) {
			String optStr = argIt.next();
			if (optionMap.containsKey(optStr)) {
				OptionParser opt = optionMap.get(optStr);
				String[] optArgs = createOptionArgs(opt, argIt);
				opt.process(options, optArgs);
			}
			else {
				if (options.getExpressionString() != null) {
					throw new RuntimeException();
				}
				options.setExpressionString(optStr);
			}
		}
	}
	
	private static String[] createOptionArgs(OptionParser opt, Iterator<String> argIt) {
		int n = opt.getRequiredArgumentsCount();
		String[] result = new String[n];
		for (int i = 0; i < n; ++i) {
			if (!argIt.hasNext()) {
				throw new RuntimeException();
			}
			result[i] = argIt.next();
		}
		return result;
	}
}
