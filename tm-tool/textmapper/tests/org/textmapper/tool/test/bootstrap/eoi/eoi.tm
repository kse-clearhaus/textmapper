language eoi(java);

prefix = "Eoi"
package = "org.textmapper.tool.test.bootstrap.eoi"
breaks = true
gentree = true
genast = false
positions = "line,offset"
endpositions = "offset"

:: lexer

[initial, a, b]

id: /[a-zA-Z_]+/
':':        /:/
';':        /;/
',':        /,/

_skip: /[\n\t\r ]+/  (space)

[initial]
'(':        /\(/  => a
')':        /\)/
_customEOI:       /{eoi}/  (space) 		{ if (--eoiToGo == 0) { lapg_n.symbol = Tokens.eoi; } }

[a]
'(':        /\(/  => b
')':        /\)/  => initial
_retfromA:       /{eoi}/  => initial (space)

[b]
'(':        /\(/
')':        /\)/  => a
_retfromB:       /{eoi}/  => a (space)

:: parser

input ::=
	  expr
;

expr ::=
	  id
	| '(' (id ':' expr separator ',')* ';' ')'?
;

%%

${template java_lexer.lexercode}
private int eoiToGo = 5;
${end}