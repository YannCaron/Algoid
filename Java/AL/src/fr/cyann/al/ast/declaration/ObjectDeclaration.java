
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
import fr.cyann.al.ast.interfaces.CompositeDeclaration;
import fr.cyann.jasi.ast.interfaces.Identifiable;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * The ObjectDeclaration class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ObjectDeclaration<C extends Context> extends Declaration<ObjectDeclaration<C>, C> implements CompositeDeclaration<C>, Identifiable {

	public AbstractList<Variable<C>> superClasses;
	public AbstractList<VariableDeclaration<C>> attributs;
	public final int identity;

	/**
	 * Default constructor.
	 *
	 * @param token the token corresponding to the AST.
	 */
	public ObjectDeclaration(Token token) {
		super(token);
		superClasses = new ArrayList<Variable<C>>();
		attributs = new ArrayList<VariableDeclaration<C>>();
		identity = Identifiers.getObjectIdent();
		super.mv = new MutableVariant();
	}

	@Override
	public int getIdentity() {
		return identity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected String aggregateChainName() {
		return super.getToken().getText();
	}

	/**
	 * Add super class to object for object inheritance.<br> As it is a composite,
	 * inheritance is multiple.
	 *
	 * @param e the variable that refer the super class name.
	 */
	public void addSuperClass(Variable e) {
		e.setParent(this);
		superClasses.add(e);
	}

	/**
	 * The super classes iterator accessor.
	 *
	 * @return the super classes iterator
	 */
	public List<Variable<C>> getSuperClasses() {
		return superClasses;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void addDeclaration(VariableDeclaration<C> e) {
		e.setParent(this);
		attributs.add(e);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void removeDeclaration(String name) {
		int size = attributs.size();
		for (int i = 0; i < size; i++) {
			if (name.equals(attributs.get(i).getVar().getName())) {
				attributs.get(i).setParent(null);
				attributs.remove(i);
				return;
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isDeclarationExists(Variable<C> v) {
		int size = attributs.size();
		for (int i = 0; i < size; i++) {
			if (attributs.get(i).getVar().equals(v)) {
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
		int size = attributs.size();
		for (int i = 0; i < size; i++) {
			if (name.equals(attributs.get(i).getVar().getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(Variable<C> v) {
		int size = attributs.size();
		for (int i = 0; i < size; i++) {
			VariableDeclaration<C> attr = attributs.get(i);
			if (attr.getVar().equals(v)) {
				return attr;
			}
		}
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(String name) {
		int size = attributs.size();
		for (int i = 0; i < size; i++) {
			VariableDeclaration<C> attr = attributs.get(i);
			if (name.equals(attr.getVar().getName())) {
				return attr;
			}
		}
		return null;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public VariableDeclaration<C> getDeclaration(int index) {
		return attributs.get(index);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int declarationLength() {
		return attributs.size();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<VariableDeclaration<C>> getDeclarations() {
		return attributs;
	}

	@Override
	public int hashCode() {
		int hash = 9;
		hash = 29 * hash + identity;
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
		final ObjectDeclaration other = (ObjectDeclaration) obj;
		if (identity != other.identity) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean replaceChild(AST it, AST by) {
		if (replaceOnList(this.attributs, it, by)) {
			return true;
		}

		return replaceOnList(this.superClasses, it, by);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void injectVisitor(VisitorInjector injector) {
		super.injectVisitor(injector);
		AST.inject(injector, superClasses);
		AST.inject(injector, attributs);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
		super.depthFirstTraversal(func);
		AST.depthfirstTraverse(superClasses, func);
		AST.depthfirstTraverse(attributs, func);
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "{ObjectDeclaration#" + identity + ", superClasses=" + superClasses + ", attributs=" + attributs + '}';
	}
}
