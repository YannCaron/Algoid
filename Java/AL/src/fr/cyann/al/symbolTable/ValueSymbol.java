/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.al.data.Types;
import fr.cyann.jasi.lexer.Token;

/**
 * <p>
 * @author cyann
 */
public class ValueSymbol extends LeafSymbol {

    // constructor
    public ValueSymbol(Token token, ScopeSymbol parent, Types type) {
        super(token, parent);
        super.setType(type);
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return null;
    }

    // override

    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }
}
