
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

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.parser.StatementLeafToken;
import fr.cyann.jasi.parser.StatementNode;
import java.util.List;

/**
 * The InterpreterBuilder interface.<br>
 * Inspired from GoF builder design pattern and modified with dependency injection.<br>
 * Used to separate the PEG responcibility (parse) to the AST one (execute, generate new source or interprete).
 * Creation date: 7 janv. 2012.
 * @author CyaNn 
 * @version v0.1
 */
public interface InterpreterBuilder {

	/**
	 * Add a new AST into the top of stack.
	 *
	 * @param item the AST.
	 */
	public void push(AST item);

	/**
	 * Get and remove the last AST from the stack.
	 *
	 * @return
	 */
	public AST poll();

	/**
	 * Get and remove the last added AST from the stack.
	 *
	 * @return
	 */
	public AST pop();

	/**
	 * Get the last AST from the stack.
	 *
	 * @return
	 */
	public AST peek();

	/**
	 * Get if the stack is empty.
	 *
	 * @return true if stack is empty.
	 */
	public boolean isEmpty();

	/**
	 * Build Leaf with factory.
	 * @param statement the statement in entry.
	 * @param strategy the factory that build AST from statement leaf.
	 */
	public void build(StatementLeafToken statement, FactoryStrategy strategy);

	/**
	 * Build Node by agregating the stack ASTs.
	 * @param statement the statement in entry.
	 * @param strategy the agregator that is responcible how to do.
	 */
	public void build(StatementNode statement, AgregatorStrategy strategy);
}
