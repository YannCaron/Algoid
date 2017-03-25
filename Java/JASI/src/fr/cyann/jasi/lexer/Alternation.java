
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
 * The Alternation class.<br> Alternation is an OR logical composite of term.
 * This term or this one ect....
 *
 * @author Yann Caron
 * @version v0.1
 */
public class Alternation extends TermNode {

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
		int size = super.children.size();
		for (int i = 0; i < size; i++) {
			Term child = children.get(i);
			if (child.find(it)) {
				term = child.getTerm();
				return true;
			}

		}
		return false;
	}
}
