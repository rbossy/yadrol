'use strict';

var globalScope = new Scope();

class Validate {
    constructor() {
    }

    check(value) {
	throw new Error('not implemented: check()');
    }

    display() {
	throw new Error('not implemented: display()');
    }
}

class ValueEqual extends Validate {
    constructor(value) {
	super();
	this.value = value;
    }

    check(value) {
	return compare(value, this.value) == 0;
    }

    display() {
	return valueString(new Scope(), this.value);
    }
}

class Test {
    constructor(name, validate) {
	this.name = name;
	this.validate = validate;
    }

    getResult() {
	throw new Error('not implemented: getResult()');
    }

    run() {
	var result = {
	    'name': this.name,
	    'validate': this.validate
	};
	try {
	    result.result = this.getResult();
	    if (this.validate.check(result.result)) {
		result.status = 'success';
	    }
	    else {
		result.status = 'fail';
	    }
	}
	catch (err) {
	    result.status = 'error';
	    result.error = err;
	    console.log(err);
	}
	return result;
    }

    row(id) {
	var result = this.run();
	var rcol;
	if (result.error) {
	    rcol = result.error;
	}
	else {
	    rcol = valueString(globalScope, result.result);
	}
	var html = '<tr><th class="test">'+result.name+'</th><td class="test test-'+result.status+'">'+result.status+'</td><td class="test yadrol-value">'+rcol+'</td><td class="test yadrol-value">'+this.validate.display()+'</td>';
	$('#'+id).append(html);
	return result;
    }
}

class TestConversion extends Test {
    constructor(value, type, expect) {
	super('convert: ' + valueString(new Scope(), value) + ' -> ' + type, new ValueEqual(expect));
	this.value = value;
	this.type = type;
    }

    getResult() {
	return convert(new Scope(), this.value, this.type);
    }
}

class TestLexer extends Test {
    constructor(input, tokens) {
	super('lex: ' + input, new ValueEqual(tokens));
	this.input = input;
    }

    getResult() {
	var result = [];
	parser.lexer.setInput(this.input);
	while (true) {
	    var tokId = parser.lexer.next();
	    if (tokId === false) {
		continue;
	    }
	    var tokType = parser.terminals_[tokId];
	    if ((tokType == 'EOF') || (tokType === undefined)) {
		break;
	    }
	    result.push(tokType);
	}
	return result;
    }
}

class TestEvaluation extends Test {
    constructor(expression, type, value) {
	super('evaluate: ' + expression + ' -> ' + type, new ValueEqual(value));
	this.expression = expression;
	this.type = type;
    }

    getResult() {
	return this.expression.evaluate(new Scope(), this.type);
    }
}

class TestParse extends Test {
    constructor(name, validate, input) {
	super(name, validate);
	this.input = input;
    }

    parse() {
	return parseExpressions('test.js', this.input, new RecordLogger(10000));
    }
}

class TestParseEvaluate extends TestParse {
    constructor(input, type, value) {
	super('parse+evaluate ' + type + ': ' + input, new ValueEqual(value), input);
	this.type = type;
    }

    getResult() {
	var exprs = this.parse();
	return exprs[0].evaluate(new Scope(), this.type);
    }
}

class TestParseAndString extends TestParse {
    constructor(input) {
	super('parse+string: ' + input, new ValueEqual(input), input);
    }

    getResult() {
	var exprs = this.parse();
	return exprs[0].toString();
    }
}

class TestGroup {
    constructor(name, tests) {
	this.name = name;
	this.id = name.replace(/ /g, '-').toLowerCase();
	this.tests = tests;
    }

    table() {
	this.headerRow();
	var summary = new Map([['success', 0], ['fail', 0], ['error', 0]]);
	for (var t of this.tests) {
	    var r = t.row(this.id);
	    summary.set(r.status, summary.get(r.status) + 1);
	}
	this.footerRow(summary);
    }

    headerRow() {
	$('#test-content').append('<table><thead><tr class="test-header" onclick="toggleDisplay(\''+this.id+'\')"><th width="360px">'+this.name+'</th><th width="120px" id="'+this.id+'-status">Status</th><th width="360px">Result</th><th width="360px">Expected</th></tr></thead><tbody id="'+this.id+'" style="display: none"></tbody></table>');
    }

    footerRow(summary) {
	var html = ' : ';
	for (var e of summary) {
	    if (e[1] > 0) {
		html += '<span class="test-' + e[0] + '">' + e[1] + '</span>';
	    }
	}
	$('#'+this.id+'-status').append(html);
    }
}

var toggleDisplay = function(id) {
    var current = $('#'+id).css('display');
    var next = (current == 'none') ? 'table-row-group' : 'none';
    $('#'+id).css('display', next);
}

$(document).ready(function() {
    new TestGroup('Convert undefined', [
	new TestConversion(undefined, 'undefined', undefined),
	new TestConversion(undefined, 'string', ''),
	new TestConversion(undefined, 'boolean', false),
	new TestConversion(undefined, 'number', 0),
	new TestConversion(undefined, 'list', []),
	new TestConversion(undefined, 'map', new YadrolMap())
    ]).table();

    new TestGroup('Convert string', [
	new TestConversion('foo', 'undefined', undefined),
	new TestConversion('foo', 'string', 'foo'),
	new TestConversion('', 'boolean', false),
	new TestConversion('foo', 'boolean', true),
	new TestConversion('', 'number', 0),
	new TestConversion('4', 'number', 4),
	new TestConversion('foo', 'number', 0),
	new TestConversion('foo', 'list', ['foo']),
	new TestConversion('foo', 'map', new YadrolMap([['_', 'foo']]))
    ]).table();

    new TestGroup('Convert boolean', [
	new TestConversion(false, 'undefined', undefined),
	new TestConversion(true, 'undefined', undefined),
	new TestConversion(false, 'string', ''),
	new TestConversion(true, 'string', 'true'),
	new TestConversion(false, 'boolean', false),
	new TestConversion(true, 'boolean', true),
	new TestConversion(false, 'number', 0),
	new TestConversion(true, 'number', 1),
	new TestConversion(false, 'list', [false]),
	new TestConversion(true, 'list', [true]),
	new TestConversion(false, 'map', new YadrolMap([['_', false]])),
	new TestConversion(true, 'map', new YadrolMap([['_', true]]))
    ]).table();

    new TestGroup('Convert number', [
	new TestConversion(0, 'undefined', undefined),
	new TestConversion(0, 'string', '0'),
	new TestConversion(17, 'string', '17'),
	new TestConversion(0, 'boolean', false),
	new TestConversion(17, 'boolean', true),
	new TestConversion(0, 'number', 0),
	new TestConversion(17, 'number', 17),
	new TestConversion(0, 'list', [0]),
	new TestConversion(17, 'list', [17]),
	new TestConversion(0, 'map', new YadrolMap([['_', 0]])),
	new TestConversion(17, 'map', new YadrolMap([['_', 17]]))
    ]).table();

    new TestGroup('Convert list', [
	new TestConversion([], 'undefined', undefined),
	new TestConversion([42, 'foo'], 'undefined', undefined),
	new TestConversion([], 'string', ''),
	new TestConversion([42, 'foo'], 'string', '42foo'),
	new TestConversion([], 'boolean', false),
	new TestConversion([false], 'boolean', true),
	new TestConversion([42, 'foo'], 'boolean', true),
	new TestConversion([], 'number', 0),
	new TestConversion([0], 'number', 0),
	new TestConversion([42, 'foo'], 'number', 42),
	new TestConversion([1, 2, 3], 'number', 6),
	new TestConversion([], 'list', []),
	new TestConversion([42, 'foo'], 'list', [42,'foo']),
	new TestConversion([], 'map', new YadrolMap()),
	new TestConversion([42, 'foo'], 'map', new YadrolMap([['0', 42], ['1', 'foo']]))
    ]).table();

    new TestGroup('Convert map', [
	new TestConversion(new YadrolMap(), 'undefined', undefined),
	new TestConversion(new YadrolMap([['foo', 42]]), 'undefined', undefined),
	new TestConversion(new YadrolMap(), 'string', ''),
	new TestConversion(new YadrolMap([['foo', 42]]), 'string', '42'),
	new TestConversion(new YadrolMap(), 'boolean', false),
	new TestConversion(new YadrolMap([['foo', 42]]), 'boolean', true),
	new TestConversion(new YadrolMap(), 'number', 0),
	new TestConversion(new YadrolMap([['foo', 42]]), 'number', 42),
	new TestConversion(new YadrolMap(), 'list', []),
	new TestConversion(new YadrolMap([['foo', 42]]), 'list', [42]),
	new TestConversion(new YadrolMap(), 'map', new YadrolMap()),
	new TestConversion(new YadrolMap([['foo', 42]]), 'map', new YadrolMap([['foo', 42]]))
    ]).table();
    
    new TestGroup('Lexer', [
	new TestLexer(' \t\r\n#le comment', []),
	new TestLexer('d6', ['DICE_NUMBER']),
	new TestLexer('dX', ['DICE_UPPER']),
	new TestLexer('dx', ['IDENTIFIER']),
	new TestLexer('2d6', ['NUMBER', 'DICE_NUMBER']),
	new TestLexer('2dX', ['NUMBER', 'DICE_UPPER']),
	new TestLexer('2dx', ['NUMBER', 'IDENTIFIER']),
	new TestLexer('Yd6', ['UPPER_DICE_NUMBER']),
	new TestLexer('YdX', ['UPPER_DICE_UPPER']),
	new TestLexer('Ydx', ['IDENTIFIER']),
	new TestLexer('yd6', ['IDENTIFIER']),
	new TestLexer('ydX', ['IDENTIFIER']),
	new TestLexer('ydx', ['IDENTIFIER']),
	new TestLexer('--', ['PLUS', 'PLUS']),
	new TestLexer('---', ['BREAK']),
	new TestLexer('-------', ['BREAK']),
	new TestLexer(';', ['SEMICOLON']),
	new TestLexer('import', ['IMPORT']),
	new TestLexer('importo', ['IDENTIFIER']),
	new TestLexer('Import', ['IDENTIFIER']),
	new TestLexer('from', ['FROM']),
	new TestLexer('roll sample', ['OUTPUT', 'OUTPUT']),
	new TestLexer('rollsample', ['IDENTIFIER']),
	new TestLexer('if', ['IF']),
	new TestLexer('then', ['THEN']),
	new TestLexer('else', ['ELSE']),
	new TestLexer('for', ['FOR']),
	new TestLexer('in', ['IN']),
	new TestLexer('being', ['IDENTIFIER']),
	new TestLexer('repeat', ['REPEAT']),
	new TestLexer('while', ['WHILE']),
	new TestLexer('limit', ['LIMIT']),
	new TestLexer('<<', ['APPEND']),
	new TestLexer('..', ['RANGE']),
	new TestLexer('count', ['COUNT']),
	new TestLexer('string', ['CONVERT']),
	new TestLexer('boolean', ['CONVERT']),
	new TestLexer('number list map', ['CONVERT', 'CONVERT', 'CONVERT']),
	new TestLexer('sorted', ['REORDER']),
	new TestLexer('reversed shuffled', ['REORDER', 'REORDER']),
	new TestLexer('or', ['OR']),
	new TestLexer('and', ['AND']),
	new TestLexer('not', ['NOT']),
	new TestLexer('===', ['GEN_COMP']),
	new TestLexer('!==', ['GEN_COMP']),
	new TestLexer('==', ['NUM_COMP']),
	new TestLexer('!=', ['NUM_COMP']),
	new TestLexer('<', ['NUM_COMP']),
	new TestLexer('>', ['NUM_COMP']),
	new TestLexer('<=', ['NUM_COMP']),
	new TestLexer('>=', ['NUM_COMP']),
	new TestLexer('highest', ['BEST']),
	new TestLexer('lowest first last highestlast', ['BEST', 'BEST', 'BEST', 'IDENTIFIER']),
	new TestLexer('of', ['OF']),
	new TestLexer('draw', ['DRAW']),
	new TestLexer('(', ['LPAREN']),
	new TestLexer(')', ['RPAREN']),
	new TestLexer('[', ['LBRACKET']),
	new TestLexer(']', ['RBRACKET']),
	new TestLexer(',', ['COMMA']),
	new TestLexer(':', ['COLON']),
	new TestLexer('.', ['DOT']),
	new TestLexer('undef', ['UNDEF']),
	new TestLexer('local', ['SCOPE']),
	new TestLexer('outer global', ['SCOPE', 'SCOPE']),
	new TestLexer('false', ['BOOLEAN']),
	new TestLexer('true', ['BOOLEAN']),
	new TestLexer('fun', ['FUN']),
	new TestLexer('000', ['NUMBER']),
	new TestLexer('1234', ['NUMBER']),
	new TestLexer('{', ['LCURLY']),
	new TestLexer('}', ['RCURLY']),
	new TestLexer('123abc123', ['NUMBER', 'IDENTIFIER']),
	new TestLexer('""', ['STRING']),
	new TestLexer('"cool"', ['STRING']),
	new TestLexer('"cool beer"', ['STRING']),
	new TestLexer('"cool\\nbeer"', ['STRING']),
	new TestLexer('"cool and \\"cool\\""', ['STRING']),
	new TestLexer('1+2', ['NUMBER', 'PLUS', 'NUMBER']),
	new TestLexer('2d6+1', ['NUMBER', 'DICE_NUMBER', 'PLUS', 'NUMBER']),
	new TestLexer('1*2==foo', ['NUMBER', 'MULT', 'NUMBER', 'NUM_COMP', 'IDENTIFIER'])
    ]).table();
    
    new TestGroup('Evaluation', [
	new TestEvaluation(new Constant(Location.NONE, undefined), 'native', undefined),
	new TestEvaluation(new Constant(Location.NONE, 0), 'native', 0),
	new TestEvaluation(new ContainerConstructor(Location.NONE, [new Constant(Location.NONE, 1), new Constant(Location.NONE, 2), new Constant(Location.NONE, 3)], 'list'), 'native', [1, 2, 3]),
	new TestEvaluation(new ContainerConstructor(Location.NONE, [new Constant(Location.NONE, 1), new Constant(Location.NONE, 2), new Constant(Location.NONE, 3)], 'list'), 'number', 6),
	new TestEvaluation(new ContainerConstructor(Location.NONE, new YadrolMap([['foo', new Constant(Location.NONE, 1)], ['bar', new Constant(Location.NONE, 2)], ['baz', new Constant(Location.NONE, 3)]]), 'map'), 'native', new YadrolMap([['foo', 1], ['bar', 2], ['baz', 3]])),
	new TestEvaluation(new ContainerConstructor(Location.NONE, new YadrolMap([['foo', new Constant(Location.NONE, 1)], ['bar', new Constant(Location.NONE, 2)], ['baz', new Constant(Location.NONE, 3)]]), 'map'), 'number', 6)
    ]).table();
    
    new TestGroup('Parse and evaluation', [
	new TestParseEvaluate('undef', 'native', undefined),
	new TestParseEvaluate('""', 'native', ''),
	new TestParseEvaluate('"foo"', 'native', 'foo'),
	new TestParseEvaluate('false', 'native', false),
	new TestParseEvaluate('true', 'native', true),
	new TestParseEvaluate('0', 'native', 0),
	new TestParseEvaluate('42', 'native', 42),
	new TestParseEvaluate('[]', 'native', []),
	new TestParseEvaluate('[1, 2, 4, 8]', 'native', [1, 2, 4, 8]),
	new TestParseEvaluate('{}', 'native', new YadrolMap()),
	new TestParseEvaluate('{foo: true, bar: 42}', 'native', new YadrolMap([['foo', true], ['bar', 42]])),
	new TestParseEvaluate('42 == 0', 'native', false),
	new TestParseEvaluate('42 != 0', 'native', true),
	new TestParseEvaluate('42 > 0', 'native', true),
	new TestParseEvaluate('42 < 0', 'native', false),
	new TestParseEvaluate('42 >= 0', 'native', true),
	new TestParseEvaluate('42 >= 42', 'native', true),
	new TestParseEvaluate('42 <= 0', 'native', false),
	new TestParseEvaluate('42 <= 42', 'native', true),
	new TestParseEvaluate('7 + 35', 'native', 42),
	new TestParseEvaluate('108 - 66', 'native', 42),
	new TestParseEvaluate('6 * 7', 'native', 42),
	new TestParseEvaluate('126 / 3', 'native', 42),
	new TestParseEvaluate('343 % 43', 'native', 42),
	new TestParseEvaluate('0 .. 4', 'native', [0,1,2,3]),
	new TestParseEvaluate('0 .. 4 << 4 .. 6', 'native', [0,1,2,3,4,5]),
	new TestParseEvaluate('x = 44; x - 2', 'native', 42),
	new TestParseEvaluate('x = {foo: true, bar: 42}; x.bar', 'native', 42),
	new TestParseEvaluate('x = {foo: true, bar: 42}; x["bar"]', 'native', 42),
	new TestParseEvaluate('x = {foo: true, bar: 42}; x[["foo", "bar"]]', 'native', [true, 42]),
	new TestParseEvaluate('x = {foo: true, bar: 42}; x[{bar: "foo", foo: "bar"}]', 'native', new YadrolMap([["bar", true], ["foo", 42]])),
	new TestParseEvaluate('foo = [12, 17, 13]; foo[1]', 'native', 17),
	new TestParseEvaluate('foo = [12, 17, 13]; foo', 'number', 42),
	new TestParseEvaluate('foo = fun(x) { x * 2 }; foo(21)', 'native', 42),	
	new TestParseEvaluate('highest of [17, 1, 42, 33]', 'native', 42),	
	new TestParseEvaluate('lowest of [17, 1, 42, 33]', 'native', 1),	
	new TestParseEvaluate('first of [17, 1, 42, 33]', 'native', 17),	
	new TestParseEvaluate('last of [17, 1, 42, 33]', 'native', 33),	
	new TestParseEvaluate('highest 2 of [17, 1, 42, 33]', 'native', [42, 33]),	
	new TestParseEvaluate('lowest 2 of [17, 1, 42, 33]', 'native', [17, 1]),	
	new TestParseEvaluate('sorted [17, 1, 42, 33]', 'native', [1, 17, 33, 42]),	
	new TestParseEvaluate('sorted [17, 1, [true], 42, false, 33]', 'native', [false, 1, 17, 33, 42, [true]]),	
	new TestParseEvaluate('', 'native', [false, 1, 17, 33, 42, [true]]),	
    ]).table();

    new TestGroup('Parse and string', [
	new TestParseAndString('undef'),
	new TestParseAndString('1 + 1 * 2'),
	new TestParseAndString('(1 + 1) * 2'),
	new TestParseAndString('2d6'),
	new TestParseAndString('2dX'),
	new TestParseAndString('Yd6'),
	new TestParseAndString('YdX'),
	new TestParseAndString('Yd type'),
 	new TestParseAndString('x = {foo: true, bar: 42}; x["bar"]'),
   ]).table();
});
