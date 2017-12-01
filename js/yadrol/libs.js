class Libs {

}
Libs.libsContent = [
/*
{
	title: '',
	address: '',
	body: function() {
		return [
			Elements.row('help-row',
				Elements.col(
					Elements.card('default', '', undefined,
					)
				)
			)
		];
	}
}
*/
{
	title: 'Exploding & Imploding dice',
	address: 'lib/exploding.yadrol',
	body: function() {
		return [
			Elements.row('help-row',
				Elements.col(
					Elements.card('default', 'Exploding dice', undefined,
						Elements.highlight('dX4'),
						Elements.highlight('dX6'),
						Elements.highlight('dX8'),
						Elements.highlight('dX10'),
						Elements.highlight('dX12'),
						Elements.highlight('dX20'),
						'The <em>X-</em> functions roll an exploding die: if the die rolls the highest number, then it is rerolled.'
					)
				),
				Elements.col(
					Elements.card('default', 'Wild dice', undefined,
						Elements.highlight('dXX4'),
						Elements.highlight('dXX6'),
						Elements.highlight('dXX8'),
						Elements.highlight('dXX10'),
						Elements.highlight('dXX12'),
						Elements.highlight('dXX20'),
						'The <em>XX-</em> functions roll a wild die. Wild dice work the same way as exploding dice, though they keep rerolling while they show the highest possible value.'
					)
				),
				Elements.col(
					Elements.card('default', 'Exploding-imploding dice', undefined,
						Elements.highlight('dI4'),
						Elements.highlight('dI6'),
						Elements.highlight('dI8'),
						Elements.highlight('dI10'),
						Elements.highlight('dI12'),
						Elements.highlight('dI20'),
						'The <em>I-</em> functions roll an exploding-imploding die. Exploding-imploding dice work the same way as exploding dice, though they are also rerolled if they show a 1, the second result is then substracted to the total.'
					)
				),
			)
		];
	}
}
];