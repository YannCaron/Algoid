/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.jasi.lexer.Token;

/**
 <p>
 @author CARONYN
 */
public abstract class OperatorSymbol extends LeafSymbol {

	// attribute
	private final boolean enforce;

	// attribute
	public boolean isEnforce() {
		return enforce;
	}

	// constructor
	public OperatorSymbol(Token token, ScopeSymbol parent, boolean enforce) {
		super(token, parent);
		this.enforce = enforce;
	}

	// toString
	@Override
	public String toString() {
		return "OperatorSymbol{" + "enforce=" + enforce + '}';
	}

}
