/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.VisitorInjector;

/**
 *
 * @author cyann
 */
public class Association extends Value {
    private final StringAST name;
    private final Value value;

    public Association(Token token, StringAST name, Value value) {
        super(token);
        this.name = name;
        this.value = value;
    }

    public StringAST getAssoName() {
        return name;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void injectVisitor(VisitorInjector injector) {
        super.injectVisitor(injector);
        AST.inject(injector, name);
        AST.inject(injector, value);
    }
    
}
