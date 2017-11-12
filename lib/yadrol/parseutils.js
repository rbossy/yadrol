parser.yy.extractPositionalArgs = function(callArgs) {
    var posArgs = [];
    while (callArgs.length > 0) {
	var a = callArgs.shift();
	if (a.length == 2) {
            callArgs.unshift(a);
	    break;
	}
	posArgs.push(a[0]);
    }
    for (var a of callArgs) {
	if (a.length == 1) {
            throw new Error('missing named argument name');
	}
    }
    return posArgs;
}

parser.yy.getArithmeticOperator = function(op) {
    switch (op) {
    case '+': return Arithmetic.PLUS;
    case '-': return Arithmetic.MINUS;
    case '*': return Arithmetic.MULT;
    case '/': return Arithmetic.DIV;
    case '%': return Arithmetic.MOD;
    }
}

parser.yy.getNumberComparisonOperator = function(op) {
    switch (op) {
    case '==': return NumberComparison.EQ;
    case '!=': return NumberComparison.NE;
    case '<': return NumberComparison.LT;
    case '>': return NumberComparison.GT;
    case '<=': return NumberComparison.LE;
    case '>=': return NumberComparison.GE;
    }
}

parser.yy.getGeneralComparisonOperator = function(op) {
    switch (op) {
    case '===': return GeneralComparison.EQ;
    case '!==': return GeneralComparison.NE;
    }
}

parser.yy.getSignOperator = function(op) {
    switch (op) {
    case '+': return Sign.PLUS;
    case '-': return Sign.MINUS;
    }
}

var parseExpressions = function(sourceFile, input, recordLogger) {
    parser.yy.sourceFile = sourceFile;
    if (recordLogger === undefined) {
	throw new Error('missing record logger');
    }
    parser.yy.recordLogger = recordLogger;
    return parser.parse(input);
}
