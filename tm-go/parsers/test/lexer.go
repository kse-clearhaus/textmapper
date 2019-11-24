// generated by Textmapper; DO NOT EDIT

package test

import (
	"strconv"
	"strings"
	"unicode/utf8"
)

// Lexer states.
const (
	StateInitial     = 0
	StateInMultiLine = 1
)

// Lexer uses a generated DFA to scan through a utf-8 encoded input string. If
// the string starts with a BOM character, it gets skipped.
type Lexer struct {
	source string

	ch          rune // current character, -1 means EOI
	offset      int  // character offset
	tokenOffset int  // last token offset
	scanOffset  int  // scanning offset
	value       interface{}

	State int // lexer state, modifiable
}

var bomSeq = "\xef\xbb\xbf"

// Init prepares the lexer l to tokenize source by performing the full reset
// of the internal state.
func (l *Lexer) Init(source string) {
	l.source = source

	l.ch = 0
	l.offset = 0
	l.tokenOffset = 0
	l.State = 0

	if strings.HasPrefix(source, bomSeq) {
		l.offset += len(bomSeq)
	}

	l.rewind(l.offset)
}

// Next finds and returns the next token in l.source. The source end is
// indicated by Token.EOI.
//
// The token text can be retrieved later by calling the Text() method.
func (l *Lexer) Next() Token {
	var commentOffset, commentDepth int
restart:
	l.tokenOffset = l.offset

	state := tmStateMap[l.State]
	hash := uint32(0)
	backupRule := -1
	var backupOffset int
	backupHash := hash
	for state >= 0 {
		var ch int
		if uint(l.ch) < tmRuneClassLen {
			ch = int(tmRuneClass[l.ch])
		} else if l.ch < 0 {
			state = int(tmLexerAction[state*tmNumClasses])
			continue
		} else {
			ch = mapRune(l.ch)
		}
		state = int(tmLexerAction[state*tmNumClasses+ch])
		if state > tmFirstRule {
			if state < 0 {
				state = (-1 - state) * 2
				backupRule = tmBacktracking[state]
				backupOffset = l.offset
				backupHash = hash
				state = tmBacktracking[state+1]
			}
			hash = hash*uint32(31) + uint32(l.ch)

			// Scan the next character.
			// Note: the following code is inlined to avoid performance implications.
			l.offset = l.scanOffset
			if l.offset < len(l.source) {
				r, w := rune(l.source[l.offset]), 1
				if r >= 0x80 {
					// not ASCII
					r, w = utf8.DecodeRuneInString(l.source[l.offset:])
				}
				l.scanOffset += w
				l.ch = r
			} else {
				l.ch = -1 // EOI
			}
		}
	}

	rule := tmFirstRule - state
recovered:
	switch rule {
	case 4:
		hh := hash & 7
		switch hh {
		case 0:
			if hash == 0x5b098c8 && "decl2" == l.source[l.tokenOffset:l.offset] {
				rule = 8
				break
			}
		case 2:
			if hash == 0x364492 && "test" == l.source[l.tokenOffset:l.offset] {
				rule = 6
				break
			}
		case 7:
			if hash == 0x5b098c7 && "decl1" == l.source[l.tokenOffset:l.offset] {
				rule = 7
				break
			}
		}
	}

	token := tmToken[rule]
	space := false
	switch rule {
	case 0:
		if backupRule >= 0 {
			rule = backupRule
			hash = backupHash
			l.rewind(backupOffset)
		} else if l.offset == l.tokenOffset {
			l.rewind(l.scanOffset)
		}
		if rule != 0 {
			goto recovered
		}
	case 2: // WhiteSpace: /[ \t\r\n]/
		space = true
	case 5: // IntegerConstant: /[0-9]+/
		{
			l.value = mustParseInt(l.Text())
		}
	case 21: // MultiLineComment: /\/\*/
		{
			l.State = StateInMultiLine
			commentOffset = l.tokenOffset
			commentDepth = 0
			space = true
		}
	case 22: // invalid_token: /{eoi}/
		{
			l.tokenOffset = commentOffset
			l.State = StateInitial
		}
	case 23: // MultiLineComment: /\/\*/
		{
			commentDepth++
			space = true
		}
	case 24: // MultiLineComment: /\*\//
		{
			if commentDepth == 0 {
				space = false
				l.tokenOffset = commentOffset
				l.State = StateInitial
				break
			}
			space = true
			commentDepth--
		}
	case 25: // WhiteSpace: /[^\/*]+|[*\/]/
		space = true
		{
			space = true
		}
	}
	if space {
		goto restart
	}
	return token
}

// Pos returns the start and end positions of the last token returned by Next().
func (l *Lexer) Pos() (start, end int) {
	start = l.tokenOffset
	end = l.offset
	return
}

// Text returns the substring of the input corresponding to the last token.
func (l *Lexer) Text() string {
	return l.source[l.tokenOffset:l.offset]
}

// Value returns the value associated with the last returned token.
func (l *Lexer) Value() interface{} {
	return l.value
}

// rewind can be used in lexer actions to accept a portion of a scanned token, or to include
// more text into it.
func (l *Lexer) rewind(offset int) {
	// Scan the next character.
	l.scanOffset = offset
	l.offset = offset
	if l.offset < len(l.source) {
		r, w := rune(l.source[l.offset]), 1
		if r >= 0x80 {
			// not ASCII
			r, w = utf8.DecodeRuneInString(l.source[l.offset:])
		}
		l.scanOffset += w
		l.ch = r
	} else {
		l.ch = -1 // EOI
	}
}

func mustParseInt(s string) int {
	i, err := strconv.Atoi(s)
	if err != nil {
		panic(`lexer internal error: ` + err.Error())
	}
	return i
}
