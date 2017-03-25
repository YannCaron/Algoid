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

import fr.cyann.al.Constants;
import fr.cyann.jasi.lexer.Token;

/**
 * The AssertException class.
 * @author Yann Caron
 * @version v0.1
 */
public class AssertException extends ALRuntimeException implements Constants {

    private String expected, constated;

    /**
     * Default constructor.
     * @param expected the expected expression
     * @param constated the constated expression
     * @param token the token where the exception was thrown
     */
    public AssertException(String expected, String constated, Token token) {
        super(EX_ASSERT.setArgs(expected, constated, token.getLine(), token.getCol()), token);
        this.expected = expected;
        this.constated = constated;
    }

    /**
     * The Expected result accessor.
     * @return The expected accessor string.
     */
    public String getExpected() {
        return expected;
    }

    /**
     * The Constated result accessor.
     * @return the constated result string.
     */
    public String getConstated() {
        return constated;
    }
}
