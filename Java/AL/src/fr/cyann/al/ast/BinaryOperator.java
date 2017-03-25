
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

import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.Signed;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;

/**
 * The BinaryOperator class.<br>
 * Used for all signed arithmetic calculus. e.g. 1 + 1 or 8 % 2<br>
 * Process by applying the sign to the left then right expressions. Creation
 * date: 8 janv. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class BinaryOperator<C extends Context> extends Expression<BinaryOperator<C>, C> implements Signed {

	public Expression<? extends Expression, C> left, right;

	private final Accessor<AST> leftAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return left;
		}

		@Override
		public void setter(AST value) {
			left = (Expression<? extends Expression, C>) value;
		}
	};

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
	public BinaryOperator(Token token) {
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
	 * The left expression accessor.
	 *
	 * @return
	 */
	public Expression<? extends Expression, C> getLeft() {
		return left;
	}

	/**
	 * The Right expression accessor.
	 *
	 * @return the right expression.
	 */
	public Expression<? extends Expression, C> getRight() {
		return right;
	}

	/**
	 * The Left expression mutator
	 *
	 * @param left
	 */
	public void setLeft(Expression<? extends Expression, C> left) {
		left.setParent(this);
		this.left = left;
	}

	/**
	 * The Right expression mutator
	 *
	 * @param right
	 */
	public void setRight(Expression<? extends Expression, C> right) {
		right.setParent(this);
		this.right = right;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		if (replaceOnField(leftAccessor, it, by)) {
			return true;
		}

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
		AST.inject(injector, left);
		AST.inject(injector, right);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(left, func);
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
		return "BinaryOperator{left=" + left + ", right=" + right + ", token=" + super.getToken().getText() + '}';
	}
}
