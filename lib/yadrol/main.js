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
	    Action.codeMirror.setValue(expr);
	    //$('#expression-string').val(expr);
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
	    //var expressionString = $('#expression-string').val().trim();
	    var expressionString = Action.codeMirror.getValue();
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
	//var query = '?expr=' + $('#expression-string').val().trim();
	var query = '?expr=' + Action.codeMirror.getValue().trim();
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

    static setSampleSize() {
	yadrolApp.setSampleSize(Number($('#sample-size').val()));
    }

    static startTutorial() {
	//Alert.warning('Tutorial is not implemented...');
	Action.tutorial.init();
	//$('[data-toggle="popover"]').popover();
	Action.tutorial.start(true);
    }

    static initCodeMirror() {
	CodeMirror.defineMode('yadrol', Action.yadrolMode);
	Action.codeMirror = CodeMirror.fromTextArea(document.getElementById('expression-string'), {
	    lineNumbers: true,
	    mode: 'yadrol',
	    lineWrapping: true,
	});
	Action.codeMirror.on('changes', function(changeObj) {
	    var expr = changeObj.getValue().trim();
	    var disable = (expr === '');
	    $('#output-mode').attr('disabled', disable);
	    $('#url-button').attr('disabled', disable);	
	});
    }

    static yadrolMode(conf, parserConf) {
	return {
	    eatToken: function(stream) {
		for (var _c of yadrolParser.lexer.yytext) {
		    stream.next();
		}
	    },
	    
	    token: function(stream, state) {
		var input = stream.string.slice(stream.start);
		yadrolParser.lexer.setInput(input);
		var inString = false;
		while (true) {
		    try {
			var tokId = yadrolParser.lexer.next();
		    }
		    catch (e) {
			stream.skipToEnd();
			return 'STR';
		    }
		    this.eatToken(stream);
		    if (tokId === false) {
			return null;
		    }
		    if (tokId === yadrolParser.symbols_.STR_START) {
			inString = true;
			continue;
		    }
		    if (tokId === yadrolParser.symbols_.STR_END) {
			return 'STR';
		    }
		    if (inString) {
			continue;
		    }
		    if (tokId === yadrolParser.symbols_.NUMBER) {
			return 'NUM';
		    }
		    var tokType = yadrolParser.terminals_[tokId];
		    return tokType;
		}
	    }
	}
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
Action.tutorial = new Tour({
    //template: '<div class="popover" role="tooltip"> <div class="arrow"></div> <h3 class="popover-header"></h3> <div class="popover-body"></div> <div class="popover-navigation"> <div class="btn-group"> <button class="btn btn-sm btn-secondary" data-role="prev">&laquo; Prev</button> <button class="btn btn-sm btn-secondary" data-role="next">Next &raquo;</button> <button class="btn btn-sm btn-secondary" data-role="pause-resume" data-pause-text="Pause" data-resume-text="Resume">Pause</button> </div> <button class="btn btn-sm btn-secondary" data-role="end">End tour</button> </div> </div>',
    storage: false,
    steps: [
	{
	    orphan: true,
	    backdrop: true,
	    title: "Welcome to Yadrol tutorial!",
	    content: "<p>Yadrol is a dice simulator. It presents the dice results or the result distribution after rolling thousands of times.</p> <p>Yadrol is designed for tabletop role playing games and boardgames enthousiasts, especially those who want to study existing dice mechanics or to create new mechanics.</p>"
	},
	{
	    element: ".CodeMirror-wrap",
	    backdrop: true,
	    title: "How does it work?",
	    content: "<p>This is a text area where you specify the dice mechanics to simulate.</p> <p>The dice mechanics is written as an expression in a language designed to capture the most widely used dice mechanics.</p>"
	},
	{
	    element: ".CodeMirror-wrap",
	    backdrop: true,
	    title: "Dice expression basics.",
	    content: "<p>Let's try something simple.</p> <p>The core expression to specify a dice is <code>NdX</code> , where <var>N</var> is the number of dice to roll, and <var>X</var> is the number of sides of each die.</p> <p>For instance <code>3d6</code> tells Yadrol to roll three six-sided die.</p>",
	    onShow: function(tour) { Action.setExpressionString('3d6'); }
	},
	{
	    element: "#output-mode",
	    backdrop: true,
	    title: "Run the simulation",
	    content: "<p>To run the simulation, click this button.</p>",
	    reflex: true,
	    placement: 'bottom',
	    onHide: function(tour) { Action.run(); }
	},
	{
	    element: "#sample-records",
	    backdrop: true,
	    title: "Aha! You blinked!",
	    content: "<p>Yadrol has rolled your dice several thousand of times.</p> <p>This chart represents the frequency of Yadrol roll results.</p>",
	    placement: 'left'
	},
	{
	    element: "#y-button-group",
	    backdrop: true,
	    title: "Y axis values",
	    content: "<p>By default Yadrol displays as Y-axis the frequency to obtain <emph>at least</emph> the result.</p> <p>These buttons allow you to chose <emp>exact</emph> or <emph>at most</emph> as Y-axis.</p>",
	    placement: 'left'
	},
	{
	    element: "#sample-records",
	    backdrop: true,
	    title: "Mean value",
	    content: "<p>The little inverted triangle indicates the <emph>mean</emph> value.</p>",
	    placement: 'bottom'
	},
	{
	    element: "#centrum-button-group",
	    backdrop: true,
	    title: "Centrum values",
	    content: "<p>Though these buttons let you choose either to place the triangle on the <emph>mean</emph>, the <emph>mode</emph>, or the <emph>median</emph>.</p>",
	    placement: 'bottom'
	},
	{
	    element: "#output-modes-menu",
	    backdrop: true,
	    title: "Simulation modes",
	    content: '<p>There are three simulation modes:</p> <p><img class="output-mode-icon" src="'
		+Action.modes.roll.icon+'"> <em>Roll</em>: Yadrol rolls once and displays the result.</p> <p><img class="output-mode-icon" src="'
		+Action.modes.sample.icon+'"> <em>Sample</em>: Yadrol rolls several times and displays a distribution. This is the default mode.</p> <p><img class="output-mode-icon" src="'
		+Action.modes.advanced.icon+'"> <em>Advanced</em>: This is similar to the <em>Roll</em> mode but is used if the result is not necessarily a number.</p>',
	    placement: 'right',
	    onShow: function(tour) { $('#output-modes-menu').show(); },
	    onHide: function(tour) { $('#output-modes-menu').hide(); },
	},
	{
	    element: "#history-button",
	    backdrop: true,
	    title: "History of expressions.",
	    content: "<p>Yadrol keeps an history of simulated rolls. This button gives you access to your previous expressions.</p>",
	    placement: 'bottom'
	},
	{
	    element: "#url-button",
	    backdrop: true,
	    title: "Share your simulations.",
	    content: "<p>This button creates an URL for the expression in the editor that you can share.</p>",
	    placement: 'right'
	},
	{
	    orphan: true,
	    backdrop: true,
	    title: "That's it! And now?",
	    content: '<p>This is the end of the Yadrol introduction tutorial.</p> <p>You can learn more on the dice expression language by looking at the <a href="">Recipes</a>. You will realize that Yadrol supports a wide range of mechanics (roll and keep, exploding dice, etc).</p> <p>For the bravest, the <a href="">Reference</a> is more comprehensive and will allow you to write original and cutting edge expressions.</p>'
	}
    ]});

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
    SamplesCharter.clear();
    Action.initCodeMirror();
    URLOption.parseURL();
});

