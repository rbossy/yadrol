'use strict';

class Scope {
    constructor(parent, variables) {
	this.parent = parent;
	this.depth = parent == null ? 0 : parent.depth + 1;
	this.variables = variables || new YadrolMap();
    }

    _lookup(name) {
	for (var scope = this; scope != null; scope = scope.parent) {
	    if (scope.variables.has(name)) {
		return scope.variables;
	    }
	}
	return this.variables;
    }

    get(name) {
	var vars = this._lookup(name);
	return vars.get(name);
    }

    set(name, value) {
	var vars = this._lookup(name);
	vars.set(name, value);
    }
}

class YadrolFunction {
    constructor(parentScope, args, body, owner) {
	this.parentScope = parentScope;
	this.args = args;
	this.body = body;
	this.owner = owner;
    }

    call(posArgs, namedArgs, type) {
	var scope = this._createScope(posArgs, namedArgs);
	return this.body.evaluate(scope, type);
    }

    reassignOwner(owner) {
	return new YadrolFunction(this.parentScope, this.args, this.body, owner);
    }

    _createScope(posArgs, namedArgs) {
	var variables = new YadrolMap();
	this._applyPositionalArgs(variables, posArgs);
	this._applyNamedArgs(variables, namedArgs);
	this._setDefaults(variables);
	return new Scope(this.parentScope, variables);
    }

    _setDefaults(variables) {
	for (var entry of this.args) {
	    var name = entry[0];
	    if (!variables.has(name)) {
		variables.set(name, entry[1]);
	    }
	}
    }

    _applyPositionalArgs(variables, posArgs) {
	var posArgsIt = posArgs.entries();
	for (var arg of this.args) {
	    var e = posArgsIt.next();
	    if (e.done) {
		break;
	    }
	    variables.set(arg[0], e.value[1]);
	}
	if (!posArgsIt.next().done) {
	    throw 'extra argument in call';
	}
    }

    _applyNamedArgs(variables, namedArgs) {
	for (var entry of namedArgs) {
	    var name = entry[0];
	    if (variables.has(name)) {
		throw 'call already set argument ' + name;
	    }
	    if (!this.args.has(name)) {
		throw 'no argument named ' + name;
	    }
	}
    }

}

class YadrolMap extends Map {
    constructor(iterable) {
	super(iterable);
    }

    map(callback, thisArg) {
	var result = new YadrolMap();
	for (var entry of this) {
	    result.set(entry[0], callback.call(thisArg, entry[1], entry[0], this));
	}
	return result;
    }
}

var TYPES = [
    'undefined',
    'boolean',
    'number',
    'string',
    'list',
    'map',
    'function'
]

var valueType = function(value) {
    if (value === undefined) {
	return 'undefined';
    }
    var t = typeof value;
    switch (t) {
    case 'boolean':
    case 'string':
    case 'number':
	return t;
    case 'object':
	if (value instanceof Array)
	    return 'list';
	if (value instanceof YadrolMap)
	    return 'map';
	if (value instanceof YadrolFunction)
	    return 'function';
    }
    console.log(value);
    throw new Error('unhandled data type: ' + value);
}

var _typeOrder = {
    'undefined' : 0,
    'string'    : 1,
    'boolean'   : 2,
    'number'    : 3,
    'list'      : 4,
    'map'       : 5,
    'function'  : 6
}

var _compareTypes = function(a, b) {
    var aOrd = _typeOrder[a];
    var bOrd = _typeOrder[b];
    if (aOrd < bOrd) {
	return -1;
    }
    if (aOrd > bOrd) {
	return 1;
    }
    return 0;
}

var _compareDefault = function(a, b) {
    if (a < b) {
	return -1;
    }
    if (a > b) {
	return 1;
    }
    return 0;
}

var _compareIterables = function(a, b) {
    var aIt = a.entries();
    var bIt = b.entries();
    while(true) {
	var aValue = aIt.next();
	var bValue = bIt.next();
	if (aValue.done) {
	    if (bValue.done) {
		return 0;
	    }
	    return -1;
	}
	if (bValue.done) {
	    return 1;
	}
	var c = compare(aValue.value[1], bValue.value[1]);
	if (c != 0) {
	    return c;
	}
    }
}

var compare = function(a, b) {
    var aType = valueType(a);
    var bType = valueType(b);
    var typeComp = _compareTypes(aType, bType);
    if (typeComp != 0) {
	return typeComp;
    }
    switch (aType) {
    case 'undefined': return 0;
    case 'string':
    case 'boolean':
    case 'number':
    case 'function':
	return _compareDefault(a, b);
    case 'list':
    case 'map':
	return _compareIterables(a, b);
    }
}



var _convertToUndefined = function(scope, _value) {
    return undefined;
}

var _convertToSingleton = function(scope, value) {
    return [value];
}

var _convertToSingletonMap = function(scope, value) {
    return new YadrolMap([['_', value]]);
}

var _convertToSelf = function(scope, value) {
    return value;
}

var _convertToConstant = function(scope, value) {
    return new Constant(Location.NONE, value);
}

var _convertToContainerConstructor = function(scope, value) {
    return new ContainerConstructor(Location.NONE, value.map(function(v) { return _convertToExpression(scope, v); }), valueType(value));
}

var EXPRESSION_CONVERTER = {
    'undefined': _convertToConstant,
    'string': _convertToConstant,
    'boolean': _convertToConstant,
    'number': _convertToConstant,
    'list': _convertToContainerConstructor,
    'map': _convertToContainerConstructor,
    'function': _convertToSelf
}

var _convertToExpression = function(scope, value) {
    return EXPRESSION_CONVERTER[valueType(value)](scope, value);
}

var _convertToFunction = function(type) {
    return function(scope, value) {
	return new YadrolFunction(scope, [], new YadrolMap(), EXPRESSION_CONVERTER[type](scope, value));
    };
}

var _sumContainer = function(start, type) {
    return function(scope, value) {
	var result = start;
	for (var x of value.entries()) {
	    result += convert(scope, x[1], type);
	}
	return result;
    }
}

var CONVERTER = {
    'undefined': {
	'undefined': _convertToUndefined,
	'string': function(scope, value) { return ''; },
	'boolean': function(scope, value) { return false; },
	'number': function(scope, value) { return 0; },
	'list': function(scope, value) { return []; },
	'map': function(scope, value) { return new YadrolMap(); },
	'function': _convertToFunction('undefined')
    },
    'string': {
	'undefined': _convertToUndefined,
	'string': _convertToSelf,
	'boolean': function(scope, value) { return value != ''; },
	'number': function(scope, value) { var r = Number(value); if (isNaN(r)) return 0; return r; },
	'list': _convertToSingleton,
	'map': _convertToSingletonMap,
	'function': _convertToFunction('string')
    },
    'boolean': {
	'undefined': _convertToUndefined,
	'string': function(scope, value) { if (value) return 'true'; return ''; },
	'boolean': _convertToSelf,
	'number': function(scope, value) { if (value) return 1; return 0; },
	'list': _convertToSingleton,
	'map': _convertToSingletonMap,
	'function': _convertToFunction('boolean')
    },
    'number': {
	'undefined': _convertToUndefined,
	'string': function(scope, value) { return String(value); },
	'boolean': function(scope, value) { return value != 0; },
	'number': _convertToSelf,
	'list': _convertToSingleton,
	'map': _convertToSingletonMap,
	'function': _convertToFunction('number')
    },
    'list': {
	'undefined': _convertToUndefined,
	'string': _sumContainer('', 'string'),
	'boolean': function(scope, value) { return value.length != 0; },
	'number': _sumContainer(0, 'number'),
	'list': _convertToSelf,
	'map': function(scope, value) { return new YadrolMap(value.map(function(x, i) { return [String(i), x]; })); },
	'function': _convertToFunction('list')
    },
    'map': {
	'undefined': _convertToUndefined,
	'string': _sumContainer('', 'string'),
	'boolean': function(scope, value) { return value.size != 0; },
	'number': _sumContainer(0, 'number'),
	'list': function(scope, value) { var result = []; for (var x of value.values()) { result.push(x); } return result; },
	'map': _convertToSelf,
	'function': _convertToFunction('map')
    },
    'function': {
	'undefined': function(scope, value) { value.call([], new YadrolMap(), 'undefined'); },
	'string': function(scope, value) { value.call([], new YadrolMap(), 'string'); },
	'boolean': function(scope, value) { value.call([], new YadrolMap(), 'boolean'); },
	'number': function(scope, value) { value.call([], new YadrolMap(), 'number'); },
	'list': function(scope, value) { value.call([], new YadrolMap(), 'list'); },
	'map': function(scope, value) { value.call([], new YadrolMap(), 'map'); },
	'function': _convertToSelf
    }
}

var convert = function(scope, value, type) {
    return CONVERTER[valueType(value)][type](scope, value);
}

var valueString = function(scope, value) {
    var expr = _convertToExpression(scope, value);
    var result = expr.toString();
    return result;
}
