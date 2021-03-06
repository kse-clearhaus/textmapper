package syntax

import (
	"github.com/inspirer/textmapper/tm-go/status"
	"log"

	"github.com/inspirer/textmapper/tm-go/util/container"
	"github.com/inspirer/textmapper/tm-go/util/set"
)

// PropagateLookaheads figures out which nonterminals need to be templated because of lookahead
// flags and then updates the model in-place.
func PropagateLookaheads(m *Model) error {
	type nontermExt struct {
		compat        bool    // true if supports lookahead flags
		refs          []*Expr // sub-expressions for flag propagation
		requiredFlags []int
		pending       *set.FutureSet // LA flags this nonterminal can accept
		flags         container.BitSet
	}
	var state []nontermExt

	// 1. Compute the set of lookahead flags each nonterminal can potentially accept.
	closure := set.NewClosure(len(m.Params))
	used := container.NewBitSet(len(m.Params))
	reuse := make([]int, 0, len(m.Params))
	for _, nt := range m.Nonterms {
		used.ClearAll(len(m.Params))
		usedLA(m, nt.Value, func(param int, _ status.SourceNode) { used.Set(param) })
		required := used.Slice(reuse)
		state = append(state, nontermExt{pending: closure.Add(required), requiredFlags: required})
	}
	for i, nt := range m.Nonterms {
		state[i].compat = entryPoints(nt.Value, func(ref *Expr) {
			if sub := ref.Symbol - len(m.Terminals); sub >= 0 {
				used.SetAll(len(m.Params))
				var laArgs bool
				for _, arg := range ref.Args {
					if m.Params[arg.Param].Lookahead {
						used.Clear(arg.Param)
						laArgs = true
					}
				}
				if laArgs {
					// Do not propagate arguments supplied explicitly upwards (mask them out).
					mask := closure.Add(used.Slice(reuse))
					state[i].pending.Include(closure.Intersect(mask, state[sub].pending))
				} else {
					state[i].pending.Include(state[sub].pending)
				}
				state[i].refs = append(state[i].refs, ref)
			}
		})
	}
	closure.Compute()
	for i := range m.Nonterms {
		state[i].flags = state[i].pending.BitSet(len(m.Params))
	}

	// 2. Propagate lookahead arguments through all intermediate nonterminals to their usages.
	var s status.Status
	type task struct {
		nonterm int
		param   int
	}
	seen := make(map[task]bool)
	var queue []task
	enqueue := func(t task) {
		if !seen[t] {
			seen[t] = true
			queue = append(queue, t)
		}
	}
	visitArgs(m, func(nonterm int, arg Arg) bool {
		if m.Params[arg.Param].Lookahead {
			if !state[nonterm].flags.Get(arg.Param) {
				s.Errorf(arg.Origin, "%v is not used in %v", m.Params[arg.Param].Name, m.Nonterms[nonterm].Name)
				return false // delete
			}
			enqueue(task{nonterm, arg.Param})
		}
		return true // keep
	})
	for len(queue) > 0 {
		it := queue[len(queue)-1]
		queue = queue[:len(queue)-1]

		nt := m.Nonterms[it.nonterm]
		if !state[it.nonterm].compat {
			state[it.nonterm].compat = true // report only once
			s.Errorf(nt.Origin, "cannot propagate lookahead flag %v through nonterminal %v; avoid nullable alternatives and optional clauses", m.Params[it.param].Name, nt.Name)
		}

		nt.Params = append(nt.Params, it.param)
		for _, ref := range state[it.nonterm].refs {
			if containsArg(ref, it.param) {
				continue
			}
			target := ref.Symbol - len(m.Terminals)
			if state[target].flags.Get(it.param) {
				enqueue(task{target, it.param})
			}
			ref.Args = append(ref.Args, Arg{Param: it.param, TakeFrom: it.param})
		}
	}

	// 3. Check that all the flag requirements are met.
	for i, nt := range m.Nonterms {
		used.ClearAll(len(m.Params))
		for _, param := range nt.Params {
			used.Set(param)
		}
		for _, param := range state[i].requiredFlags {
			if used.Get(param) {
				continue
			}
			usedLA(m, nt.Value, func(p int, origin status.SourceNode) {
				if p == param {
					s.Errorf(origin, "lookahead flag %v is never provided", m.Params[p].Name)
				}
			})
		}
	}

	// 4. Remove the lookahead bit.
	for i := range m.Params {
		m.Params[i].Lookahead = false
	}
	return s.Err()
}

func containsArg(ref *Expr, param int) bool {
	for _, arg := range ref.Args {
		if arg.Param == param {
			return true
		}
	}
	return false
}

// entryPoints collects all symbol references that start a given expression.
// Returns true if all alternatives of "expr" start with a non-nullable clause (start symbols can
// still be nullable).
func entryPoints(expr *Expr, consumer func(ref *Expr)) bool {
	switch expr.Kind {
	case Empty, StateMarker, Command, Lookahead:
		// Note: these are allowed as part of a sequence only.
		return false
	case Set:
		return true
	case Optional:
		entryPoints(expr.Sub[0], consumer)
		return false
	case List:
		ret := entryPoints(expr.Sub[0], consumer)
		return ret && expr.ListFlags&OneOrMore != 0
	case Assign, Append, Arrow, Conditional, Prec:
		return entryPoints(expr.Sub[0], consumer)
	case Choice:
		ret := len(expr.Sub) > 0
		for _, c := range expr.Sub {
			ret = ret && entryPoints(c, consumer)
		}
		return ret
	case Sequence:
		for _, c := range expr.Sub {
			switch c.Kind {
			case Empty, StateMarker, Command, Lookahead:
				continue
			}
			return entryPoints(c, consumer)
		}
		return false
	case Reference:
		consumer(expr)
		return true
	default:
		log.Fatal("invariant failure")
		return false
	}
}

// usedLA finds all lookahead template parameters used inside "expr".
func usedLA(m *Model, expr *Expr, consumer func(param int, origin status.SourceNode)) {
	switch expr.Kind {
	case Conditional:
		expr.Predicate.ForEach(func(pred *Predicate) {
			if pred.Op == Equals && m.Params[pred.Param].Lookahead {
				consumer(pred.Param, pred.Origin)
			}
		})
	case Reference:
		for _, arg := range expr.Args {
			if arg.Value == "" && m.Params[arg.TakeFrom].Lookahead {
				consumer(arg.TakeFrom, arg.Origin)
			}
		}
	}
	for _, c := range expr.Sub {
		usedLA(m, c, consumer)
	}
}

// visitArgs visits all templated nonterminal references and allows
func visitArgs(m *Model, consumer func(nonterm int, arg Arg) bool) {
	m.ForEach(Reference, func(_ *Nonterm, expr *Expr) {
		out := expr.Args[:0]
		for _, arg := range expr.Args {
			if consumer(expr.Symbol-len(m.Terminals), arg) {
				out = append(out, arg)
			}
		}
		expr.Args = out
	})
	for _, set := range m.Sets {
		set.ForEach(func(ts *TokenSet) {
			out := ts.Args[:0]
			for _, arg := range ts.Args {
				if consumer(ts.Symbol-len(m.Terminals), arg) {
					out = append(out, arg)
				}
			}
			ts.Args = out
		})
	}
}
