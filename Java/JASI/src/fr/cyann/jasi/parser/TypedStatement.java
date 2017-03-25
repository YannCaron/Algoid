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

import fr.cyann.jasi.builder.VoidFactory;
import fr.cyann.jasi.builder.FactoryStrategy;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;

/**
 * The TokenStatement class.
 * Creation date: 28 déc. 2011.
 * Find statement by its token type onyl.
 * @author CyaNn 
 * @version v0.1
 */
public class TypedStatement extends StatementLeafToken {

	private TokenType type;

	/** @inheritDoc */
	@Override
	public boolean isToken(Token token) {
		return type.equals(token.getType());
	}

	/** @inheritDoc */
	@Override
	public String getName() {
		return type.toString();
	}

	/** @inheritDoc */
	@Override
	public String toBNFString() {
		return getName();
	}

	/**
	 * Default constructor.
	 * @param type the tokentype to recognize.
	 */
	public TypedStatement(TokenType type) {
		super(new VoidFactory()); // default behaviour
		this.type = type;
	}

	/**
	 * Default constructor.
	 * @param type the tokentype to recognize.
	 * @param strategy the strategy to execute in case of grammar matched
	 */
	public TypedStatement(TokenType type, FactoryStrategy strategy) {
		super(strategy);
		this.type = type;
	}
}
