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
 @author CARONYN
 */
public class FunctionSymbol extends ScopeSymbol {

	private Symbol returnSymbol = null;

	// property
	public boolean hasReturnSymbol() {
		return returnSymbol != null;
	}

	public Symbol getReturnSymbol() {
		return returnSymbol;
	}

	public void setReturnSymbol(Symbol returnSymbol) {
		this.returnSymbol = returnSymbol;
	}

	// constructor
	public FunctionSymbol(Token token, ScopeSymbol parent) {
		super(token, parent);
	}

	// override
	@Override
	public DeclarationSymbol find(String name, int pos, boolean isForWrite) {
		if (isForWrite && parent instanceof ObjectSymbol) {
			return parent.find(name, pos, isForWrite);
		} else {
			return super.find(name, pos, isForWrite);
		}
	}

	@Override
	public void visite(SymbolTableVisitor visitor) {
		visitor.visite(this);
	}
}
