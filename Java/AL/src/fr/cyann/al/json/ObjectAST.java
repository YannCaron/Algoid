/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cyann
 */
public class ObjectAST extends Value {


    private final List<Association> list;

    public ObjectAST(Token token) {
        super(token);
        list = new ArrayList<Association>();
    }

    public void addChild(Association ast) {
        list.add(ast);
    }

    public Iterable<Association> getChildren() {
        return list;
    }

    @Override
    public void injectVisitor(VisitorInjector injector) {
        super.injectVisitor(injector);
        AST.inject(injector, list);
    }

    @Override
    public String toString() {
        return "Object{" + "list=" + list + '}';
    }

}
