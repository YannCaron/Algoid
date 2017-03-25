
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
package fr.cyann.al.ast;

import fr.cyann.al.Constants;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.exception.NotACallException;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.Iterator;

/**
 * The Symbol class.<br>
 * Based on composite design pattern but for linear use (next previous) Creation
 * date: 22 févr. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public abstract class Expression<T extends AST, C extends Context> extends AST<T, C> implements Iterator, Constants {

    public Expression<? extends Expression, C> previous, next, _this;
    public String partName;
    public MutableVariant mv;
    public boolean mutable;

    /**
     * Default constructor.
     *
     * @param token the token corresponding to the AST.
     */
    public Expression(Token token) {
	super(token);
	partName = token.getText();
	_this = (Expression<? extends Expression, C>) this;
	mutable = true;
    }

    @Override
    protected boolean replaceChild(AST it, AST by) {
	if (hasNext()) {
	    return next.replaceChild(it, by);
	}

	return false;
    }

    /**
     * Get the current part name.
     *
     * @return the name.
     */
    @Override
    public String getName() {
	return super.getToken().getText();
    }

    /**
     * @inheritDoc
     */
    public String getChainName() {
	return getFirst().aggregateChainName();
    }

    /**
     * Get the chain of all symbols string representation.
     *
     * @return the string representation.
     */
    protected String aggregateChainName() {
	if (hasNext()) {
	    return next.aggregateChainName();
	}
	return "";

		// TODO pourquoi avais-je mis cela puisque géré dans les sous-types ?
	//return this.getToken().getText();
    }

    /**
     * Return if a previous symbol exists in the chain.
     *
     * @return true if previous exists.
     */
    public boolean hasPrevious() {
	return previous != null;
    }

    /**
     * The previous symbol in the chain accessor.
     *
     * @return the previous symbol.
     */
    public Expression<? extends Expression, C> getPrevious() {
	return previous;
    }

    /**
     * The previous symbol in the chain mutator.
     *
     * @param previous the previous symbol.
     */
    public void setPrevious(Expression<? extends Expression, C> previous) {
	this.previous = previous;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean hasNext() {
	return next != null;
    }

    /**
     * Visit the next expression of the chain
     * @param context the program context
     */
    public void visitNext(C context) {
	if (next != null) {
	    next.visite(context);
	}
    }

    /**
     * @inheritDoc
     */
    @Override
    public Expression<? extends Expression, C> next() {
	return next;
    }

    /**
     * Set the next element of the chain. <br>
     * Based on cascad pattern.
     *
     * @param next the next element of the chain.
     * @return the next to add on cascade.
     */
    public Expression<? extends Expression, C> setNext(Expression<? extends Expression, C> next) {

	// chain of responcibility
	if (hasNext()) {
	    next().setNext(next);
	} else {
	    this.next = next;
	    this.next.setPrevious(_this);
	}
	return next;
    }

    /**
     * Get the first symbol of the chain.
     *
     * @return the first symbol.
     */
    public Expression<? extends Expression, C> getFirst() {
	if (hasPrevious()) {
	    return previous.getFirst();
	} else {
	    return _this;
	}
    }

    /**
     * Get the last symbol of the chain.
     *
     * @return the last symbol.
     */
    public Expression<? extends Expression, C> getLast() {
	if (hasNext()) {
	    return next.getLast();
	} else {
	    return _this;
	}
    }

    /**
     * Get the next call in the chain.
     *
     * @return the next call.
     */
    public Call nextCall() {
	if (hasNext()) {
	    return next().nextCall();
	} else {
	    throw new NotACallException(EX_CALL_SYMBOL.setArgs(this.aggregateChainName()), this.getToken().getPos());
	}
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove() {
	if (hasPrevious()) {
	    this.previous.setNext(next);
	}

	this.next = null;
	this.previous = null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void injectVisitor(VisitorInjector injector) {
	super.injectVisitor(injector);
	AST.inject(injector, next);
    }

    @Override
    public void depthFirstTraversal(TraversalFunctor func) {
	func.traverse(this);
	if (this.hasNext()) {
	    this.next.depthFirstTraversal(func);
	}
    }

    /**
     * Get the object unique hash code to generate hash with java algorithm
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
	return 987854;
    }

    /**
     * Determine if two variables are equals.<br>
     * Based on the getname methode, that ensure the string is unique for a
     * variable in a special context.<br>
     * Scope where it is tested is important.
     *
     * @param obj the object to compare.
     * @return true if equal.
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}

	return this.getChainName().equals(((Expression) obj).getChainName());
    }
}
