var yadrolApp = new YadrolApp();

var parseOutputMode = function(s) {
    switch (s) {
    case 'Roll':
    case 'roll':
	return Output.ROLL;
    default:
	return Output.SAMPLE;
    }
}

var setOutputMode = function(outputMode, andRun) {
    yadrolApp.setDefaultOutputMode(parseOutputMode(outputMode));
    $('#output-mode').html(outputMode);
    if (andRun) {
	run();
    }
}

SampleRecord.prototype.buildHTML = function() {
    console.log('sample build HTML: ' + this.name);
}

RollRecord.prototype.buildHTML = function() {
    console.log('roll build HTML: ' + this.name);
}

run = function() {
    var expressionString = $('#expression-string').val().trim();
    if (expressionString === '') {
	return;
    }
    yadrolApp.parseAndEvaluate('textbox', expressionString);
    console.log(yadrolApp);
    for (var rec of yadrolApp.recordLogger.outputRecords) {
	rec.buildHTML();
    }
}

$(document).ready(function() {
    
});
