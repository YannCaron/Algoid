
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
package fr.cyann.al.ast.declaration;

import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.Cloneable;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.utils.Accessor;

/**
 * The AttributeDeclaration class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class VariableDeclaration<C extends Context> extends Declaration<VariableDeclaration<C>, C> implements Cloneable<VariableDeclaration<C>> {

    public Variable<C> var;
    public Expression<? extends Expression, C> expr;

    private final Accessor<AST> varAccessor = new Accessor<AST>() {

        @Override
        public AST getter() {
            return var;
        }

        @Override
        public void setter(AST value) {
            var = (Variable<C>) value;
        }
    };

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
    public VariableDeclaration(Token token) {
        super(token);
        super.mv = new MutableVariant();
    }

    /**
     * The variable accessor.
     *
     * @return the variable
     */
    public Variable<C> getVar() {
        return var;
    }

    /**
     * The variable mutator
     *
     * @param var the value
     */
    public void setVar(Variable<C> var) {
        var.setParent(var);
        this.var = var;
    }

    /**
     * The expression accessor.
     *
     * @return the expression.
     */
    public Expression<? extends Expression, C> getExpr() {
        return expr;
    }

    /**
     * The expression mutator.
     *
     * @param expr the expression.
     */
    public void setExpr(Expression<? extends Expression, C> expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    protected boolean replaceChild(AST it, AST by) {
        if (replaceOnField(varAccessor, it, by)) {
            return true;
        }

        return replaceOnField(exprAccessor, it, by);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void injectVisitor(VisitorInjector injector) {
        super.injectVisitor(injector);
        AST.inject(injector, var);
        AST.inject(injector, expr);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void depthFirstTraversal(TraversalFunctor func) {
        super.depthFirstTraversal(func);
        AST.depthfirstTraverse(var, func);
        AST.depthfirstTraverse(expr, func);
    }

    /**
     * Return the string representation of the object.<br>
     * Use is reserved to debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
        return "VariableDeclaration{name=" + var.partName + ", expr=" + expr + '}';
    }

    @Override
    public VariableDeclaration<C> clone() {
        VariableDeclaration<C> clone = new VariableDeclaration<C>(getToken());
        clone.visitor = this.visitor;

        clone.var = var.clone();
        clone.expr = expr;

        return clone;
    }
}
