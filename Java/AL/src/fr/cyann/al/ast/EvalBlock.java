/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.ast;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.StaticMethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.ArrayList;
import java.util.List;

/**
 * The EvalBlock class. Creation date: 1 avr. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class EvalBlock<C extends Context> extends AST<EvalBlock, C> {

    private String source;
    public ArrayList<AST<? extends AST, C>> statements;

    public EvalBlock(Token token) {
	this(token, "");
    }

    public EvalBlock(Token token, String source) {
	super(token);
	this.source = source;
	statements = new ArrayList<AST<? extends AST, C>>();
    }

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    /**
     * Add the statement node. Block is a composite.
     *
     * @param ast the node.
     */
    public void addStatement(AST<? extends AST, C> ast) {
	ast.setParent(this);
	statements.add(ast);
    }

    public void addAllStatements(List<AST<? extends AST, C>> asts) {
	int size = asts.size();
	for (int i = 0; i < size; i++) {
	    statements.add(asts.get(i));
	}
    }

    /**
     * Get the statements iterator.
     *
     * @return the statements iterator.
     */
    public ArrayList<AST<? extends AST, C>> getStatements() {
	return statements;
    }

    @Override
    protected boolean replaceChild(AST it, AST by) {
	return replaceOnList(this.statements, it, by);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void depthFirstTraversal(TraversalFunctor func) {
	func.traverse(this);
	AST.depthfirstTraverse(statements, func);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void injectVisitor(VisitorInjector injector) {
	if (!(this.visitor instanceof StaticMethodVisitor)) {
	    super.injectVisitor(injector);
	    AST.inject(injector, statements);
	}
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
	return "Eval Block{statements=" + statements + '}';
    }

}
