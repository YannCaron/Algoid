
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

import fr.cyann.al.ast.Expression;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.Context;

/**
 * The Assign class.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Declaration<T extends Declaration<T, C>, C extends Context> extends Expression<T, C> {

	/**
	 * Default constructor.<br>
	 * Token is mendatory.
	 * @param token te token of the AST.
	 */
	public Declaration(Token token) {
		super(token);
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return
	 */
	@Override
	public String toString() {
		return "Declaration{}";
	}
}
