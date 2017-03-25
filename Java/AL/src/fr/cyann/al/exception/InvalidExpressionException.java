/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.exception;

import fr.cyann.al.Constants;
import fr.cyann.al.ast.Expression;

/**
 * The InvalidExpressionException class.
 * Creation date: 30 mars 2013.
 * @author CyaNn
 * @version v0.1
 */
public class InvalidExpressionException extends ALRuntimeException implements Constants {

	public InvalidExpressionException(Expression ast) {
		super(EX_INVALID_EXPRESSION.setArgs(ast.getName()), ast.getToken());
	}
}
