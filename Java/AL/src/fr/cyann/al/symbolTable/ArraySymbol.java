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
 * @author CARONYN
 */
public class ArraySymbol extends NodeSymbol {

    // constructor
    public ArraySymbol(Token token, ScopeSymbol parent) {
        super(token, parent);
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

    // toString
    @Override
    public String toString() {
        return "ArraySymbol{" + '}';
    }

}
