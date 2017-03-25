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
import fr.cyann.al.ast.Expression;
import fr.cyann.al.data.Types;
import fr.cyann.al.scope.Scope;

/**
 * The VariableNotFoundException class.
 * @author Yann Caron
 * @version v0.1
 */
public class VariableNotFoundException extends ALRuntimeException implements Constants {

	/**
	 * Default constructor.
	 * @param var the variable that throw exception.
	 * @param scope the current scope to search.
	 */
	public VariableNotFoundException(Expression var, Scope scope) {
		//super(String.format("Variable [%s] not found in %s !", var.getChainName(), scope.toString()), var.getToken());
		super(EX_VARIABLE_NOT_FOUND.setArgs(var.getChainName()), var.getToken());
	}

	public VariableNotFoundException(Expression var, Types type, Scope scope) {
		//super(String.format("Variable [%s] not found for type [%s] in %s !", var.getChainName(), type.toString(), scope.toString()), var.getToken());
		super(EX_VARIABLE_NOT_FOUND_LINE.setArgs(var.getChainName(), var.getToken().getLine() + 1), var.getToken());
	}

}
