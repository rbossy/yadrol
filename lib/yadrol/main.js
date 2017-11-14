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

var createRollDiv = function(rec) {
    var container = $('<div class="container-fluid"></div>');
    $('#output-container').append(
	div('row roll-record roll-record').append(
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
	container.append(
	    div('row dice-record').append(
		div('col dice-record').text(String(drec.result.length)+'d'+yadrolApp.valueString(drec.diceType)+' = '+yadrolApp.valueString(drec.result))
	    )
	);
    }
    container.append(
	$('<div class="row roll-record-result"></div>').append(
	    $('<div class="col"></div>').text(yadrolApp.valueString(rec.result))
	)
    );
}

var createRollDivs = function() {
    for (var rec of yadrolApp.recordLogger.outputRecords) {
	if (!rec.isSampleRecord) {
	    createRollDiv(rec);
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

var run = function() {
    var expressionString = $('#expression-string').val().trim();
    if (expressionString === '') {
	return;
    }
    yadrolApp.parseAndEvaluate('textbox', expressionString);
    clearOutput();
    createGraph();
    createRollDivs();
}

$(document).ready(function() {
    clearOutput();
});
