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
				Element.col(
					Element.card('default', 'Difficulty', 'Difficulties and Ranges',
						Element.highlight('Difficulty.Easy'),
						Element.highlight('Difficulty.Moderate'),
						Element.highlight('Difficulty.Hard'),
						Element.highlight('Difficulty.Formidable'),
						Element.highlight('Difficulty.Mighty'),
						Element.highlight('Difficulty.Thongorean'),
						Element.highlight('Range.PointBlank'),
						Element.highlight('Range.Close'),
						Element.highlight('Range.Medium'),
						Element.highlight('Range.Long'),
						Element.highlight('Range.Distant'),
						Element.highlight('Range.Extreme'),
						'These are mnemonics for standard difficulties and ranges. Wherever a difficulty is required, these mnemonic hold appropriate number values.'
					)
				),
				Element.col(
					Element.card('default', 'Weapons', undefined,
						Element.highlight('Weapon.Fist'),
						Element.highlight('Weapon.Dagger'),
						Element.highlight('Weapon.Sword'),
						Element.highlight('Weapon.Axe'),
						Element.highlight('Weapon.Club'),
						Element.highlight('Weapon.Mace'),
						Element.highlight('Weapon.Spear'),
						Element.highlight('Weapon.Flail'),
						Element.highlight('Weapon.ValkarthanSword'),
						Element.highlight('Weapon.GreatAxe'),
						Element.highlight('Weapon.Staff'),
						Element.highlight('Weapon.Sling'),
						Element.highlight('Weapon.ShortBow'),
						Element.highlight('Weapon.Bow'),
						Element.highlight('Weapon.Crossbow'),
						Element.highlight('Weapon.Warbow'),
						'These mnemonics are functions that emulate weapon damages. Wherever a weapon damage is required, these mnemonics hold appropriate functions.'
					)
				)
			),
			Element.row('help-row',
				Element.col(
					Element.card('default', 'Action resilution', undefined,
						Element.highlight('Resolve($Ability$, $Difficulty$)'),
						'The <span class="cm-IDENTIFIER">Resolve</span> function rolls the appropriate dice to resolve an action, then returns a boolean indicating the success of the action.',
						'The parameters <em>Ability</em> and <em>Difficulty</em> represent respectively the score of the character in the appropriate ability, and the difficulty of the action.',
						'Difficulty or range mnemonics can be used for <em>Difficulty</em>. If <em>Difficulty</em> is omitted, then the default is a Moderate difficulty.'
					)
				),
				Element.col(
					Element.card('default', 'Combat strike', undefined,
						Element.highlight('Attack($Melee$, $Defense$, $Damage$, $Difficulty$)'),
						'The <span class="cm-IDENTIFIER">Attack</span> function simulates a strike from a character, then returns the amount of damage (zero if the strike fails).',
						'The <em>Melee</em> and <em>Defense</em> parameters represent respectively the score of the character in Melee (or Shoot) end the score of the opponent in Defense.',
						'The <em>Weapon</em> parameter should be a function that returns the amount of damage if the strike succeeds. By default the character strikes with a sword.',
						'The <em>Difficulty</em> parameter represents the difficulty, by default it is Moderate.'
					)
				)
			)
		];
	}
},
];