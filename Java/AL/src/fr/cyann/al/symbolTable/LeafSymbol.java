/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.jasi.lexer.Token;

/**
 * <p>
 * @author cyann
 */
public abstract class LeafSymbol extends Symbol {

    // constructor
    public LeafSymbol(Token token, ScopeSymbol parent) {
        super(token, parent);
    }

    // toString
    @Override
    public String toString() {
        return "LeafSymbol{" + '}';
    }

}
