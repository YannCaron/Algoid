
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
import fr.cyann.jasi.parser.Statement;

/**
 * The InvalidGrammarException class.
 * @author Yann Caron
 * @version v0.1
 */
public class InvalidGrammarException extends JASIException implements Constants {

	private final Token token;

	/**
	 * Default constructor.
	 * @param token the token that throw the exception.
	 */
	public InvalidGrammarException(Token token) {
		super(EX_INVALID_GRAMMAR.setArgs(token.getText(), token.getLine() + 1, token.getCol() + 1), token.getPos());
		this.token = token;
	}

	/**
	 * get the token where the exception occured.
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

}
