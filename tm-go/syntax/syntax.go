// Package syntax analyzes and translates a context-free grammar in the Textmapper notation into a
// simpler representation that is understood by a LALR parser generator.
package syntax

import (
	"github.com/inspirer/textmapper/tm-go/status"
)

// Model is a model of a language's syntax built on top of a set of terminals.
type Model struct {
	Terminals []string
	Params    []Param
	Nonterms  []Nonterm // all params and nonterms must have distinct names
	Inputs    []Input
	Sets      []TokenSet // extra token sets to compute
}

// Input introduces a start nonterminal.
type Input struct {
	Symbol int
	Args   []Arg
}

// Param is a grammar-wide template parameter.
type Param struct {
	Name   string
	Origin status.SourceNode
}

// Nonterm is a grammar nonterminal.
type Nonterm struct {
	Name   string
	Value  Expr
	Origin status.SourceNode
}

// Expr represents the right-hand side of a production rule (or its part).
type Expr struct {
	Kind      ExprKind
	Name      string
	Sub       []Expr
	Symbol    int
	Args      []Arg
	Set       *TokenSet
	Predicate *Predicate
	ListFlags ListFlags
	Pos       int // Positional index of a reference in the original rule.
	Origin    status.SourceNode
}

// ListFlags define the layout of list production rules.
type ListFlags int8

// List flags.
const (
	OneOrMore ListFlags = 1 << iota
	RightRecursive
)

// ExprKind is an production rule
type ExprKind int8

// All expression kinds.
const (
	Empty       ExprKind = iota
	Optional             // of {Sub0}
	Choice               // of 2+ Sub
	Sequence             // of 2+ Sub
	Reference            // {Symbol}<{Args}>
	Assign               // field={Sub0}
	Append               // field+={Sub0}
	Arrow                // {Sub0} -> {Name}
	List                 // of {Sub0}, separator={Sub1} (if present), also {ListFlags}
	Set                  // set({Set})
	Conditional          // [{Predicate}] {Sub}
	StateMarker          // .{Name}
)

// TokenSet is a grammar expression that resolves to a set of tokens.
type TokenSet struct {
	Kind   SetOp
	Symbol int
	Args   []Arg
	Sub    []TokenSet
	Origin status.SourceNode
}

// SetOp is a set operator.
type SetOp int8

// Set operators.
const (
	Any SetOp = iota
	First
	Last
	Precede
	Follow
	Union
	Intersection
	Complement
)

// Arg provides a value for a template parameter.
type Arg struct {
	Param    int
	Value    string
	TakeFrom int // when >= 0, the value should be taken from this parameter
	Origin   status.SourceNode
}

// Predicate is an expression which, given a template environment, evaluates to true or false.
type Predicate struct {
	Op    PredicateOp
	Sub   []Predicate
	Param int
	Value string
}

// PredicateOp is a predicate operator.
type PredicateOp int8

// Predicate operators.
const (
	Or PredicateOp = iota
	And
	Not
	Equals // true if {Param} == {Value}
)