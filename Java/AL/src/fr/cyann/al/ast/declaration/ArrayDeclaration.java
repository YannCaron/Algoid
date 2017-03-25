
/*
 YANN CARON CONFIDENTIAL
 __________________

 Yann Caron Copyright (c) 2011
 All Rights Reserved.
 __________________

 NOTICE:  All information contained herein is, and remains
 the property of Yann Caron and its suppliers, if any.
 The intellectual and technical concepts contained
 herein are proprietary to Yann Caron
 and its suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Yann Caron.
 */
package fr.cyann.al.ast.declaration;

import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Expression;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * The Table AST class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ArrayDeclaration<C extends Context> extends Declaration<ArrayDeclaration<C>, C> {

    public AbstractList<Expression<? extends Expression, C>> keys;
    public AbstractList<Expression<? extends Expression, C>> elements;

    /**
     * Default constructor.
     *
     * @param token the token corresponding to the AST.
     */
    public ArrayDeclaration(Token token) {
        super(token);
        keys = new ArrayList<Expression<? extends Expression, C>>();
        elements = new ArrayList<Expression<? extends Expression, C>>();
    }

    private void completeKeys() {
        for (int i = keys.size(); i < elements.size(); i++) {
            keys.add(null);
        }
    }

    /**
     * Add key to the composite.
     *
     * @param e the key to add.
     * @return true if ok.
     */
    public boolean addKey(Expression<? extends Expression, C> e) {
        e.setParent(this);
        completeKeys();
        return keys.add(e);
    }

    /**
     * Add elements to the composite.
     *
     * @param e the element to add.
     * @return true if ok.
     */
    public boolean addElement(Expression<? extends Expression, C> e) {
        e.setParent(this);
        return elements.add(e);
    }

    /**
     * The keys iterable accessor.
     *
     * @return the list of keys of the composite.
     */
    public List<Expression<? extends Expression, C>> getKeys() {
        return keys;
    }

    /**
     * The elements iterable accessor.
     *
     * @return the list of elements of the composite.
     */
    public List<Expression<? extends Expression, C>> getElements() {
        return elements;
    }

    /**
     * @inheritDoc
     */
    /*
     @Override
     public ArrayDeclaration<C> clone() {
     ArrayDeclaration clone = new ArrayDeclaration(getToken());

     for (Expression expr : elements) {
     // TODO à revoir "cast beurk"
     if (expr instanceof Declaration) {
     clone.elements.add(((Declaration) expr).clone());
     } else {
     clone.elements.add(expr);
     }
     }

     clone.visitor = visitor;
     return clone;
     }*/
    @Override
    protected boolean replaceChild(AST it, AST by) {
        if (replaceOnList(this.keys, it, by)) {
            return true;
        }

        return replaceOnList(this.elements, it, by);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void injectVisitor(VisitorInjector injector) {
        super.injectVisitor(injector);
        AST.inject(injector, elements);
        AST.inject(injector, keys);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void depthFirstTraversal(TraversalFunctor func) {
        super.depthFirstTraversal(func);
        AST.depthfirstTraverse(keys, func);
        AST.depthfirstTraverse(elements, func);
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
        return "Array{elements=" + elements + '}';
    }
}
