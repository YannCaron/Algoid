
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
 * The optional statement class. Decorator to set sub statement optional.
 *
 * @author Yann Caron
 */
// decorator
public class OptionalStatement extends StatementLeafDecorator {

	private Memorizer memorizer;

	/**
	 * Default constructor based on Decorator.
	 *
	 * @param statement the statement to decorate
	 */
	public OptionalStatement(Statement statement) {
		super(statement);
		memorizer = new Memorizer();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getName() {
		return "[" + decored.getName() + "]";
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		int pos = iterator.getLookaheadPosition();

		if (decored.tryParse(iterator)) {
			memorizer.store(pos, decored);
		} else {
			iterator.setLookaheadPosition(pos);
		}
		return true;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		Statement memo = memorizer.get(iterator.getPosition());

		if (memo != null) {
			return memo.parse(iterator, builder);
		}
		return true;
	}

	/** @inheritDoc */
	@Override
	void clearMemorizerImpl() {
		super.clearMemorizerImpl();
		memorizer.clear();
	}
}
