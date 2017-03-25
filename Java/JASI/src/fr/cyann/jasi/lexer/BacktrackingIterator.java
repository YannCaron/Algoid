
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

import java.util.Iterator;

/**
 * The BacktrackingIterator interface.<br>
 * Based on GoF Iterator design pattern.<br>
 * Add backtracking faculty to java.util.Iterator interface.<br>
 * @param <E> the object to iterate
 * @author Yann Caron
 * @version v0.1
 */
public interface BacktrackingIterator<E> extends Iterator<E> {

	/**
	 * Init the iterator.<br>
	 * set all counter to 0.
	 * @return the first element of the list.
	 */
	public E first();

	/**
	 * The current element.
	 * @return the current element.
	 */
	public E current();

	/**
	 * Get the current position.
	 * @return the current position.
	 */
	public int getPosition();

	/**
	 * Get the current position of the lookahead.
	 * @return the lookahead current position.
	 */
	public int getLookaheadPosition();

	/**
	 * The current position of the lookahead setter.
	 * @param pos the new position.
	 */
	public void setLookaheadPosition(int pos);

	/**
	 * Goto first position of the lookahead.
	 */
	public void resumeLookahead();

	/**
	 * Goto next position of the lookahead and return it.
	 * @return the current lookahead after position incrementation.
	 */
	public E nextLookahead();

	/**
	 * Get the current lookahead element.
	 * @return the current lookahead element.
	 */
	public E currentLookahead();

}
