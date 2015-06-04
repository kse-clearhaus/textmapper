language templ1(java);

prefix = "Templ1"
breaks = true
gentree = true
genast = false
positions = "line,offset"
endpositions = "offset"

:: lexer

identifier(String): /[a-zA-Z_][a-zA-Z_0-9]*/ (class)  { $$ = tokenText(); }
icon(Integer):  /-?[0-9]+/                            { $$ = Integer.parseInt(tokenText()); }
scon(String):    /"([^\n\\"]|\\.)*"/                   { $$ = unescape(tokenText(), 1, tokenSize() - 1); }

# soft keyword

kw_object: /object/      (soft)

skip: /[\n\t\r ]+/  (space)
comment:  /#.*(\r?\n)?/ (space)

'=':        /=/
':':        /:/
'(':        /\(/
')':        /\)/

:: parser

input ::=
	  (identifier '=' object)+
;

object ::=
	  kw_object ('(' icon  ':' scon ')')?
;

%param T string;

list<T> ::=  T ;

%%

${template java_lexer.lexercode}
private String unescape(String s, int start, int end) {
	StringBuilder sb = new StringBuilder();
	end = Math.min(end, s.length());
	for (int i = start; i < end; i++) {
		char c = s.charAt(i);
		if (c == '\\') {
			if (++i == end) {
				break;
			}
			c = s.charAt(i);
			if (c == 'u' || c == 'x') {
				// FIXME process unicode
			} else if (c == 'n') {
				sb.append('\n');
			} else if (c == 'r') {
				sb.append('\r');
			} else if (c == 't') {
				sb.append('\t');
			} else {
				sb.append(c);
			}
		} else {
			sb.append(c);
		}
	}
	return sb.toString();
}
${end}
