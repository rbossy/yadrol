/**
 * 
 */

var ICON = {
		'Sample': 'icons/buttons/graph4.svg',
		'Roll': 'icons/buttons/dice3.svg',
		'Advanced Roll': 'icons/buttons/dice2.svg'
}

var set_output_mode = function(mode, andRun) {
	$('#button-run').html('<img height="17ex" src="'+ICON[mode]+'">&nbsp;'+mode);
	if (andRun) {
		run();
	}
}

var run = function() {
	settings = {
			'expression-string': $('#expression-string').val(),
			'output-mode': $('#button-run').text().trim().toLowerCase(),
			'evaluation-type': $('#evaluation-type').val(),
			'sample-size': $('#sample-size').val(),
			'confidence-interval-risk': $('#confidence-interval-risk').val()
	};
	var outMode = $('#button-run').text().trim();
	if (outMode == 'Sample') {
		settings['output-mode'] = 'sample';
		settings['evaluation-type'] = 'integer';
	}
	else if (outMode == 'Roll') {
		settings['output-mode'] = 'roll';
		settings['evaluation-type'] = 'integer';
	}
	else {
		settings['output-mode'] = 'roll';
		settings['evaluation-type'] = 'any';
	}
	//console.log(settings);
	if (settings['expression-string'].trim() == '') {
		return;
	}
	$.get('api/run', settings)
	.done(_done)
	.fail(_fail);
}
;

var _fail = function(data) {
	console.log(data);
	if (data.status == 422) {
		if (data.responseJSON.error == 'parse') {
			console.log(data.responseJSON.message);
			return;
		}
		return;
	}
	if (data.status >= 500 && data.status < 600) {
		// server error
		return;
	}
}
;

var _sample_graph = null;

var _done = function(data) {
	//console.log(data);

	_clear_output();
	
	var sample_records = data.result['sample-records'];
	if (sample_records.length > 0) {
		_create_graph(sample_records);
	}
	
	var roll_records = data.result['roll-records'];
	for (var i = 0; i < roll_records.length; ++i) {
		_create_roll_output(i, roll_records[i]);
	}
}
;

var _clear_output = function() {
	$('#sample-records-box').hide();
	if (_sample_graph != null) {
		_sample_graph.destroy();
	}
	$('.roll-record').remove();
}
;

var CENTRUM_FUN = {
		'mean': function(d) { return d.mean; },
		'mode': function(d) { return d.mode.value; },
		'median': function(d) { return (d['median-sup'].value + d['median-inf'].value) / 2; }
}
;

var _current_y = 'relative-at-least-frequency';
var _current_centrum = CENTRUM_FUN['mean'];

var _create_graph = function(sample_records) {
	var xScale = new Plottable.Scales.Linear();
	var yScale = new Plottable.Scales.Linear();
	yScale.scale(10);
	var colorScale = new Plottable.Scales.Color();

	var xAxis = new Plottable.Axes.Numeric(xScale, "bottom");
	var yAxis = new Plottable.Axes.Numeric(yScale, "left");

	var legend = new Plottable.Components.Legend(colorScale);

	var linePlot = new Plottable.Plots.Line();
	for (var i = 0; i < sample_records.length; ++i) {
		var sr = sample_records[i];
		linePlot.addDataset(new Plottable.Dataset(sr.counts, sr));
	}
	linePlot
	  .x(function(d) { return d.value; }, xScale)
	  .y(function(d) { return d[_current_y]; }, yScale)
	  .attr("stroke", function(d, i, dataset) { return dataset.metadata().name; }, colorScale);
	
	var centrumPlot = new Plottable.Plots.Scatter();
	for (var i = 0; i < sample_records.length; ++i) {
		var sr = sample_records[i];
		centrumPlot.addDataset(new Plottable.Dataset([sr], sr));
	}
	centrumPlot
	  .x(_current_centrum, xScale)
	  .y(function(d) { return 0; }, yScale)
	  .attr('fill', function(d, i, dataset) { return dataset.metadata().name; }, colorScale)
	  .size(12)
	  .symbol(function(d) { return new Plottable.SymbolFactories.triangleDown(); });

	var plots = new Plottable.Components.Group([linePlot, centrumPlot]);

	_sample_graph = new Plottable.Components.Table([
	                                            [legend, yAxis, plots],
	                                            [null, null,  xAxis]
	                                            ]);
	$('#sample-records-box').show();
	_sample_graph.renderTo("svg#sample-records");
}
;

var change_y = function(y) {
	_current_y = y;
	var yScale = _sample_graph._rows[0][1]._scale;
	var linePlot = _sample_graph._rows[0][2]._components[0];
	linePlot.y(function(d) { return d[y]; }, yScale);
}
;

var change_centrum = function(c) {
	_current_centrum = CENTRUM_FUN[c];
	var xScale = _sample_graph._rows[1][2]._scale;
	var centrumPlot = _sample_graph._rows[0][2]._components[1];
	centrumPlot.x(_current_centrum, xScale);
}
;

var _standard_die_fun = function(dt) {
	return function(n) {
		return '<img height="60px" src="icons/dice/d'+dt+'_'+n+'.svg">';
	}
}
;

var DICE_RECORD_FUN = {
	4: _standard_die_fun(4),
	6: _standard_die_fun(6),
	8: _standard_die_fun(8),
	10: _standard_die_fun(10),
	12: _standard_die_fun(12),
	20: _standard_die_fun(20),
}
;

var DEFAULT_DICE_RECORD = function(n) {
	return '' + n;
}

var _create_roll_output = function(i, rec) {
	$('#output').append('<div class="row roll-record"><div class="col-md-3 roll-name"><code>'+rec.name+'</code></div><div class="col-md-2 roll-result lead">'+rec['string-result']+'</div><div class="col-md-7 dice-records" id="dice-records-'+i+'"></div></div>');
	console.log(rec);
	for (var j = 0; j < rec['dice-records'].length; ++j) {
		var dr = rec['dice-records'][j];
		var drfun;
		if (dr.type in DICE_RECORD_FUN) {
			drfun = DICE_RECORD_FUN[dr.type];
		}
		else {
			drfun = DEFAULT_DICE_RECORD;
		}
		$('#dice-records-'+i).append('<div class="row"><div class="col-md-1 dice-type lead">d'+dr.type+'</div><div class="col-md-11" id="dice-records-'+i+'-'+j+'"></div></div>');
		for (var k = 0; k < dr.result.length; ++k) {
			$('#dice-records-'+i+'-'+j).append('&nbsp;' + drfun(dr.result[k]));
		}
	}
}
;

var _pop_searchParams = function(q, k) {
	var r = q.get(k);
	q['delete'](k);
	return r;
}

var _parse_url = function() {
	var url = new URL(document.baseURI);
	var q = url.searchParams;
	if (q.has('expression-string')) {
		$('#expression-string').val(_pop_searchParams(q, 'expression-string'));
	}
	if (q.has('run')) {
		_pop_searchParams(q, 'run');
		run();
	}
	if (q.has('roll')) {
		set_output_mode('Roll');
		_pop_searchParams(q, 'roll');
		run();
	}
	if (q.has('advanced-roll')) {
		set_output_mode('Advanced Roll');
		_pop_searchParams(q, 'advanced-roll');
		run();
	}
	if (q.has('sample')) {
		set_output_mode('Sample');
		_pop_searchParams(q, 'sample');
		run();
	}
}
;

var try_it = function(expressionString, outputMode, runNow) {
	$('#expression-string').val(expressionString);
	if (outputMode == 'roll') {
		set_output_mode('Roll');
	}
	if (outputMode == 'sample') {
		set_output_mode('Sample');
	}
	if (outputMode == 'advanced-roll') {
		set_output_mode('Advanced Roll');
	}
	if (runNow) {
		run();
	}
}
;


$(document).ready(function() {
	set_output_mode('Sample');
	_clear_output();
	_parse_url();
	$('#help-contents').load('help.html', function() {
		$('.tryit').on('click', function() {
			var e = $(this);
			var expressionString = e[0].innerText;
			var outputMode = e[0].dataset['output-mode'];
			var runNow = false;
			if (e[0].dataset['run']) {
				runNow = Boolean(e[0].dataset['run']);
			}
			try_it(expressionString, outputMode, runNow);
		});
	});
});

