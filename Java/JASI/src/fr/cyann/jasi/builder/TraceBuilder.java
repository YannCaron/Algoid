
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
package fr.cyann.jasi.builder;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.parser.StatementLeafToken;
import fr.cyann.jasi.parser.StatementNode;
import java.util.List;

/**
 * The TraceBuilder class.
 * Creation date: 7 janv. 2012.
 * @author CyaNn 
 * @version v0.1
 */
public final class TraceBuilder implements InterpreterBuilder {

	private volatile static TraceBuilder singleton;

	/**
	 * Hide the constructor for singleton design pattern.
	 */
	public TraceBuilder() {
	}


	/** @inheritDoc */
	@Override
	public void build(StatementLeafToken statement, FactoryStrategy strategy) {
		if (strategy != null && !(strategy instanceof VoidFactory)) {
			System.out.println("Build leaf: " + statement.getName() + " => " + strategy.getClass().getSimpleName());
		}
	}

	/** @inheritDoc */
	@Override
	public void build(StatementNode statement, AgregatorStrategy strategy) {
		if (strategy != null && !(strategy instanceof VoidAgregator)) {
			System.out.println("Build node : " + statement.getName() + " => " + strategy.getClass().getSimpleName());
		}
	}

	@Override
	public void push(AST item) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AST poll() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AST pop() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AST peek() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
