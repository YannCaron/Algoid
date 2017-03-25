/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.ast;

import fr.cyann.al.ast.interfaces.Executable;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;

/**
 * The Loop class. Creation date: 7 janv. 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Loop<C extends Context> extends AST<Loop, C> implements Executable<C> {

	public Expression<? extends Expression, C> limit;
	public Block<C> statement;

	private final Accessor<AST> limitAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return limit;
		}

		@Override
		public void setter(AST value) {
			limit = (Expression<? extends Expression, C>) value;
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
	 * @param token the if token.
	 */
	public Loop(Token token) {
		super(token);
	}

	/**
	 * Get the limit statement
	 *
	 * @return the limit statement
	 */
	public Expression<? extends Expression, C> getLimit() {
		return limit;
	}

	/**
	 * Set the limit statement
	 *
	 * @param limit
	 */
	public void setLimit(Expression<? extends Expression, C> limit) {
		limit.setParent(this);
		this.limit = limit;
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
		if (replaceOnField(limitAccessor, it, by)) {
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

		AST.inject(injector, this.limit);
		AST.inject(injector, this.statement);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		func.traverse(this);
		AST.depthfirstTraverse(limit, func);
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
		return "Loop{limit=" + limit + ", statement=" + statement + '}';
	}
}
