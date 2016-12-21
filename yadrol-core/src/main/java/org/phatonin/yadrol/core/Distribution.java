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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.phatonin.yadrol.core.values.ValueComparator;
import org.phatonin.yadrol.core.values.ValueType;

public class Distribution {
	private final Map<Object,Count> counts = new TreeMap<Object,Count>(ValueComparator.INSTANCE);
	private long total;
	private boolean computed = false;

	public Distribution() {
		super();
	}
	
	public void incr(Object value) {
		Count count = ensureCount(value);
		count.incr();
		total++;
		computed = false;
	}
	
	Count ensureCount(Object value) {
		if (counts.containsKey(value)) {
			return counts.get(value);
		}
		Count result = new Count(value);
		counts.put(value, result);
		computed = false;
		return result;
	}
	
	public Collection<Object> getValues() {
		return Collections.unmodifiableCollection(counts.keySet());
	}
	
	public Count getCount(Object value) {
		return counts.get(value);
	}
	
	public Collection<Count> getCounts() {
		return Collections.unmodifiableCollection(counts.values());
	}
	
	public void clear() {
		counts.clear();
		total = 0;
		computed = false;
	}
	
	public void sample(Expression expr, EvaluationContext ctx, Scope scope, ValueType type, long repeats) throws EvaluationException {
		boolean logDice = ctx.isLogDice();
		ctx.setLogDice(false);
		for (int i = 0; i < repeats; ++i) {
			Object value = expr.evaluate(ctx, scope, type);
			incr(value);
		}
		ctx.setLogDice(logDice);
	}
	
	public void sample(Expression expr, EvaluationContext ctx, ValueType type, long repeats) throws EvaluationException {
		sample(expr, ctx, ctx.getGlobalScope(), type, repeats);
	}
	
	public void sample(Expression expr, EvaluationContext ctx, Scope scope, long repeats) throws EvaluationException {
		sample(expr, ctx, scope, null, repeats);
	}
	
	public void sample(Expression expr, EvaluationContext ctx, long repeats) throws EvaluationException {
		sample(expr, ctx, ctx.getGlobalScope(), null, repeats);
	}

	public void compute() {
		if (computed) {
			return;
		}
		Count prev = null;
		for (Count count : counts.values()) {
			count.compute(prev, total);
			prev = count;
		}
		computed = true;
	}

	public long getTotal() {
		return total;
	}

	public double mean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		compute();
		double result = 0.0;
		for (Count count : counts.values()) {
			Object value = count.getValue();
			long intVal = ctx.valueToInteger(scope, value);
			double f = count.getRelativeFrequency();
			result += intVal * f;
		}
		return result;
	}
	
	public double stddev(EvaluationContext ctx, Scope scope) throws EvaluationException {
		double mean = mean(ctx, scope);
		double result = 0.0;
		for (Count count : counts.values()) {
			Object value = count.getValue();
			long intVal = ctx.valueToInteger(scope, value);
			double f = count.getRelativeFrequency();
			result += Math.pow((intVal - mean) * f, 2);
		}
		return Math.sqrt(result);
	}
	
	public Count mode() {
		Count result = null;
		for (Count count : counts.values()) {
			if (result == null || count.getFrequency() > result.getFrequency()) {
				result = count;
			}
		}
		return result;
	}
	
	public Count medianSup() {
		compute();
		for (Count count : counts.values()) {
			if (count.getRelativeAtMostFrequency() >= 0.5) {
				return count;
			}
		}
		return null;
	}
	
	public Count medianInf() {
		compute();
		for (Count count : counts.values()) {
			if (count.getRelativeAtLeastFrequency() <= 0.5) {
				return count;
			}
		}
		return null;
	}
}
