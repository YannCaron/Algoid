
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
 * The InvalidSymbolException class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class InvalidSymbolException extends JASIException implements Constants {

	private char chr;

	/**
	 * Constructor for char.
	 * @param chr the char that throw exception.
	 * @param pos the position of the character in source.
	 */
	public InvalidSymbolException(char chr, int pos) {
		super(EX_INVALID_SYMBOL.setArgs(chr, pos + 1), pos);
		this.chr = chr;
	}

	/**
	 * Constructor for token.
	 *
	 * @param chr the char that throw exception.
	 * @param pos the position of the character in source.
	 * @param token the last token (nearest) before error.
	 */
	public InvalidSymbolException(char chr, int pos, Token token) {
		super(EX_INVALID_SYMBOL_LINE.setArgs(chr, token.getLine() + 1, token.getCol() + token.getLength() + 1), pos);
		this.chr = chr;
	}

	/**
	 * Get the char where exception occured.
	 *
	 * @return the char
	 */
	public char getChar() {
		return chr;
	}

}
