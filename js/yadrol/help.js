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
	title: 'Data types, literals and costructors', // undef, bool, str, num, list, map, func
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Undef', undefined, 'Undef type has a single value: <em>undef</em>.')
				),
				Element.col(
					Element.card('default', 'Boolean', undefined, 'Boolean type has two values: <em>false</em> and <em>true</em>.')
				),
				Element.col(
					Element.card('default', 'String', undefined, 'String values are sequence of Unicode characters.')
				),
				Element.col(
					Element.card('default', 'Number', undefined, 'In Yadrol, numbers are signed integer values.')
				),
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'List', undefined, 'Lists are ordered collections of values of any type.')
				),
				Element.col(
					Element.card('default', 'Map', undefined, 'Maps are ordered collections of named values of any type.', 'In a map, entry keys are unique.')
				),
				Element.col(
					Element.card('default', 'Function', undefined, 'Functions are expressions that can be called and evaluated with a specific set of parameters.')
				)
			)
		];
	}
},
{
	title: 'Data conversion', // table
	body: function() { return ''; }
},
{
	title: 'Operator precedence', // table
	body: function() { return ''; }
},
{
	title: 'Scoping', // Dynamic, parent scope, spawning
	body: function() { return ''; }
},
{
	title: 'Comments & whitespace', // ok
	body: function() { return ''; }
},
{
	title: 'Literals', // undef, bool, str, num
	body: function() { return ''; }
},
{
	title: 'Constructors', // list, map
	body: function() { return ''; }
},
{
	title: 'Variables & Scopes', // identifier, scopes
	body: function() { return ''; }
},
{
	title: 'Lambda & function call', // lambda, call
	body: function() { return ''; }
},
{
	title: 'Subscript',
	body: function() { return ''; }
},
{
	title: 'Assignment', // too var, to list, to map, to subscript
	body: function() { return ''; }
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






