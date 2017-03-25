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
public class ReturnSymbol extends Symbol {

    private Symbol expression;

    public Symbol getExpression() {
        return expression;
    }

    public void setExpression(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public Symbol find(Token token) {
        if (token.getPos() == super.getToken().getPos()) {
            return this;
        }

        return expression.find(token);
    }

    public ReturnSymbol(Token token, ScopeSymbol parent) {
        super(token, parent);
    }

    // override
    @Override
    public void visite(SymbolTableVisitor visitor) {
        visitor.visite(this);
    }

}
