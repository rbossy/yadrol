lib/yadrol/parser.js: yadrol-grammar.jison
	jison $< -o $@
