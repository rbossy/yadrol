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

var createRollDiv = function(rec) {
    var diceRecordRows = '';
    for (var drec of rec.diceRecords) {
	diceRecordRows += '<div class="row dice-record"><div class="col">'+drec.result.length+'d'+yadrolApp.valueString(drec.diceType)+' = '+yadrolApp.valueString(drec.result)+'</div></div>';
    }
    return '<div class="row output-record roll-record"><div class="col"><div class="container-fluid"><div class="row"><div class="col roll-record-name">'+rec.name+'</div></div>'+diceRecordRows+'<div class="row roll-record-result"><div class="col">'+yadrolApp.valueString(rec.result)+'</div></div></div></div></div>';
}

var createRollDivs = function() {
    for (var rec of yadrolApp.recordLogger.outputRecords) {
	if (!rec.isSampleRecord) {
	    $('#output-container').append(createRollDiv(rec));
	}
    }
}

var createGraph = function() {
}

var run = function() {
    var expressionString = $('#expression-string').val().trim();
    if (expressionString === '') {
	return;
    }
    yadrolApp.parseAndEvaluate('textbox', expressionString);
    $('.output-record').remove();
    createGraph();
    createRollDivs();
}

$(document).ready(function() {
    
});
