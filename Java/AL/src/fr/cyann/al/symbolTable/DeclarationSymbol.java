/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.jasi.lexer.Token;

/**
 * <p>
 * @author cyann
 */
public class DeclarationSymbol extends LeafSymbol {

    private final String name;
    private Symbol expression;

    @PrettyPrinter.Optional
    private boolean member;

    // property
    public String getName() {
	return name;
    }

    public Symbol getExpression() {
	return expression;
    }

    public void setExpression(Symbol expression) {
	this.expression = expression;
    }

    public boolean isMember() {
	return member;
    }

    public void setMember(boolean member) {
	this.member = member;

    }

    // method
    @Override
    public Symbol find(Token token) {
	if (token.getPos() == super.getToken().getPos()) {
	    return this;
	}

	return expression.find(token);
    }

    // constructor
    public DeclarationSymbol(Token token, ScopeSymbol parent, String name) {
	super(token, parent);
	this.name = name;
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
	visitor.visite(this);
    }

    // toString
    @Override
    public String toString() {
	return "DeclarationSymbol{" + "name=" + name + ", expression=" + expression + ", member=" + member + '}';
    }

}
