
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
public class TokenStore extends StatementLeafDecorator {

	private String kind;

	/** @inheritDoc */
	@Override
	public String getName() {
		return "? " + decored.getName() + " \\" + kind + "?";
	}

	/** @inheritDoc */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		if (decored.tryParse(iterator)) {
			return true;
		} else {
			return false;
		}
	}

	/** @inheritDoc */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		Token current = iterator.current();
		boolean result = decored.parse(iterator, builder);
		if (result) {
			TokenWarehouse.getInstance().store(this.kind, current);
		}
		return result;
	}

	/**
	 * Default constructor.
	 * Based on decorator design pattern.
	 * @param statement the statement to decorate.
	 * @param kind the kind of token to store.
	 */
	public TokenStore(Statement statement, String kind) {
		super(statement);
		this.kind = kind;
	}
}
