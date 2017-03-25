
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
package fr.cyann.jasi.lexer;

import fr.cyann.jasi.Travelable;
import fr.cyann.utils.Method;
import java.text.CharacterIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Term interface.<br>
 * Represent the parts caracter of the sentence.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class Term implements Travelable<Term> {

	private static int col;
	private static int line;

	/**
	 * Column of source code position accessor.
	 * @return the column position.
	 */
	protected static int getCol() {
		return col;
	}

	/**
	 * Line of source code position accessor.
	 * @return the line position.
	 */
	protected static int getLine() {
		return line;
	}

	/**
	 * Initialize the object and set value to begin.
	 */
	protected static void initialize() {
		col = 0;
		line = 0;
	}

	/**
	 * Jump forward in position counter.
	 * @param i the jume value.
	 */
	protected static void forward(int i) {
		col += i;
	}

	/**
	 * Jump to new line.
	 */
	protected static void newLine() {
		col = 0;
		line++;
	}

	/**
	 * Add node.<br/>
	 * Based on composite GoF design pattern.<br>
	 * Based on cascade SmallTalk design pattern.
	 * @param e the child to add.
	 * @return this for cascade writing.
	 */
	public abstract Term add(Term e);

	/**
	 * Remove node.<br/>
	 * Based on composite GoF design pattern.
	 * Based on cascade SmallTalk design pattern.
	 * @param e the child to remove.
	 * @return this for cascade writing.
	 */
	public abstract Term remove(Term e);

	/**
	 * Get the term string value.
	 * @return the term.
	 */
	public abstract String getTerm();

	/**
	 * Find the term and append to lexer.
	 * @param it the source caracter iterator.
	 * @return return true if found. 
	 */
	abstract boolean find(CharacterIterator it);

	/** @inheritDoc */
	@Override
	public final void depthFirstTravel(Method<Term, Term> method) {
		this.initTravel();
		this.depthFirstTravelImpl(method);
	}

	/** @inheritDoc */
	@Override
	public final void breadthFirstTravel(Method<Term, Term> method) {
		this.initTravel();
		this.breadthFirstTravelImpl(method, new ConcurrentLinkedQueue<Term>());
	}
}
