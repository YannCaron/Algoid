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
public class VariableSymbol extends IdentifierSymbol {

    private final String name;

    // property
    public String getName() {
        return name;
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return null;
    }

    // constructor

    public VariableSymbol(Token token, ScopeSymbol parent, String name) {
        super(token, parent);
        this.name = name;
    }

    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }

}
