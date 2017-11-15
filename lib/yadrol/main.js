var yadrolApp = new YadrolApp();

var capitalize = function(s) {
    return s[0].toUpperCase() + s.slice(1).toLowerCase();
}

var parseOutputMode = function(s) {
    switch (s.toLowerCase()) {
    case 'roll':
	return Output.ROLL;
    case 'sample':
	// XXX warning
	return Output.SAMPLE;
    default:
	return Output.SAMPLE;
    }
}

Output.ROLL.icon = 'icons/buttons/graph4.svg';
Output.SAMPLE.icon = 'icons/buttons/dice3.svg';
var setOutputMode = function(outputMode, andRun) {
    var out = parseOutputMode(outputMode);
    yadrolApp.setDefaultOutputMode(out);
    $('#output-mode').html('<img class="output-mode-icon" src="' + out.icon + '">' + capitalize(out.symbol));
    if (andRun) {
	run();
    }
}

var setExpressionString = function(expr) {
    if (expr.trim() !== '') {
	$('#expression-string').val(expr);
    }
}

var div = function(klass, id) {
    var result = $('<div></div>');
    if (klass) {
	result.attr('class', klass);
    }
    if (id) {
	result.attr('id', id);
    }
    return result;
}

var createDefaultDiceRecordDiv = function(container, drec) {
    container.append(
	div('row dice-record').append(
	    div('col dice-record').text(String(drec.result.length)+'d'+yadrolApp.valueString(drec.diceType)+' = '+yadrolApp.valueString(drec.result))
	)
    );
}

var createIconsDiceRecordDiv = function(container, drec) {
    var col = div('col dice-record');
    for (r of drec.result) {
	col.append('<img height="60px" src="icons/dice/d'+drec.diceType+'_'+r+'.svg">');
    }
    container.append(
	div('row dice-record').append(
	    col
	)
    );
}

var createDiceRecordDiv = function(container, drec) {
    switch (drec.diceType) {
    case 4: case 6: case 8: case 10: case 12: case 20:
	createIconsDiceRecordDiv(container, drec);
	break;
    default:
	createDefaultDiceRecordDiv(container, drec);
    }
}

var createRollRecordDiv = function(rec) {
    var container = $('<div class="container-fluid"></div>');
    $('#output-container').append(
	div('row roll-record').append(
	    div('col').append(
		container
	    )
	)
    );
    container.append(
	div('row roll-record-name').append(
	    div('col roll-record-name').text(rec.name)
	)
    );
    for (var drec of rec.diceRecords) {
	createDiceRecordDiv(container, drec);
    }
    container.append(
	$('<div class="row roll-record-result"></div>').append(
	    $('<div class="col"></div>').text(yadrolApp.valueString(rec.result))
	)
    );
}

var createRollRecordDivs = function() {
    for (var rec of yadrolApp.recordLogger.outputRecords) {
	if (!rec.isSampleRecord) {
	    createRollRecordDiv(rec);
	}
    }
}

var GRAPH = undefined;
var renderGraph = function() {
    GRAPH.renderTo("svg#sample-records-graph");
}
var currentY = 'relativeAtLeast';
var _graphX = function(d) {
    return d.value;
}
var _graphY = function(d) {
    return d[currentY];
}
var centrumFunctions = {
    'mean': function(rec) { return rec.result.mean(); },
    'mode': function(rec) { return rec.result.mode().value; },
    'median': function(rec) { return (rec.result.medianInf().value + rec.result.medianSup().value) / 2; },
};
var currentCentrum = 'mean';
var _centrumX = function(d) {
    return centrumFunctions[currentCentrum](d)
}
var _centrumY = function(d) {
    return 0;
}
var createGraph = function() {
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
	.x(_graphX, xScale)
	.y(_graphY, yScale)
	.attr("stroke", function(d, i, dataset) { return dataset.metadata().name; }, colorScale);
    
    var centrumPlot = new Plottable.Plots.Scatter();
    for (var rec of sampleRecords) {
	centrumPlot.addDataset(new Plottable.Dataset([rec], rec));
    }
    centrumPlot
	.x(_centrumX, xScale)
	.y(_centrumY, yScale)
	.attr('fill', function(d, i, dataset) { return dataset.metadata().name; }, colorScale)
	.size(12)
	.symbol(function(d) { return new Plottable.SymbolFactories.triangleDown(); });
    
    var plots = new Plottable.Components.Group([linePlot, centrumPlot]);
    
    GRAPH = new Plottable.Components.Table([
	[legend, yAxis, plots],
	[null, null,  xAxis]
    ]);
    $('#sample-records').show();
    renderGraph();
}

var setY = function(y) {
    currentY = y;
    renderGraph();
}

var setCentrum = function(centrum) {
    currentCentrum = centrum;
    renderGraph();
}

var clearOutput = function() {
    $('.roll-record').remove();
    $('#sample-records').hide();
    if (GRAPH !== undefined) {
	GRAPH.destroy();
    }
}

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
}
URLOption.ALL = [
    new URLOption('expr', function(value) { setExpressionString(value); }),
    new URLOption('mode', function(value) { setOutputMode(value); }),
    new URLOption('run', function(value) { run(); }),
];

var parseURL = function() {
    var url = new URL(window.location);
    for (var opt of URLOption.ALL) {
	opt.setValue(url);
    }
}

var run = function() {
    var expressionString = $('#expression-string').val().trim();
    if (expressionString === '') {
	return;
    }
    yadrolApp.parseAndEvaluate('textbox', expressionString);
    clearOutput();
    createGraph();
    createRollRecordDivs();
}

$(document).ready(function() {
    clearOutput();
    parseURL();
});
