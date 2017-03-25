
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
package fr.cyann.jasi.parser;

import fr.cyann.jasi.Travelable;
import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.utils.Method;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Statement interface.<br/>
 * The most level up type of the composite.<br>
 * Based on Composite / Interpreter GoF design pattern.<br>
 * Use jasy.parser.mode to determine backtracking. <br>
 * Set jasy.parser.mode=ll1 to desactivate backtracking.<br>
 * @author Yann Caron
 */
public abstract class Statement implements Travelable<Statement> {

	/**
	 * Get the statement name.
	 * @return the statement name.
	 */
	public abstract String getName();

	/**
	 * Get node by its number.<br/>
	 * Based on composite GoF design pattern.
	 * @param index the index of statement in the list.
	 * @return the child statement.
	 */
	public abstract Statement get(int index);

	/**
	 * Add node.<br/>
	 * Based on composite GoF design pattern.
	 * Based on cascade SmallTalk design pattern.
	 * @param e the child to add.
	 * @return this for cascade writing.
	 */
	public abstract StatementNode add(Statement e);

	/**
	 * Remove node.<br/>
	 * Based on composite GoF design pattern.
	 * Based on cascade SmallTalk design pattern.
	 * @param e the child to remove.
	 * @return this for cascade writing.
	 */
	public abstract StatementNode remove(Statement e);

	/**
	 * Try to recognize statements (speculating).
	 * Necessary for backtracking parser.
	 * @param iterator the token iterator.
	 * @return true if found.
	 */
	public abstract boolean tryParse(BacktrackingIterator<Token> iterator);

	/**
	 * Parse the syntax and construct AST.
	 * @param iterator the Backtracking lexer list provider.
	 * @param builder the builder to construct interpreter (trace, syntax, ast ou bycode interpreter)
	 * @return true if found.
	 */
	public abstract boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder);

	/**
	 * Return the BNF representation of the statement.\n
	 * Based on EBNF grammar specification standard.\n
	 * Reference: http://fr.wikipedia.org/wiki/Extended_Backus-Naur_Form
	 * @return the BNF representation.
	 */
	public abstract String toBNFString();

	/** @inheritDoc */
	@Override
	public final void depthFirstTravel(Method<Statement, Statement> method) {
		this.initTravel();
		this.depthFirstTravelImpl(method);
	}

	/** @inheritDoc */
	@Override
	public final void breadthFirstTravel(Method<Statement, Statement> method) {
		this.initTravel();
		this.breadthFirstTravelImpl(method, new ConcurrentLinkedQueue<Statement>());
	}

	/** 
	 * Travel the entire tree to clear memorizer.<br>
	 * Based on GoF template methode design pattern.
	 */
	public final void clearMemorizer() {
		this.initTravel();
		this.clearMemorizerImpl();
	}

	/**
	 * Travel te entire tree to clear memorizer template methode.
	 */
	abstract void clearMemorizerImpl();
}
