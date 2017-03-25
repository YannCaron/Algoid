
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
 * The LexerBuilder class.<br>
 * Based on Builder (or Factory) and Decorator GoF Design pattern.<br>
 * @author Yann Caron
 * @version v0.1
 */
public class LexerBuilder extends TermLeafDecorator {

	private TokenType tokenType;
	private Lexer lexer;
	private String term;

	/**
	 * Default Constructor.
	 * @param decored the term to decorate (Based on GoF Decorator)
	 * @param tokenType the token type used for token creation.
	 * @param lexer the lexer where to add the created token.
	 */
	public LexerBuilder(Term decored, TokenType tokenType, Lexer lexer) {
		super(decored);
		this.tokenType = tokenType;
		this.lexer = lexer;
	}

	/** @inheritDoc */
	@Override
	public String getTerm() {
		return term;
	}

	/** @inheritDoc */
	@Override
	public boolean find(CharacterIterator it) {
		int pos = it.getIndex();
		int col = getCol();

		boolean result = decored.find(it);
		if (result) {
			term = decored.getTerm();
			lexer.add(new Token(tokenType, decored.getTerm(), getLine(), col, pos));
		}

		return result;
	}
}
