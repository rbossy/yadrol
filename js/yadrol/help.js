class Help {
	static startTutorial() {
		Help.tutorial.init();
		Help.tutorial.start(true);
	}

	static clear() {
		$('#help-toc').empty();
		$('#help-content').empty();
	}

	static hide() {
		$('#help').hide();
		Help.clear();
		Help.current = undefined;
	}

	static show() {
		$('#help').show();
	}

	static tableofcontents(helpTitle, helpContent) {
		if (Help.current === helpContent) {
			return;
		}
		Help.clear();
		Help.current = helpContent;
		var list = $('<ul></ul>');
		$('#help-toc')
		.append(
			$('<div class="card card-primary"></div>')
			.append(
				$('<div class="card-header"></div>')
				.append(
					$('<button type="button" class="close" onclick="Help.hide()"></button>')
					.append(
						$('<span>&times;</span>')
					),
					$('<h3></h3>').text(helpTitle)
				),
				$('<div class="card-body"></div>')
				.append(list)
			)
		);
		for (var e of helpContent.entries()) {
			var index = e[0];
			var page = e[1];
			list.append('<li class="toc-item" onclick="Help.page('+index+')">'+page.title+'</li>');
		}
		Help.show();
	}

	static page(index) {
		$('.toc-item-selected').removeClass('toc-item-selected');
		$('.toc-item').slice(index, index+1).addClass('toc-item-selected');
		var page = Help.current[index];
		var body = page.body();
		$('#help-content')
			.empty()
			.append(
				$('<div class="container-fluid"></div>').append(
					body
				)
			);
	}

	static placeholder(placeholder, body) {
		return Element.highlight('$'+placeholder+'$').add($('<span> is </span>')).add(((typeof body) === 'strig') ? $('<p></p>').html(body) : body);
	}
}
Help.tutorial = new Tour({
	storage: false,
	onEnd: function(tour) { localStorage.setItem('seen-tutorial', 'true'); },
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
		content: function() { return '<p>There are three simulation modes:</p> <p><img class="output-mode-icon" src="'
		+Action.modes.roll.icon+'"> <em>Roll</em>: Yadrol rolls once and displays the result.</p> <p><img class="output-mode-icon" src="'
		+Action.modes.sample.icon+'"> <em>Sample</em>: Yadrol rolls several times and displays a distribution. This is the default mode.</p> <p><img class="output-mode-icon" src="'
		+Action.modes.advanced.icon+'"> <em>Advanced</em>: This is similar to the <em>Roll</em> mode but is used if the result is not necessarily a number.</p>' },
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
		element: "#help-buttons",
		backdrop: true,
		title: "Help!",
		content: "<p>These buttons show help pages. <em>Recipes</em> are expressions for the most used mechanics, and <em>Reference</em> contains all Yadrol constructs.</p>",
		placement: 'right'
	},
	{
		orphan: true,
		backdrop: true,
		title: "That's it! And now?",
		content: '<p>This is the end of the Yadrol introduction tutorial.</p> <p><strong>Enjoy!</strong></p>'
	}
	]});
Help.current = undefined;
Help.recipesContent = [
{
	title: 'Basics',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Dungeons & Dragons', 'Naturally...', Element.tryit('d20', 'roll'))
				),
				Element.col(
					Element.card('default', 'BRP', 'Basic RolePlaying', Element.tryit('d100', 'roll'))
				),
				Element.col(
					Element.card('default', 'Seldom used d8', 'Unfortunately', Element.tryit('d8', 'roll'))
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'd13', 'does exist', 'in the hyperdimensional irrational space. Not a problem for Yadrol', Element.tryit('d13', 'roll'))
				),
				Element.col(
					Element.card('default', 'Barbarians of Lemuria', 'by Simon Washbourne', Element.tryit('2d6', 'roll'))
				)
			),
		];
	}
},
{
	title: 'Roll & Keep',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Roll two dice', 'Keep highest', Element.tryit('highest of 2d10', 'roll'))
				),
				Element.col(
					Element.card('default', 'Roll two dice', 'Keep lowest', Element.tryit('lowest of 2d10', 'roll'))
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Roll three dice', 'Keep the two highest', Element.tryit('highest 2 of 3d6', 'roll'))
				),
				Element.col(
					Element.card('default', 'Roll three dice', 'Keep the two lowest', Element.tryit('lowest 2 of 3d6', 'roll'))
				),
			),
		];
	}
},
{
	title: 'Count hits',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'World of Darkness', 'Bucketful of dice', Element.tryit('count (for x in 6d10 if x>=7)', 'roll'))
				),
				Element.col(
					Element.card('default', 'Count 1s', 'Because one is glorious', Element.tryit('#(for x in 4d6 if x==1)', 'roll'))
				)
			)
		]
	}
},
{
	title: 'Exploding die',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Exploding die', 'The nice kind', Element.tryit('repeat (x=d6) if x==6', 'sample')),
				),
				Element.col(
					Element.card('default', 'Wild die', 'The savage kind', Element.tryit('repeat (x=d6) while x==6', 'sample')),
				),
				Element.col(
					Element.card('default', 'Reroll 1s', 'Because one is infamous', Element.tryit('last of repeat (x=d6) if x==1', 'sample')),
				),
			)
		]
	}
},
{
	title: 'Custom dice',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Fudge dice', 'Also: FATE', Element.tryit('4d[-1, -1, 0, 0, 1, 1]', 'sample'))
				),
				Element.col(
					Element.card('default', 'Shorter Fudge dice', 'With equivalent results', Element.tryit('4d[-1, 0, 1]', 'sample'))
				),
			)
		];
	}
},
{
	title: 'Barbarians of Lemuria damage',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'A mediocre barbarian', 'strikes with a sword', Element.tryit('if 2d6>=9 then d6+1 else 0', 'roll'))
				),
				Element.col(
					Element.card('default', 'A more sensible barbarian', 'strikes with a sword', Element.tryit('Melee=2;\nDefense=1;\nif 2d6+Melee-Defense>=9 then d6+1 else 0', 'roll'))
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'A more sensible barbarian', 'strikes with a sword', Element.tryit('BoL = fun(Melee, Defense) { if 2d6+Melee-Defense>=9 then d6+1 else 0 }\n---\nBoL(3, 0)', 'roll'))
				),
			)
		];
	}
},
{
	title: 'Multiple outputs',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Explicit mode', 'Overrides UI mode', Element.tryit('sample d20', 'roll'))
				),
				Element.col(
					Element.card('default', 'Compare two graphs', 'For science!', Element.tryit('sample d20; sample 3d6', 'roll'))
				),
				Element.col(
					Element.card('default', 'Roll and roll', 'roll is like sample', Element.tryit('roll d20; roll 3d6', 'sample'))
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Ad&D Strength', 'Keep rolling until hitting a 18', Element.tryit('if (roll 3d6)==18 then (roll d100) else undef', 'roll'))
				),
				Element.col(
					Element.card('default', 'Effect of dice pool size', 'in Roll & Keep', Element.tryit('(sample highest 2 of Nd6) for N in 2..7', 'sample'))
				),
			)
		];
	}
}
];
Help.referenceContent = [
{
	title: 'Data types, literals and constructors',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Undef', undefined,
						Element.highlight('undef'),
						'Undef type has a single value: <em>undef</em>.'
					)
				),
				Element.col(
					Element.card('default', 'Boolean', undefined,
						Element.highlight('false'),
						Element.highlight('true'),
						'Boolean type has two values: <em>false</em> and <em>true</em>.'
					)
				),
				Element.col(
					Element.card('default', 'String', undefined,
						Element.highlight('"..."'),
						'String values are sequence of Unicode characters.'
					)
				),
				Element.col(
					Element.card('default', 'Number', undefined,
					 	$('<code class="cm-NUM">[+-]?[0-9]+</code>'),
						'In Yadrol, numbers are signed integer values.'
					)
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'List', undefined,
						Element.highlight('[ $expr$, $expr$, $...$ ]'),
						'Lists are ordered collections of expressions of any type.'
					)
				),
				Element.col(
					Element.card('default', 'Map', undefined, 
						Element.highlight('{ name: $expr$, name: $expr$, $...$ }'),
						'Maps are ordered collections of named expressions of any type.', 'In a map, entry keys are unique.'
					)
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Function', undefined,
						Element.highlight('fun (name: $value$, name: $value$, $...$) { $body$ }'),
						'Functions are expressions that can be called and evaluated with a specific set of parameters.',
						'Function expressions are also referred as <em>lambda</em> expressions.',
						'Each argument is composed of an identifier that specifies its name, and the default value. The default value is optional. If the default vaue is specified, then it must be separated from the name with a colon.'
					)
				)
			)
		];
	}
},
{
	title: 'Data conversion',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					$('<table class="table table-bordered table-hover"></table>').append(
						$('<thead class="thead-light"></thead>').append(
							Element.tr(
								Element.th('from \\ to'),
								Element.th('string'),
								Element.th('boolean'),
								Element.th('number'),
								Element.th('list'),
								Element.th('map'),
								Element.th('function')
							)
						),
						$('<tbody></tbody>').append(
							Element.tr(
								Element.th('undef'),
								Element.td(Element.highlight('""')),
								Element.td(Element.highlight('false')),
								Element.td(Element.highlight('0')),
								Element.td(Element.highlight('[]')),
								Element.td(Element.highlight('{}')),
								Element.td(Element.highlight('fun () { undef }'))
							),
							Element.tr(
								Element.th('string'),
								Element.td(),
								Element.td(Element.highlight('$str$ != ""')),
								Element.td('Base 10, or zero'),
								Element.td(Element.highlight('[ $str$ ]')),
								Element.td(Element.highlight('{ _ : $str$ }')),
								Element.td(Element.highlight('fun () { $str$ }'))
							),
							Element.tr(
								Element.th('boolean'),
								Element.td(Element.highlight('if $bool$ then "true" else ""')),
								Element.td(),
								Element.td(Element.highlight('if $bool$ then 1 else 0')),
								Element.td(Element.highlight('[ $bool$ ]')),
								Element.td(Element.highlight('{ _ : $bool$ }')),
								Element.td(Element.highlight('fun () { $bool$ }'))
							),
							Element.tr(
								Element.th('number'),
								Element.td('Base 10'),
								Element.td(Element.highlight('$num != 0$')),
								Element.td(),
								Element.td(Element.highlight('[ $num$ ]')),
								Element.td(Element.highlight('{ _ : $num$ }')),
								Element.td(Element.highlight('fun () { $num$ }'))
							),
							Element.tr(
								Element.th('list'),
								Element.td('Concatenation'),
								Element.td(Element.highlight('#$lst$ > 0')),
								Element.td('Sum'),
								Element.td(),
								Element.td(Element.highlight('{ _0 : $lst$[0] , _1 : $lst$[1] , $...$ }')),
								Element.td(Element.highlight('fun () { $lst$ }'))
							),
							Element.tr(
								Element.th('map'),
								Element.td('Concatenation of values'),
								Element.td(Element.highlight('#$map$ > 0')),
								Element.td('Sum of values'),
								Element.td('List of values'),
								Element.td(),
								Element.td(Element.highlight('fun () { $map$ }'))
							),
							Element.tr(
								Element.th('function'),
								Element.td('Call function without argument and convert the result').attr('colspan', 6),
							)
						)
					)
				)
			)
		]
	}
},
{
	title: 'Operator precedence',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					$('<table class="table table-bordered table-hover"></table>').append(
						$('<thead class="thead-light"></thead>').append(
							Element.tr(
								Element.th('Operators'),
								Element.th('associativity')
							)
						),
						$('<tbody></tbody>').append(
							Element.tr(
								Element.td(Element.highlight('---')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight(';')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('import roll sample as')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('=')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('if while limit for')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('or')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('and')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('not')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('=== !== == != < > <= >=')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('in')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('<<')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('..')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('+ -')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('* / %')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('+ - $(unary)$')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('highest lowest first last')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('draw')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('d')),
								Element.td('left')
							),
							Element.tr(
								Element.td(Element.highlight('# sorted shuffled reversed boolean string number list map')),
								Element.td('no')
							),
							Element.tr(
								Element.td(Element.highlight('() $(call)$ . [] $(subscript)$')),
								Element.td('left')
							),
						)
					)
				)
			)
		]
	}
},
{
	title: 'Scoping',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Dynamic scoping', undefined,
						'Variables need not to be declared, they are declared within the scope of their first assignment.',
						'Function and loop scopes are dynamic: the scope is created at runtime.'
					)
				),
				Element.col(
					Element.card('default', 'Parent scope', undefined,
						'Variable values are searched within the current scope. If no variable of the specified name was found, then it is searched in the current\'s scope parent scope. The scope parents are searched until the global scope (which has no parent) is reached.',
						'If no variabe is found in the global scope, then its value is undef.',
						'Assignment works the same way: the value is assigned to the variable in the innermost scope where the variable name was defined.',
					)
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Spawning scopes', undefined,
						'Scopes are spawned in the following constructs:',
						Element.ul(
							'<strong>Function call</strong>: a function body is evaluated in its own scope, arguments are set in this scope, the parent scope is the scope where the lambda was evaluated.',
							'<strong>Loops</strong>: loop bodies and conditions are evaluated in their own scope, the loop variable (for-loops) is set in this scope, the parent scope is the scope where the loop was initiated.',
							'<strong>Import</strong>: imported files are evaluated in their own scope, this scope has no parent.',
						)
					)
				)
			)
		];
	}
},
{
	title: 'Whitespace & comments',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Whitespaces', 'don\'t count',
						'Whitespaces always terminate a token.',
						'All whitespace characters are equivalent: there is no difference between a space and a newline.'
					)
				),
				Element.col(
					Element.card('default', 'Comments', undefined,
						Element.highlight('// this is a comment'),
						'Comments run until the end of the line.'
					)
				)
			)
		];
	}
},
{
	title: 'Identifiers & Variables',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Identifiers', undefined,
						Element.highlight('$[A-Z_a-z][0-9A-Z_a-z]*$'),
						'Identifiers are used in the following constructs:',
						Element.ul(
							'Variable names.',
							'Entry key in map constructor.',
							'Argument name in lambda expression.',
							'Argument name in call expression.',
							'Subscript when using the dot.'
						)
					)
				),
				Element.col(
					Element.card('default', 'Variables', undefined,
						Element.highlight('name'),
						'A variable is named with an identifier.',
						'The value returned is that of the innermost scope that contains a value for a variable of the specified name. If none is found, then undef.'
					)
				)	
			)
		];
	}
},
{
	title: 'Function call',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Function call', undefined,
						Element.highlight('$fun_expr$ ( $pos_args$ , $named_args$ )'),
						Help.placeholder('fun_expr', 'an expression evluated as a function'),
						Help.placeholder('pos_args', Element.highlight('$expr$ , $expr$ , $...$')),
						Help.placeholder('named_args', Element.highlight('name : $expr$ , name : $expr$ , $...$')),
						'Evaluates the body of the function with the specified argument.',
						'The positional arguments are first set to the variables in the function scope named after the function definition.',
						'Then named arguments are set in the function scope.'
					)
				)
			)
		];
	}
},
{
	title: 'Subscript',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Bracket form', undefined,
						Element.highlight('$container$ [ $sub$ ]'),
						'Evaluates <em>container</em> and <em>sub</em>, then returns the part of <em>container</em> specified by <em>sub</em>.',
						'The part depends on the type of <em>sub</em>:',
						Element.ul(
							'<strong>number</strong>: <em>container</em> must be a list. The subscript returns the elemet at the index specified by <em>sub</em> (starting at zero). If the index is negative, then the index is counted starting by the end. If the index is out of the list boundaries, then the subscript returns undef.',
							'<strong>string</strong>: <em>container</em> must be a map. The suscript is the entry with the specified name. If there is no entry with this name, then undef.',
							'<strong>list</strong>: returns a list of the same size, each element is the subscript result of the corresponding subsript item.',
							'<strong>map</strong>: returns a map with the same keys, each value is the subscript result of the corresponding subscript entry.'
						),
						'A subscript of type undef, boolean or function is an error.'
					)
				),
				Element.col(
					Element.card('default', 'Dot form', undefined,
						Element.highlight('$container$ . name'),
						'This is equivalent to:',
						Element.highlight('$container$ [ "name" ]')
					)
				)
			)
		]
	}
},
{
	title: 'Assignment',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Assign to variable', undefined,
						Element.highlight('name = $rvalue$'),
						'Evaluates <em>rvalue</em> and set the variable to the result.',
						'If the variable is already set in the current scope or in one of its parents, then the variable is overwritten. Otherwise the value is set to the variable in the current scope.'
					)
				),
				Element.col(
					Element.card('default', 'Assign to list constructor', undefined,
						Element.highlight('[ $expr$ , $expr$ , $...$ ] = $rvalue$'),
						'Evaluates <em>rvalue</em> as a list then assign each item of the result to the corresponding <em>expr</em> in the list constructor.'
					)
				),
				Element.col(
					Element.card('default', 'Assign to map constructor', undefined,
						Element.highlight('{ name : $expr$ , name : $expr$ , $...$ } = $rvalue$'),
						'Evaluates <em>rvalue</em> as a map then assign each entry of the result to the corresponding <em>expr</em> in the map constructor.'
					)
				)
			)
		]
	}
},
{
	title: 'Sequences', // soft/hard
	body: function() { return ''; }
},
{
	title: 'Conditional', // if then else
	body: function() { return ''; }
},
{
	title: 'For loops', // 3 forms 
	body: function() { return ''; }
},
{
	title: 'Repeat loops', // 3 forms, limit
	body: function() { return ''; }
},
{
	title: 'Boolean operators', // and or not
	body: function() { return ''; }
},
{
	title: 'Comparison', // numeric, general
	body: function() { return ''; }
},
{
	title: 'Arithmetic', // binary, unary
	body: function() { return ''; }
},
{
	title: 'Dice', // number, list, map, function, multiple, identifiers
	body: function() { return ''; }
},
{
	title: 'Draw', // single multiple
	body: function() { return ''; }
},
{
	title: 'Best', // single multiple selectors
	body: function() { return ''; }
},
{
	title: 'List tools', // count append reorder
	body: function() { return ''; }
},
{
	title: 'Range', // two directions
	body: function() { return ''; }
},
{
	title: 'Conversion', // conversion
	body: function() { return ''; }
},
{
	title: 'Output', // roll sample
	body: function() { return ''; }
},
{
	title: 'Import', // import
	body: function() { return ''; }
},
];






