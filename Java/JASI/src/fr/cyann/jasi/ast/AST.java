
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
package fr.cyann.jasi.ast;

import fr.cyann.jasi.Constants;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.ast.interfaces.Visitable;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;
import java.util.AbstractList;
import java.util.List;

/**
 * The AST class. Creation date: 8 janv. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public abstract class AST<T extends AST, C extends Context> implements Visitable<C>, Constants {

    protected MethodVisitor<T, C> visitor;
    private Token token, endToken;
    private AST parent;

    /**
     * Default constructor.<br> Token is mendatory for all AST except Null
     * one.<br> Token represents the source code key that indicate the kind of
     * AST to use to the parser.<br> It is the main key to make grammar
     * decision.<br>
     * For expression AST it represent the textual value of the expression.
     *
     * @param token the famous token.
     */
    public AST(Token token) {
	this.token = token;
    }

    public AST getParent() {
	return parent;
    }

    public void setParent(AST parent) {
	this.parent = parent;
    }

    protected abstract boolean replaceChild(AST it, AST by);

    public boolean replaceBy(T ast) {
	return this.getParent().replaceChild(this, ast);
    }

    protected boolean replaceOnField(Accessor<AST> accessor, AST it, AST by) {
	AST value = accessor.getter();

	if (value == it) {
	    value.setParent(null);
	    accessor.setter(by);
	    by.setParent(this);
	    return true;
	}

	return false;

    }

    protected boolean replaceOnList(AbstractList list, AST it, AST by) {
	int i = list.indexOf(it);

	if (i != -1) {
	    ((AST) list.get(i)).setParent(null);
	    by.setParent(this);
	    list.set(i, by);
	    return true;
	}

	return false;
    }

    /**
     * The token accessor.
     *
     * @return
     */
    public final Token getToken() {
	return this.token;
    }

    /**
     * The token mutator.
     *
     * @param token
     */
    public final void setToken(Token token) {
	this.token = token;
    }

    /**
     * The endToken accessor.
     *
     * @return
     */
    public Token getEndToken() {
	return endToken;
    }

    /**
     * The endToken mutator.
     *
     * @param token
     */
    public void setEndToken(Token endToken) {
	this.endToken = endToken;
    }

    /**
     * The name accessor.<br> In most case, the token text.
     *
     * @return
     */
    public String getName() {
	return token.getText();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void injectVisitor(VisitorInjector injector) {
	this.visitor = injector.getVisitor(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visite(C context) {
	visitor.visite((T) this, context);
    }

    public static void depthfirstTraverse(AST ast, TraversalFunctor func) {
	if (ast != null) {
	    ast.depthFirstTraversal(func);
	}
    }

    public static void depthfirstTraverse(List<? extends AST> asts, TraversalFunctor func) {
	if (asts != null) {
	    int size = asts.size();
	    for (int i = 0; i < size; i++) {
		asts.get(i).depthFirstTraversal(func);
	    }
	}
    }

    public abstract void depthFirstTraversal(TraversalFunctor func);

    /**
     * The injector helper that test if optional ast branche is not null before
     * inject.
     *
     * @param injector the injector.
     * @param ast the optional AST to test and then inject.
     */
    public static void inject(VisitorInjector injector, AST ast) {
	if (ast != null) {
	    ast.injectVisitor(injector);
	}
    }

    /**
     * The injector helper that test and inject to AST branches list.
     *
     * @param injector the injector.
     * @param asts the AST list to inject.
     */
    public static void inject(VisitorInjector injector, List<? extends AST> asts) {
	if (asts != null) {
	    int size = asts.size();
	    for (int i = 0; i < size; i++) {
		AST ast = asts.get(i);
		inject(injector, ast);
	    }
	}
    }

    public MethodVisitor<T, C> getVisitor() {
	return visitor;
    }

    public void setVisitor(MethodVisitor<T, C> visitor) {
	this.visitor = visitor;
    }

    /**
     * Visite an ast after not null checking.
     *
     * @param ast the ast to visite
     * @param c the execution context
     */
    public static void visite(AST ast, Context c) {
	if (ast != null) {
	    ast.visite(c);
	}
    }
}
