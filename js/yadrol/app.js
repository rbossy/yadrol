class YadrolApp {
	constructor() {
		this.globalScope = new Scope();
		this.recordLogger = new RecordLogger(100000, 'number');
		this.defaultOutput = Output.SAMPLE;
		this.elapsed = 0;
	}

	valueString(value) {
		return ValueConverter.valueString(this.globalScope, value);
	}

	static _needsOutput(expressions) {
		for (var e of expressions) {
			if (e.hasOutput()) {
				return false;
			}
		}
		return true;
	}

	parseAndEvaluate(sourceFile, expressionString) {
		var timestamp = Date.now();
		this.recordLogger.clear();
		var expressions = yadrolParser.parseExpressions(sourceFile, expressionString, this.recordLogger);
		if (YadrolApp._needsOutput(expressions)) {
			var last = expressions[expressions.length - 1];
			expressions[expressions.length - 1] = new Output(last.location, undefined, last, this.recordLogger.defaultType, this.defaultOutput, this.recordLogger);
		}
		for (var e of expressions) {
			e.evaluate(this.globalScope);
		}
		for (var rec of this.recordLogger.outputRecords) {
			if (rec.isSampleRecord) {
				rec.result.aggregate();
			}
		}
		this.elapsed = Date.now() - timestamp;
		return this;
	}

	setSampleSize(sampleSize) {
		this.recordLogger.sampleSize = sampleSize;
	}

	setDefaultType(defaultType) {
		this.recordLogger.defaultType = defaultType;
	}

	setDefaultOutputMode(outputMode) {
		this.defaultOutput = outputMode;
	}
}
