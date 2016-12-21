// generated by Textmapper; DO NOT EDIT

package test

import (
	"fmt"
)

// Symbol represents a set of all terminal and non-terminal symbols of the test language.
type Symbol int

var symbolStr = [...]string{
	"Declaration_list",
	"Test",
	"Declaration",
	"QualifiedName",
	"Decl1",
	"Decl2",
}

func (n Symbol) String() string {
	if n < Symbol(NumTokens) {
		return Token(n).String()
	}
	i := int(n) - int(NumTokens)
	if i < len(symbolStr) {
		return symbolStr[i]
	}
	return fmt.Sprintf("nonterminal(%d)", n)
}

var tmAction = []int32{
	-1, -1, 9, -1, -3, 1, 3, 4, -1, -1, 0, 6, -1, 5, 8, -1,
	7, -1, -2,
}

var tmLalr = []int32{
	6, -1, 7, -1, 8, -1, 0, 2, -1, -2,
}

var tmGoto = []int32{
	0, 1, 1, 1, 1, 3, 3, 7, 11, 15, 16, 17, 18, 18, 18, 19,
	19, 19, 19, 19, 21, 22, 26, 27, 31, 35,
}

var tmFrom = []int8{
	17, 8, 15, 0, 3, 4, 9, 0, 3, 4, 9, 0, 3, 4, 9, 9,
	1, 12, 12, 0, 3, 0, 0, 3, 4, 9, 8, 0, 3, 4, 9, 0,
	3, 4, 9,
}

var tmTo = []int8{
	18, 11, 16, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 13,
	8, 14, 15, 4, 9, 17, 5, 5, 10, 10, 12, 6, 6, 6, 6, 7,
	7, 7, 7,
}

var tmRuleLen = []int8{
	2, 1, 1, 1, 1, 3, 1, 3, 4, 1,
}

var tmRuleSymbol = []int32{
	19, 19, 20, 21, 21, 21, 22, 22, 23, 24,
}
