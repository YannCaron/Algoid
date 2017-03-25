
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
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Accessor;
import java.util.ArrayList;
import java.util.List;

/**
 * The If class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class If<C extends Context> extends Expression<If<C>, C> implements Conditionable<C>, Executable<C> {

	public Expression<? extends Expression, C> condition;
	public Block<C> trueStatement;
	public List<If<C>> elseIfs;
	public Block<C> falseStatement;

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
	private final Accessor<AST> trueStatementAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return trueStatement;
		}

		@Override
		public void setter(AST value) {
			trueStatement = (Block<C>) value;
		}
	};

	private final Accessor<AST> falseStatementAccessor = new Accessor<AST>() {

		@Override
		public AST getter() {
			return falseStatement;
		}

		@Override
		public void setter(AST value) {
			falseStatement = (Block<C>) value;
		}
	};

	/**
	 * Default constructor.
	 *
	 * @param token the if token.
	 */
	public If(Token token) {
		super(token);
		super.mv = new MutableVariant();
		elseIfs = new ArrayList<If<C>>();
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
	 * The else AST accessor.
	 *
	 * @return else AST
	 */
	public Block<C> getElseStatement() {
		return falseStatement;
	}

	/**
	 * The false AST mutator.
	 *
	 * @param falseStatement the false statement
	 */
	public void setElseStatement(Block<C> falseStatement) {
		falseStatement.setParent(this);
		this.falseStatement = falseStatement;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Block<C> getStatement() {
		return trueStatement;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setStatement(Block<C> statement) {
		statement.setParent(this);
		this.trueStatement = statement;
	}

	/**
	 * Append else if AST to the composite.
	 *
	 * @param e the if
	 * @return true if ok
	 */
	public boolean addElseIf(If<C> e) {
		e.setParent(this);
		return elseIfs.add(e);
	}

	/**
	 * Remove specific else if from composite.
	 *
	 * @param e the if
	 * @return true if ok
	 */
	public boolean removeElseIf(If<C> e) {
		e.setParent(null);
		return elseIfs.remove(e);
	}

	/**
	 * Get the list of else if.
	 *
	 * @return the list
	 */
	public List<If<C>> getElseIfs() {
		return elseIfs;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		if (replaceOnField(conditionAccessor, it, by)) {
			return true;
		}

		if (replaceOnField(trueStatementAccessor, it, by)) {
			return true;
		}

		for (If<C> _elseIf : elseIfs) {
			boolean res = _elseIf.replaceChild(it, by);
			if (res) {
				return true;
			}
		}

		if (replaceOnField(falseStatementAccessor, it, by)) {
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
		this.condition.injectVisitor(injector);
		this.trueStatement.injectVisitor(injector);

		AST.inject(injector, elseIfs);
		AST.inject(injector, falseStatement);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		func.traverse(this);
		AST.depthfirstTraverse(condition, func);
		AST.depthfirstTraverse(trueStatement, func);
		AST.depthfirstTraverse(elseIfs, func);
		AST.depthfirstTraverse(falseStatement, func);
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "If{condition=" + condition + ", trueStatement=" + trueStatement + ", elseIfs=" + elseIfs + ", falseStatement=" + falseStatement + '}';
	}

}
