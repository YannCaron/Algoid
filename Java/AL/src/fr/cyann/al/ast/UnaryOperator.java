
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

import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.Signed;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;

/**
 * The UnaryOperator class. Creation date: 8 janv. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class UnaryOperator<C extends Context> extends Expression<UnaryOperator, C> implements Signed {

	public Expression<? extends Expression, C> right;

	private final Accessor<AST> rightAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return right;
		}

		@Override
		public void setter(AST value) {
			right = (Expression<? extends Expression, C>) value;
		}
	};

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public UnaryOperator(Token token) {
		super(token);
		super.mv = new MutableVariant();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getSign() {
		return super.getToken().getText();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getChainName() {
		return getSign();
	}

	/**
	 * Get the right operand expression of the operation.
	 *
	 * @return the right operand
	 */
	public Expression<? extends Expression, C> getRight() {
		return right;
	}

	/**
	 * Set the right operand expression of the operation.
	 *
	 * @param right the value
	 */
	public void setRight(Expression<? extends Expression, C> right) {
		right.setParent(this);
		this.right = right;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		if (replaceOnField(rightAccessor, it, by)) {
			return true;
		}

		return super.replaceChild(it, by);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, right);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(right, func);
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "UnaryOperator{right=" + right + '}';
	}
}
