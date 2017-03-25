
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
 * The For class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class For<C extends Context> extends AST<For<C>, C> implements Conditionable<C>, Executable<C> {

    public Expression<? extends Expression, C> decl, condition, increment;
    public Block<C> statement;

    private final Accessor<AST> declAccessor = new Accessor<AST>() {

	@Override
	public AST getter() {
	    return decl;
	}

	@Override
	public void setter(AST value) {
	    decl = (Expression<? extends Expression, C>) value;
	}
    };

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

    private final Accessor<AST> incrementAccessor = new Accessor<AST>() {

	@Override
	public AST getter() {
	    return increment;
	}

	@Override
	public void setter(AST value) {
	    increment = (Expression<? extends Expression, C>) value;
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
    public For(Token token) {
	super(token);
    }

    /**
     * The declaration accessor.
     *
     * @return the declaration AST.
     */
    public Expression<? extends Expression, C> getDecl() {
	return decl;
    }

    /**
     * The declaration mutator.
     *
     * @param decl the declaration
     */
    public void setDecl(Expression<? extends Expression, C> decl) {
	decl.setParent(this);
	this.decl = decl;
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
     * The increment AST accessor.
     *
     * @return the increment
     */
    public Expression<? extends Expression, C> getIncrement() {
	return increment;
    }

    /**
     * The increment mutator.
     *
     * @param increment the increment
     */
    public void setIncrement(Expression<? extends Expression, C> increment) {
	increment.setParent(this);
	this.increment = increment;
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
	statement.setParent(statement);
	this.statement = statement;
    }

    @Override
    protected boolean replaceChild(AST it, AST by) {
	if (replaceOnField(declAccessor, it, by)) {
	    return true;
	}

	if (replaceOnField(conditionAccessor, it, by)) {
	    return true;
	}

	if (replaceOnField(incrementAccessor, it, by)) {
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
	AST.inject(injector, this.decl);
	AST.inject(injector, this.condition);
	AST.inject(injector, this.increment);
	AST.inject(injector, this.statement);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void depthFirstTraversal(TraversalFunctor func) {
	func.traverse(this);
	AST.depthfirstTraverse(decl, func);
	AST.depthfirstTraverse(condition, func);
	AST.depthfirstTraverse(increment, func);
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
	return "For{decl=" + decl + ", condition=" + condition + ", increment=" + increment + ", statement=" + statement + '}';
    }

}
