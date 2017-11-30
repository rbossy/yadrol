var yadrolApp = new YadrolApp();

class Alert {
	static alert(level, message) {
		$('#output-container').prepend('<div class="row alert alert-'+level+' alert-dismissible" role="alert">'+message.replace(/\n/g, '<br>')+'<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button></div>');
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

class Element {
	static row(klass, ...args) {
		return $('<div class="row '+klass+'"></div>').append(args);
	}

	static col(...args) {
		return $('<div class="col"></div>').append(args);
	}

	static tr(...args) {
		return $('<tr></tr>').append(args);
	}

	static td(...contents) {
		return $('<td></td>').append(contents);
	}

	static th(...contents) {
		return $('<th></th>').append(contents);
	}

	static ul(...items) {
		return $('<ul></ul>').append(items.map(function(i) { return $('<li></li>').html(i); }));
	}

	static card(klass, title, subtitle, ...content) {
		var header = $('<div class="card-header bg-'+klass+'"></div>')
			.append(
				$('<h5></h5>').text(title)
			);
		if (subtitle) {
			header.append($('<h6></h6>').text(subtitle));
		}
		return $('<div class="card border-'+klass+'"></div>')
			.append(
				header,
				$('<div class="card-body"></div>')
				.append(
					content.map(function(e) { if ($(e[0]).is('div')) { return $(e[0]); } return $('<p class="card-text"></p>').append(e); })
				)
			);
	}

	static highlight(input, klass) {
		var result = $('<code></code>');
		if (klass) {
			result.addClass(klass);
		}
		yadrolParser.lexer.setInput(input);
		var inString = false;
		while (true) {
			var tokId = yadrolParser.lexer.next();
			if (tokId === false) {
				if (yadrolParser.lexer.yytext.slice(0, 2) === '//') {
					result.append(
						$('<span class="cm-COMM"></span>').append(yadrolParser.lexer.yytext)
					);
					continue;
				}
				result.append(yadrolParser.lexer.yytext.replace('\n', '<br>'));
				continue;
			}
			if (tokId === yadrolParser.symbols_.EOF) {
				break;
			}
			if (tokId === yadrolParser.symbols_.STR_START) {
				inString = yadrolParser.lexer.yytext;
				continue;
			}
			if (tokId === yadrolParser.symbols_.STR_END) {
				inString += yadrolParser.lexer.yytext;
				result.append(
					$('<span class="cm-STR"></span>').append(inString)
					);
				inString = false;
				continue;
			}
			if (inString) {
				inString += yadrolParser.lexer.yytext;
				continue;
			}
			if (tokId === yadrolParser.symbols_.NUMBER) {
				result.append(
					$('<span class="cm-NUM"></span>').append(yadrolParser.lexer.yytext)
				);
				continue;
			}
			if (tokId === yadrolParser.symbols_.PLACEHOLDER) {
				result.append(
					$('<span class="cm-PLACEHOLDER"></span>').append(yadrolParser.lexer.yytext.slice(1, yadrolParser.lexer.yytext.length - 1))
				);
				continue;
			}
			var tokType = yadrolParser.terminals_[tokId];
			result.append(
				$('<span class="cm-'+tokType+'"></span>').append(yadrolParser.lexer.yytext)
				);
		}
		return result;
	}

	static tryit(input, mode) {
		var result = Element.highlight(input, 'tryit');
		result.on('click', function() {
			Action.setExpressionString(input);
			if (Action.modes.hasOwnProperty(mode)) {
				Action.setMode(mode);
				Action.run();
			}
		});
		return result;
	}	
}

class RollOutput {
	static defaultDice(drec) {
		return Element.row('dice-row',
			Element.col().text(String(drec.result.length)+'d'+yadrolApp.valueString(drec.diceType)+' = '+yadrolApp.valueString(drec.result))
		);
	}

	static iconDice(drec) {
		var col = Element.col();
		for (var r of drec.result) {
			col.append('<img height="60px" src="icons/dice/d'+drec.diceType+'_'+r+'.svg">');
		}
		return Element.row('dice-row', col);
	}

	static diceRecord(drec) {
		switch (drec.diceType) {
			case 4: case 6: case 8: case 10: case 12: case 20:
			return RollOutput.iconDice(drec);
			default:
			return RollOutput.defaultDice(drec);
		}
	}

	static rollRecord(rec, first) {
		var klass = first ? ' first-roll-record' : '';
		return Element.row('roll-record' + klass,
			Element.col(
				Element.card('success', rec.name, yadrolApp.valueString(rec.result), rec.diceRecords.map(RollOutput.diceRecord))
			)
		);
	}

	static rollRecords() {
		var first = true;
		for (var rec of yadrolApp.recordLogger.outputRecords) {
			if (!rec.isSampleRecord) {
				$('#output-container').append(RollOutput.rollRecord(rec, first));
				first = false;
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
		localStorage.setItem('mode', modeObj.key);
		if (andRun) {
			Action.run();
		}
	}

	static setExpressionString(expr) {
		if (this instanceof HTMLElement) {
			expr = this.textContent;
		}
		if (expr.trim() !== '') {
			Action.codeMirror.setValue(expr);
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
		RollOutput.clear();
		SamplesCharter.clear();
	}

	static historyExpressions() {
		return $('#history > span');
	}

	static findInHistory(expr) {
		return Action.historyExpressions().children('code').filter(function(_index, e) { return e.textContent === expr; }).parent();
	}

	static addToHistory(expr) {
		if (Action.findInHistory(expr).length > 0) {
			return;
		}
		$('#history').prepend(
			$('<span class="dropdown-item"></span>').append(
				Element.tryit(expr),
				$('<span class="history-button float-right">&times;<span>')
				.click(Action.removeFromHistory)
			)
		);
		$('#history-button').attr('disabled', false);
		Action.updateLocalStorageHistory();
	}

	static removeFromHistory() {
		$(this).parent().remove();
		if (Action.historyExpressions().length === 0) {
			$('#history-button').attr('disabled', true);
		}
		Action.updateLocalStorageHistory();
	}

	static updateLocalStorageHistory() {
		var historyList = Action.historyExpressions().children('code').toArray().map(function(e) { return new Constant(Location.NONE, e.textContent); });
		localStorage.setItem('history', new ContainerConstructor(Location.NONE, historyList, 'list').toString());
	}

	static run() {
		try {
			var expressionString = Action.codeMirror.getValue();
			if (expressionString === '') {
				return;
			}
			Alert.clear();
			yadrolApp.parseAndEvaluate('textbox', expressionString);
			Action.clearOutput();
			SamplesCharter.createGraph();
			RollOutput.rollRecords();
			Action.addToHistory(expressionString);
			localStorage.setItem('run', 'true');
			localStorage.setItem('global-scope', ValueConverter.valueString(yadrolApp.globalScope, yadrolApp.globalScope.variables));
		}
		catch (e) {
			Alert.error(e.message);
			console.log(e);
		}
	}

	static updateURL() {
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

	static setSampleSize(n) {
		if (n) {
			$('#sample-size').val(n);
		}
		else {
			n = Number($('#sample-size').val());
		}
		yadrolApp.setSampleSize(n);
		localStorage.setItem('sample-size', String(n));
	}

	static initCodeMirror() {
		CodeMirror.defineMode('yadrol', Action.yadrolMode);
		Action.codeMirror = CodeMirror.fromTextArea(document.getElementById('expression-string'), {
			lineNumbers: true,
			mode: 'yadrol',
			lineWrapping: true,
			extraKeys: {
				'Ctrl-Enter': Action.run,
				'Shift-Enter': Action.run,
			}
		});
		Action.codeMirror.on('changes', function(changeObj) {
			var expr = changeObj.getValue().trim();
			var disable = (expr === '');
			$('#output-mode').attr('disabled', disable);
			$('#url-button').attr('disabled', disable);
			localStorage.setItem('expr', expr);
			localStorage.removeItem('run');
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
						if (yadrolParser.lexer.yytext.slice(0, 2) === '//') {
							stream.skipToEnd();
							return 'COMM';
						}
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

class StartOption {
	constructor(name, defaultValue, converter, apply) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.storageValue = undefined;
		this.urlValue = undefined;
		this.converter = converter;
		this.apply = apply;
		StartOption.ALL.set(name, this);
	}

	loadFromStorage() {
		this.storageValue = this.convert(localStorage.getItem(this.name));
	}

	loadFromURL(url) {
		this.urlValue = this.convert(url.searchParams.get(this.name));
	}

	finalValue() {
		if (this.urlValue !== undefined) {
			return this.urlValue;
		}
		if (this.storageValue !== undefined) {
			return this.storageValue;
		}
		return this.defaultValue;
	}

	convert(s) {
		if ((s === null) || (s === undefined)) {
			return undefined;
		}
		return this.converter(s);
	}

	static stringValue(s) {
		return s;
	}

	static expressionValue(s) {
		var expressions = yadrolParser.parseExpressions('<history>', s, yadrolApp.recordLogger);
		var result = expressions[0].evaluate(yadrolApp.globalScope);
		yadrolApp.recordLogger.clear();
		return result;
	}

	static booleanValue() {
		return true;
	}

	static numberValue(s) {
		return Number(s);
	}

	static urlExpression() {
		return StartOption.ALL.get('expr').urlValue;
	}

	static load() {
		var url = new URL(window.location);
		for (var opt of StartOption.ALL.values()) {
			opt.loadFromStorage();
			opt.loadFromURL(url);
		}
		for (var opt of StartOption.ALL.values()) {
			opt.apply();
		}
	}
}
StartOption.ALL = new Map();
new StartOption('expr', '', StartOption.stringValue, function() {
	Action.setExpressionString(this.finalValue());
});
new StartOption('history', [], StartOption.expressionValue, function() {
	var h = this.finalValue();
	if (h) {
		h.reverse().forEach(Action.addToHistory);
	}
});
new StartOption('seen-tutorial', false, StartOption.booleanValue, function() {
	if ((!StartOption.urlExpression()) && (!this.finalValue())) {
		Help.startTutorial();
	}
});
new StartOption('sample-size', 100000, StartOption.numberValue, function() {
	Action.setSampleSize(this.finalValue());
});
new StartOption('mode', 'sample', StartOption.stringValue, function() {
	if (StartOption.urlExpression()) {
		if (this.urlValue) {
			Action.setMode(this.urlValue);
		}
	}
	else {
		Action.setMode(this.finalValue());
	}
});
new StartOption('global-scope', undefined, StartOption.expressionValue, function() {
	var gs = this.finalValue();
	if (gs === undefined) {

	}
	else {
		console.log(gs);
		yadrolApp.globalScope.variables = gs;
	}
});
new StartOption('run', false, StartOption.booleanValue, function() {
	if (StartOption.urlExpression()) {
		if (this.urlValue) {
			Action.run();
		}
	}
	else {
		if (this.finalValue()) {
			Action.run();
		}
	}
});
new StartOption('help', undefined, StartOption.stringValue, function() {
	switch (this.finalValue()) {
		case 'Recipes': Help.tableofcontents('Recipes', Help.recipesContent); break;
		case 'Reference': Help.tableofcontents('Reference', Help.referenceContent); break;
	}
});
new StartOption('help-page', undefined, StartOption.numberValue, function() {
	if (StartOption.ALL.get('help').finalValue() !== undefined) {
		var val = this.finalValue();
		if (val !== undefined) {
			Help.page(val);
		}
	}
});

$(document).ready(function() {
	SamplesCharter.clear();
	Action.initCodeMirror();
	StartOption.load();
});

