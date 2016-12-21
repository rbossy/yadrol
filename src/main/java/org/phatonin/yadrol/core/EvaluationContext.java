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

package org.phatonin.yadrol.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.phatonin.yadrol.core.expressions.AbstractListExpression;
import org.phatonin.yadrol.core.expressions.AbstractMapExpression;
import org.phatonin.yadrol.core.expressions.BooleanConstant;
import org.phatonin.yadrol.core.expressions.IntegerConstant;
import org.phatonin.yadrol.core.expressions.Lambda;
import org.phatonin.yadrol.core.expressions.ListConstructor;
import org.phatonin.yadrol.core.expressions.MapConstructor;
import org.phatonin.yadrol.core.expressions.StringConstant;
import org.phatonin.yadrol.core.expressions.Undef;
import org.phatonin.yadrol.core.importManagers.NoImport;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueComparator;
import org.phatonin.yadrol.core.values.ValueType;
import org.phatonin.yadrol.core.values.ValueVisitor;

public class EvaluationContext {
	private final Random random;
	private long seed;
	private final List<DiceRecord> diceRecords = new ArrayList<DiceRecord>();
	private final Scope globalScope = new Scope();
	private boolean logDice = true;
	private ImportManager importManager = NoImport.INSTANCE;
	private long sampleSize = 30000;
	private final List<RollRecord> rollRecords = new ArrayList<RollRecord>();
	private final List<SampleRecord> sampleRecords = new ArrayList<SampleRecord>();
	private RollRecord currentRollRecord;
	private SampleRecord currentSampleRecord;
	private ValueType defaultEvaluationType = ValueType.ANY;
	private long maxReroll = 1000;
	private OutputMode defaultOutputMode = OutputMode.ROLL;
	private long maxCallDepth = 100;

	public EvaluationContext(Long seed) {
		this.seed = seed == null ? System.currentTimeMillis() : seed;
		this.random = new Random(this.seed);
	}
	
	public EvaluationContext() {
		this(null);
	}
	
	public Random getRandom() {
		return random;
	}
	
	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
		random.setSeed(seed);
	}

	public Scope getGlobalScope() {
		return globalScope;
	}

	public List<DiceRecord> getDiceRecords() {
		return Collections.unmodifiableList(diceRecords);
	}

	public boolean isLogDice() {
		return logDice;
	}

	public void setLogDice(boolean logDice) {
		this.logDice = logDice;
	}

	private void addDiceRecord(Object type, List<Object> result) {
		System.err.println("dice record");
		DiceRecord rec = new DiceRecord(type, result);
		diceRecords.add(rec);
		if (currentRollRecord != null) {
			currentRollRecord.addDiceRecord(rec);
		}
	}

	public void logDice(Object type, List<Object> result) {
		if (logDice) {
			addDiceRecord(type, result);
		}
	}
	
	public void logDice(Object type, Object result) {
		if (logDice) {
			List<Object> listResult = new ArrayList<Object>(1);
			listResult.add(result);
			addDiceRecord(type, listResult);
		}
	}

	public ImportManager getImportManager() {
		return importManager;
	}

	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}

	public long getSampleSize() {
		return sampleSize;
	}

	public long getMaxCallDepth() {
		return maxCallDepth;
	}

	public void setMaxCallDepth(long maxCallDepth) {
		this.maxCallDepth = maxCallDepth;
	}

	public void setSampleSize(long sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public long getMaxRepeat() {
		return maxReroll;
	}

	public void setMaxReroll(long maxReroll) {
		this.maxReroll = maxReroll;
	}

	public boolean hasRollRecord() {
		return !rollRecords.isEmpty();
	}
	
	public boolean hasSampleRecord() {
		return !sampleRecords.isEmpty();
	}
	
	public boolean hasOutputRecord() {
		return hasRollRecord() || hasSampleRecord();
	}
	
	public OutputMode getDefaultOutputMode() {
		return defaultOutputMode;
	}

	public void setDefaultOutputMode(OutputMode defaultOutputMode) {
		if (defaultOutputMode == OutputMode.DEFAULT) {
			throw new IllegalArgumentException();
		}
		this.defaultOutputMode = defaultOutputMode;
	}

	private void checkCurrentOutputRecord(Expression expression) throws EvaluationException {
		if (currentRollRecord != null || currentSampleRecord != null) {
			throw new EvaluationException(expression, "nested output");
		}
	}

	RollRecord startRollRecord(String name, Expression expression, ValueType evaluationType) throws EvaluationException {
		checkCurrentOutputRecord(expression);
		RollRecord rec = new RollRecord(name, expression, evaluationType);
		this.currentRollRecord = rec;
		rollRecords.add(rec);
		return rec;
	}

	SampleRecord startSampleRecord(String name, Expression expression, ValueType evaluationType) throws EvaluationException {
		checkCurrentOutputRecord(expression);
		SampleRecord rec = new SampleRecord(name, expression, evaluationType);
		this.currentSampleRecord = rec;
		sampleRecords.add(rec);
		return rec;
	}
	
	void endRollRecord() {
		this.currentRollRecord = null;
	}
	
	void endSampleRecord() {
		this.currentSampleRecord = null;
	}

	public void clear() {
		diceRecords.clear();
		currentRollRecord = null;
		currentSampleRecord = null;
		rollRecords.clear();
		sampleRecords.clear();
	}

	public ValueType getDefaultEvaluationType() {
		return defaultEvaluationType;
	}

	public void setDefaultEvaluationType(ValueType defaultEvaluationType) {
		if (defaultEvaluationType == ValueType.DEFAULT) {
			throw new IllegalArgumentException();
		}
		this.defaultEvaluationType = defaultEvaluationType;
	}

	public List<RollRecord> getRollRecords() {
		return Collections.unmodifiableList(rollRecords);
	}

	public List<SampleRecord> getSampleRecords() {
		return Collections.unmodifiableList(sampleRecords);
	}
	
	public List<MultiCount> getMultiCounts(CountSelector selector) {
		Collection<Object> values = collectValues();
		ensureAllValues(values);
		if (selector != CountSelector.FREQUENCY) {
			ensureComputed();
		}
		return collectMultiCounts(values, selector);
	}

	private List<MultiCount> collectMultiCounts(Collection<Object> values, CountSelector selector) {
		List<MultiCount> result = new ArrayList<MultiCount>(values.size());
		for (Object v : values) {
			MultiCount mc = getMultiCount(v, selector);
			result.add(mc);
		}
		return result;
	}
	
	private MultiCount getMultiCount(Object v, CountSelector selector) {
		Map<String,Number> counts = new HashMap<String,Number>();
		for (SampleRecord rec : sampleRecords) {
			String name = rec.getName();
			Distribution dist = rec.getDistribution();
			Count count = dist.getCount(v);
			Number n = selector.get(count);
			counts.put(name, n);
		}
		return new MultiCount(v, counts);
	}

	private void ensureComputed() {
		for (SampleRecord rec : sampleRecords) {
			Distribution dist = rec.getDistribution();
			dist.compute();
		}
	}

	private void ensureAllValues(Collection<Object> values) {
		for (SampleRecord rec : sampleRecords) {
			Distribution dist = rec.getDistribution();
			for (Object v : values) {
				dist.ensureCount(v);
			}
		}
	}

	private Collection<Object> collectValues() {
		Collection<Object> result = new TreeSet<Object>(ValueComparator.INSTANCE);
		for (SampleRecord rec : sampleRecords) {
			Distribution dist = rec.getDistribution();
			result.addAll(dist.getValues());
		}
		return result;
	}




	public static boolean undefToBoolean() {
		return false;
	}
	
	public static boolean integerToBoolean(long value) {
		return value != 0;
	}
	
	public static boolean stringToBoolean(String value) {
		return !value.isEmpty();
	}
	
	public static boolean listToBoolean(List<Object> value) {
		return !value.isEmpty();
	}
	
	public static boolean mapToBoolean(Map<String,Object> value) {
		return !value.isEmpty();
	}
	
	public boolean functionToBoolean(Scope scope, Function value) throws EvaluationException {
		if (value.getPositionalArgs().isEmpty()) {
			Expression body = value.getBody();
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = Collections.emptyMap();
			return value.callBoolean(body, this, positionalArgs, namedArgs, scope.getDepth());
		}
		return false;
	}
	
	public static void undefToString(@SuppressWarnings("unused") StringBuilder sb) {
	}
	
	public static void booleanToString(StringBuilder sb, boolean value) {
		if (value) {
			sb.append("true");
		}
	}
	
	public static void stringToString(StringBuilder sb, String value) {
		sb.append(value);
	}
	
	public static void integerToString(StringBuilder sb, long value) {
		sb.append(value);
	}
	
	public void listToString(StringBuilder sb, Scope scope, List<Object> value) throws EvaluationException {
		for (Object v : value) {
			valueToString(sb, scope, v);
		}
	}
	
	public void mapToString(StringBuilder sb, Scope scope, Map<String,Object> value) throws EvaluationException {
		for (Object v : value.values()) {
			valueToString(sb, scope, v);
		}
	}
	
	public static String undefToString() {
		return "";
	}
	
	public static String booleanToString(boolean value) {
		if (value) {
			return "true";
		}
		return "";
	}
	
	public static String integerToString(long value) {
		return Long.toString(value);
	}
	
	public String listToString(Scope scope, List<Object> value) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		listToString(sb, scope, value);
		return sb.toString();
	}
	
	public String mapToString(Scope scope, Map<String,Object> value) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		mapToString(sb, scope, value);
		return sb.toString();
	}
	
	public String functionToString(Scope scope, Function value) throws EvaluationException {
		if (value.getPositionalArgs().isEmpty()) {
			Expression body = value.getBody();
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = Collections.emptyMap();
			return value.callString(body, this, positionalArgs, namedArgs, scope.getDepth());
		}
		return value.toString();
	}
	
	public static void functionToString(StringBuilder sb, Function value) {
		sb.append(value.toString());
	}

	public static long undefToInteger() {
		return 0L;
	}
	
	public static long booleanToInteger(boolean value) {
		return value ? 1L : 0L;
	}
	
	public static long stringToInteger(String value) {
		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			return 0L;
		}
	}
	
	public long listToInteger(Scope scope, List<Object> value) throws EvaluationException {
		long result = 0L;
		for (Object v : value) {
			result += valueToInteger(scope, v);
		}
		return result;
	}
	
	public long mapToInteger(Scope scope, Map<String,Object> value) throws EvaluationException {
		long result = 0L;
		for (Object v : value.values()) {
			result += valueToInteger(scope, v);
		}
		return result;
	}
	
	public long functionToInteger(Scope scope, Function value) throws EvaluationException {
		if (value.getPositionalArgs().isEmpty()) {
			Expression body = value.getBody();
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = Collections.emptyMap();
			return value.callInteger(body, this, positionalArgs, namedArgs, scope.getDepth());
		}
		return value.hashCode();
	}
	
	private static List<Object> singleton(Object value) {
		List<Object> result = new ArrayList<Object>(1);
		result.add(value);
		return result;
	}
	
	public static List<Object> undefToList() {
		return new ArrayList<Object>(0);
	}
	
	public static List<Object> booleanToList(boolean value) {
		return singleton(value);
	}
	
	public static List<Object> integerToList(long value) {
		return singleton(value);
	}
	
	public static List<Object> stringToList(String value) {
		return singleton(value);
	}
	
	public static List<Object> mapToList(Map<String,Object> value) {
		return new ArrayList<Object>(value.values());
	}
	
	public List<Object> functionToList(Scope scope, Function value) throws EvaluationException {
		if (value.getPositionalArgs().isEmpty()) {
			Expression body = value.getBody();
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = Collections.emptyMap();
			return value.callList(body, this, positionalArgs, namedArgs, scope.getDepth());
		}
		return singleton(value);
	}
	
	private static Map<String,Object> singletonMap(Object value) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		result.put("_", value);
		return result;
	}
	
	public static Map<String,Object> undefToMap() {
		return new LinkedHashMap<String,Object>();
	}
	
	public static Map<String,Object> stringToMap(String value) {
		return singletonMap(value);
	}
	
	public static Map<String,Object> booleanToMap(boolean value) {
		return singletonMap(value);
	}
	
	public static Map<String,Object> integerToMap(long value) {
		return singletonMap(value);
	}
	
	public static Map<String,Object> listToMap(List<Object> value) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		for (int i = 0; i < value.size(); ++i) {
			result.put(Integer.toString(i), value.get(i));
		}
		return result;
	}
	
	public Map<String,Object> functionToMap(Scope scope, Function value) throws EvaluationException {
		if (value.getPositionalArgs().isEmpty()) {
			Expression body = value.getBody();
			List<Object> positionalArgs = Collections.emptyList();
			Map<String,Object> namedArgs = Collections.emptyMap();
			return value.callMap(body, this, positionalArgs, namedArgs, scope.getDepth());
		}
		return singletonMap(value);
	}
	
	public static Expression undefToExpression() {
		return new Undef(Location.NONE);
	}
	
	public static Expression stringToExpression(String value) {
		return new StringConstant(Location.NONE, value);
	}
	
	public static Expression booleanToExpression(boolean value) {
		return new BooleanConstant(Location.NONE, value);
	}
	
	public static Expression integerToExpression(long value) {
		return new IntegerConstant(Location.NONE, value);
	}
	
	public static AbstractListExpression listToExpression(List<Object> value) {
		List<Expression> elements = new ArrayList<Expression>(value.size());
		for (Object v : value) {
			Expression e = valueToExpression(v);
			elements.add(e);
		}
		return new ListConstructor(Location.NONE, elements);
	}
	
	public static AbstractMapExpression mapToExpression(Map<String,Object> value) {
		Map<String,Expression> entries = new LinkedHashMap<String,Expression>();
		for (Map.Entry<String,Object> e : value.entrySet()) {
			String key = e.getKey();
			Object v = e.getValue();
			Expression expr = valueToExpression(v);
			entries.put(key, expr);
		}
		return new MapConstructor(Location.NONE, entries);
	}
	
	public static Expression functionToExpression(Function value) {
		List<String> positionalArgs = value.getPositionalArgs();
		Map<String,Expression> namedArgs = new LinkedHashMap<String,Expression>();
		for (Map.Entry<String,Object> fa : value.getNamedArgs().entrySet()) {
			Expression e = valueToExpression(fa.getValue());
			namedArgs.put(fa.getKey(), e);
		}
		return new Lambda(Location.NONE, positionalArgs, namedArgs, value.getBody());
	}

	private static Function asFunction(Expression body) {
		List<String> positionalArgs = Collections.emptyList();
		Map<String,Object> namedArgs = Collections.emptyMap();
		return new Function(null, positionalArgs, namedArgs, body, null);
	}

	public static Function undefToFunction() {
		return asFunction(undefToExpression());
	}
	
	public static Function stringToFunction(String value) {
		return asFunction(stringToExpression(value));
	}
	
	public static Function booleanToFunction(boolean value) {
		return asFunction(booleanToExpression(value));
	}
	
	public static Function integerToFunction(long value) {
		return asFunction(integerToExpression(value));
	}
	
	public static Function listToFunction(List<Object> value) {
		return asFunction(listToExpression(value));
	}
	
	public static Function mapToFunction(Map<String,Object> value) {
		return asFunction(mapToExpression(value));
	}
	
	public String valueToString(Object value) throws EvaluationException {
		return TO_STRING.visit(value, null);
	}
	
	public void valueToString(StringBuilder sb, Scope scope, Object value) throws EvaluationException {
		new ToStringBuilder(sb).visit(value, scope);
	}
	
	public long valueToInteger(Scope scope, Object value) throws EvaluationException {
		return TO_INTEGER.visit(value, scope);
	}
	
	public List<Object> valueToList(Scope scope, Object value) throws EvaluationException {
		return TO_LIST.visit(value, scope);
	}
	
	public Map<String,Object> valueToMap(Scope scope, Object value) throws EvaluationException {
		return TO_MAP.visit(value, scope);
	}
	
	public static Expression valueToExpression(Object value) {
		return TO_EXPRESSION.visit(value, null);
	}
	
	public static Function valueToFunction(Object value) {
		return TO_FUNCTION.visit(value, null);
	}

	public boolean valueToBoolean(Scope scope, Object value) throws EvaluationException {
		return TO_BOOLEAN.visit(value, scope);
	}
	
	private final class ToStringBuilder extends ValueVisitor<Void,Scope,EvaluationException> {
		private final StringBuilder sb;

		private ToStringBuilder(StringBuilder sb) {
			super();
			this.sb = sb;
		}

		@Override
		public Void visitUndef(Scope param) {
			undefToString(sb);
			return null;
		}

		@Override
		public Void visit(String value, Scope param) {
			stringToString(sb, value);
			return null;
		}

		@Override
		public Void visit(long value, Scope param) {
			integerToString(sb, value);
			return null;
		}

		@Override
		public Void visit(List<Object> value, Scope param) throws EvaluationException {
			listToString(sb, param, value);
			return null;
		}

		@Override
		public Void visit(Map<String,Object> value, Scope param) throws EvaluationException {
			mapToString(sb, param, value);
			return null;
		}

		@Override
		public Void visit(Function value, Scope param) throws EvaluationException {
			sb.append(functionToString(param, value));
			return null;
		}

		@Override
		public Void visit(boolean value, Scope param) {
			booleanToString(sb, value);
			return null;
		}
	};

	private final ValueVisitor<String,Scope,EvaluationException> TO_STRING = new ValueVisitor<String,Scope,EvaluationException>() {
		@Override
		public String visitUndef(Scope param) {
			return undefToString();
		}

		@Override
		public String visit(Function value, Scope param) throws EvaluationException {
			return functionToString(param, value);
		}

		@Override
		public String visit(List<Object> value, Scope param) throws EvaluationException {
			return listToString(param, value);
		}

		@Override
		public String visit(Map<String,Object> value, Scope param) throws EvaluationException {
			return mapToString(param, value);
		}

		@Override
		public String visit(long value, Scope param) {
			return integerToString(value);
		}

		@Override
		public String visit(String value, Scope param) {
			return value;
		}

		@Override
		public String visit(boolean value, Scope param) {
			return booleanToString(value);
		}
	};

	private final ValueVisitor<Long,Scope,EvaluationException> TO_INTEGER = new ValueVisitor<Long,Scope,EvaluationException>() {
		@Override
		public Long visitUndef(Scope param) {
			return undefToInteger();
		}

		@Override
		public Long visit(Function value, Scope param) throws EvaluationException {
			return functionToInteger(param, value);
		}

		@Override
		public Long visit(List<Object> value, Scope param) throws EvaluationException {
			return listToInteger(param, value);
		}

		@Override
		public Long visit(Map<String,Object> value, Scope param) throws EvaluationException {
			return mapToInteger(param, value);
		}

		@Override
		public Long visit(long value, Scope param) {
			return value;
		}

		@Override
		public Long visit(String value, Scope param) {
			return stringToInteger(value);
		}

		@Override
		public Long visit(boolean value, Scope param) {
			return booleanToInteger(value);
		}
	};

	private final ValueVisitor<List<Object>,Scope,EvaluationException> TO_LIST = new ValueVisitor<List<Object>,Scope,EvaluationException>() {
		@Override
		public List<Object> visitUndef(Scope param) {
			return undefToList();
		}

		@Override
		public List<Object> visit(Function value, Scope param) throws EvaluationException {
			return functionToList(param, value);
		}

		@Override
		public List<Object> visit(List<Object> value, Scope param) {
			return value;
		}

		@Override
		public List<Object> visit(Map<String,Object> value, Scope param) {
			return mapToList(value);
		}

		@Override
		public List<Object> visit(long value, Scope param) {
			return integerToList(value);
		}

		@Override
		public List<Object> visit(String value, Scope param) {
			return stringToList(value);
		}

		@Override
		public List<Object> visit(boolean value, Scope param) {
			return booleanToList(value);
		}
	};

	private static final ValueVisitor<Expression,Void,RuntimeException> TO_EXPRESSION = new ValueVisitor<Expression,Void,RuntimeException>() {
		@Override
		public Expression visitUndef(Void param) {
			return undefToExpression();
		}

		@Override
		public Expression visit(Function value, Void param) {
			return functionToExpression(value);
		}

		@Override
		public Expression visit(List<Object> value, Void param) {
			return listToExpression(value);
		}

		@Override
		public Expression visit(Map<String,Object> value, Void param) {
			return mapToExpression(value);
		}

		@Override
		public Expression visit(long value, Void param) {
			return integerToExpression(value);
		}

		@Override
		public Expression visit(String value, Void param) {
			return stringToExpression(value);
		}

		@Override
		public Expression visit(boolean value, Void param) {
			return booleanToExpression(value);
		}
	};

	private static final ValueVisitor<Function,Void,RuntimeException> TO_FUNCTION = new ValueVisitor<Function,Void,RuntimeException>() {
		@Override
		public Function visitUndef(Void param) {
			return undefToFunction();
		}

		@Override
		public Function visit(Function value, Void param) {
			return value;
		}

		@Override
		public Function visit(List<Object> value, Void param) {
			return listToFunction(value);
		}

		@Override
		public Function visit(Map<String,Object> value, Void param) {
			return mapToFunction(value);
		}

		@Override
		public Function visit(long value, Void param) {
			return integerToFunction(value);
		}

		@Override
		public Function visit(String value, Void param) {
			return stringToFunction(value);
		}

		@Override
		public Function visit(boolean value, Void param) {
			return booleanToFunction(value);
		}
	};
	
	private final ValueVisitor<Boolean,Scope,EvaluationException> TO_BOOLEAN = new ValueVisitor<Boolean,Scope,EvaluationException>() {
		@Override
		public Boolean visitUndef(Scope param) {
			return undefToBoolean();
		}
		
		@Override
		public Boolean visit(Function value, Scope param) throws EvaluationException {
			return functionToBoolean(param, value);
		}
		
		@Override
		public Boolean visit(List<Object> value, Scope param) {
			return listToBoolean(value);
		}
		
		@Override
		public Boolean visit(Map<String,Object> value, Scope param) {
			return mapToBoolean(value);
		}

		@Override
		public Boolean visit(long value, Scope param) {
			return integerToBoolean(value);
		}
		
		@Override
		public Boolean visit(boolean value, Scope param) {
			return value;
		}
		
		@Override
		public Boolean visit(String value, Scope param) {
			return stringToBoolean(value);
		}
	};

	private final ValueVisitor<Map<String,Object>,Scope,EvaluationException> TO_MAP = new ValueVisitor<Map<String,Object>,Scope,EvaluationException>() {
		@Override
		public Map<String,Object> visitUndef(Scope param) {
			return undefToMap();
		}

		@Override
		public Map<String,Object> visit(String value, Scope param) {
			return stringToMap(value);
		}

		@Override
		public Map<String,Object> visit(boolean value, Scope param) {
			return booleanToMap(value);
		}

		@Override
		public Map<String,Object> visit(long value, Scope param) {
			return integerToMap(value);
		}

		@Override
		public Map<String,Object> visit(List<Object> value, Scope param) {
			return listToMap(value);
		}

		@Override
		public Map<String,Object> visit(Map<String,Object> value, Scope param) {
			return value;
		}

		@Override
		public Map<String,Object> visit(Function value, Scope param) throws EvaluationException {
			return functionToMap(param, value);
		}
	};
	
	public static List<Object> copy(List<Object> list, boolean deep) {
		List<Object> result = new ArrayList<Object>(list.size());
		if (deep) {
			for (Object v : list) {
				result.add(deepCopy(v));
			}
		}
		else {
			result.addAll(list);
		}
		return result;
	}

	public static List<Object> copy(List<Object> list) {
		return copy(list, false);
	}

	public static List<Object> deepCopy(List<Object> list) {
		return copy(list, true);
	}
	
	public static Map<String,Object> copy(Map<String,Object> map, boolean deep) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		if (deep) {
			for (Map.Entry<String,Object> e : map.entrySet()) {
				result.put(e.getKey(), deepCopy(e.getValue()));
			}
		}
		else {
			result.putAll(map);
		}
		return result;
	}

	public static Map<String,Object> copy(Map<String,Object> map) {
		return copy(map, false);
	}

	public static Map<String,Object> deepCopy(Map<String,Object> map) {
		return copy(map, true);
	}
	
	public static Object copy(Object value) {
		return copy(value, false);
	}
	
	public static Object copy(Object value, boolean deep) {
		return COPY_VISITOR.visit(value, deep);
	}
	
	public static Object deepCopy(Object value) {
		return copy(value, true);
	}
	
	private static final ValueVisitor<Object,Boolean,RuntimeException> COPY_VISITOR = new ValueVisitor<Object,Boolean,RuntimeException>() {
		@Override
		public Object visitUndef(Boolean param) throws RuntimeException {
			return null;
		}
		
		@Override
		public Object visit(Function value, Boolean param) throws RuntimeException {
			return value.reassignOwner(null);
		}
		
		@Override
		public Object visit(Map<String,Object> value, Boolean param) throws RuntimeException {
			return copy(value, param);
		}
		
		@Override
		public Object visit(List<Object> value, Boolean param) throws RuntimeException {
			return copy(value, param);
		}
		
		@Override
		public Object visit(long value, Boolean param) throws RuntimeException {
			return value;
		}
		
		@Override
		public Object visit(boolean value, Boolean param) throws RuntimeException {
			return value;
		}
		
		@Override
		public Object visit(String value, Boolean param) throws RuntimeException {
			return value;
		}
	};
	
	public static Boolean asBoolean(Object value) {
		return value instanceof Boolean ? (Boolean) value : null;
	}
	
	public static String asString(Object value) {
		return value instanceof String ? (String) value : null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> asList(Object value) {
		return value instanceof List ? (List<Object>) value : null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> asMap(Object value) {
		return value instanceof Map ? (Map<String,Object>) value : null;
	}
	
	public static Function asFunction(Object value) {
		return value instanceof Function ? (Function) value : null;
	}
	
	public static Long asInteger(Object value) {
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Integer) {
			return (long) (Integer) value;
		}
		return null;
	}
}
