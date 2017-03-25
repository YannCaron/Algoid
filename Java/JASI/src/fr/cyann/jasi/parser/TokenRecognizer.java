
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

/**
 * The TokenRecognizer class.
 * Recognize a stored token in the warehouse.
 * @author Yann Caron
 * @version v0.1
 */
public class TokenRecognizer extends StatementLeafToken {

	String kind;

	/**
	 * Default constructor.
	 * @param kind the kind of token to recognize.
	 */
	public TokenRecognizer(String kind) {
		super(new VoidFactory()); // default behaviour
		this.kind = kind;
	}

	/**
	 * Default constructor.
	 * @param kind the kind of token to recognize.
	 * @param strategy the strategy to execute in case of grammar matched
	 */
	public TokenRecognizer(String kind, FactoryStrategy strategy) {
		super(strategy);
		this.kind = kind;
	}

	/** @inheritDoc */
	@Override
	public String getName() {
		return "is" + kind;
	}

	/** @inheritDoc */
	@Override
	public boolean isToken(Token token) {
		return TokenWarehouse.getInstance().contains(kind, token);
	}

	/** @inheritDoc */
	@Override
	public String toBNFString() {
		return this.getName();
	}
}
