
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
import fr.cyann.jasi.builder.FactoryStrategy;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.utils.Method;
import java.util.Queue;

/**
 * The StatementLeafToken class.
 * 
 * @author Yann Caron
 * @version v0.1
 */
public abstract class StatementLeafToken extends StatementLeaf {

	private Token token;
	private FactoryStrategy strategy;

	/**
	 * Default constructor.
	 * @param strategy the strategy to execute in case of grammar matched
	 */
	public StatementLeafToken(FactoryStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * The token accessor.
	 * @return the token.
	 */
	public Token getToken() {
		return token;
	}

	/** @inheritDoc */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		boolean ret = isToken(iterator.currentLookahead());
		if (ret) {
			iterator.nextLookahead();

			// bypass ctrl
			while (iterator.currentLookahead().getType().isIgoreParsing()) {
				iterator.nextLookahead();
			}
		}

		return ret;
	}

	/** @inheritDoc */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		this.token = iterator.current();
		boolean res = isToken(this.token);
		if (res) {
			iterator.next();

			if (builder != null) {
				builder.build(this, strategy);
			}

			// bypass ctrl
			while (iterator.current().getType().isIgoreParsing()) {
				iterator.next();
			}

			return true;
		} else {
			return false;
		}

	}

	/**
	 * Verify if the token correspond to the expected one.
	 * Template methode design pattern.
	 * @param token The token to verify.
	 * @return True if token is the expected one.
	 */
	public abstract boolean isToken(Token token);

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return 
	 */
	@Override
	public String toString() {
		return token.getText();
	}

	/** @inheritDoc */
	@Override
	public void initTravel() {
		// do nothing
	}

	/** @inheritDoc */
	@Override
	public void depthFirstTravelImpl(Method<Statement, Statement> method) {
		method.invoke(this);
	}

	/** @inheritDoc */
	@Override
	public void breadthFirstTravelImpl(Method<Statement, Statement> method, Queue<Statement> queue) {
		method.invoke(this);
	}

	/** @inheritDoc */
	@Override
	void clearMemorizerImpl() {
		// do nothing
	}
}
