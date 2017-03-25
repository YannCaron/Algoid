
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

import fr.cyann.al.ast.interfaces.Conditionable;
import fr.cyann.al.ast.interfaces.Executable;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;

/**
 * The While class. Creation date: 1 févr. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Until<C extends Context> extends AST<Until, C> implements Conditionable<C>, Executable<C> {

	public Expression<? extends Expression, C> condition;
	public Block<C> statement;

	private final Accessor<AST> conditionAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return condition;
		}

		@Override
		public void setter(AST value) {
			condition = (Expression<? extends Expression, C>) value;
		}
	};

	private final Accessor<AST> statementAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return statement;
		}

		@Override
		public void setter(AST value) {
			statement = (Block<C>) value;
		}
	};

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public Until(Token token) {
		super(token);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Expression<? extends Expression, C> getCondition() {
		return condition;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setCondition(Expression<? extends Expression, C> condition) {
		condition.setParent(this);
		this.condition = condition;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Block<C> getStatement() {
		return statement;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setStatement(Block<C> statement) {
		statement.setParent(this);
		this.statement = statement;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		if (replaceOnField(conditionAccessor, it, by)) {
			return true;
		}

		return replaceOnField(statementAccessor, it, by);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		this.condition.injectVisitor(injector);
		this.statement.injectVisitor(injector);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		func.traverse(this);
		AST.depthfirstTraverse(condition, func);
		AST.depthfirstTraverse(statement, func);
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Until{condition=" + condition + ", statement=" + statement + '}';
	}
}
