
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
package fr.cyann.jasi.builder;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.parser.StatementLeafToken;
import fr.cyann.jasi.parser.StatementNode;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.ArrayList;
import java.util.List;

/**
 * The ASTBuilder class. Creation date: 8 janv. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public final class ASTBuilder implements InterpreterBuilder {

    private List<AST> program;

    /**
     * Hide constructor.<br> Based on GoF singleton.
     */
    public ASTBuilder() {
	program = new ArrayList<AST>();
    }

    public List<AST> getProgram() {
	return program;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void push(AST item) {
	program.add(item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public AST poll() {
	return program.remove(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public synchronized AST pop() {
	if (program.size() == 0) {
	    return null;
	}
	return program.remove(program.size() - 1);
    }

    /**
     * @inheritDoc
     */
    @Override
    public synchronized AST peek() {
	if (program.size() == 0) {
	    return null;
	}
	return program.get(program.size() - 1);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEmpty() {
	return program.isEmpty();
    }

    /**
     * Inject visitor to all the AST yet present in the stack.<br> After
     * agregation the result of the stack must be sequential programm to
     * execute. The entry point to run result programm.
     *
     * @param injector the injector.
     * @return this
     */
    public ASTBuilder injectVisitor(VisitorInjector injector) {
	int size = program.size();
	for (int i = 0; i < size; i++) {
	    program.get(i).injectVisitor(injector);
	}

	return this;
    }

    /**
     * Visit all the AST yet present.<br> the entry point to execution.
     *
     * @param <C> the specific context type
     * @param context the execution context
     */
    public <C extends Context> void visite(C context) {
	// KEEP size into for else eval does not works
	for (int i = 0; i < program.size(); i++) {
	    program.get(i).visite(context);
	}
    }

    /**
     * @inheritDoc
     */
    @Override
    public void build(StatementLeafToken statement, FactoryStrategy strategy) {
	strategy.buildLeaf(this, statement);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void build(StatementNode statement, AgregatorStrategy strategy) {
	strategy.build(this, statement);
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
	return "ASTBuilder{" + "stack=" + program + '}';
    }

}
