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
public class IndexSymbol extends IdentifierSymbol {

    // constructor
    private final Symbol ident;

    // property
    public Symbol getIdent() {
        return ident;
    }

    // method
    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return ident.find(token);
    }

    // constructor
    public IndexSymbol(Token token, ScopeSymbol parent, Symbol ident) {
        super(token, parent);
        this.ident = ident;
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }
}
