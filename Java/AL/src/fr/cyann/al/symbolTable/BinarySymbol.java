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
public class BinarySymbol extends OperatorSymbol {

    // attribute
    private final Symbol left;
    private final Symbol right;

    // property
    public Symbol getLeft() {
        return left;
    }

    public Symbol getRight() {
        return right;
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        if (left.find(token) != null) {
            return left;
        }
        
        return right.find(token);
    }

    // constructor
    public BinarySymbol(Token token, ScopeSymbol parent, Types type, boolean enforce, Symbol left, Symbol right) {
        super(token, parent, enforce);
        super.setType(type);
        this.left = left;
        this.right = right;
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }

    // toString
    @Override
    public String toString() {
        return "BinarySymbol{" + "left=" + left + ", right=" + right + '}';
    }

}
