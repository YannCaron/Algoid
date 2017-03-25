
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
package fr.cyann.jasi.visitor;

import fr.cyann.jasi.ast.AST;

/**
 * The MethodVisitor interface.
 * @param <T> the AST type to visit
 * @param <C> the Execution context when visit
 * @author Yann Caron
 * @version v0.1
 */
public interface MethodVisitor<T extends AST, C extends Context> {

	/**
	 * Visite the corresponding ast.
	 * @param ast the AST to visite.
	 */
	void visite(T ast, C context);

}
