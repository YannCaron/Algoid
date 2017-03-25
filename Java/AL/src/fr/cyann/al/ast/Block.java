
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

import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.scope.NestedScope;
import fr.cyann.jasi.ast.interfaces.Scopeable;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.StaticMethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * The Block class.<br> Based on GoF composite design pattern. Creation date: 1
 * févr. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Block<C extends Context> extends AST<Block<C>, C> implements Scopeable<NestedScope> {

	public AbstractList<AST<? extends AST, C>> statements;
	public NestedScope scope;
	public FunctionInstance function;

	public Block() {
		this(new Token(TokenType.SYMBOL, "{"));
	}

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public Block(Token token) {
		super(token);
		statements = new ArrayList<AST<? extends AST, C>>();
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
	public AbstractList<AST<? extends AST, C>> getStatements() {
		return statements;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public NestedScope getScope() {
		return scope;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setScope(NestedScope scope) {
		this.scope = scope;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		return replaceOnList(this.statements, it, by);
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
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		func.traverse(this);
		AST.depthfirstTraverse(statements, func);
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Block{statements=" + statements + ", scope=" + scope + '}';
	}
}
