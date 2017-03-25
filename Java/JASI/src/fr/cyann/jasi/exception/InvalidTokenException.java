
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
package fr.cyann.jasi.exception;

import fr.cyann.jasi.Constants;
import fr.cyann.jasi.lexer.Token;

/**
 * The InvalidTokenException class.
 * @author Yann Caron
 * @version v0.1
 */
public class InvalidTokenException extends JASIException implements Constants {

	private Token token;

	/**
	 * Default constructor.
	 * @param token the token where exception occured.
	 * @param cls the type that generate exception
	 */
	public InvalidTokenException(Token token, Class cls) {
		super(EX_INVALID_SYMBOL_LINE_CLASS.setArgs(token.getText(), token.getLine() + 1, token.getCol() + 1, cls.getName()), token.getPos());
		this.token = token;
	}

	/**
	 * The token accessor.
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

}
