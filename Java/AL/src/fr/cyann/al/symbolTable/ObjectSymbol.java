/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.jasi.lexer.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * @author cyann
 */
public class ObjectSymbol extends ScopeSymbol {

	private final List<ObjectSymbol> superClasses;

	public ObjectSymbol(Token token, ScopeSymbol parent) {
		super(token, parent);
		superClasses = new ArrayList<ObjectSymbol>();
	}

	public void addSuperClass(ObjectSymbol symbol) {
		superClasses.add(symbol);
	}

	// override
	@Override
	public DeclarationSymbol find(String name, int pos, boolean isForWrite) {
		// first find localy
		DeclarationSymbol symbol = super.variables.get(name);
		if (found(symbol, pos)) {
			return symbol;
		}

		// get on super class
		for (ObjectSymbol superClass : superClasses) {
			symbol = superClass.find(name, pos, isForWrite);
			if (found(symbol, pos)) {
				return symbol;
			}
		}

		// get enclosing
		if (!isForWrite) {
			return super.find(name, pos, isForWrite);
		}

		return null;
	}

	@Override
	public void visite(SymbolTableVisitor visitor) {
		visitor.visite(this);
	}

}
