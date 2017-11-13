class YadrolApp {
    constructor() {
	this.globalScope = new Scope();
	this.recordLogger = new RecordLogger(100000, 'number');
	this.defaultOutput = Output.SAMPLE;
    }

    parseAndEvaluate(sourceFile, expressionString) {
	this.recordLogger.clear();
	var expressions = yadrolParser.parseExpressions(sourceFile, expressionString, this.recordLogger);
	for (var i = 0; i < expressions.length - 1; ++i) {
	    expressions[i].evaluate(this.globalScope);
	}
	var last = expressions[expressions.length - 1];
	last.evaluate(this.globalScope);
	if ((this.recordLogger.rollRecords.length == 0) && (this.recordLogger.sampleRecords.length == 0)) {
	    last = new Output(last.location, undefined, last, this.recordLogger.defaultType, this.defaultOutput, this.recordLogger);
	    last.evaluate(this.globalScope);
	}
	for (var s of this.recordLogger.sampleRecords) {
	    s.result.aggregate();
	}
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


