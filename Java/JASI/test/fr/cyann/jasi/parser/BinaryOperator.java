
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

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.Signed;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;

/**
 * The BinaryOperator class.<br>
 * Used for all signed arithmetic calculus. e.g. 1 + 1 or 8 % 2<br>
 * Process by applying the sign to the left then right expressions.
 * Creation date: 8 janv. 2012.
 * @author CyaNn
 * @version v0.1
 */
public class BinaryOperator<C extends Context> extends AST<BinaryOperator<C>, C> implements Signed {

	public Expression<Expression, C> left, right;

	/**
	 * Default constructor.
	 * @param token the token corresponding to the AST.
	 */
	public BinaryOperator(Token token) {
		super(token);
	}

	/** @inheritDoc */
	@Override
	public String getSign() {
		return super.getToken().getText();
	}

	/** @inheritDoc */
	@Override
	public String getName() {
		return getSign();
	}

	/**
	 * The left expression accessor.
	 * @return
	 */
	public Expression<Expression, C> getLeft() {
		return left;
	}

	/**
	 * The Right expression accessor.
	 * @return the right expression.
	 */
	public Expression<Expression, C> getRight() {
		return right;
	}

	/**
	 * The Left expression mutator
	 * @param left
	 */
	public void setLeft(Expression<Expression, C> left) {
		this.left = left;
	}

	/**
	 * The Right expression mutator
	 * @param right
	 */
	public void setRight(Expression<Expression, C> right) {
		this.right = right;
	}

	/** @inheritDoc */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, left);
		AST.inject(injector, right);
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return
	 */
	@Override
	public String toString() {
		return "BinaryOperator{left=" + left + ", right=" + right + ", token=" + super.getToken().getText() + '}';
	}

	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean replaceChild(AST it, AST by) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
