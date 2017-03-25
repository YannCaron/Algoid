/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.ast;

import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;

/**
 * The default expression class implementation. Null object.
 */
public class NullExpression<C extends Context> extends Expression<NullExpression<C>, C> {

	public NullExpression() {
		this(new Token(TokenType.SYMBOL, ""));
	}

	/**
	 * Default constructor.
	 */
	public NullExpression(Token token) {
		super(token);
		super.mv = new MutableVariant();
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "NullExpression{}";
	}
}
