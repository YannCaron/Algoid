
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
 * The Sequence class. Sequence is an AND logical composite of term. This term
 * and this one ect.... are necessary to evaluate the term.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class Sequence extends TermNode {

	private String term;

	/**
	 * @inheritDoc
	 */
	@Override
	public String getTerm() {
		return term;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	boolean find(CharacterIterator it) {
		// store current position
		int pos = it.getIndex();
		StringBuilder sb = new StringBuilder();

		int size = super.children.size();
		for (int i = 0; i < size; i++) {
			Term child = children.get(i);
			if (!child.find(it)) {
				it.setIndex(pos);
				return false;
			} else {
				sb.append(child.getTerm());
			}
		}

		term = sb.toString();
		return true;
	}
}
