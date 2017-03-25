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
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.jasi.ast.AST;

/**
 * The InvalidNumberOfParameterException class.
 * Creation date: 15 mai 2012.
 * @author CyaNn
 * @version v0.1
 */
public class InvalidNumberOfParameterException extends ALRuntimeException implements Constants {

	private AST ast;

	/**
	 * The AST accessor
	 * @return the concerned AST
	 */
	public AST getAst() {
		return ast;
	}

	/**
	 * Default constructor.
	 * @param ast the concerned AST
	 * @param expected the expected number of argument
	 */
	public InvalidNumberOfParameterException(Call ast, int expected) {
		super(EX_INVALID_NUMBER_OF_PARAMETER.setArgs(ast.getChainName(), expected, ast.getArgs().size()), ast.getToken());
		this.ast = ast;
	}

	public InvalidNumberOfParameterException(FunctionDeclaration ast, int expected) {
		super(EX_INVALID_NUMBER_OF_PARAMETER.setArgs(ast.getChainName(), expected, ast.getDeclarations().size()), ast.getToken());
		this.ast = ast;
	}
}
