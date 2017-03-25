
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
package fr.cyann.al.ast.interfaces;

import fr.cyann.al.ast.Block;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.visitor.Context;

/**
 * The Executable interface.<br>
 * Add the capability to AST to have statement to execute.
 * @author Yann Caron
 * @version v0.1
 */
public interface Executable<C extends Context> {

	/**
	 * The statement accessor.
	 * @return the statement.
	 */
	public Block<C> getStatement();

	/**
	 * The statement mutator.
	 * @param statement the statement.
	 */
	public void setStatement(Block<C> statement);
}
