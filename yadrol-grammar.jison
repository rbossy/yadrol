%lex

%%

<<EOF>>		return 'EOF';
\s+		/* skip */
"#".*$		/* skip */
[-]{3,}		return 'BREAK';
";"		return 'SEMICOLON';
"import"	return 'IMPORT';
"output"	return 'OUTPUT';
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
"string"	return 'CONVERT';
"boolean"	return 'CONVERT';
"number"	return 'CONVERT';
"list"		return 'CONVERT';
"map"		return 'CONVERT';
"sorted"	return 'REORDER';
"reversed"	return 'REORDER';
"shuffled"	return 'REORDER';
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
\"(?:\\["\\ntr]|[^\n\t\r"\\])*\" return 'STRING';

/lex

%left BREAK
%left SEMICOLON
%nonassoc IMPORT OUTPUT AS 
%nonassoc ASSIGN
%nonassoc IF WHILE LIMIT FOR
%left OR
%left AND
%left NOT
%nonassoc GEN_COMP NUM_COMP
%nonassoc IN
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
: LPAREN expression RPAREN { $$ = $2; }
| UNDEF { $$ = new Constant(null, undefined); }
| BOOLEAN { $$ = new Constant(null, Boolean(yytext)); }
| STRING { $$ = new Constant(null, yytext.slice(1, yytext.length - 1)); }
| NUMBER { $$ = new Constant(null, Number(yytext)); }
| LBRACKET list RBRACKET { $$ = new ContainerConstructor(null, $2, 'list'); }
| LCURLY map RCURLY { $$ = new ContainerConstructor(null, new YadrolMap($2), 'map'); }
| FUN LPAREN lambdaArgs RPAREN LCURLY expression RCURLY { $$ = new Lambda(null, $3, $6); }
| IDENTIFIER { $$ = new Variable(null, yytext); }
| SCOPE { $$ = new ScopeVariables(null, ScopeVariables[yytext.toUpperCase()]); }
| expression DOT IDENTIFIER { $$ = new Subscript(null, $1, new Constant(null, $3)); }
| expression LBRACKET expression RBRACKET { $$ = new Subscript(null, $1, $3); }
| expression LPAREN callArgs RPAREN {
    var posArgs = [];
    while ($3.length > 0) {
      var a = $3.shift();
      if (a.length == 2) {
        $3.unshift(a);
	break;
      }
      posArgs.push(a[0]);
    }
    for (var a of $3) {
      if (a.length == 1) {
        throw new Error();
      }
    }
    return new Call(null, $1, posArgs, new YadrolMap($3));
  }
| COUNT expression { $$ = new Count(null, $2); }
| REORDER expression { $$ = new ListReorder(null, ListReorder[$1.toUpperCase()], $2); }
| CONVERT expression { $$ = new Convert(null, $2, $1); }
| expression DICE expression { $$ = new Dice(null, $1, $3); }
| expression DICE_UPPER { $$ = new Dice(null, $1, new Variable(null, $2.slice(1))); }
| expression DICE_NUMBER { $$ = new Dice(null, $1, new Constant(null, Number($2.slice(1)))); }
| UPPER_DICE expression { $$ = new Dice(null, new Variable(null, $1.slice(0, 1)), $2); }
| UPPER_DICE_UPPER { $$ = new Dice(null, new Variable(null, $1.slice(0, 1)), new Variable(null, $1.slice(2))); }
| UPPER_DICE_NUMBER { $$ = new Dice(null, new Variable(null, $1.slice(0, 1)), new Constant(null, Number($1.slice(2)))); }
| DICE expression { $$ = new Die(null, $2); }
| DICE_UPPER { $$ = new Die(null, new Variable(null, $1.slice(1))); }
| DICE_NUMBER { $$ = new Die(null, new Constant(null, Number($1.slice(1)))); }
| DRAW expression FROM expression { throw new Error('not implemented'); }
| DRAW FROM expression { throw new Error('not implemented: draw'); }
| BEST expression OF expression { $$ = new BestMultiple(null, Best[$1.toUpperCase()], $2, $4); }
| BEST OF expression { $$ = new Best(null, Best[$1.toUpperCase()], $3); }
| PLUS expression %prec SIGN { $$ = new Sign(null, Sign.getOperator($1), $2); }
| expression MULT expression { $$ = new Arithmetic(null, Arithmetic.getOperator($2), $1, $3); }
| expression PLUS expression { $$ = new Arithmetic(null, Arithmetic.getOperator($2), $1, $3); }
| expression RANGE expression { $$ = new Range(null, $1, $3); }
| expression APPEND expression { $$ = new Append(null, $1, $3); }
| expression GEN_COMP expression { $$ = new GeneralComparison(null, GeneralComparison.getOperator($2), $1, $3);  }
| expression NUM_COMP expression { $$ = new NumberComparison(null, NumberComparison.getOperator($2), $1, $3); }
| expression IN expression { $$ = new IndexOf(null, $1, $3); }
| NOT expression { $$ = new BooleanNot(null, $2); }
| expression AND expression { $$ = new BooleanAnd(null, $1, $3); }
| expression OR expression { $$ = new BooleanOr(null, $1, $3); }
| IF expression THEN expression ELSE expression { $$ = new Conditional(null, $2, $4, $6); }
| WHILE expression REPEAT expression { $$ = new Repeat(null, $4, $2, true, Number.MAX_VALUE); }
| WHILE expression REPEAT expression LIMIT NUMBER { $$ = new Repeat(null, $4, $2, true, Number($6)); }
| REPEAT expression WHILE expression { $$ = Repeat(null, $2, $4, false, Number.MAX_VALUE); }
| REPEAT expression WHILE expression LIMIT NUMBER { $$ = Repeat(null, $2, $4, false, Number($6)); }
| REPEAT expression IF expression { $$ = Repeat(null, $2, $4, false, 1); }
| FOR loopVars IN expression { $$ = new ForLoop(null, $2[0], $2[1], new Variable(null, $2[1]), $4, new Constant(null, true)); }
| FOR loopVars IN expression IF expression { $$ = new ForLoop(null, $2[0], $2[1], new Variable(null, $2[1]), $4, $6); }
| expression FOR loopVars IN expression { $$ = new ForLoop(null, $3[0], $3[1], $1, $5, new Constant(null, true)); }
| expression FOR loopVars IN expression IF expression { $$ = new ForLoop(null, $3[0], $3[1], $1, $5, $7); }
| expression ASSIGN expression { $$ = new Assign(null, $1, $3); }
| IMPORT STRING { throw new Error('not implemented: import'); }
| IMPORT IDENTIFIER ASSIGN STRING { throw new Error('not implemented: import'); }
| OUTPUT expression { throw new Error('not implemented: output'); }
| OUTPUT expression AS CONVERT { throw new Error('not implemented: output'); }
| OUTPUT expression AS STRING { throw new Error('not implemented: output'); }
| OUTPUT expression AS CONVERT STRING { throw new Error('not implemented: output'); }
| OUTPUT expression AS STRING CONVERT { throw new Error('not implemented: output'); }
| expression semicolon expression %prec SEMICOLON { $$ = new Sequence(null, $1, $3); }
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
: { $$ = new Constant(null, undefined); }
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

loopVars
: IDENTIFIER { $$ = [undefined, $1]; }
| IDENTIFIER COMMA IDENTIFIER { $$ = [$1, $3]; }
;

semicolon
: SEMICOLON
| SEMICOLON semicolon
;

optSemicolon
:
| SEMICOLON optSemicolon
;
