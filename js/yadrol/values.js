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

	has(name) {
		for (var scope = this; scope != null; scope = scope.parent) {
			if (scope.variables.has(name)) {
				return true;
			}
		}
		return false;
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
		variables.set(YadrolFunction.OWNER_VARIABLE, this.owner);
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
YadrolFunction.OWNER_VARIABLE = 'this';

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

class ValueComparator {
	static _compareTypes(a, b) {
		var aOrd = ValueComparator._typeOrder[a];
		var bOrd = ValueComparator._typeOrder[b];
		if (aOrd < bOrd) {
			return -1;
		}
		if (aOrd > bOrd) {
			return 1;
		}
		return 0;
	}

	static _compareDefault(a, b) {
		if (a < b) {
			return -1;
		}
		if (a > b) {
			return 1;
		}
		return 0;
	}

	static _compareIterables(a, b) {
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
			var c = ValueComparator.compare(aValue.value[1], bValue.value[1]);
			if (c != 0) {
				return c;
			}
		}
	}

	static compare(a, b) {
		var aType = valueType(a);
		var bType = valueType(b);
		var typeComp = ValueComparator._compareTypes(aType, bType);
		if (typeComp != 0) {
			return typeComp;
		}
		switch (aType) {
			case 'undefined': return 0;
			case 'string':
			case 'boolean':
			case 'number':
			case 'function':
			return ValueComparator._compareDefault(a, b);
			case 'list':
			case 'map':
			return ValueComparator._compareIterables(a, b);
		}
	}

	static equal(a, b) {
		return ValueComparator.compare(a, b) === 0;
	}
}
ValueComparator._typeOrder = {
	'undefined' : 0,
	'string'    : 1,
	'boolean'   : 2,
	'number'    : 3,
	'list'      : 4,
	'map'       : 5,
	'function'  : 6
}

class ValueConverter {
	static _convertToUndefined(scope, _value) {
		return undefined;
	}

	static _convertToSingleton(scope, value) {
		return [value];
	}

	static _convertToSingletonMap(scope, value) {
		return new YadrolMap([['_', value]]);
	}

	static _convertToSelf(scope, value) {
		return value;
	}

	static _convertToConstant(scope, value) {
		return new Constant(Location.NONE, value);
	}

	static _convertToContainerConstructor(scope, value) {
		return new ContainerConstructor(Location.NONE, value.map(function(v) { return ValueConverter._convertToExpression(scope, v); }), valueType(value));
	}

	static _convertToExpression(scope, value) {
		return ValueConverter.expressionConverter[valueType(value)](scope, value);
	}

	static _toFunctionConverter(type) {
		return function(scope, value) {
			return new YadrolFunction(scope, [], new YadrolMap(), ValueConverter.expressionConverter[type](scope, value));
		};
	}

	static _sumContainer(start, type) {
		return function(scope, value) {
			var result = start;
			for (var x of value.entries()) {
				result += ValueConverter.convert(scope, x[1], type);
			}
			return result;
		}
	}

	static convert(scope, value, type) {
		return ValueConverter.CONVERTER[valueType(value)][type](scope, value);
	}

	static valueString(scope, value) {
		var expr = ValueConverter._convertToExpression(scope, value);
		var result = expr.toString();
		return result;
	}
}
ValueConverter.expressionConverter = {
	'undefined': ValueConverter._convertToConstant,
	'string': ValueConverter._convertToConstant,
	'boolean': ValueConverter._convertToConstant,
	'number': ValueConverter._convertToConstant,
	'list': ValueConverter._convertToContainerConstructor,
	'map': ValueConverter._convertToContainerConstructor,
	'function': ValueConverter._convertToSelf
}
ValueConverter.CONVERTER = {
	'undefined': {
		'undefined': ValueConverter._convertToUndefined,
		'string': function(scope, value) { return ''; },
		'boolean': function(scope, value) { return false; },
		'number': function(scope, value) { return 0; },
		'list': function(scope, value) { return []; },
		'map': function(scope, value) { return new YadrolMap(); },
		'function': ValueConverter._toFunctionConverter('undefined')
	},
	'string': {
		'undefined': ValueConverter._convertToUndefined,
		'string': ValueConverter._convertToSelf,
		'boolean': function(scope, value) { return value != ''; },
		'number': function(scope, value) { var r = Number(value); if (isNaN(r)) return 0; return r; },
		'list': ValueConverter._convertToSingleton,
		'map': ValueConverter._convertToSingletonMap,
		'function': ValueConverter._toFunctionConverter('string')
	},
	'boolean': {
		'undefined': ValueConverter._convertToUndefined,
		'string': function(scope, value) { if (value) return 'true'; return ''; },
		'boolean': ValueConverter._convertToSelf,
		'number': function(scope, value) { if (value) return 1; return 0; },
		'list': ValueConverter._convertToSingleton,
		'map': ValueConverter._convertToSingletonMap,
		'function': ValueConverter._toFunctionConverter('boolean')
	},
	'number': {
		'undefined': ValueConverter._convertToUndefined,
		'string': function(scope, value) { return String(value); },
		'boolean': function(scope, value) { return value != 0; },
		'number': ValueConverter._convertToSelf,
		'list': ValueConverter._convertToSingleton,
		'map': ValueConverter._convertToSingletonMap,
		'function': ValueConverter._toFunctionConverter('number')
	},
	'list': {
		'undefined': ValueConverter._convertToUndefined,
		'string': ValueConverter._sumContainer('', 'string'),
		'boolean': function(scope, value) { return value.length != 0; },
		'number': ValueConverter._sumContainer(0, 'number'),
		'list': ValueConverter._convertToSelf,
		'map': function(scope, value) { return new YadrolMap(value.map(function(x, i) { return ['_' + String(i), x]; })); },
		'function': ValueConverter._toFunctionConverter('list')
	},
	'map': {
		'undefined': ValueConverter._convertToUndefined,
		'string': ValueConverter._sumContainer('', 'string'),
		'boolean': function(scope, value) { return value.size != 0; },
		'number': ValueConverter._sumContainer(0, 'number'),
		'list': function(scope, value) { var result = []; for (var x of value.values()) { result.push(x); } return result; },
		'map': ValueConverter._convertToSelf,
		'function': ValueConverter._toFunctionConverter('map')
	},
	'function': {
		'undefined': function(scope, value) { value.call([], new YadrolMap(), 'undefined'); },
		'string': function(scope, value) { value.call([], new YadrolMap(), 'string'); },
		'boolean': function(scope, value) { value.call([], new YadrolMap(), 'boolean'); },
		'number': function(scope, value) { value.call([], new YadrolMap(), 'number'); },
		'list': function(scope, value) { value.call([], new YadrolMap(), 'list'); },
		'map': function(scope, value) { value.call([], new YadrolMap(), 'map'); },
		'function': ValueConverter._convertToSelf
	}
}
