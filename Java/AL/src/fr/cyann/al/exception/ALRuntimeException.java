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
package fr.cyann.al.exception;

import fr.cyann.jasi.exception.MultilingMessage;
import fr.cyann.al.Constants;
import fr.cyann.jasi.lexer.Token;

/**
 * The ALRuntimeException class.
 * @author Yann Caron
 * @version v0.1
 */
public class ALRuntimeException extends RuntimeException implements Constants {

	private Token token;

	/**
	 * The token accessor.
	 * @return the token where exception occured.
	 */
	public Token getToken() {
		return token;
	}

	/** @inheritDoc */
	public ALRuntimeException(Throwable cause, Token token) {
		super(cause);
		this.token = token;
	}

	/** @inheritDoc */
	public ALRuntimeException(MultilingMessage message, Throwable cause, Token token) {
		super(message.toString(), cause);
		this.token = token;
	}

	/** @inheritDoc */
	public ALRuntimeException(MultilingMessage message, Token token) {
		super(message.toString());
		this.token = token;
	}

	/** @inheritDoc */
	public ALRuntimeException(Token token) {
		this.token = token;
	}

}
