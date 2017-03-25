
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

import fr.cyann.al.ast.interfaces.TailCache;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * The Call class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class Call<C extends Context> extends Expression<Call, C> {

	public AbstractList<Expression<? extends Expression, C>> args;
	public FunctionInstance function;
	public boolean isTailCall;
	public TailCache tailCache = TailCache.NOT_INITIALIZED;

	public Call() {
		this(new Token(TokenType.SYMBOL, "("));
	}

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public Call(Token token) {
		super(token);
		args = new ArrayList<Expression<? extends Expression, C>>();
		super.mv = new MutableVariant();
	}

	/**
	 * Add an argument to the call AST.<br>
	 * Parameter is copied into function parameters.
	 *
	 * @param e
	 */
	public void addArg(Expression<? extends Expression, C> e) {
		e.setParent(this);
		args.add(e);
	}

	/**
	 * The arguments accessor.
	 *
	 * @return
	 */
	public List<Expression<? extends Expression, C>> getArgs() {
		return args;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		return replaceOnList(this.args, it, by);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, args);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(args, func);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected String aggregateChainName() {
		return "()" + super.aggregateChainName();
	}

	@Override
	public Call nextCall() {
		return this;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Call{args=" + args + '}';
	}
}
