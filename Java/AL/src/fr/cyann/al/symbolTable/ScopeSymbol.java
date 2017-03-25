/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.jasi.lexer.Token;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * @author CARONYN
 */
public class ScopeSymbol extends NodeSymbol {

    final Map<String, DeclarationSymbol> variables;

    // constructor
    public ScopeSymbol(Token token, ScopeSymbol parent) {
	super(token, parent);
	variables = new HashMap<String, DeclarationSymbol>();
    }

    // method
    public void push(DeclarationSymbol symbol) {
	stack.push(symbol);
	variables.put(symbol.getName(), symbol);
    }

    boolean found(DeclarationSymbol symbol, int pos) {
	return (symbol != null && pos >= symbol.getToken().getPos());
    }

    public DeclarationSymbol find(String name, int pos, boolean isForWrite) {
	DeclarationSymbol symbol = variables.get(name);
	if (!found(symbol, pos)) {
	    if (parent == null) {
		return null;
	    }
	    return parent.find(name, pos, isForWrite);
	} else {
	    return symbol;
	}
    }

    @Override
    public Symbol find(Token token) {
	if (token.getPos() == super.getToken().getPos()) {
	    return this;
	}

	return find(stack, token);
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
	visitor.visite(this);
    }

}
