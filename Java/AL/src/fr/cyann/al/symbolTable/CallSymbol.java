/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.jasi.lexer.Token;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * @author cyann
 */
public class CallSymbol extends IdentifierSymbol {

    // attribute
    private final AbstractList<Symbol> arguments;

    // property
    public List<Symbol> getArguments() {
        return arguments;
    }

    // constructor
    public CallSymbol(Token token, ScopeSymbol parent) {
        super(token, parent);
        arguments = new LinkedList<Symbol>();
    }

    // method
    public boolean addArgument(Symbol e) {
        return arguments.add(e);
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return find(arguments, token);
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }

    // toString
    @Override
    public String toString() {
        return "CallSymbol{" + "arguments=" + arguments + '}';
    }

}
