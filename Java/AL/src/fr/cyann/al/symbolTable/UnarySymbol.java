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
public class UnarySymbol extends OperatorSymbol {

    // attribute
    private final Symbol right;

    // property
    public Symbol getRight() {
        return right;
    }

    // constructor
    public UnarySymbol(Token token, ScopeSymbol parent, Types type, boolean enforce, Symbol right) {
        super(token, parent, enforce);
        super.setType(type);
        this.right = right;
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return right.find(token);
    }

    // override

    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }

    // toString
    @Override
    public String toString() {
        return "UnarySymbol{" + "right=" + right + '}';
    }

}
