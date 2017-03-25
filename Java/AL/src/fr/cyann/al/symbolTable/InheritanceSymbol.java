/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.jasi.lexer.Token;

/**
 <p>
 @author cyann
 */
public class InheritanceSymbol extends DeclarationSymbol {

	// constructor
	public InheritanceSymbol(Token token, ScopeSymbol parent, String name) {
		super(token, parent, name);
	}

	// override
	@Override
	public void visite(SymbolTableVisitor visitor) {
		visitor.visite(this);
	}
}
