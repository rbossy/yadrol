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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.phatonin.yadrol.core.values.ValueComparator;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Distribution of value occurrences.
 * 
 *
 */
public class Distribution {
	private final Map<Object,Count> counts = new TreeMap<Object,Count>(ValueComparator.INSTANCE);
	private long total;
	private boolean computed = false;

	/**
	 * Create an empty distribution.
	 */
	public Distribution() {
		super();
	}
	
	/**
	 * Increase the count by one for the specified value.
	 * @param value
	 */
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
	
	/**
	 * Returns all values in this distribution that have at least one occurrence.
	 * @return
	 */
	public Collection<Object> getValues() {
		return Collections.unmodifiableCollection(counts.keySet());
	}
	
	/**
	 * Returns the count object for the specified value.
	 * @param value
	 * @return
	 */
	public Count getCount(Object value) {
		return counts.get(value);
	}
	
	/**
	 * Returns all counts in this distribution.
	 * @return
	 */
	public Collection<Count> getCounts() {
		return Collections.unmodifiableCollection(counts.values());
	}
	
	/**
	 * Removes all values and counts in this distribution.
	 */
	public void clear() {
		counts.clear();
		total = 0;
		computed = false;
	}
	
	/**
	 * Fill this distribution by evaluating repeatedly the specified expression.
	 * @param expr
	 * @param ctx
	 * @param scope
	 * @param type
	 * @param repeats
	 * @throws EvaluationException
	 */
	public void sample(Expression expr, EvaluationContext ctx, Scope scope, ValueType type, long repeats) throws EvaluationException {
		boolean logDice = ctx.isLogDice();
		ctx.setLogDice(false);
		for (int i = 0; i < repeats; ++i) {
			Object value = expr.evaluate(ctx, scope, type);
			incr(value);
		}
		ctx.setLogDice(logDice);
	}
	
	/**
	 * Fill this distribution by evaluating repeatedly the specified expression, using the global scope of the specified evaluation context.
	 * @param expr
	 * @param ctx
	 * @param type
	 * @param repeats
	 * @throws EvaluationException
	 */
	public void sample(Expression expr, EvaluationContext ctx, ValueType type, long repeats) throws EvaluationException {
		sample(expr, ctx, ctx.getGlobalScope(), type, repeats);
	}
	
	/**
	 * Fill this distribution by evaluating repeatedly the specified expression, using the default value type of the specified evaluation context.
	 * @param expr
	 * @param ctx
	 * @param scope
	 * @param repeats
	 * @throws EvaluationException
	 */
	public void sample(Expression expr, EvaluationContext ctx, Scope scope, long repeats) throws EvaluationException {
		sample(expr, ctx, scope, ValueType.DEFAULT, repeats);
	}

	/**
	 * Fill this distribution by evaluating repeatedly the specified expression, using the global scope and the default value type of the specified evaluation context.
	 * @param expr
	 * @param ctx
	 * @param repeats
	 * @throws EvaluationException
	 */
	public void sample(Expression expr, EvaluationContext ctx, long repeats) throws EvaluationException {
		sample(expr, ctx, ctx.getGlobalScope(), null, repeats);
	}

	/**
	 * Compute secondary scores in this distribution counts.
	 */
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

	/**
	 * Returns the total number of occurrences in this distribution.
	 * @return
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Computes the mean of all values weighted by their occurrences.
	 * Each value is converted into an integer.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
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
	
	/**
	 * Computes the standard deviation of the mean.
	 * Each value is converted into an integer.
	 * @param ctx
	 * @param scope
	 * @return
	 * @throws EvaluationException
	 */
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
	
	/**
	 * Returns the count that has the highest number of occurrences.
	 * @return
	 */
	public Count mode() {
		Count result = null;
		for (Count count : counts.values()) {
			if (result == null || count.getFrequency() > result.getFrequency()) {
				result = count;
			}
		}
		return result;
	}
	
	/**
	 * Returns the higher median count in this distribution.
	 * @return
	 */
	public Count medianSup() {
		compute();
		for (Count count : counts.values()) {
			if (count.getRelativeAtMostFrequency() >= 0.5) {
				return count;
			}
		}
		return null;
	}
	
	/**
	 * Returns the lower median count in this distribution.
	 * @return
	 */
	public Count medianInf() {
		compute();
		for (Count count : counts.values()) {
			if (count.getRelativeAtLeastFrequency() <= 0.5) {
				return count;
			}
		}
		return null;
	}
	
	public Count[] confidenceInterval(double risk) {
		compute();
		List<Count> counts = new ArrayList<Count>(getCounts());
		double halfRisk = risk / 2;
		Count lo = getIntervalBoundary(counts, halfRisk, CountSelector.RELATIVE_AT_MOST);
		Collections.reverse(counts);
		Count hi = getIntervalBoundary(counts, halfRisk, CountSelector.RELATIVE_AT_LEAST);
		return new Count[] { lo , hi };
	}
	
	public static Count getIntervalBoundary(List<Count> counts, double halfRisk, CountSelector selector) {
		Count result = counts.get(0);
		for (Count count : counts) {
			if (selector.get(count).doubleValue() > halfRisk) {
				break;
			}
			result = count;
		}
		return result;
	}
	
}
