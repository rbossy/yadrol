class Libs {
	static init() {
		var libraries = $('#libraries');
		for (var e of Libs.libsContent.entries()) {
			var index = e[0];
			var lib = e[1];
			libraries.append(
				$('<span class="dropdown-item" onclick="Libs.import('+index+')"></span>').text(lib.title)
			);
		}
	}

	static import(index) {
		var lib = Libs.libsContent[index];
		var e = new Import(Location.NONE, new Constant(Location.NONE, lib.address), undefined, yadrolApp.recordLogger);
		e.evaluate(yadrolApp.globalScope);
		Help.tableofcontents('Libraries', Libs.libsContent);
		Help.page(index);
		Alert.alert('success', 'Library <em>' + lib.title + '</em>&nbsp;successfully imported, help available below.')
	}
};
Libs.libsContent = [
/*
{
	title: '',
	address: '',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', '', undefined,
					)
				),
			),
		];
	}
},
*/
{
	title: 'Exploding & Imploding dice',
	address: 'lib/exploding.yadrol',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Exploding dice', undefined,
						Element.highlight('dX4'),
						Element.highlight('dX6'),
						Element.highlight('dX8'),
						Element.highlight('dX10'),
						Element.highlight('dX12'),
						Element.highlight('dX20'),
						'The <em>X-</em> functions roll an exploding die: if the die rolls the highest number, then it is rerolled.'
					)
				),
				Element.col(
					Element.card('default', 'Wild dice', undefined,
						Element.highlight('dXX4'),
						Element.highlight('dXX6'),
						Element.highlight('dXX8'),
						Element.highlight('dXX10'),
						Element.highlight('dXX12'),
						Element.highlight('dXX20'),
						'The <em>XX-</em> functions roll a wild die. Wild dice work the same way as exploding dice, though they keep rerolling while they show the highest possible value.'
					)
				),
				Element.col(
					Element.card('default', 'Exploding-imploding dice', undefined,
						Element.highlight('dI4'),
						Element.highlight('dI6'),
						Element.highlight('dI8'),
						Element.highlight('dI10'),
						Element.highlight('dI12'),
						Element.highlight('dI20'),
						'The <em>I-</em> functions roll an exploding-imploding die. Exploding-imploding dice work the same way as exploding dice, though they are also rerolled if they show a 1, the second result is then substracted to the total.'
					)
				),
			)
		];
	}
},
{
	title: 'Barbarians of Lemuria',
	address: 'lib/BoL.yadrol',
	body: function() {
		return [
			Element.row('help-row',
				Element.col(
					Element.card('default', 'About', undefined,
						'Barbarians of Lemuria is an excellent Sword & Sorcery game by Simon Washbourne with a simple resolution mechanics.',
						'This library offers functions for resolving actions and combat turns, and useful mnemonics for difficulties and weapons damage.'
					)
				),
			)
		];
	}
},
];