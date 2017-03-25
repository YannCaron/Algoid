
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
package fr.cyann.al.scope;

import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.Nature;
import fr.cyann.jasi.scope.ChainPredicate;
import fr.cyann.utils.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The ObjectScope class.
 *
 * @param <T> the variable type.
 * @author Yann Caron
 * @version v0.1
 */
public class ObjectScope extends NestedScope {

	private List<ObjectScope> parents;

	/**
	 * default constructor.
	 *
	 * @param ast the object AST that define the scope. (scope and object
	 * definition are linked).
	 * @param enclosing the enclosing scope.
	 */
	public ObjectScope(String name, Scope enclosing) {
		super(name, enclosing);
		parents = new ArrayList<ObjectScope>();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void define(Integer identifier, MutableVariant variable) {
		super.define(identifier, variable);
		variable.setNature(Nature.ATTRIBUTE);
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MutableVariant resolve(Integer identifier, boolean isForWrite) {
		MutableVariant result = super.variables.get(identifier);
		if (result != null) {
			return result;
		}

		// chain of responcibility on composite
		int size = parents.size();
		for (int i = 0; i < size; i++) {
			result = parents.get(i).resolve(identifier, isForWrite);
			if (result != null) {
				return result;
			}
		}

		// chain of responcibility
		if (!isForWrite) {
			return getEnclosing().resolve(identifier, isForWrite);
		}

		return null;
	}

	/**
	 * @inheritDoc
	 */
	public boolean addParent(ObjectScope e) {
		return parents.add(e);
	}

	/**
	 * @inheritDoc
	 */
	public List<ObjectScope> getParents() {
		return parents;
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		// chain of responcibility
		StringBuilder sb = new StringBuilder();
		sb.append("object (");
		sb.append(getName());
		sb.append(") [");

		boolean first = true;
		Iterator<Integer> it = variables.keySet().iterator();
		while (it.hasNext()) {
			if (!first) {
				sb.append(", ");
			}
			first = false;

			Integer item = it.next();
			sb.append(Identifiers.valueOf(item));
		}
		sb.append("]");
		if (!parents.isEmpty()) {
			sb.append(" inherits (");
			for (int i = 0; i < parents.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(parents.get(i).name);
			}
			sb.append(")");
		}
		sb.append(" => ");
		sb.append(enclosing);

		return sb.toString();
	}

	@Override
	public void debug(int indent, boolean system) {
		super.debug(indent, system);
		indent++;
		System.out.print(StringUtils.repeat("  ", indent));
		System.out.print("- object scope with supers [");
		int size = parents.size();
		for (int i = 0; i < size; i++) {
			parents.get(i).debug(indent + 1, system);
		}
		System.out.println("]");
	}

	@Override
	public ParameterScope functionScope() {
		return super.functionScope();
	}

	@Override
	public ObjectScope clone() {
		ObjectScope clone = new ObjectScope(name, enclosing);
		clone.parents = this.parents;
		super.copyParametersTo(clone);
		return clone;
	}

}
