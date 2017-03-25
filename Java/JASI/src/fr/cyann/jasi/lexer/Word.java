
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
import java.text.StringCharacterIterator;

/**
 * The Word class.<br>
 * Designed make capability to find a word (sequence of character) in source.
 * @author Yann Caron
 * @version v0.1
 */
public class Word extends TermLeaf {

	private String term;
	private String word;
	private StringCharacterIterator match;

	/**
	 * Default constructor. The word to find is mendator.
	 * @param word the word to find.
	 */
		public Word(String word) {
		this.word = word;
		match = new StringCharacterIterator("");
	}

	/** @inheritDoc */
	@Override
	public String getTerm() {
		return term;
	}

	/** @inheritDoc */
	@Override
	boolean find(CharacterIterator it) {

		// store current position
		int pos = it.getIndex();
		term = "";

		// try
		//CharacterIterator match = new StringCharacterIterator(word);
		match.setText(word);
		for (char m = match.first(); m != CharacterIterator.DONE; m = match.next()) {
			char c = it.current();
			if (c == CharacterIterator.DONE || c != m) {
				// restore
				it.setIndex(pos);
				return false;
			}
			it.next();
		}

		forward(it.getIndex() - pos);
		term = word;
		return true;
	}

}
