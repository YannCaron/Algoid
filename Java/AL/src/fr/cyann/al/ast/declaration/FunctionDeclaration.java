
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
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.interfaces.Executable;
import fr.cyann.al.ast.interfaces.CompositeDeclaration;
import fr.cyann.jasi.ast.interfaces.Identifiable;
import fr.cyann.jasi.ast.interfaces.Scopeable;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.al.scope.ParameterScope;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.Cloneable;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.utils.Accessor;
import java.util.ArrayList;
import java.util.List;

/**
 * The Call class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class FunctionDeclaration<C extends Context> extends Declaration<FunctionDeclaration<C>, C> implements Scopeable<ParameterScope>, CompositeDeclaration<C>, Executable<C>, Identifiable, Cloneable<FunctionDeclaration<C>> {

	public Block<C> statement;
	public ArrayList<VariableDeclaration<C>> params;
	public ParameterScope scope;
	public boolean lazyArgEvaluation;
	public final int identity;
	public boolean system, isCloned;

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
	public FunctionDeclaration(Token token) {
		super(token);
		super.mv = new MutableVariant();
		params = new ArrayList<VariableDeclaration<C>>();
		lazyArgEvaluation = false;
		identity = Identifiers.getObjectIdent();
		system = false;
		isCloned = false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getIdentity() {
		return identity;
	}

	public boolean isLazyArgEvaluation() {
		return lazyArgEvaluation;
	}

	public void setLazyArgEvaluation(boolean lazyArgEvaluation) {
		this.lazyArgEvaluation = lazyArgEvaluation;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ParameterScope getScope() {
		return scope;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setScope(ParameterScope scope) {
		this.scope = scope;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected String aggregateChainName() {
		return super.getToken().getText();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void addDeclaration(VariableDeclaration<C> e) {
		e.setParent(this);
		params.add(e);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void removeDeclaration(String name) {
		for (int i = 0; i < params.size(); i++) {
			if (name.equals(params.get(i).getVar().getName())) {
				params.get(i).setParent(null);
				params.remove(i);
				return;
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isDeclarationExists(Variable v) {

		int size = params.size();
		for (int i = 0; i < size; i++) {
			if (((VariableDeclaration) params.get(i)).getVar().equals(v)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isDeclarationExists(String name) {

		int size = params.size();
		for (int i = 0; i < size; i++) {
			if (name.equals(((VariableDeclaration) params.get(i)).getVar().getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(Variable v) {
		int size = params.size();
		for (int i = 0; i < size; i++) {
			VariableDeclaration param = params.get(i);
			if (param.getVar().equals(v)) {
				return param;
			}
		}
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(String name) {
		int size = params.size();
		for (int i = 0; i < size; i++) {
			VariableDeclaration param = params.get(i);
			if (name.equals(param.getVar().getName())) {
				return param;
			}
		}
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int declarationLength() {
		return params.size();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(int index) {
		return params.get(index);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<VariableDeclaration<C>> getDeclarations() {
		return params;
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
		if (replaceOnField(statementAccessor, it, by)) {
			return true;
		}

		return replaceOnList(this.params, it, by);
	}

	@Override
	public int hashCode() {
		int hash = 8;
		hash = 29 * hash + this.identity;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FunctionDeclaration other = (FunctionDeclaration) obj;
		if (identity != other.identity) {
			return false;
		}
		return true;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, params);
		AST.inject(injector, statement);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(statement, func);
		AST.depthfirstTraverse(params, func);
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "FunctionDeclaration{scope=" + scope + ", params=" + params + ", statement=" + statement + '}';
	}

	@Override
	public FunctionDeclaration<C> clone() {
		FunctionDeclaration clone = new FunctionDeclaration(getToken());
		clone.statement = statement;

		// clone all parameters
		int size = params.size();
		for (int i = 0; i < size; i++) {
			clone.addDeclaration(params.get(i).clone());
		}

		clone.visitor = visitor;
		return clone;
	}
}
