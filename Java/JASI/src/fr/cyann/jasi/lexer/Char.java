
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
 * The Char class.<br>
 * The term is found if it is equals to character.
 * @author Yann Caron
 * @version v0.1
 */
public class Char extends TermLeaf {

	char chr;

	/**
	 * Default constructor.
	 * @param chr the character to match.
	 */
	public Char(char chr) {
		this.chr = chr;
	}

	/** @inheritDoc */
	@Override
	public String getTerm() {
		return String.valueOf(chr);
	}

	/** @inheritDoc */
	@Override
	boolean find(CharacterIterator it) {

		if (chr == it.current()) {
			it.next();
			if (chr == '\n') {
				newLine();
			} else {
				forward(1);
			}
			return true;
		} else {
			return false;
		}

	}
}
