/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.jasi.lexer.Token;

/**
 * <p>
 * @author CARONYN
 */
public abstract class IdentifierSymbol extends LeafSymbol {

    // attribute
    @PrettyPrinter.Ignore
    private IdentifierSymbol next, previous;
    private Symbol pointer;

    // property
    public boolean hasNext() {
	return next != null;
    }

    public IdentifierSymbol getNext() {
	return next;
    }

    public void setNext(IdentifierSymbol next) {
	if (this.next != null) {
	    this.next.setNext(next);
	} else {
	    this.next = next;
	    next.previous = this;
	}
    }

    public boolean hasPrevious() {
	return previous != null;
    }

    public IdentifierSymbol getPrevious() {
	return previous;
    }

    public void setPrevious(IdentifierSymbol previous) {
	this.previous = previous;
    }

    public IdentifierSymbol getLast() {
	if (hasNext()) {
	    return next.getLast();
	}
	return this;
    }

    public Symbol getPointer() {
	return pointer;
    }

    public void setPointer(Symbol pointer) {
	this.pointer = pointer;
    }

    public boolean hasPointer() {
	return pointer != null;
    }

    // method
    @Override
    public Symbol find(Token token) {
	if (token.getPos() == super.getToken().getPos()) {
	    return this;
	}

	if (next.find(token) != null) {
	    return next;
	}

	return pointer.find(token);
    }

    // constructor

    public IdentifierSymbol(Token token, ScopeSymbol parent) {
	super(token, parent);
    }

    // toString
    @Override
    public String toString() {
	return "IdentifierSymbol{" + "next=" + next + '}';
    }

}
