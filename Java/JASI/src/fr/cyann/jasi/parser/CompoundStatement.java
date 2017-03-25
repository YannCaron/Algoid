
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
package fr.cyann.jasi.parser;

import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.VoidAgregator;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;

/**
 * The compound statement class.<br> Used to chain grammar expression. e.g.
 * 'set' ident '=' expr ';' are chained.
 *
 * @author CARONYN
 */
public class CompoundStatement extends StatementNode {

    private String name;
    private AgregatorStrategy strategy;

    /**
     * Default constructor.
     *
     * @param name the node name
     */
    public CompoundStatement(String name) {
	this(name, new VoidAgregator()); // default behaviour
    }

    /**
     * IOC constructor.
     *
     * @param name the node name
     * @param strategy the strategy to execute in case of grammar matching
     */
    public CompoundStatement(String name, AgregatorStrategy strategy) {
	this.name = name;
	this.strategy = strategy;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean tryParse(BacktrackingIterator<Token> iterator) {
	int size = super.children.size();
	for (int i = 0; i < size; i++) {
	    Statement child = children.get(i);

	    if (!child.tryParse(iterator)) {
		return false;
	    }
	}
	return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {

	boolean result;
	int size = super.children.size();
	for (int i = 0; i < size; i++) {
	    Statement child = children.get(i);

	    result = child.parse(iterator, builder);
	    if (!result) {
		return false;
	    }
	}

	builder.build(this, strategy);
	return true;
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
	return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toBNFString() {
	StringBuilder sb = new StringBuilder();

	int size = super.children.size();
	for (int i = 0; i < size; i++) {
	    Statement child = children.get(i);
	    if (sb.length() > 0) {
		sb.append(" ");
	    }

	    sb.append(child.getName());
	}

	sb.insert(0, "= ");
	sb.insert(0, getName());

	return sb.toString();
    }
}
