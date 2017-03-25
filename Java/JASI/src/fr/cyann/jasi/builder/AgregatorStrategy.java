
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
package fr.cyann.jasi.builder;

import fr.cyann.jasi.parser.StatementNode;

/**
 * The AgregatorStrategy interface.<br>
 * Used to indicate to the AST builder how to agregate the differents AST in the stack.<br>
 * Based on Dependency injection practice.<br>
 * Used to separate the PEG responcibility (parse) to the AST one (execute, generate new source or interprete).
 * Creation date: 8 janv. 2012.
 * @author CyaNn 
 * @version v0.1
 */
public interface AgregatorStrategy {
		
	/**
	 * Build the AST by getting the branches from the stack and set it to appropriate attributes.
         * @param node the node to agregate
	 */
	void build(InterpreterBuilder builder, StatementNode node);

}
