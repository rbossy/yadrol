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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.phatonin.yadrol.app.YadrolResult;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.parser.ParseException;

public class YadrolCLI {
	public static void main(String args[]) throws ParseException, EvaluationException, IOException {
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

	private static void usage() throws IOException {
		Properties usageProps = getUsageProperties();
		String usage = usageProps.getProperty("usage");
		Map<String,UsageGroup> groupMap = getUsageGroups(usageProps);
		for (OptionParser opt : OptionParser.values()) {
			getOptionUsage(usageProps, groupMap, opt);
		}
		System.out.println(usage);
		System.out.println();
		List<UsageGroup> groups = new ArrayList<UsageGroup>(groupMap.values());
		Collections.sort(groups);
		for (UsageGroup group : groups) {
			System.out.println(group.label);
			Collections.sort(group.options);
			for (OptionUsage opt : group.options) {
				System.out.format("  %-20s%s\n", opt.opt.getTrigger() + " " + opt.meta, opt.help);
			}
			System.out.println();
		}
	}
	
	private static void getOptionUsage(Properties props, Map<String,UsageGroup> groups, OptionParser opt) {
		String name = opt.name();
		String groupName = props.getProperty(name + ".group");
		UsageGroup group = groups.get(groupName);
		int order = Integer.parseInt(props.getProperty(name + ".order"));
		String meta = props.getProperty(name + ".meta");
		String help = props.getProperty(name + ".help");
		new OptionUsage(opt, group, order, meta, help);
	}

	private static Map<String,UsageGroup> getUsageGroups(Properties props) {
		Map<String,UsageGroup> result = new HashMap<String,UsageGroup>();
		String groupsString = props.getProperty("groups");
		String[] groupNames = groupsString.split(",");
		for (String name : groupNames) {
			UsageGroup group = getUsageGroup(props, name);
			result.put(name, group);
		}
		return result;
	}
	
	private static UsageGroup getUsageGroup(Properties props, String name) {
		int order = Integer.parseInt(props.getProperty(name + ".order"));
		String label = props.getProperty(name + ".label");
		return new UsageGroup(order, label);
	}

	private static Properties getUsageProperties() throws IOException {
		try (InputStream is = YadrolCLI.class.getResourceAsStream("YadrolCLIUsage.properties")) {
			Properties result = new Properties();
			result.load(is);
			return result;
		}
	}
	
	private static class UsageGroup implements Comparable<UsageGroup> {
		private final int order;
		private final String label;
		private final List<OptionUsage> options = new ArrayList<OptionUsage>();
		
		private UsageGroup(int order, String label) {
			super();
			this.order = order;
			this.label = label;
		}

		@Override
		public int compareTo(UsageGroup o) {
			return Integer.compare(this.order, o.order);
		}
		
	}
	
	private static class OptionUsage implements Comparable<OptionUsage> {
		private final OptionParser opt;
		private final int order;
		private final String meta;
		private final String help;
		
		private OptionUsage(OptionParser opt, UsageGroup group, int order, String meta, String help) {
			super();
			this.opt = opt;
			this.order = order;
			this.meta = meta;
			this.help = help;
			group.options.add(this);
		}

		@Override
		public int compareTo(OptionUsage o) {
			return Integer.compare(this.order, o.order);
		}
	}
}
