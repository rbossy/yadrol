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
    var diceRecordRows = '';
    for (var rec of this.diceRecords) {
	diceRecordRows += '<div class="row dice-record"><div class="col">'+rec.result.length+'d'+yadrolApp.valueString(rec.diceType)+' = '+yadrolApp.valueString(rec.result)+'</div></div>';
    }
    return '<div class="row output-record roll-record"><div class="col"><div class="container-fluid"><div class="row"><div class="col roll-record-name">'+this.name+'</div></div>'+diceRecordRows+'<div class="row roll-record-result"><div class="col">'+yadrolApp.valueString(this.result)+'</div></div></div></div></div>';
}

run = function() {
    var expressionString = $('#expression-string').val().trim();
    if (expressionString === '') {
	return;
    }
    yadrolApp.parseAndEvaluate('textbox', expressionString);
    console.log(yadrolApp);
    $('.output-record').remove();
    for (var rec of yadrolApp.recordLogger.outputRecords) {
	$('#output-container').append(rec.buildHTML());
    }
}

$(document).ready(function() {
    
});
