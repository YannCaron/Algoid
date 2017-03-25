
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

import java.text.CharacterIterator;

/**
 * The RepeatTerm class.<br>
 * Repeat the decored term.
 * @author Yann Caron
 * @version v0.1
 */
public class RepeatTerm extends TermLeafDecorator {

	private String term;

	/**
	 * Default constructor.
	 * @param decored the decored (repeated) term.
	 */
	public RepeatTerm(Term decored) {
		super(decored);
	}

	/** @inheritDoc */
	@Override
	public String getTerm() {
		return term;
	}

	/** @inheritDoc */
	@Override
	boolean find(CharacterIterator it) {
		StringBuilder sb = new StringBuilder("");

		while (decored.find(it)) {
			sb.append(decored.getTerm());
		}

		term = sb.toString();
		return true;
	}
}
