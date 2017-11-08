var globalScope = new Scope();

var test = function(name, fun, validate) {
    var result = {
	'name': name,
	'validate': validate
    };
    var validateFun;
    if ((typeof validate) === 'function') {
	validateFun = validate;
    }
    else {
	validateFun = function(value) { return compare(value, validate) == 0; }
    }
    try {
	result.result = fun(globalScope);
	if (validateFun(result.result)) {
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

var test_conversion = function(value, type, expect) {
    return test('convert: ' + valueString(globalScope, value) + ' -> ' + type, function(scope) { return convert(scope, value, type); }, expect);
}

var validate_range = function(min, max) {
    return function(value) {
	return (value >= min) && (value <= max);
    }
}

var test_evaluation = function(expression, type, validate) {
    return test('evaluate: ' + expression + ' -> ' + type, function(scope) { return expression.evaluate(scope, type); }, validate);
}

var test_row = function(result) {
    var html = '<tr><th class="test">'+result.name+'</th><td class="test test-'+result.status+'">'+result.status+'</td><td class="test yadrol-value">'+valueString(globalScope, result.result)+'</td><td class="test yadrol-value">'+valueString(globalScope, result.validate)+'</td>';
    if (result.error) {
	html += '<td class="test test-'+result.status+'">'+result.error+'</td></tr>';
    }
    else {
	html += '<td class="test"></td></tr>';
    }
    $('tbody').append(html);
}

var test_header_row = function(title) {
    $('tbody').append('<tr><th colspan="5" class="test-header">'+title+'</th></tr>');
}

$(document).ready(function() {
    test_header_row('Convert undefined');
    test_row(test_conversion(undefined, 'undefined', undefined));
    test_row(test_conversion(undefined, 'string', ''));
    test_row(test_conversion(undefined, 'boolean', false));
    test_row(test_conversion(undefined, 'number', 0));
    test_row(test_conversion(undefined, 'list', []));
    test_row(test_conversion(undefined, 'map', new YadrolMap()));

    test_header_row('Convert string');
    test_row(test_conversion('foo', 'undefined', undefined));
    test_row(test_conversion('foo', 'string', 'foo'));
    test_row(test_conversion('', 'boolean', false));
    test_row(test_conversion('foo', 'boolean', true));
    test_row(test_conversion('', 'number', 0));
    test_row(test_conversion('4', 'number', 4));
    test_row(test_conversion('foo', 'number', 0));
    test_row(test_conversion('foo', 'list', ['foo']));
    test_row(test_conversion('foo', 'map', new YadrolMap([['_', 'foo']])));

    test_header_row('Convert boolean');
    test_row(test_conversion(false, 'undefined', undefined));
    test_row(test_conversion(true, 'undefined', undefined));
    test_row(test_conversion(false, 'string', ''));
    test_row(test_conversion(true, 'string', 'true'));
    test_row(test_conversion(false, 'boolean', false));
    test_row(test_conversion(true, 'boolean', true));
    test_row(test_conversion(false, 'number', 0));
    test_row(test_conversion(true, 'number', 1));
    test_row(test_conversion(false, 'list', [false]));
    test_row(test_conversion(true, 'list', [true]));
    test_row(test_conversion(false, 'map', new YadrolMap([['_', false]])));
    test_row(test_conversion(true, 'map', new YadrolMap([['_', true]])));

    test_header_row('Convert number');
    test_row(test_conversion(0, 'undefined', undefined));
    test_row(test_conversion(0, 'string', '0'));
    test_row(test_conversion(17, 'string', '17'));
    test_row(test_conversion(0, 'boolean', false));
    test_row(test_conversion(17, 'boolean', true));
    test_row(test_conversion(0, 'number', 0));
    test_row(test_conversion(17, 'number', 17));
    test_row(test_conversion(0, 'list', [0]));
    test_row(test_conversion(17, 'list', [17]));
    test_row(test_conversion(0, 'map', new YadrolMap([['_', 0]])));
    test_row(test_conversion(17, 'map', new YadrolMap([['_', 17]])));

    test_header_row('Convert list');
    test_row(test_conversion([], 'undefined', undefined));
    test_row(test_conversion([42, 'foo'], 'undefined', undefined));
    test_row(test_conversion([], 'string', ''));
    test_row(test_conversion([42, 'foo'], 'string', '42foo'));
    test_row(test_conversion([], 'boolean', false));
    test_row(test_conversion([false], 'boolean', true));
    test_row(test_conversion([42, 'foo'], 'boolean', true));
    test_row(test_conversion([], 'number', 0));
    test_row(test_conversion([0], 'number', 0));
    test_row(test_conversion([42, 'foo'], 'number', 42));
    test_row(test_conversion([1, 2, 3], 'number', 6));
    test_row(test_conversion([], 'list', []));
    test_row(test_conversion([42, 'foo'], 'list', [42,'foo']));
    test_row(test_conversion([], 'map', new YadrolMap()));
    test_row(test_conversion([42, 'foo'], 'map', new YadrolMap([['0', 42], ['1', 'foo']])));

    test_header_row('Convert map');
    test_row(test_conversion(new YadrolMap(), 'undefined', undefined));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'undefined', undefined));
    test_row(test_conversion(new YadrolMap(), 'string', ''));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'string', '42'));
    test_row(test_conversion(new YadrolMap(), 'boolean', false));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'boolean', true));
    test_row(test_conversion(new YadrolMap(), 'number', 0));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'number', 42));
    test_row(test_conversion(new YadrolMap(), 'list', []));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'list', [42]));
    test_row(test_conversion(new YadrolMap(), 'map', new YadrolMap()));
    test_row(test_conversion(new YadrolMap([['foo', 42]]), 'map', new YadrolMap([['foo', 42]])));

    /*
    test_header_row('Lexer');
    test_row(test_lexer(' \t\r\n#le comment', []));
    test_row(test_lexer('d6', ['DICE', 'NUMBER']));
    test_row(test_lexer('dX', ['DICE', 'IDENTIFIER']));
    test_row(test_lexer('dx', ['IDENTIFIER']));
    test_row(test_lexer('2d6', ['NUMBER', 'DICE', 'NUMBER']));
    test_row(test_lexer('2dX', ['NUMBER', 'DICE', 'IDENTIFIER']));
    test_row(test_lexer('2dx', ['NUMBER', 'IDENTIFIER']));
    test_row(test_lexer('Yd6', ['UPPER_DICE', 'NUMBER']));
    test_row(test_lexer('YdX', ['UPPER_DICE_UPPER']));
    test_row(test_lexer('Ydx', ['IDENTIFIER']));
    test_row(test_lexer('yd6', ['IDENTIFIER']));
    test_row(test_lexer('ydX', ['IDENTIFIER']));
    test_row(test_lexer('ydx', ['IDENTIFIER']));
    test_row(test_lexer('--', ['PLUS', 'PLUS']));
    test_row(test_lexer('---', ['BREAK']));
    test_row(test_lexer('-------', ['BREAK']));
    test_row(test_lexer(';', ['SEMICOLON']));
    test_row(test_lexer('import', ['IMPORT']));
    test_row(test_lexer('importo', ['IDENTIFIER']));
    test_row(test_lexer('Import', ['IDENTIFIER']));
    test_row(test_lexer('from', ['FROM']));
    test_row(test_lexer('roll sample', ['OUTPUT', 'OUTPUT']));
    test_row(test_lexer('rollsample', ['IDENTIFIER']));
    test_row(test_lexer('if', ['IF']));
    test_row(test_lexer('then', ['THEN']));
    test_row(test_lexer('else', ['ELSE']));
    test_row(test_lexer('for', ['FOR']));
    test_row(test_lexer('in', ['IN']));
    test_row(test_lexer('being', ['IDENTIFIER']));
    test_row(test_lexer('repeat', ['REPEAT']));
    test_row(test_lexer('while', ['WHILE']));
    test_row(test_lexer('limit', ['LIMIT']));
    test_row(test_lexer('<<', ['APPEND']));
    test_row(test_lexer('..', ['RANGE']));
    test_row(test_lexer('count', ['COUNT']));
    test_row(test_lexer('string', ['CONVERT']));
    test_row(test_lexer('boolean', ['CONVERT']));
    test_row(test_lexer('number list map', ['CONVERT', 'CONVERT', 'CONVERT']));
    test_row(test_lexer('sorted', ['REORDER']));
    test_row(test_lexer('reversed shuffled', ['REORDER', 'REORDER']));
    test_row(test_lexer('or', ['OR']));
    test_row(test_lexer('and', ['AND']));
    test_row(test_lexer('not', ['NOT']));
    test_row(test_lexer('===', ['GEN_COMP']));
    test_row(test_lexer('!==', ['GEN_COMP']));
    test_row(test_lexer('==', ['NUM_COMP']));
    test_row(test_lexer('!=', ['NUM_COMP']));
    test_row(test_lexer('<', ['NUM_COMP']));
    test_row(test_lexer('>', ['NUM_COMP']));
    test_row(test_lexer('<=', ['NUM_COMP']));
    test_row(test_lexer('>=', ['NUM_COMP']));
    test_row(test_lexer('highest', ['BEST']));
    test_row(test_lexer('lowest first last highestlast', ['BEST', 'BEST', 'BEST', 'IDENTIFIER']));
    test_row(test_lexer('of', ['OF']));
    test_row(test_lexer('draw', ['DRAW']));
    test_row(test_lexer('(', ['LPAREN']));
    test_row(test_lexer(')', ['RPAREN']));
    test_row(test_lexer('[', ['LBRACKET']));
    test_row(test_lexer(']', ['RBRACKET']));
    test_row(test_lexer(',', ['COMMA']));
    test_row(test_lexer(':', ['COLON']));
    test_row(test_lexer('.', ['DOT']));
    test_row(test_lexer('undef', ['UNDEF']));
    test_row(test_lexer('local', ['SCOPE']));
    test_row(test_lexer('outer global', ['SCOPE', 'SCOPE']));
    test_row(test_lexer('false', ['BOOLEAN']));
    test_row(test_lexer('true', ['BOOLEAN']));
    test_row(test_lexer('fun', ['FUN']));
    test_row(test_lexer('000', ['NUMBER']));
    test_row(test_lexer('1234', ['NUMBER']));
    test_row(test_lexer('{', ['LCURLY']));
    test_row(test_lexer('}', ['RCURLY']));
    test_row(test_lexer('123abc123', ['NUMBER', 'IDENTIFIER']));
    test_row(test_lexer('""', ['STRING']));
    test_row(test_lexer('"cool"', ['STRING']));
    test_row(test_lexer('"cool beer"', ['STRING']));
    test_row(test_lexer('"cool\\nbeer"', ['STRING']));
    test_row(test_lexer('"cool and \\"cool\\""', ['STRING']));
    test_row(test_lexer('1+2', ['NUMBER', 'PLUS', 'NUMBER']));
    test_row(test_lexer('2d6+1', ['NUMBER', 'DICE', 'NUMBER', 'PLUS', 'NUMBER']));
    test_row(test_lexer('1*2==foo', ['NUMBER', 'MULT', 'NUMBER', 'NUM_COMP', 'IDENTIFIER']));
    */
    
    test_header_row('Evaluation');
    test_row(test_evaluation(new Constant(null, undefined), 'native', undefined));
    test_row(test_evaluation(new Constant(null, 0), 'native', 0));
    test_row(test_evaluation(new ContainerConstructor(null, [new Constant(null, 1), new Constant(null, 2), new Constant(null, 3)], 'list'), 'native', [1, 2, 3]));
    test_row(test_evaluation(new ContainerConstructor(null, [new Constant(null, 1), new Constant(null, 2), new Constant(null, 3)], 'list'), 'number', 6));
    test_row(test_evaluation(new ContainerConstructor(null, new YadrolMap([['foo', new Constant(null, 1)], ['bar', new Constant(null, 2)], ['baz', new Constant(null, 3)]]), 'map'), 'native', new YadrolMap([['foo', 1], ['bar', 2], ['baz', 3]])));
    test_row(test_evaluation(new ContainerConstructor(null, new YadrolMap([['foo', new Constant(null, 1)], ['bar', new Constant(null, 2)], ['baz', new Constant(null, 3)]]), 'map'), 'number', 6));
});
