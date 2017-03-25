
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

import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.interfaces.Access;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.Cloneable;
import fr.cyann.jasi.visitor.Context;

/**
 * The Variable class.
 * @author Yann Caron
 * @version v0.1
 */
public class Variable<C extends Context> extends Expression<Variable, C> implements Cloneable<Variable<C>> {

	public Access access = null;
	public MutableVariant pointer;
	public final Integer identifier;

	/**
	 * Default constructor.
	 * @param token the token corresponding to the AST.
	 */
	public Variable(Token token) {
		super(token);
		access = Access.READ;
		identifier = Identifiers.getID(token.getText());
	}

	/** @inheritDoc */
	@Override
	protected String aggregateChainName() {
		String str = "";
		if (hasPrevious()) {
			str += ".";
		}
		str += super.getToken().getText() + super.aggregateChainName();
		return str;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return
	 */
	@Override
	public String toString() {
		return "Variable{" + "name=" + super.getToken().getText() + '}';
	}

	@Override
	public Variable<C> clone() {
		Variable<C> clone = new Variable<C>(this.getToken());
		clone.visitor = this.visitor;
		return clone;
	}
}
