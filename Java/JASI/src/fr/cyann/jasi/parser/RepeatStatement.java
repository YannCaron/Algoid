
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
 * The repeat statement node.
 * Decorator used to repeat the decorated statement.
 * @author Yann Caron.
 */
// decorator
public class RepeatStatement extends StatementLeafDecorator {

	
	/**
	 * Default constructor based on Decorator.
	 * @param decored the statement to decorate
	 */
	public RepeatStatement(Statement decored) {
		super(decored);
	}

	/**
	 * Get simple name representation
	 * @return 
	 */
	String getSimpleName() {
		return decored.getName();
	}

	/** @inheritDoc */
	@Override
	public String getName() {
		return '{' + this.getSimpleName() + '}';
	}

	/** @inheritDoc */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		boolean res = decored.tryParse(iterator);;

		if (!res) {
			return false;
		}

		while (res) {
			res = decored.tryParse(iterator);
		}

		return true;
	}

	/** @inheritDoc */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		boolean res = decored.parse(iterator, builder);;

		if (!res) {
			return false;
		}

		while (res) {
			res = decored.parse(iterator, builder);
		}

		return true;
	}
}
