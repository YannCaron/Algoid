
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
import fr.cyann.jasi.ast.AST;

/**
 * The FactoryException class.
 * @author Yann Caron
 * @version v0.1
 */
public class FactoryException extends RuntimeException implements Constants {

	/**
	 * Default constructor.<br>
	 * Create message with original exception and class type that generate the factory exception.
	 * @param th the original exception
	 * @param cls the class
	 */
	public FactoryException(Throwable th, Class<? extends AST> cls) {
		super(EX_FACTORY.setArgs(cls.getName(), th.getMessage()).toString());
	}

}
