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
import fr.cyann.al.data.Types;
import fr.cyann.al.visitor.RuntimeContext;

/**
 * The ConvertionException class.
 * @author Yann Caron
 * @version v0.1
 */
public class UnexpectedTypeException extends ALRuntimeException implements Constants {

	/**
	 * Default constructor.
	 */
	public UnexpectedTypeException() {
		super(EX_UNEXPECTED_TYPE, RuntimeContext.currentAST.getToken());
	}

	/**
	 * Constructor for typed convertion exception.
	 * @param type the dynamic variant type.
	 * @param expected  the expected one.
	 */
	public UnexpectedTypeException(Types type) {
		super(EX_UNEXPECTED_TYPE_TYPE.setArgs(type), RuntimeContext.currentAST.getToken());
	}
}
