var yadrolApp = new YadrolApp();

Output.ROLL.icon = 'icons/buttons/graph4.svg';
Output.SAMPLE.icon = 'icons/buttons/dice3.svg';

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

    static parseOutputMode(s) {
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

    static setOutputMode(outputMode, andRun) {
	var out = Action.parseOutputMode(outputMode);
	yadrolApp.setDefaultOutputMode(out);
	$('#output-mode').html('<img class="output-mode-icon" src="' + out.icon + '">' + Action.capitalize(out.symbol));
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

    static run() {
	var expressionString = $('#expression-string').val().trim();
	if (expressionString === '') {
	    return;
	}
	yadrolApp.parseAndEvaluate('textbox', expressionString);
	Action.clearOutput();
	SamplesCharter.createGraph();
	RollDivs.createDivs();
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

    static parseURL() {
	var url = new URL(window.location);
	for (var opt of URLOption.ALL) {
	    opt.setValue(url);
	}
    }
}
URLOption.ALL = [
    new URLOption('expr', Action.setExpressionString),
    new URLOption('mode', Action.setOutputMode),
    new URLOption('run', Action.run),
];

$(document).ready(function() {
    URLOption.parseURL();
});