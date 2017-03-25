
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
package fr.cyann.al.ast.reference;

import fr.cyann.al.ast.Expression;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;

/**
 * The Index class.<br>
 * Represents the index part of a symbol chain. eg. a[5]
 *
 * @author Yann Caron
 * @version v0.1
 */
public class Index<C extends Context> extends Expression<Index, C> {

	public Expression<? extends Expression, C> expr;

	private final Accessor<AST> exprAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return expr;
		}

		@Override
		public void setter(AST value) {
			expr = (Expression<? extends Expression, C>) value;
		}
	};

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public Index(Token token) {
		super(token);
	}

	/**
	 * The index expression accessor.
	 *
	 * @return the expression.
	 */
	public Expression<? extends Expression, C> getExpr() {
		return expr;
	}

	/**
	 * The index expression mutator.
	 *
	 * @param expr
	 */
	public void setExpr(Expression<? extends Expression, C> expr) {
		expr.setParent(this);
		this.expr = expr;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, expr);
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		return replaceOnField(exprAccessor, it, by);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(expr, func);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected String aggregateChainName() {
		return "[]" + super.aggregateChainName();
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Index{" + "number=" + super.getToken().getText() + '}';
	}
}
