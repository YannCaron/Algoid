
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

import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;

/**
 * The IdentStore class.
 * Based on decorator design pattern.
 * @author Yann Caron
 * @version v0.1
 */
public class OnceCloseStatement extends StatementLeafDecorator {

	private OnceStatement once = null;

	/** @inheritDoc */
	@Override
	public String getName() {
		return decored.getName();
	}

	/** @inheritDoc */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		if (decored.tryParse(iterator)) {
			once.decrement();
			return true;
		}
		return false;
	}

	/** @inheritDoc */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		return decored.parse(iterator, builder);
	}

	/**
	 * Default constructor.
	 * Based on decorator design pattern.
	 * @param statement the statement to decorate.
	 * @param kind the kind of token to store.
	 */
	public OnceCloseStatement(Statement statement, OnceStatement once) {
		super(statement);
		this.once = once;
	}
}
