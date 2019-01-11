// generated by Textmapper; DO NOT EDIT

package tm

import (
	"fmt"
)

// Token is an enum of all terminal symbols of the tm language.
type Token int

// Token values.
const (
	UNAVAILABLE Token = iota - 1
	EOI
	INVALID_TOKEN
	SCON
	ICON
	WHITESPACE
	COMMENT
	MULTILINECOMMENT
	REM               // %
	COLONCOLON        // ::
	OR                // |
	OROR              // ||
	ASSIGN            // =
	ASSIGNASSIGN      // ==
	EXCLASSIGN        // !=
	SEMICOLON         // ;
	DOT               // .
	COMMA             // ,
	COLON             // :
	LBRACK            // [
	RBRACK            // ]
	LPAREN            // (
	LPARENQUESTASSIGN // (?=
	MINUSGT           // ->
	RPAREN            // )
	RBRACE            // }
	LT                // <
	GT                // >
	MULT              // *
	PLUS              // +
	PLUSASSIGN        // +=
	QUEST             // ?
	EXCL              // !
	TILDE             // ~
	AND               // &
	ANDAND            // &&
	DOLLAR            // $
	ATSIGN            // @
	ERROR
	ID
	AS         // as
	FALSE      // false
	IMPLEMENTS // implements
	IMPORT     // import
	SEPARATOR  // separator
	SET        // set
	TRUE       // true
	ASSERT     // assert
	BRACKETS   // brackets
	CLASS      // class
	EMPTY      // empty
	EXPLICIT   // explicit
	FLAG       // flag
	GENERATE   // generate
	GLOBAL     // global
	INLINE     // inline
	INPUT      // input
	INTERFACE  // interface
	LALR       // lalr
	LANGUAGE   // language
	LAYOUT     // layout
	LEFT       // left
	LEXER      // lexer
	LOOKAHEAD  // lookahead
	NOMINUSEOI // no-eoi
	NONASSOC   // nonassoc
	NONEMPTY   // nonempty
	PARAM      // param
	PARSER     // parser
	PREC       // prec
	RETURNS    // returns
	RIGHT      // right
	CHAR_S     // s
	SHIFT      // shift
	SOFT       // soft
	SPACE      // space
	VOID       // void
	CHAR_X     // x
	CODE       // {
	LBRACE     // {
	REGEXP
	DIV // /

	NumTokens
)

var tokenStr = [...]string{
	"EOI",
	"INVALID_TOKEN",
	"SCON",
	"ICON",
	"WHITESPACE",
	"COMMENT",
	"MULTILINECOMMENT",
	"%",
	"::",
	"|",
	"||",
	"=",
	"==",
	"!=",
	";",
	".",
	",",
	":",
	"[",
	"]",
	"(",
	"(?=",
	"->",
	")",
	"}",
	"<",
	">",
	"*",
	"+",
	"+=",
	"?",
	"!",
	"~",
	"&",
	"&&",
	"$",
	"@",
	"ERROR",
	"ID",
	"as",
	"false",
	"implements",
	"import",
	"separator",
	"set",
	"true",
	"assert",
	"brackets",
	"class",
	"empty",
	"explicit",
	"flag",
	"generate",
	"global",
	"inline",
	"input",
	"interface",
	"lalr",
	"language",
	"layout",
	"left",
	"lexer",
	"lookahead",
	"no-eoi",
	"nonassoc",
	"nonempty",
	"param",
	"parser",
	"prec",
	"returns",
	"right",
	"s",
	"shift",
	"soft",
	"space",
	"void",
	"x",
	"{",
	"{",
	"REGEXP",
	"/",
}

func (tok Token) String() string {
	if tok >= 0 && int(tok) < len(tokenStr) {
		return tokenStr[tok]
	}
	return fmt.Sprintf("token(%d)", tok)
}
