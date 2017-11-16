var yadrolApp = new YadrolApp();

class Alert {
    static alert(level, message) {
	$('#output-container').prepend('<div class="row alert alert-'+level+' alert-dismissible" role="alert">'+message+'<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>');
    }

    static error(message) {
	Alert.alert('danger', message);
    }

    static warning(message) {
	Alert.alert('warning', message);
    }

    static clear() {
	$('.alert').remove();
    }
}

class RollDivs {
    static div(klass, id) {
	var result = $('<div></div>');
	if (klass) {
	    result.attr('class', klass);
	}
	if (id) {
	    result.attr('id', id);
	}
	return result;
    }

    static createDefaultDiceRecordDiv(container, drec) {
	container.append(
	    RollDivs.div('row dice-record').append(
		RollDivs.div('col dice-record').text(String(drec.result.length)+'d'+yadrolApp.valueString(drec.diceType)+' = '+yadrolApp.valueString(drec.result))
	    )
	);
    }

    static createIconsDiceRecordDiv(container, drec) {
	var col = RollDivs.div('col dice-record');
	for (var r of drec.result) {
	    col.append('<img height="60px" src="icons/dice/d'+drec.diceType+'_'+r+'.svg">');
	}
	container.append(
	    RollDivs.div('row dice-record').append(
		col
	    )
	);
    }

    static createDiceRecordDiv(container, drec) {
	switch (drec.diceType) {
	case 4: case 6: case 8: case 10: case 12: case 20:
	    RollDivs.createIconsDiceRecordDiv(container, drec);
	    break;
	default:
	    RollDivs.createDefaultDiceRecordDiv(container, drec);
	}
    }

    static createDiv(rec) {
	var container = $('<div class="container-fluid"></div>');
	$('#output-container').append(
	    RollDivs.div('row roll-record').append(
		RollDivs.div('col').append(
		    container
		)
	    )
	);
	container.append(
	    RollDivs.div('row roll-record-name').append(
		RollDivs.div('col roll-record-name').text(rec.name)
	    )
	);
	for (var drec of rec.diceRecords) {
	    RollDivs.createDiceRecordDiv(container, drec);
	}
	container.append(
	    $('<div class="row roll-record-result"></div>').append(
		$('<div class="col"></div>').text(yadrolApp.valueString(rec.result))
	    )
	);
    }

    static createDivs() {
	for (var rec of yadrolApp.recordLogger.outputRecords) {
	    if (!rec.isSampleRecord) {
		RollDivs.createDiv(rec);
	    }
	}
    }

    static clear() {
	$('.roll-record').remove();
    }
}

class SamplesCharter {
    static render() {
	$('#sample-records').show();
	SamplesCharter.GRAPH.renderTo("svg#sample-records-graph");
    }
    
    static _graphX(d) {
	return d.value;
    }
    
    static _graphY(d) {
	return d[SamplesCharter.currentY];
    }

    static _centrumX(d) {
	return SamplesCharter.centrumFunctions[SamplesCharter.currentCentrum](d)
    }
    
    static _centrumY(d) {
	return 0;
    }
    
    static createGraph() {
	var sampleRecords = yadrolApp.recordLogger.outputRecords.filter(function(rec) { return rec.isSampleRecord; });
	if (sampleRecords.length === 0) {
	    return;
	}
	var xScale = new Plottable.Scales.Linear();
	var yScale = new Plottable.Scales.Linear();
	yScale.scale(10);
	var colorScale = new Plottable.Scales.Color();
	
	var xAxis = new Plottable.Axes.Numeric(xScale, "bottom");
	var yAxis = new Plottable.Axes.Numeric(yScale, "left");
	
	var legend = new Plottable.Components.Legend(colorScale);
	
	var linePlot = new Plottable.Plots.Line();
	for (var rec of sampleRecords) {
	    linePlot.addDataset(new Plottable.Dataset(rec.result, rec));
	}
	linePlot
	    .x(SamplesCharter._graphX, xScale)
	    .y(SamplesCharter._graphY, yScale)
	    .attr("stroke", function(d, i, dataset) { return dataset.metadata().name; }, colorScale);
	
	var centrumPlot = new Plottable.Plots.Scatter();
	for (var rec of sampleRecords) {
	    centrumPlot.addDataset(new Plottable.Dataset([rec], rec));
	}
	centrumPlot
	    .x(SamplesCharter._centrumX, xScale)
	    .y(SamplesCharter._centrumY, yScale)
	    .attr('fill', function(d, i, dataset) { return dataset.metadata().name; }, colorScale)
	    .size(12)
	    .symbol(function(d) { return new Plottable.SymbolFactories.triangleDown(); });
	
	var plots = new Plottable.Components.Group([linePlot, centrumPlot]);
	
	SamplesCharter.GRAPH = new Plottable.Components.Table([
	    [legend, yAxis, plots],
	    [null, null,  xAxis]
	]);
	SamplesCharter.render();
    }

    static clear() {
	$('#sample-records').hide();
	if (SamplesCharter.GRAPH !== undefined) {
	    SamplesCharter.GRAPH.destroy();
	}
    }
}
SamplesCharter.GRAPH = undefined;
SamplesCharter.currentY = 'relativeAtLeast';
SamplesCharter.centrumFunctions = {
    'mean': function(rec) { return rec.result.mean(); },
    'mode': function(rec) { return rec.result.mode().value; },
    'median': function(rec) { return (rec.result.medianInf().value + rec.result.medianSup().value) / 2; },
};
SamplesCharter.currentCentrum = 'mean';

class Action {
    static capitalize(s) {
	return s[0].toUpperCase() + s.slice(1).toLowerCase();
    }

    static setMode(mode, andRun) {
	var modeObj = Action.modes[mode.toLowerCase()];
	if (modeObj === undefined) {
	    Alert.warning('unknown mode: ' + mode + ', defaults to: sample');
	    modeObj = Action.modes['sample'];
	}
	yadrolApp.setDefaultOutputMode(modeObj.outputMode);
	yadrolApp.setDefaultType(modeObj.evaluationType);
	$('#output-mode').html('<img class="output-mode-icon" src="' + modeObj.icon + '">' + modeObj.label);
	Action.currentMode = modeObj.key;
	if (andRun) {
	    Action.run();
	}
    }

    static setExpressionString(expr) {
	if (expr.trim() !== '') {
	    $('#expression-string').val(expr);
	}
    }

    static setY(y) {
	SamplesCharter.currentY = y;
	SamplesCharter.render();
    }

    static setCentrum(centrum) {
	SamplesCharter.currentCentrum = centrum;
	SamplesCharter.render();
    }

    static clearOutput() {
	RollDivs.clear();
	SamplesCharter.clear();
    }

    static addToHistory(expr) {
	var prev = $('#history').children('a').filter(function(_index, e) { return e.textContent == expr; });
	if (prev.length > 0) {
	    return;
	}
	$('#history').prepend('<a class="dropdown-item" onclick="Action.setExpressionString(\''+expr+'\')">'+expr+'</a>');
	$('#history-button').attr('disabled', false);
    }

    static run() {
	try {
	    var expressionString = $('#expression-string').val().trim();
	    if (expressionString === '') {
		return;
	    }
	    Alert.clear();
	    yadrolApp.parseAndEvaluate('textbox', expressionString);
	    Action.clearOutput();
	    SamplesCharter.createGraph();
	    RollDivs.createDivs();
	    Action.addToHistory(expressionString);
	}
	catch (e) {
	    Alert.error(e);
	    console.log(e);
	}
    }

    static updateURL() {
	var query = '?expr=' + $('#expression-string').val().trim();
	if ($('#url-include-mode').prop('checked')) {
	    query += '&mode=' + Action.currentMode;
	}
	if ($('#url-autorun').prop('checked')) {
	    query += '&run';
	}
	var url = new URL(query, window.location);
	$('#url').val(url.href);
    }

    static copyURL() {
	$('#url').select();
	document.execCommand("Copy");
    }
}
Action.currentMode = 'sample';
Action.modes = {
    'sample': {
	'key': 'sample',
	'label': 'Sample',
	'icon': 'icons/buttons/dice3.svg',
	'outputMode': Output.SAMPLE,
	'evaluationType': 'number'
    },
    'roll': {
	'key': 'roll',
	'label': 'Roll',
	'icon': 'icons/buttons/graph4.svg',
	'outputMode': Output.ROLL,
	'evaluationType': 'number'
    },
    'advanced': {
	'key': 'advanced',
	'label': 'Advanced',
	'icon': 'icons/buttons/dice2.svg',
	'outputMode': Output.ROLL,
	'evaluationType': 'native'
    },
};

class URLOption {
    constructor(name, setter) {
	this.name = name;
	this.setter = setter;
    }

    setValue(url) {
	var value = url.searchParams.get(this.name);
	if ((value === null) || (value === undefined)) {
	    return;
	}
	this.setter(value.trim());
    }

    static parseURL() {
	var url = new URL(window.location);
	for (var opt of URLOption.ALL) {
	    opt.setValue(url);
	}
    }
}
URLOption.ALL = [
    new URLOption('expr', Action.setExpressionString),
    new URLOption('mode', Action.setMode),
    new URLOption('run', Action.run),
];

$(document).ready(function() {
    URLOption.parseURL();
});
