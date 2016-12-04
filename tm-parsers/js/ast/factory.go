// generated by Textmapper; DO NOT EDIT

package ast

import (
	"github.com/inspirer/textmapper/tm-parsers/js"
	"log"
)

func ToJsNode(node Node) JsNode {
	if node == nil {
		return nil
	}
	switch node.Type() {
	case js.AdditiveExpression:
		return &AdditiveExpression{node}
	case js.Arguments:
		return &Arguments{node}
	case js.ArrayLiteral:
		return &ArrayLiteral{node}
	case js.ArrayPattern:
		return &ArrayPattern{node}
	case js.ArrowFunction:
		return &ArrowFunction{node}
	case js.AssignmentExpression:
		return &AssignmentExpression{node}
	case js.AssignmentOperator:
		return &AssignmentOperator{node}
	case js.BindingIdentifier:
		return &BindingIdentifier{node}
	case js.BindingRestElement:
		return &BindingRestElement{node}
	case js.BitwiseANDExpression:
		return &BitwiseANDExpression{node}
	case js.BitwiseORExpression:
		return &BitwiseORExpression{node}
	case js.BitwiseXORExpression:
		return &BitwiseXORExpression{node}
	case js.Block:
		return &Block{node}
	case js.Body:
		return &Body{node}
	case js.BreakStatement:
		return &BreakStatement{node}
	case js.CallExpression:
		return &CallExpression{node}
	case js.Case:
		return &Case{node}
	case js.Catch:
		return &Catch{node}
	case js.Class:
		return &Class{node}
	case js.ClassBody:
		return &ClassBody{node}
	case js.ClassExpr:
		return &ClassExpr{node}
	case js.CommaExpression:
		return &CommaExpression{node}
	case js.ComputedPropertyName:
		return &ComputedPropertyName{node}
	case js.ConciseBody:
		return &ConciseBody{node}
	case js.ConditionalExpression:
		return &ConditionalExpression{node}
	case js.ContinueStatement:
		return &ContinueStatement{node}
	case js.DebuggerStatement:
		return &DebuggerStatement{node}
	case js.Default:
		return &Default{node}
	case js.DoWhileStatement:
		return &DoWhileStatement{node}
	case js.ElementBinding:
		return &ElementBinding{node}
	case js.EmptyDecl:
		return &EmptyDecl{node}
	case js.EmptyStatement:
		return &EmptyStatement{node}
	case js.EqualityExpression:
		return &EqualityExpression{node}
	case js.ExponentiationExpression:
		return &ExponentiationExpression{node}
	case js.ExportClause:
		return &ExportClause{node}
	case js.ExportDeclaration:
		return &ExportDeclaration{node}
	case js.ExportDefault:
		return &ExportDefault{node}
	case js.ExportSpecifier:
		return &ExportSpecifier{node}
	case js.ExpressionStatement:
		return &ExpressionStatement{node}
	case js.Extends:
		return &Extends{node}
	case js.Finally:
		return &Finally{node}
	case js.ForBinding:
		return &ForBinding{node}
	case js.ForCondition:
		return &ForCondition{node}
	case js.ForFinalExpression:
		return &ForFinalExpression{node}
	case js.ForInStatement:
		return &ForInStatement{node}
	case js.ForInStatementWithVar:
		return &ForInStatementWithVar{node}
	case js.ForOfStatement:
		return &ForOfStatement{node}
	case js.ForOfStatementWithVar:
		return &ForOfStatementWithVar{node}
	case js.ForStatement:
		return &ForStatement{node}
	case js.ForStatementWithVar:
		return &ForStatementWithVar{node}
	case js.Function:
		return &Function{node}
	case js.FunctionExpression:
		return &FunctionExpression{node}
	case js.Generator:
		return &Generator{node}
	case js.GeneratorExpression:
		return &GeneratorExpression{node}
	case js.GeneratorMethod:
		return &GeneratorMethod{node}
	case js.Getter:
		return &Getter{node}
	case js.IdentifierReference:
		return &IdentifierReference{node}
	case js.IfStatement:
		return &IfStatement{node}
	case js.ImportDeclaration:
		return &ImportDeclaration{node}
	case js.ImportSpecifier:
		return &ImportSpecifier{node}
	case js.IndexAccess:
		return &IndexAccess{node}
	case js.Initializer:
		return &Initializer{node}
	case js.JSXAttributeName:
		return &JSXAttributeName{node}
	case js.JSXClosingElement:
		return &JSXClosingElement{node}
	case js.JSXElement:
		return &JSXElement{node}
	case js.JSXElementName:
		return &JSXElementName{node}
	case js.JSXExpression:
		return &JSXExpression{node}
	case js.JSXLiteral:
		return &JSXLiteral{node}
	case js.JSXNormalAttribute:
		return &JSXNormalAttribute{node}
	case js.JSXOpeningElement:
		return &JSXOpeningElement{node}
	case js.JSXSelfClosingElement:
		return &JSXSelfClosingElement{node}
	case js.JSXSpreadAttribute:
		return &JSXSpreadAttribute{node}
	case js.JSXText:
		return &JSXText{node}
	case js.LabelIdentifier:
		return &LabelIdentifier{node}
	case js.LabelledStatement:
		return &LabelledStatement{node}
	case js.LexicalBinding:
		return &LexicalBinding{node}
	case js.LexicalDeclaration:
		return &LexicalDeclaration{node}
	case js.Literal:
		return &Literal{node}
	case js.LiteralPropertyName:
		return &LiteralPropertyName{node}
	case js.LogicalANDExpression:
		return &LogicalANDExpression{node}
	case js.LogicalORExpression:
		return &LogicalORExpression{node}
	case js.Method:
		return &Method{node}
	case js.Module:
		return &Module{node}
	case js.ModuleSpecifier:
		return &ModuleSpecifier{node}
	case js.MultiplicativeExpression:
		return &MultiplicativeExpression{node}
	case js.NameSpaceImport:
		return &NameSpaceImport{node}
	case js.NamedImports:
		return &NamedImports{node}
	case js.NewExpression:
		return &NewExpression{node}
	case js.NewTarget:
		return &NewTarget{node}
	case js.ObjectLiteral:
		return &ObjectLiteral{node}
	case js.ObjectPattern:
		return &ObjectPattern{node}
	case js.Parameter:
		return &Parameter{node}
	case js.Parameters:
		return &Parameters{node}
	case js.Parenthesized:
		return &Parenthesized{node}
	case js.PostDec:
		return &PostDec{node}
	case js.PostInc:
		return &PostInc{node}
	case js.PreDec:
		return &PreDec{node}
	case js.PreInc:
		return &PreInc{node}
	case js.Property:
		return &Property{node}
	case js.PropertyAccess:
		return &PropertyAccess{node}
	case js.PropertyBinding:
		return &PropertyBinding{node}
	case js.Regexp:
		return &Regexp{node}
	case js.RelationalExpression:
		return &RelationalExpression{node}
	case js.RestParameter:
		return &RestParameter{node}
	case js.ReturnStatement:
		return &ReturnStatement{node}
	case js.Setter:
		return &Setter{node}
	case js.ShiftExpression:
		return &ShiftExpression{node}
	case js.ShorthandProperty:
		return &ShorthandProperty{node}
	case js.SingleNameBinding:
		return &SingleNameBinding{node}
	case js.SpreadElement:
		return &SpreadElement{node}
	case js.StaticMethod:
		return &StaticMethod{node}
	case js.SuperExpression:
		return &SuperExpression{node}
	case js.SwitchStatement:
		return &SwitchStatement{node}
	case js.SyntaxError:
		return &SyntaxError{node}
	case js.TaggedTemplate:
		return &TaggedTemplate{node}
	case js.TemplateLiteral:
		return &TemplateLiteral{node}
	case js.This:
		return &This{node}
	case js.ThrowStatement:
		return &ThrowStatement{node}
	case js.TryStatement:
		return &TryStatement{node}
	case js.UnaryExpression:
		return &UnaryExpression{node}
	case js.VariableDeclaration:
		return &VariableDeclaration{node}
	case js.VariableStatement:
		return &VariableStatement{node}
	case js.WhileStatement:
		return &WhileStatement{node}
	case js.WithStatement:
		return &WithStatement{node}
	case js.Yield:
		return &Yield{node}
	}
	log.Fatalf("unknown node type %v\n", node.Type())
	return nil
}
