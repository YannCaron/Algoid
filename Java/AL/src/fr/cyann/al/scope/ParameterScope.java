
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

import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.Nature;
import fr.cyann.utils.StringUtils;

/**
 * The ParameterScope class.
 *
 * @param <T> the variable type.
 * @author Yann Caron
 * @version v0.1
 */
public class ParameterScope extends NestedScope {

	public boolean used;

	/**
	 * default constructor.
	 *
	 * @param name the scope name for identification.
	 * @param enclosing the enclosing scope.
	 */
	public ParameterScope(String name, Scope enclosing) {
		super(name, enclosing);
		used = false;
	}

	@Override
	public ParameterScope clone() {
		ParameterScope clone = new ParameterScope(name, enclosing);
		super.copyParametersTo(clone);
		return clone;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void define(Integer identifier, MutableVariant variable) {
		super.define(identifier, variable);
		variable.setNature(Nature.PARAMETER);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MutableVariant resolve(Integer identifier, boolean isForWrite) {
		if (isForWrite && super.getEnclosing() instanceof ObjectScope && super.getVariables().containsKey(identifier)) {
			return super.getEnclosing().resolve(identifier, isForWrite);
		} else {
			return super.resolve(identifier, isForWrite);
		}
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("param ");
		sb.append(super.toString());
		sb.append(" => ");
		sb.append(enclosing);

		return sb.toString();
	}

	@Override
	public void debug(int indent, boolean system) {
		System.out.print(StringUtils.repeat('\t', indent));
		System.out.println("parameter scope " + getName() + " nest of " + enclosing.getName());
		super.debug(indent, system);
	}

	@Override
	public ParameterScope functionScope() {
		return this;
	}

}
