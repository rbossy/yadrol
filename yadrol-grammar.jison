%lex
%options ranges
%x string

%%

<<EOF>>		return 'EOF';
\s+		/* skip */
"//".*\n	/* skip */
"//".*<<EOF>>  /* skip */
\$[^\$]+\$ return 'PLACEHOLDER';
[-]{3,}		return 'BREAK';
";"		return 'SEMICOLON';
"import"	return 'IMPORT';
"sample"	return 'OUTPUT';
"roll"		return 'OUTPUT';
"as"		return 'AS';
"if"		return 'IF';
"then"		return 'THEN';
"else"		return 'ELSE';
"for"		return 'FOR';
"repeat"	return 'REPEAT';
"while"		return 'WHILE';
"limit"		return 'LIMIT';
"in"		return 'IN';
"count"		return 'COUNT';
"#"       return 'COUNT';
"string"	return 'CONVERT';
"boolean"	return 'CONVERT';
"number"	return 'CONVERT';
"list"		return 'CONVERT';
"map"		return 'CONVERT';
"sort"	return 'REORDER';
"reverse"	return 'REORDER';
"shuffle"	return 'REORDER';
"or"		return 'OR';
"and"		return 'AND';
"not"		return 'NOT';
"<<"		return 'APPEND';
"==="		return 'GEN_COMP';
"!=="		return 'GEN_COMP';
"=="		return 'NUM_COMP';
"!="		return 'NUM_COMP';
"<="		return 'NUM_COMP';
">="		return 'NUM_COMP';
"<"		return 'NUM_COMP';
">"		return 'NUM_COMP';
".."		return 'RANGE';
"+"		return 'PLUS';
"-"		return 'PLUS';
"*"		return 'MULT';
"/"		return 'MULT';
"%"		return 'MULT';
"="		return 'ASSIGN';
"highest"	return 'BEST';
"lowest"	return 'BEST';
"first"		return 'BEST';
"last"		return 'BEST';
"of"		return 'OF';
"draw"		return 'DRAW';
"from"		return 'FROM';
"d"		return 'DICE';
[A-Z]"d"	return 'UPPER_DICE';
"d"[A-Z]\w*	return 'DICE_UPPER';
[A-Z]"d"[A-Z]\w* return 'UPPER_DICE_UPPER';
"d"\d+		return 'DICE_NUMBER';
[A-Z]"d"\d+	return 'UPPER_DICE_NUMBER';
"("		return 'LPAREN';
")"		return 'RPAREN';
"["		return 'LBRACKET';
"]"		return 'RBRACKET';
","		return 'COMMA';
":"		return 'COLON';
"."		return 'DOT';
"local"		return 'SCOPE';
"outer"		return 'SCOPE';
"global"	return 'SCOPE';
"undef"		return 'UNDEF';
"false"		return 'BOOLEAN';
"true"		return 'BOOLEAN';
[0-9]+		return 'NUMBER';
"fun"		return 'FUN';
'{'		return 'LCURLY';
'}'		return 'RCURLY';
[A-Z_a-z]\w*	return 'IDENTIFIER';
"\""		this.begin('string'); return 'STR_START';
<string><<EOF>>	throw new Error('unterminated string literal');
<string>"\""    this.popState(); return 'STR_END';
<string>\\n     return 'STR_NL';
<string>\\\"    return 'STR_DQ';
<string>[^\\\"]* return 'STR_CONST';

/lex

%left BREAK
%left SEMICOLON
%nonassoc IMPORT OUTPUT
%nonassoc ASSIGN
%nonassoc IF WHILE LIMIT FOR
%left OR
%left AND
%left NOT
%nonassoc GEN_COMP NUM_COMP
%left IN
%nonassoc APPEND
%nonassoc RANGE
%left PLUS
%left MULT
%nonassoc SIGN
%nonassoc BEST
%nonassoc DRAW
%left DICE DICE_UPPER UPPER_DICE DICE_NUMBER
%nonassoc COUNT REORDER CONVERT
%left LPAREN LBRACKET DOT

%start top

%%

top
: expressionList EOF { return $1; }
;

expressionList
: expression optSemicolon { $$ = [$1]; }
| expression optSemicolon BREAK expressionList { $4.unshift($1); $$ = $4; }
;

expression
: LPAREN expression RPAREN
  { $$ = $2; }

| UNDEF
  { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1, @1), undefined); }

| BOOLEAN
  { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1, @1), (yytext == 'true')); }

| STR_START STR_END
  { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1, @1), ''); }

| STR_START string STR_END
  { $$ = new StringInterpolation(Location.fromLexer(yy.sourceFile, @1, @3), $2); }

| NUMBER
  { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1, @1), Number(yytext)); }

| LBRACKET list RBRACKET
  { $$ = new ContainerConstructor(Location.fromLexer(yy.sourceFile, @1, @3), $2, 'list'); }

| LCURLY map RCURLY
  { $$ = new ContainerConstructor(Location.fromLexer(yy.sourceFile, @1, @3), new YadrolMap($2), 'map'); }

| FUN LPAREN lambdaArgs RPAREN LCURLY expression RCURLY
  { $$ = new Lambda(Location.fromLexer(yy.sourceFile, @1, @7), new YadrolMap($3), $6); }

| IDENTIFIER
  { $$ = new Variable(Location.fromLexer(yy.sourceFile, @1, @1), yytext); }

| SCOPE
  { $$ = new ScopeVariables(Location.fromLexer(yy.sourceFile, @1, @1), ScopeVariables[yytext.toUpperCase()]); }

| expression DOT IDENTIFIER
  { $$ = new Subscript(Location.fromLexer(yy.sourceFile, @1, @3), $1, new Constant(Location.fromLexer(yy.sourceFile, @3, @3), $3)); }

| expression LBRACKET expression RBRACKET
  { $$ = new Subscript(Location.fromLexer(yy.sourceFile, @1, @4), $1, $3); }

| expression LPAREN callArgs RPAREN
  { var posArgs = yy.extractPositionalArgs($3); $$ = new Call(Location.fromLexer(yy.sourceFile, @1, @4), $1, posArgs, new YadrolMap($3)); }

| COUNT expression
  { $$ = new Count(Location.fromLexer(yy.sourceFile, @1, @2), $2); }

| REORDER expression
  { $$ = new ListReorder(Location.fromLexer(yy.sourceFile, @1, @2), ListReorder[$1.toUpperCase()], $2); }

| CONVERT expression
  { $$ = new Convert(Location.fromLexer(yy.sourceFile, @1, @2), $2, $1); }

| expression DICE expression
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3, yy.recordLogger); }

| expression DICE_UPPER
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @2), $1, new Variable(Location.fromLexer(yy.sourceFile, @2, @2), $2.slice(1)), yy.recordLogger); }

| expression DICE_NUMBER
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @2), $1, new Constant(Location.fromLexer(yy.sourceFile, @2, @2), Number($2.slice(1))), yy.recordLogger); }

| UPPER_DICE expression
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @2), new Variable(Location.fromLexer(yy.sourceFile, @1, @1), $1.slice(0, 1)), $2, yy.recordLogger); }

| UPPER_DICE_UPPER
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @1), new Variable(Location.fromLexer(yy.sourceFile, @1, @1), $1.slice(0, 1)), new Variable(Location.fromLexer(yy.sourceFile, @1, @1), $1.slice(2)), yy.recordLogger); }

| UPPER_DICE_NUMBER
  { $$ = new Dice(Location.fromLexer(yy.sourceFile, @1, @1), new Variable(Location.fromLexer(yy.sourceFile, @1, @1), $1.slice(0, 1)), new Constant(Location.fromLexer(yy.sourceFile, @1, @1), Number($1.slice(2))), yy.recordLogger); }

| DICE expression
  { $$ = new Die(Location.fromLexer(yy.sourceFile, @1, @2), $2, yy.recordLogger); }

| DICE_UPPER
  { $$ = new Die(Location.fromLexer(yy.sourceFile, @1, @1), new Variable(Location.fromLexer(yy.sourceFile, @1, @1), $1.slice(1)), yy.recordLogger); }

| DICE_NUMBER
  { $$ = new Die(Location.fromLexer(yy.sourceFile, @1, @1), new Constant(Location.fromLexer(yy.sourceFile, @1, @1), Number($1.slice(1))), yy.recordLogger); }

| DRAW expression FROM expression
  { $$ = new DrawMultiple(Location.fromLexer(yy.sourceFile, @1, @4), $2, $4); }

| DRAW FROM expression
  { $$ = new Draw(Location.fromLexer(yy.sourceFile, @1, @3), $3); }

| BEST expression OF expression
  { $$ = new BestMultiple(Location.fromLexer(yy.sourceFile, @1, @4), Best[$1.toUpperCase()], $2, $4); }

| BEST OF expression
  { $$ = new Best(Location.fromLexer(yy.sourceFile, @1, @3), Best[$1.toUpperCase()], $3); }

| PLUS expression %prec SIGN
  { $$ = new Sign(Location.fromLexer(yy.sourceFile, @1, @2), yy.getSignOperator($1), $2); }

| expression MULT expression
  { $$ = new Arithmetic(Location.fromLexer(yy.sourceFile, @1, @3), yy.getArithmeticOperator($2), $1, $3); }

| expression PLUS expression
  { $$ = new Arithmetic(Location.fromLexer(yy.sourceFile, @1, @3), yy.getArithmeticOperator($2), $1, $3); }

| expression RANGE expression
  { $$ = new Range(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| expression APPEND expression
  { $$ = new Append(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| expression GEN_COMP expression
  { $$ = new GeneralComparison(Location.fromLexer(yy.sourceFile, @1, @3), yy.getGeneralComparisonOperator($2), $1, $3);  }

| expression NUM_COMP expression
  { $$ = new NumberComparison(Location.fromLexer(yy.sourceFile, @1, @3), yy.getNumberComparisonOperator($2), $1, $3); }

| expression IN expression
  { $$ = new IndexOf(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| NOT expression
  { $$ = new BooleanNot(Location.fromLexer(yy.sourceFile, @1, @2), $2); }

| expression AND expression
  { $$ = new BooleanAnd(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| expression OR expression
  { $$ = new BooleanOr(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| IF expression THEN expression ELSE expression
  { $$ = new Conditional(Location.fromLexer(yy.sourceFile, @1, @6), $2, $4, $6); }

| WHILE expression REPEAT expression limit
  { $$ = new Repeat(Location.fromLexer(yy.sourceFile, @1, @5), $4, $2, true, $5); }

| REPEAT expression WHILE expression limit
  { $$ = new Repeat(Location.fromLexer(yy.sourceFile, @1, @5), $2, $4, false, $5); }

| REPEAT expression IF expression
  { $$ = new Repeat(Location.fromLexer(yy.sourceFile, @1, @4), $2, $4, false, 1); }

| FOR loopVars IN expression IF expression
  { $$ = new ForLoop(Location.fromLexer(yy.sourceFile, @1, @6), $2[0], $2[1], new Variable(Location.fromLexer(yy.sourceFile, @2, @2), $2[1]), $4, $6); }

| expression FOR loopVars IN expression forLoopCondition
  { $$ = new ForLoop(Location.fromLexer(yy.sourceFile, @1, @6), $3[0], $3[1], $1, $5, $6); }

| expression ASSIGN expression
  { $$ = new Assign(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }

| IMPORT STR_START string STR_END
  { $$ = new Import(Location.fromLexer(yy.sourceFile, @1, @2), new StringInterpolation(Location.fromLexer(yy.sourceFile, @2, @4), $3), undefined, yy.recordLogger); }

| IMPORT IDENTIFIER ASSIGN STR_START string STR_END
  { $$ = new Import(Location.fromLexer(yy.sourceFile, @1, @4), new StringInterpolation(Location.fromLexer(yy.sourceFile, @4, @6), $5), $2, yy.recordLogger); }

| OUTPUT expression
  { $$ = new Output(Location.fromLexer(yy.sourceFile, @1, @2), undefined, $2, yy.recordLogger.defaultType, Output[$1.toUpperCase()], yy.recordLogger); }

| OUTPUT expression AS CONVERT
  { $$ = new Output(Location.fromLexer(yy.sourceFile, @1, @4), undefined, $2, $4, Output[$1.toUpperCase()], yy.recordLogger); }

| OUTPUT expression AS STR_START string STR_END
  { $$ = new Output(Location.fromLexer(yy.sourceFile, @1, @6), new StringInterpolation(Location.fromLexer(yy.sourceFile, @1, @5), $5), $2, yy.recordLogger.defaultType, Output[$1.toUpperCase()], yy.recordLogger); }

| OUTPUT expression AS CONVERT STR_START string STR_END
  { $$ = new Output(Location.fromLexer(yy.sourceFile, @1, @7), new StringInterpolation(Location.fromLexer(yy.sourceFile, @1, @6), $6), $2, $4, Output[$1.toUpperCase()], yy.recordLogger); }

| OUTPUT expression AS STR_START string STR_END CONVERT
  { $$ = new Output(Location.fromLexer(yy.sourceFile, @1, @7), new StringInterpolation(Location.fromLexer(yy.sourceFile, @1, @4), $4), $2, $6, Output[$1.toUpperCase()], yy.recordLogger); }

| expression semicolon expression %prec SEMICOLON
  { $$ = new Sequence(Location.fromLexer(yy.sourceFile, @1, @3), $1, $3); }
;

list
: { $$ = []; }
| expression { $$ = [$1]; }
| expression COMMA list { $3.unshift($1); $$ = $3; }
;

map
: { $$ = []; }
| mapEntry { $$ = [$1]; }
| mapEntry COMMA map { $3.unshift($1); $$ = $3; }
;

mapEntry
: IDENTIFIER COLON expression { $$ = [ $1, $3 ]; }
;

lambdaArgs
: { $$ = []; }
| lambdaArg { $$ = [$1]; }
| lambdaArg COMMA lambdaArgs { $3.unshift($1); $$ = $3; }
;

lambdaArg
: IDENTIFIER lambdaArgValue { $$ = [$1, $2]; }
;

lambdaArgValue
: { $$ = new Constant(Location.NONE, undefined); }
| COLON expression { $$ = $2; }
;

callArgs
: { $$ = []; }
| callArg { $$ = [$1]; }
| callArg COMMA callArgs { $3.unshift($1); $$ = $3; }
;

callArg
: IDENTIFIER COLON expression { $$ = [$1, $3]; }
| expression { $$ = [$1]; }
;

limit
: { $$ = Number.MAX_VALUE; }
| LIMIT NUMBER { $$ = Number($2); }
;

loopVars
: IDENTIFIER { $$ = [undefined, $1]; }
| IDENTIFIER COMMA IDENTIFIER { $$ = [$1, $3]; }
;

forLoopCondition
: { $$ = new Constant(Location.NONE, true) }
| IF expression { $$ = $2; }
;

semicolon
: SEMICOLON
| SEMICOLON semicolon
;

optSemicolon
:
| SEMICOLON optSemicolon
;

string
: stringElement { $$ = [$1]; }
| stringElement string { $2.unshift($1); $$ = $2; }
;

stringElement
: STR_NL { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1), '\n'); }
| STR_DQ { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1), '"'); }
| STR_CONST { $$ = new Constant(Location.fromLexer(yy.sourceFile, @1), $1); }
;

placeholder
: PLACEHOLDER
;
