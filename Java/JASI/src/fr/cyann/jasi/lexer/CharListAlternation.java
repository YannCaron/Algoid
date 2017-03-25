
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
 * The StringBasedAlternation class.<br>
 * Evaluate if the character is one of the characters contains in the list (string).
 * @author Yann Caron
 * @version v0.1
 */
public class CharListAlternation extends TermNode {

	private char chr;
	private String charList;

	/**
	 * Default constructor
	 * @param charList 
	 */
	public CharListAlternation(String charList) {
		this.charList = charList;
	}

	/** @inheritDoc */
	@Override
	public String getTerm() {
		return String.valueOf(chr);
	}

	/** @inheritDoc */
	@Override
	boolean find(CharacterIterator it) {
		char ch = it.current();

		if (charList.indexOf(ch) != -1) {
			chr = ch;
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
