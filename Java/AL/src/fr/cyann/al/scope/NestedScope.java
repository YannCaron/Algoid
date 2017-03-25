
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
import fr.cyann.jasi.scope.AbstractScope;
import fr.cyann.jasi.scope.ChainPredicate;
import fr.cyann.utils.StringUtils;
import java.util.Iterator;

/**
 The NestedScope class.<br> Implement the nested scope behaviours.
 <p>
 @param <T> the variable type.
 <p>
 @author Yann Caron
 @version v0.1
 */
public class NestedScope extends Scope {

	/**
	 The enclosing scope.
	 */
	public Scope enclosing;

	/**
	 default constructor.
	 <p>
	 @param name      the scope name to identify
	 @param enclosing the mendatory enclosing scope
	 */
	public NestedScope(String name, Scope enclosing) {
		super(name);
		this.enclosing = enclosing;
	}

	/**
	 @inheritDoc
	 */
	@Override
	public Scope getRoot() {
		return enclosing.getRoot();
	}

	/**
	 @inheritDoc
	 */
	@Override
	public void define(Integer identifier, MutableVariant variable) {
		super.define(identifier, variable);
		variable.setNature(Nature.LOCAL);
	}

	/**
	 @inheritDoc
	 */
	@Override
	public MutableVariant resolve(Integer identifier, boolean isForWrite) {

		MutableVariant result = super.variables.get(identifier);
		if (result != null) {
			return result;
		}

		// chain of responsibility
		return enclosing.resolve(identifier, isForWrite);
	}

	/**
	 Get enclosing scope
	 <p>
	 @return the nester scope
	 */
	public Scope getEnclosing() {
		return enclosing;
	}

	public void setEnclosing(Scope enclosing) {
		this.enclosing = enclosing;
	}

	/**
	 Return the string representation of the object.<br> Use is reserved to
	 debug and traces in complex recursive stack.
	 <p>
	 @return
	 */
	@Override
	public String toString() {
		// chain of responcibility
		StringBuilder sb = new StringBuilder();
		sb.append("nested ");
		sb.append(super.toString());
		sb.append(" => ");
		sb.append(enclosing);

		return sb.toString();
	}

	/**
	 @inheritDoc
	 */
	@Override
	public void debug(int indent, boolean system) {
		super.debug(indent, system);
		indent++;
		System.out.print(StringUtils.repeat("  ", indent));
		System.out.print("- nested scope of ");

		Scope enc = enclosing;
		System.out.print("\"" + enc.getName() + "\"");
		while (enc instanceof NestedScope) {
			enc = ((NestedScope) enc).enclosing;
			System.out.print(", \"" + enc.getName() + "\"");
		}
		System.out.println("");
	}

	@Override
	public ParameterScope functionScope() {
		return enclosing.functionScope();
	}

	@Override
	public NestedScope clone() {
		NestedScope clone = new NestedScope(name, enclosing);
		super.copyParametersTo(clone);
		return clone;
	}

	@Override
	public Scope cloneUntil(ChainPredicate untilPredicate) {
		NestedScope clone = clone();
		if (!untilPredicate.eval(clone)) {
			clone.enclosing = clone.enclosing.cloneUntil(untilPredicate);
		}
		return clone;
	}

	@Override
	public Scope cloneWhile(ChainPredicate whilePredicate) {
		if (whilePredicate.eval(this)) {
			NestedScope clone = clone();
			clone.enclosing = (Scope) clone.enclosing.cloneWhile(whilePredicate);
			return clone;
		}
		return this;
	}
}
