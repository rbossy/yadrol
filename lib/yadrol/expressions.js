'use strict';

var Precedence = {
    SEQUENCE: 1,
    OUTPUT: 2,
    ASSIGN: 3,
    CONTROL: 4,
    OR: 5,
    AND: 6,
    NOT: 7,
    COMPARISON: 8,
    INDEXOF: 9,
    APPEND: 10,
    RANGE: 11,
    PLUS: 12,
    MULT: 13,
    SIGN: 14,
    BEST: 15,
    DRAW: 16,
    DICE: 17,
    UNARY: 18,
    SUBSCRIPT: 19,
    ATOM: 20
}

class ExpressionStringer {
    constructor(scope) {
	this.result = '';
	this.scope = scope;
    }

    _append(s) {
	this.result += s;
    }

    lparen() {
	this._append('(');
	return this;
    }

    rparen() {
	this._append(')');
	return this;
    }

    lcurly() {
	this._append('{');
	return this;
    }

    rcurly() {
	this._append('}');
	return this;
    }

    lbracket() {
	this._append('[');
	return this;
    }

    rbracket() {
	this._append(']');
	return this;
    }

    space() {
	this._append(' ');
	return this;
    }

    comma() {
	this._append(',');
	return this;
    }

    colon() {
	this._append(':');
	return this;
    }

    operator(op) {
	this._append(op);
	return this;
    }

    keyword(kw) {
	this._append(kw);
	return this;
    }

    literal(lit) {
	this._append(lit);
	return this;
    }

    doubleQuote() {
	return this.literal('"');
    }

    identifier(id) {
	this._append(id);
	return this;
    }

    expression(expr, prec) {
	expr.toString(this, prec);
	return this;
    }

    unaryOperator(operator, operand, prec, space) {
	this.operator(operator);
	if (space) {
	    this.space();
	}
	return this.expression(operand, prec);
    }

    binaryOperator(operator, left, right, prec, spaceBefore, spaceAfter) {
	this.expression(left, prec)
	if (spaceBefore) {
	    this.space();
	}
	this.operator(operator);
	if (spaceAfter) {
	    this.space();
	}
	return this.expression(right, prec);
    }

    nAryOperator(operator, expressions, prec, space) {
	var first = true;
	for (var expr of expressions) {
	    if (first) {
		first = false;
	    }
	    else {
		if (space) {
		    this.space().operator(operator).space();
		}
		else {
		    this.operator(operator);
		}
	    }
	    this.expression(expr, prec);
	}
	return this;
    }

    expressionList(expressions) {
	var first = true;
	for (var expr of expressions) {
	    if (first) {
		first = false;
	    }
	    else {
		this.comma().space();
	    }
	    this.expression(expr, Precedence.SEQUENCE);
	}
	return this;
    }

    expressionMap(expressions, args) {
	var first = true;
	for (var entry of expressions.entries()) {
	    if (first) {
		first = false;
	    }
	    else {
		this.comma().space();
	    }
	    this.identifier(entry[0]);
	    var value = entry[1];
	    if (args) {
		if ((value instanceof Constant) && (value.value === undefined)) {
		    continue;
		}
		args = false;
	    }
	    this.colon().space().expression(value, Precedence.SEQUENCE);
	}
	return this;
    }
}

class Position {
    constructor(line, offset, absolute) {
	if ((typeof line) != 'number') {
	    throw new Error('line is not a number: ' + line);
	}
	if ((typeof offset) != 'number') {
	    throw new Error('offset is not a number: ' + offset);
	}
	if ((typeof absolute) != 'number') {
	    throw new Error('absolute is not a number: ' + absolute);
	}
	this.line = line;
	this.offset = offset;
	this.absolute = absolute;
    }
}

class Location {
    constructor(source, begin, end) {
	if ((typeof source) != 'string') {
	    console.log(sourceFile);
	    throw new Error('source is not a string: ' + source);
	}
	if (!(begin instanceof Position)) {
	    throw new Error('begin is not a Position: ' + begin);
	}
	if (!(end instanceof Position)) {
	    throw new Error('end is not a Position: ' + end);
	}
	this.source = source;
	this.begin = begin;
	this.end = end;
    }

    static fromLexer(source, first, last) {
	if (last === undefined) {
	    last = first;
	}
	return new Location(source, new Position(first.first_line, first.first_column, first.range[0]), new Position(last.last_line, last.last_column, last.range[1]));
    }

    static none() {
	return Location.NONE;
    }
}
Location.NONE = new Location('<<none>>', new Position(0, 0, 0), new Position(0, 0, 0));

class RecordLogger {
    constructor(sampleSize, defaultType) {
	this.diceRecords = [];
	this.rollRecords = RecordLogger._createRecords(RollRecord);
	this.sampleRecords = RecordLogger._createRecords(SampleRecord);
	this.sampleSize = sampleSize;
	this.defaultType = defaultType;
    }

    clear() {
	this.diceRecords = [];
	this.rollRecords = RecordLogger._createRecords(RollRecord);
	this.sampleRecords = RecordLogger._createRecords(SampleRecord);	
    }

    static _createRecords(ctor) {
	var result = [];
	result.current = undefined;
	result.ctor = ctor;
	return result;
    }

    recordDice(type, result) {
	if (this.sampleRecords.current !== undefined) {
	    return;
	}
	var rec = new DiceRecord(type, result);
	this.diceRecords.push(rec);
	if (this.rollRecords.current) {
	    this.rollRecords.current.diceRecords.push(rec);
	}
    }

    _currentOutputRecord() {
	return (this.rollRecords.current !== undefined) || (this.sampleRecords.current !== undefined);
    }

    _record(records, name, expression, type, scope) {
	if (this._currentOutputRecord()) {
	    throw new Error('nested output');
	}
	records.current = new records.ctor(name, expression, type);
	records.current.result = records.current.run(scope, this.sampleSize);
	records.push(records.current);
	records.current = undefined;
    }

    recordRoll(name, expression, type, scope) {
	this._record(this.rollRecords, name, expression, type, scope);
    }

    recordSample(name, expression, type, scope) {
	this._record(this.sampleRecords, name, expression, type, scope);
    }
}

class DiceRecord {
    constructor(diceType, result) {
	this.diceType = diceType;
	this.result = result;
    }
}

class OutputRecord {
    constructor(name, expression, type) {
	this.name = name;
	this.expression = expression;
	this.type = type;
	this.result = undefined;
    }

    run(scope, sampleSize) {
	throw new Error('unimplemented: run');
    }

    getName(scope) {
	if (this.name === undefined) {
	    return this.expression.toString(new ExpressionStringer(scope));
	}
	return this.name.evaluate(scope);
    }
}

class RollRecord extends OutputRecord {
    constructor(name, expression, type) {
	super(name, expression, type);
	this.diceRecords = [];
    }

    run(scope, sampleSize) {
	return this.expression.evaluate(scope, this.type);
    }
}

class Counter {
    constructor(value) {
	this.value = value;
	this.n = 1;
    }

    incr() {
	this.n += 1;
    }

    static COMPARATOR(a, b) {
	return ValueComparator.compare(a.value, b.value);
    }
}

class Distribution extends Array {
    constructor() {
	super();
	this.total = 0;
	this.aggregated = false;
    }

    incr(value) {
	this.aggregated = false;
	this.total += 1;
	for (var c of this) {
	    if (ValueComparator.equal(value, c.value)) {
		c.incr();
		return;
	    }
	}
	this.push(new Counter(value));
    }

    aggregate() {
	if (this.aggregated) {
	    return;
	}
	this.sort(Counter.COMPARATOR);
	var atMost = 0;
	var atLeast = this.total;
	for (var c of this) {
	    atMost += c.n;
	    c.atMost = atMost;
	    c.atLeast = atLeast;
	    atLeast -= c.n;
	    c.relative = c.n / this.total;
	    c.relativeAtMost = c.atMost / this.total;
	    c.relativeAtLeast = c.atLeast / this.total;
	}
	this.aggregated = true;
    }

    mean(scope) {
	this.aggregate();
	var result = 0.0;
	for (var c of this) {
	    var iVal = ValueConverter.convert(scope, c.value, 'number');
	    result += iVal * c.relative;
	}
	return result;
    }

    mode() {
	this.aggregate();
	var result = undefined;
	for (var c of this) {
	    if ((this.mode === undefined) || (c.n > this.mode.n)) {
		result = c;
	    }
	}
	return result;
    }

    medianInf(defaultToMedianSup) {
	this.aggregate();
	for (var c of this) {
	    if (c.relativeAtLeast <= 0.5) {
		return c;
	    }
	}
	if (defaultToMedianSup) {
	    return this.medianSup(false);
	}
    }

    medianSup(defaultToMedianInf) {
	this.aggregate();
	for (var c of this) {
	    if (c.relativeAtMost >= 0.5) {
		return c;
	    }
	}
	if (defaultToMedianInf) {
	    return this.medianInf(false);
	}
    }

    stddev(scope, mean) {
	if (mean === undefined) {
	    mean = this.mean();
	}
	this.aggregate();
	var result = 0.0;
	for (var c of this) {
	    var iVal = convert(scope, c.value, 'number');
	    result += ((iVal - mean) * c.relative) ** 2;
	}
	return result;
    }
}

class SampleRecord extends OutputRecord {
    constructor(name, expression, type) {
	super(name, expression, type);
    }

    run(scope, sampleSize) {
	var result = new Distribution();
	for (var i = 0; i < sampleSize; ++i) {
	    var v = this.expression.evaluate(scope, this.type);
	    result.incr(v);
	}
	return result;
    }
}

class Expression {
    constructor(location, prec, type) {
	if (!(location instanceof Location)) {
	    throw new Error('location is not a Location: ' + location);
	}
	if (type === undefined) {
	    throw new Error('expression has no type');
	}
	this.location = location;
	this.precedence = prec;
	this.nativeType = type;
    }

    nativeEvaluator(scope) {
	throw new Error('unimplemented nativeEvaluator(scope)');
    }

    evaluate(scope, type) {
	var nativeResult = this.nativeEvaluator(scope);
	if ((type === undefined) || (type == 'native') || (type == this.nativeType)) {
	    return nativeResult;
	}
	return ValueConverter.convert(scope, nativeResult, type);
    }

    assign(scope, value) {
	throw 'cannot assign';
    }

    toString(stringer, precedence) {
	if (precedence === undefined) {
	    precedence = Precedence.SEQUENCE;
	}
	if (stringer === undefined) {
	    stringer = new ExpressionStringer();
	}
	if (precedence > this.precedence) {
	    stringer.lparen();
	    this._toStringNoParen(stringer);
	    stringer.rparen();
	}
	else {
	    this._toStringNoParen(stringer);
	}
	return stringer.result;
    }

    _toStringNoParen(stringer) {
	throw 'unimplemented _toStringNoParen(stringer)';
    }

    requiresSpaceAsDiceNumber() {
	return true;
    }

    requiresSpaceAsDiceType() {
	return true;
    }
}

class Constant extends Expression {
    constructor(location, value) {
	super(location, Precedence.ATOM, valueType(value));
	this.value = value;
    }

    nativeEvaluator(scope) {
	return this.value;
    }

    _toStringNoParen(stringer) {
	switch (this.nativeType) {
	case 'undefined': stringer.keyword('undef'); break;
	case 'string': stringer.doubleQuote().literal(this.value).doubleQuote(); break;
	case 'boolean':
	case 'number': stringer.literal(String(this.value)); break;
	}
    }
    
    requiresSpaceAsDiceType() {
	return (this.nativeType != 'number');
    }
    
    requiresSpaceAsDiceNumber() {
	return (this.nativeType != 'number');
    }
}

class StringInterpolation extends Expression {
    constructor(location, generators) {
	super(location, Precedence.ATOM, 'string');
	this.generators = generators;
    }

    nativeEvaluator(scope) {
	var result = '';
	for (var g of this.generators) {
	    result += g.evaluate(scope);
	}
	return result;
    }

    _toStringNoParen(stringer) {
	stringer.doubleQuote();
	for (var g of this.generators) {
	    if (g instanceof Variable) {
		stringer.literal('{');
		stringer.literal(g.name);
		stringer.literal('}');
		continue;
	    }
	    switch (g.value) {
	    case '\n': stringer.literal('\\n'); break;
	    case '"': stringer.literal('\\"'); break;
	    case '{': stringer.literal('\\{'); break;
	    default: stringer.literal(g.value); break;
	    }
	}
	stringer.doubleQuote();
    }
}

class ContainerConstructor extends Expression {
    constructor(location, values, type) {
	super(location, Precedence.ATOM, type);
	this.values = values;
    }
    
    nativeEvaluator(scope) {
	return this.values.map(function(exp) { return exp.evaluate(scope); });
    }

    assign(scope, value) {
	var container = convert(scope, value, this.nativeType);
	for (var entry of this.values) {
	    var key = entry[0];
	    var lvalue = entry[1];
	    lvalue.assign(scope, container[key]);
	}
    }

    _toStringNoParen(stringer) {
	switch(this.nativeType) {
	case 'list': stringer.lbracket().expressionList(this.values).rbracket(); break;
	case 'map': stringer.lcurly().expressionMap(this.values, false).rcurly(); break;
	}
    }

    requiresSpaceAsDiceNumber() {
	return false;
    }

    requiresSpaceAsDiceType() {
	return false;
    }
}

class Lambda extends Expression {
    constructor(location, args, body) {
	super(location, Precedence.ATOM, 'function');
	this.args = args;
	this.body = body;
    }

    nativeEvaluator(scope) {
	return new YadrolFunction(scope, this.args, this.body);
    }

    _toStringNoParen(stringer) {
	stringer.keyword('fun')
	    .lparen().expressionMap(this.args, true).rparen()
	    .space().lcurly().space().expression(this.body, Precedence.SEQUENCE).space().rcurly();
    }

    requiresSpaceAsDiceNumber() {
	return false;
    }
}

class OperatorSymbol {
    constructor(symbol, precedence, operandPrecedence, spaceBefore, spaceAfter) {
	this.symbol = symbol;
	this.precedence = precedence;
	this.operandPrecedence = operandPrecedence;
	this.spaceBefore = spaceBefore;
	this.spaceAfter = (spaceAfter === undefined) ? spaceBefore : spaceAfter;
    }
}

class UnaryOperator extends Expression {
    constructor(location, operator, type, operandType, operand, compute) {
	super(location, operator.precedence, type);
	this.operator = operator;
	this.operandType = operandType;
	this.operand = operand;
	if (operator.hasOwnProperty('compute')) {
	    this.compute = operator.compute;
	}
	if (compute != undefined) {
	    this.compute = compute;
	}
    }

    nativeEvaluator(scope) {
	var operand = this.operand.evaluate(scope, this.operandType);
	return this.compute(operand);
    }

    compute(operand) {
	throw 'unimplemented compute(operand)';
    }

    _toStringNoParen(stringer) {
	stringer.unaryOperator(this.operator.symbol, this.operand, this.operator.operandPrecedence, this.operator.space);
    }
}

class BinaryOperator extends Expression {
    constructor(location, operator, type, leftType, rightType, left, right, compute) {
	super(location, operator.precedence, type);
	this.operator = operator;
	this.leftType = leftType;
	this.rightType = rightType;
	this.left = left;
	this.right = right;
	if (operator.hasOwnProperty('compute')) {
	    this.compute = operator.compute;
	}
	if (compute != undefined) {
	    this.compute = compute;
	}
    }

    nativeEvaluator(scope) {
	var left = this.left.evaluate(scope, this.leftType);
	var right = this.right.evaluate(scope, this.rightType);
	return this.compute(left, right);
    }

    compute(left, right) {
	throw 'unimplemented compute(left, right)';
    }

    _toStringNoParen(stringer) {
	stringer.binaryOperator(this.operator.symbol, this.left, this.right, this.operator.operandPrecedence, this.operator.spaceBefore, this.operator.spaceAfter);
    }
}

class Append extends Expression {
    constructor(location, target, source) {
	super(location, Precedence.APPEND, 'native');
	this.target = target;
	this.source = source;
    }

    nativeEvaluator(scope) {
	var target = this.target.evaluate(scope);
	switch(valueType(target)) {
	case 'map': {
	    var source = this.source.evaluate(scope, 'map');
	    for (var entry of source) {
		target.set(entry[0], entry[1]);
	    }
	    break;
	}
	case 'list': {
	    var source = this.source.evaluate(scope);
	    for (var v of source) {
		target.push(v);
	    }
	}
	}
	return target;
    }

    _toStringNoParen(stringer) {
	stringer.binaryOperator('<<', this.target, this.source, Precedence.RANGE, true, true);
    }
}

class Arithmetic extends BinaryOperator {
    constructor(location, operator, left, right) {
	super(location, operator, 'number', 'number', 'number', left, right);
    }
}
Arithmetic.PLUS = new OperatorSymbol('+', Precedence.PLUS, Precedence.MULT, true);
Arithmetic.PLUS.compute = function(left, right) { return left + right; }
Arithmetic.MINUS = new OperatorSymbol('-', Precedence.PLUS, Precedence.MULT, true);
Arithmetic.MINUS.compute = function(left, right) { return left - right; }
Arithmetic.MULT = new OperatorSymbol('*', Precedence.MULT, Precedence.SIGN, true);
Arithmetic.MULT.compute = function(left, right) { return left * right; }
Arithmetic.DIV = new OperatorSymbol('/', Precedence.MULT, Precedence.SIGN, true);
Arithmetic.DIV.compute = function(left, right) { return Math.floor(left / right); }
Arithmetic.MOD = new OperatorSymbol('%', Precedence.MULT, Precedence.SIGN, true);
Arithmetic.MOD.compute = function(left, right) { return left % right; }

class Best extends UnaryOperator {
    constructor(location, operator, operand) {
	super(location, operator, 'native', 'list', operand);
    }

    compute(operand) {
	if (operand.length == 0) {
	    return undefined;
	}
	return this.operator.single(operand);
    }

    _toStringNoParen(stringer) {
	stringer.keyword(this.operator.symbol).space()
	    .keyword('of').space()
	    .expression(this.operand, this.operator.operandPrecedence);
    }

    static indexComparator(list)  {
	return function(a, b) {
	    return ValueComparator.compare(list[a], list[b]);
	};
    }
}
Best.HIGHEST = new OperatorSymbol('highest', Precedence.BEST, Precedence.DRAW, true);
Best.HIGHEST.single = function(list) {
    var result = list[0];
    for (var i = 1; i < list.length; ++i) {
	var v = list[i];
	if (ValueComparator.compare(v, result) > 0) {
	    result = v;
	}
    }
    return result;
};
Best.HIGHEST.multiple = function(n, list, indexes) {
    indexes.sort(Best.indexComparator(list));
    indexes.splice(0, indexes.length - n);
    indexes.sort();
};
Best.LOWEST = new OperatorSymbol('lowest', Precedence.BEST, Precedence.DRAW, true);
Best.LOWEST.single = function(list) {
    var result = list[0];
    for (var i = 1; i < list.length; ++i) {
	var v = list[i];
	if (ValueComparator.compare(v, result) < 0) {
	    result = v;
	}
    }
    return result;
};
Best.LOWEST.multiple = function(n, list, indexes) {
    indexes.sort(Best.indexComparator(list));
    indexes.splice(n, indexes.length - n);
    indexes.sort();
};
Best.FIRST = new OperatorSymbol('first', Precedence.BEST, Precedence.DRAW, true);
Best.FIRST.single = function(list) { return list[0]; };
Best.FIRST.multiple =function(n, list, indexes) {
    indexes.splice(n, indexes.length - n);
};
Best.LAST = new OperatorSymbol('last', Precedence.BEST, Precedence.DRAW, true);
Best.LAST.single = function(list) { return list[list.length - 1]; };
Best.LAST.multiple = function(n, list, indexes) {
    indexes.splice(0, indexes.length - n);
}

class BestMultiple extends BinaryOperator {
    constructor(location, operator, left, right) {
	super(location, operator, 'list', 'number', 'list', left, right);
    }

    compute(n, list) {
	if (n < 0) {
	    return undefined;
	}
	if (n == 0) {
	    return [];
	}
	if (n >= list.length) {
	    return list.slice();
	}
	var indexes = list.map(function(v, i) { return i; });
	this.operator.multiple(n, list, indexes);
	return indexes.map(function(v) { return list[v]; });
    }

    _toStringNoParen(stringer) {
	stringer.keyword(this.operator.symbol).space()
	    .expression(this.left, Precedence.ASSIGN).space()
	    .keyword('of').space()
	    .expression(this.right, this.operator.operandPrecedence);
    }
}

class BooleanAnd extends Expression {
    constructor(location, left, right) {
	super(location, Precedence.AND, 'native');
	this.left = left;
	this.right = right;
    }

    compute(scope) {
	if (this.left.evaluate(scope, 'boolean')) {
	    return this.right.evaluate(scope, 'boolean');
	}
	return false;
    }

    _toStringNoParen(stringer) {
	stringer.expression(this.left, Precedence.NOT).space()
	    .operator('and').space()
	    .expression(this.right, Precedence.NOT);
    }
}

class BooleanOr extends Expression {
    constructor(location, left, right) {
	super(location, Precedence.OR, 'native');
	this.left = left;
	this.right = right;
    }

    compute(scope) {
	if (this.left.evaluate(scope, 'boolean')) {
	    return true;
	}
	return this.right.evaluate(scope, 'boolean');
    }

    _toStringNoParen(stringer) {
	stringer.expression(this.left, Precedence.AND).space()
	    .operator('or').space()
	    .expression(this.right, Precedence.AND);
    }
}

class BooleanNot extends UnaryOperator {
    constructor(location, operand) {
	super(location, BooleanNot.OPERATOR, 'boolean', 'boolean', operand);
    }

    compute(operand) {
	return !operand;
    }
}
BooleanNot.OPERATOR = new OperatorSymbol('not', Precedence.NOT, Precedence.COMPARISON, true);

class Call extends Expression {
    constructor(location, fun, posArgs, namedArgs) {
	super(location, Precedence.SUBSCRIPT, 'native');
	this.fun = fun;
	this.posArgs = posArgs;
	this.namedArgs = namedArgs;
    }

    nativeEvaluator(scope) {
	var fun = this.fun.evaluate(scope, 'function');
	var posArgs = this.posArgs.map(function(exp) { return exp.evaluate(scope); });
	var namedArgs = this.namedArgs.map(function(exp) { return exp.evaluate(scope); });
	return fun.call(posArgs, namedArgs);
    }

    _toStringNoParen(stringer) {
	stringer.expression(this.fun, Precedence.SUBSCRIPT)
	    .lparen()
	    .expressionList(this.posArgs);
	if ((this.posArgs.length > 0) && (this.namedArgs.size > 0)) {
	    stringer.comma().space();
	}
	stringer.expressionMap(this.namedArgs, false).rparen();
    }

    requiresSpaceAsDiceNumber() {
	return false;
    }
}

class Assign extends Expression {
    constructor(location, lvalue, rvalue) {
	super(location, Precedence.ASSIGN, rvalue.nativeType);
	this.lvalue = lvalue;
	this.rvalue = rvalue;
    }

    nativeEvaluator(scope) {
	var rvalue = this.rvalue.evaluate(scope);
	this.lvalue.assign(scope, rvalue);
	return rvalue;
    }

    _toStringNoParen(stringer) {
	stringer.binaryOperator('=', this.lvalue, this.rvalue, Precedence.CONTROL, true, true);
    }
}

class NumberComparison extends BinaryOperator {
    constructor(location, operator, left, right) {
	super(location, operator, 'boolean', 'number', 'number', left, right);
    }
}
NumberComparison.EQ = new OperatorSymbol('==', Precedence.COMPARISON, Precedence.INDEXOF, true);
NumberComparison.EQ.compute = function(left, right) { return left === right; };
NumberComparison.NE = new OperatorSymbol('!=', Precedence.COMPARISON, Precedence.INDEXOF, true);
NumberComparison.NE.compute = function(left, right) { return left !== right; };
NumberComparison.LT = new OperatorSymbol('<', Precedence.COMPARISON, Precedence.INDEXOF, true);
NumberComparison.LT.compute = function(left, right) { return left < right; };
NumberComparison.LE = new OperatorSymbol('<=', Precedence.COMPARISON, Precedence.INDEXOF, true);
NumberComparison.LE.compute = function(left, right) { return left <= right; };
NumberComparison.GT = new OperatorSymbol('>', Precedence.COMPARISON, Precedence.INDEXOF, true);
NumberComparison.GT.compute = function(left, right) { return left > right; };
NumberComparison.GE = new OperatorSymbol('<=', Precedence.COMPARISON, Precedence.INDEXOF,true);
NumberComparison.GE.compute = function(left, right) { return left >= right; };

class GeneralComparison extends BinaryOperator {
    constructor(location, operator, left, right) {
	super(location, operator, 'boolean', 'native', 'native', left, right);
    }

    compute(left, right) {
	var c = compare(left, right);
	return this.operator.compare(c);
    }
}
GeneralComparison.EQ = new OperatorSymbol('===', Precedence.COMPARISON, Precedence.INDEXOF, true);
GeneralComparison.EQ.compare = function(c) { return c == 0; };
GeneralComparison.NE = new OperatorSymbol('!==', Precedence.COMPARISON, Precedence.INDEXOF, true);
GeneralComparison.NE.compare = function(c) { return c != 0; };

class Conditional extends Expression {
    constructor(location, condition, ifTrue, ifFalse) {
	super(location, Precedence.CONTROL, 'native');
	this.condition = condition;
	this.ifTrue = ifTrue;
	this.ifFalse = ifFalse;
    }

    nativeEvaluator(scope) {
	if (this.condition.evaluate(scope, 'boolean')) {
	    return this.ifTrue.evaluate(scope);
	}
	return this.ifFalse.evaluate(scope);
    }

    _toStringNoParen(stringer) {
	stringer.keyword('if').space()
	    .expression(this.condition, Precedence.OR).space()
	    .keyword('then').space()
	    .expression(this.ifTrue, Precedence.OR).space()
	    .keyword('else').space()
	    .expression(this.ifFalse, Precedence.OR);
    }
}

class Convert extends Expression {
    constructor(location, operand, type) {
	super(location, Precedence.UNARY, type);
	this.operand = operand;
    }

    nativeEvaluator(scope) {
	return this.operand.evaluate(scope, this.nativeType);
    }

    _toStringNoParen(stringer) {
	stringer.keyword(this.type).space().expression(this.operand, Precedence.SUBSCRIPT);
    }
}

class Count extends UnaryOperator {
    constructor(location, operand) {
	super(location, Count.OPERATOR, 'number', 'native', operand);
    }

    compute(operand) {
	var type = valueType(operand);
	switch (type) {
	case 'undefined': return 0;
	case 'list': return operand.length;
	case 'map': return operand.size;
	case 'function': throw 'cannot count function';
	default: return 1;
	}
    }
}
Count.OPERATOR = new OperatorSymbol('count', Precedence.UNARY, Precedence.SUBSCRIPT, true);

class Draw extends UnaryOperator {
    constructor(location, operand) {
	super(location, Draw.OPERATOR, 'native', 'list', operand);
    }

    compute(operand) {
	return operand.shift();
    }

    _toStringNoParen(stringer) {
	stringer.keyword('draw').space().keyword('from')
	    .expression(this.operand, this.operator.operandPrecedence);
    }
}
Draw.OPERATOR = new OperatorSymbol('draw', Precedence.DRAW, Precedence.DICE, true);

class DrawMultiple extends BinaryOperator {
    constructor(location, n, list) {
	super(location, Draw.OPERATOR, 'list', 'number', 'list', n, list);
    }

    compute(n, list) {
	if (n <= 0) {
	    return [];
	}
	return list.splice(0, n);
    }

    _toStringNoParen(stringer) {
	stringer.keyword('draw').space()
	    .expression(this.left, this.operator.operandPrecedence).space()
	    .keyword('from')
	    .expression(this.operand, this.operator.operandPrecedence);
    }
}

class Die extends UnaryOperator {
    constructor(location, operand, recordLogger) {
	super(location, Die.OPERATOR, 'native', 'native', operand);
	this.recordLogger = recordLogger;
    }

    compute(operand) {
	var type = valueType(operand);
	if (!ROLLER.hasOwnProperty(type)) {
	    throw 'cannot roll ' + type;
	}
	var result = ROLLER[type](operand);
	this.recordLogger.recordDice(type, [result]);
	return result;
    }

    _toStringNoParen(stringer) {
	stringer.operator('d');
	if (this.operand.requiresSpaceAsDiceType()) {
	    stringer.space();
	}
	stringer.expression(this.operand, this.operator.operandPrecedence);
    }
}
Die.ROLLER = {
    'number': function(value) { if (value <= 0) return undefined; return Math.floor(Math.random() * value) + 1; },
    'list': function(value) { if (value.length == 0) return undefined; return value[Math.floor(Math.random() * value)]; },
    'map': function(value) {
	if (value.length == 0)
	    return undefined;
	var it = value.entries();
	for (var i = Math.floor(Math.random() * value); i > 0; --i) { it.next(); }
	return new YadrolMap([i.next().value]);
    },
    'function': function(value) { return value.call([], new YadrolMap()); }
}
Die.OPERATOR = new OperatorSymbol('d', Precedence.DICE, Precedence.UNARY, false);

class Dice extends BinaryOperator {
    constructor(location, n, diceType, recordLogger) {
	super(location, Die.OPERATOR, 'list', 'number', 'native', n, diceType);
	this.recordLogger = recordLogger;
    }

    compute(n, diceType) {
	var type = valueType(diceType);
	if (!Die.ROLLER.hasOwnProperty(type)) {
	    throw 'cannot roll ' + type;
	}
	if ((type === 'function') && (diceType.args.length > 0)) {
	    return diceType.call([n], new YadrolMap());
	}
	if (n <= 0) {
	    throw 'invalid dice number';
	}
	var result = [];
	var roller = Die.ROLLER[type]
	for (var i = 0; i < n; ++i) {
	    result.push(roller(diceType));
	}
	this.recordLogger.recordDice(diceType, result);
	return result;
    }

    _toStringNoParen(stringer) {
	stringer.expression(this.left, this.operator.operandPrecedence);
	if (this.left.requiresSpaceAsDiceNumber()) {
	    stringer.space();
	}
	stringer.operator('d');
	if (this.right.requiresSpaceAsDiceType()) {
	    stringer.space();
	}
	stringer.expression(this.right, this.operator.operandPrecedence);
    }
}

class ForLoop extends Expression {
    constructor(location, indexVariable, itemVariable, out, container, condition) {
	super(location, Precedence.CONTROL, 'native');
	this.indexVariable = indexVariable;
	this.itemVariable = itemVariable;
	this.out = out;
	this.container = container;
	this.condition = condition;
    }

    static _listAppend(key, value) {
	this.push(value);
    }

    static _mapAppend(key, value) {
	this.set(key, value);
    }

    _initResult(container) {
	switch (valueType(container)) {
	case 'list': {
	    var result = [];
	    result.append = ForLoop._listAppend;
	    return result;
	}
	case 'map': {
	    var result = new ForLoopMapResult();
	    result.append = ForLoop.mapAppend;
	    return result;
	}
	default: throw 'illegal loop: ' + container;
	}
    }

    _createScope(scope) {
	var loopVars = new YadrolMap([[this.itemVariable, undefined]]);
	if (this.indexVariable) {
	    loopVars.set(this.indexVariable, undefined);
	}
	return new Scope(scope);
    }

    _updateScope(loopScope, key, value) {
	if (this.indexVariable) {
	    loopScope.set(this.indexVariable, key);
	}
	loopScope.set(this.itemVariable, value);
    }

    nativeEvaluator(scope) {
	var container = this.container.evaluate(scope);
	var result = this._initResult(container);
	var loopScope = this._createScope(scope);
	var it = container.entries();
	while (true) {
	    var n = it.next();
	    if (n.done) {
		break;
	    }
	    var key = n.value[0];
	    var value = n.value[1];
	    this._updateScope(loopScope, key, value);
	    if (this.condition.evaluate(loopScope, 'boolean')) {
		result.append(key, this.out.evaluate(loopScope));
	    }
	}
	return result;
    }

    _toStringNoParen(stringer) {
	if ((this.out instanceof Variable) && (this.out.name == this.itemVariable)) {
	    stringer.keyword('for').space();
	}
	else {
	    stringer.expression(this.out, Precedence.OR).space()
		.keyword('for').space();
	}
	if (this.indexVariable) {
	    stringer.identifier(this.indexVariable).comma().space();
	}
	stringer.identifier(this.itemVariable).space()
	    .keyword('in').space()
	    .expression(this.container, Precedence.OR);
	if ((this.condition instanceof Constant) && (this.condition.value === true)) {
	    return;
	}
	stringer.space().keyword('if').space().expression(this.condition, Precedence.OR);
    }
}

class IndexOf extends BinaryOperator {
    constructor(location, element, container) {
	super(location, IndexOf.OPERATOR, 'native', 'native', 'native', element, container);
    }

    compute(element, container) {
	var type = valueType(container);
	switch (type) {
	case 'list':
	case 'map': {
	    for (var entry of container.entries()) {
		if (ValueComparator.equal(element, entry[1])) {
		    return entry[0];
		}
	    }
	    return undefined;
	}
	case 'undefined': return undefined;
	default: return ValueComparator.equal(element, container);
	}
    }
}
IndexOf.OPERATOR = new OperatorSymbol('in', Precedence.INDEXOF, Precedence.APPEND, true);

class ListReorder extends UnaryOperator {
    constructor(location, operator, list) {
	super(location, operator, 'list', 'list', list);
    }

    compute(list) {
	this.operator.reorder(list);
	return list;
    }
}
ListReorder.REVERSED = new OperatorSymbol('reverse', Precedence.UNARY, Precedence.SUBSCRIPT, true);
ListReorder.REVERSED.reorder = function(list) { list.reverse(); }; 
ListReorder.SORTED = new OperatorSymbol('sort', Precedence.UNARY, Precedence.SUBSCRIPT, true);
ListReorder.SORTED.reorder = function(list) { list.sort(ValueComparator.compare); }; 
ListReorder.SHUFFLED = new OperatorSymbol('shuffle', Precedence.UNARY, Precedence.SUBSCRIPT, true);
ListReorder.SHUFFLED.reorder = function(list) {
    for (var i = list.length - 1; i > 0; --i) {
	var j = Math.floor(Math.random() * (i + 1));
	if (i != j) {
	    var x = list[i];
	    list[i] = list[j];
	    list[j] = x;
	}
    }
};
    
class Range extends BinaryOperator {
    constructor(location, begin, end) {
	super(location, Range.OPERATOR, 'list', 'number', 'number', begin, end);
    }

    compute(begin, end) {
	var result = [];
	var step = begin <= end ? 1 : -1;
	for (var i = begin; i != end; i += step) {
	    result.push(i);
	}
	return result;
    }
}
Range.OPERATOR = new OperatorSymbol('..', Precedence.RANGE, Precedence.PLUS, true);

class Repeat extends Expression {
    constructor(location, expression, condition, conditionBefore, limit) {
	super(location, Precedence.CONTROL, 'list');
	this.expression = expression;
	this.condition = condition;
	this.conditionBefore = conditionBefore;
	this.limit = limit;
    }

    nativeEvaluator(scope) {
	var loopScope = new Scope(scope);
	var result = [];
	if (this.conditionBefore) {
	    while ((result.length - 1 < this.limit) && this.condition.evaluate(loopScope, 'boolean')) {
		result.push(this.expression.evaluate(loopScope));
	    }
	}
	else {
	    do {
		result.push(this.expression.evaluate(loopScope));
	    }
	    while ((result.length - 1 <  this.limit) && this.condition.evaluate(loopScope, 'boolean'));
	}
	return result;
    }

    _toStringNoParen(stringer) {
	if (this.conditionBefore) {
	    stringer.keyword('while').space()
		.expression(this.condition, Precedence.OR).space()
		.keyword('repeat').space()
		.expression(this.expression, Precedence.OR);
	    if (this.limit != Number.MAX_VALUE) {
		stringer.space().keyword('limit').space().literal(String(this.limit));
	    }
	}
	else {
	    stringer.keyword('repeat').space()
		.expression(this.expression. Precedence.OR).space()
		.keyword((this.limit == 1) ? 'if' : 'while').space()
		.expression(this.condition, Precedence.OR);
	    if ((this.limit != 1) && (this.limit != Number.MAX_VALUE)) {
		stringer.space().keyword('limit').space().literal(String(this.limit));
	    }
	}
    }
}

class ScopeVariables extends Expression {
    constructor(location, selector) {
	super(location, selector.precedence, 'map', selector.getScopeVariables);
	this.selector = selector;
    }

    _toStringNoParen(stringer) {
	stringer.keyword(this.selector.symbol);
    }

    nativeEvaluator(scope) {
	return this.selector.getScopeVariables(scope);
    }
}
ScopeVariables.LOCAL = new OperatorSymbol('local', Precedence.ATOM, Precedence.ATOM, false);
ScopeVariables.LOCAL.getScopeVariables = function(scope) { return scope.variables; };
ScopeVariables.OUTER = new OperatorSymbol('outer', Precedence.ATOM, Precedence.ATOM, false);
ScopeVariables.OUTER.getScopeVariables = function(scope) { if (scope.parent) return scope.parent.variables; return new YadrolMap(); };
ScopeVariables.GLOBAL = new OperatorSymbol('global', Precedence.ATOM, Precedence.ATOM, false);
ScopeVariables.GLOBAL.getScopeVariables = function(scope) { var result = scope; while (result.parent) { result = result.parent; } return result.variables; };

class Sequence extends BinaryOperator {
    constructor(location, left, right) {
	super(location, Sequence.OPERATOR, right.nativeType, 'native', 'native', left, right);
    }

    nativeEvaluator(scope) {
	this.left.evaluate(scope);
	return this.right.evaluate(scope);
    }
}
Sequence.OPERATOR = new OperatorSymbol(';', Precedence.SEQUENCE, Precedence.OUTPUT, false, true);

class Sign extends UnaryOperator {
    constructor(location, operator, operand) {
	super(location, operator, 'number', 'number', operand);
    }
}
Sign.PLUS = new OperatorSymbol('+', Precedence.SIGN, Precedence.BEST, false);
Sign.PLUS.compute = function(operand) { return operand; }
Sign.MINUS = new OperatorSymbol('', Precedence.SIGN, Precedence.BEST, false);
Sign.MINUS.compute = function(operand) { return -operand; }

class Subscript extends Expression {
    constructor(location, container, subscript) {
	super(location, Precedence.SUBSCRIPT, 'native');
	this.container = container;
	this.subscript = subscript;
    }

    nativeEvaluator(scope) {
	return this._compute(this.container.evaluate(scope), this.subscript.evaluate(scope));
    }

    static reassignOwner(container, value) {
	if (valueType(value) === 'function') {
	    return value.reassignOwner(container);
	}
	return value;
    }
    
    _compute(container, subscript) {
	var subType = valueType(subscript);
	switch (subType) {
	case 'undefined': {
	    throw 'invalid subscript: undefined';
	}
	case 'string': {
	    if (valueType(container) != 'map') {
		throw 'invalid container for string subscript';
	    }
	    return Subscript.reassignOwner(container, container.get(subscript));
	}
	case 'boolean': {
	    throw 'invalid subscript: ' + subscript;
	}
	case 'number': {
	    if (valueType(container) != 'list') {
		throw 'invalid container for number subscript';
	    }
	    if (subscript < 0) {
		subscript += container.length;
	    }
	    return Subscript.reassignOwner(container, container[subscript]);
	}
	case 'list':
	case 'map': {
	    return subscript.map(function(i) { return this._compute(container, i); }, this);
	}
	case 'function': {
	    throw 'invalid subscript ' + subscript;
	}
	}
    }

    assign(scope, value) {
	var container = this.left.evaluate(scope);
	var subscript = this.right.evaluate(scope);
	_assign(scope, container, subscript, value);
    }

    _assign(scope, container, subscript, value) {
	var subType = valueType(subscript);
	switch (subType) {
	case 'undefined': {
	    throw 'invalid subscript undefined';
	}
	case 'string': {
	    if (valueType(container) != 'map') {
		throw 'invalid container for string subscript';
	    }
	    container.set(subscript, value);
	    break;
	}
	case 'boolean': {
	    throw 'invalid subscript ' + subscript;
	}
	case 'number': {
	    if (valueType(container) != 'list') {
		throw 'invalid container for number subscript';
	    }
	    if (subscript < 0) {
		subscript += container.length;
	    }
	    container[subscript] = value;
	    break;
	}
	case 'list':
	case 'map': {
	    var containerValue = convert(scope, value, 'subType');
	    for (var entry of subscript) {
		var key = entry[0];
		this._assign(scope, container, entry[1], listValue[key]);
	    }
	    break;
	}
	case 'function': {
	    throw 'invalid subscript ' + subscript;
	}
	}
    }

    _toStringNoParen(stringer) {
	stringer.expression(this.container, Precedence.SUBSCRIPT)
	    .lbracket()
	    .expression(this.subscript, Precedence.SEQUENCE)
	    .rbracket();
    }
}

class Output extends Expression {
    constructor(location, name, expression, type, selector, recordLogger) {
	super(location, Precedence.OUTPUT, 'undefined');
	this.name = name;
	this.expression = expression;
	this.type = type;
	this.selector = selector;
	this.recordLogger = recordLogger;
    }
    
    nativeEvaluator(scope) {
	this.selector.call(this, scope);
    }
    
    _toStringNoParen(stringer) {
	stringer.keyword(this.selector.symbol).space()
	    .expression(this.expression, Precedence.ASSIGN).space()
	    .keyword('as').space()
	    .keyword(this.type).space()
	    .string(this.name);
    }
}
Output.ROLL = function(scope) { this.recordLogger.recordRoll(this.name, this.expression, this.type, scope); }
Output.ROLL.symbol = 'roll';
Output.SAMPLE = function(scope) { this.recordLogger.recordSample(this.name, this.expression, this.type, scope); }
Output.SAMPLE.symbol = 'sample';

class Import extends Expression {
    constructor(location, address, name) {
	super(location, Precedence.IMPORT, 'undefined');
	this.address = address;
	this.name = name;
    }

    nativeEvaluator(scope) {
	var ns = this._getNamespace(scope);
	var mod = this._resolveImport();
	for (var e of mod) {
	    ns.set(e[0], e[1]); // XXX overwrite previous value
	}
    }

    _getNamespace(scope) {
	if (this.name === undefined) {
	    return scope.variables;
	}
	var vars = scope._lookup(this.name);
	var result = vars.get(this.name);
	if (valueType(result) != 'map') {
	    result = new YadrolMap();
	    vars.set(this.name, result); // XXX overwrite previous value
	}
	return result;
    }

    _resolveImport() {
	if (Import.CACHE.has(this.address)) {
	    return Import.CACHE.get(this.address);
	}
	var input = this._retrieveInput();
	var expressions = yadrolParser.parseExpressions(this.address, input);
	var scope = new Scope();
	for (var e of expressions) {
	    e.evaluate(scope);
	}
	Import.CACHE.set(this.address, scope.variables);
	return scope.variables;
    }

    _retrieveInput() {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.open('GET', this.address, false); // false for synchronous request
	xmlHttp.send(null);
	return xmlHttp.responseText;
    }
}
Import.CACHE = new YadrolMap();

class Variable extends Expression {
    constructor(location, name) {
	super(location, Precedence.ATOM, 'native');
	this.name = name;
    }

    nativeEvaluator(scope) {
	return scope.get(this.name);
    }

    assign(scope, value) {
	scope.set(this.name, value);
    }

    _toStringNoParen(stringer) {
	if (stringer.scope !== undefined) {
	    if (stringer.scope.has(this.name)) {
		var value = stringer.scope.get(this.name);
		if (valueType(value) != 'function') {
		    var valueExpr = _convertToExpression(stringer.scope, value);
		    stringer.expression(valueExpr, Precedence.ATOM);
		    return;
		}
	    }
	}
	stringer.identifier(this.name);
    }

    requiresSpaceAsDiceType() {
	var first = this.name[0];
	return (first != first.toUpperCase());
    }

    requiresSpaceAsDiceNumber() {
	var last = this.name[this.name.length - 1];
	return (last != last.toUpperCase());
    }
}

