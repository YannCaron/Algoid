
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

import fr.cyann.utils.Method;
import java.util.Queue;

/**
 * The StatementLeafDecorator class.
 * Based on GoF decorator design pattern
 * @author Yann Caron
 * @version v0.1
 */
public abstract class StatementLeafDecorator extends StatementLeaf {

	/** The decored object. */
	protected Statement decored;

	/**
	 * Default constructor.
	 * @param decored the statement to decorate
	 */
	public StatementLeafDecorator(Statement decored) {
		this.decored = decored;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return 
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	/** @inheritDoc */
	@Override
	public String toBNFString() {
		return this.getName();
	}

	/** @inheritDoc */
	@Override
	public void initTravel() {
		decored.initTravel();
	}

	/** @inheritDoc */
	@Override
	public void depthFirstTravelImpl(Method<Statement, Statement> method) {
		method.invoke(this);
		decored.depthFirstTravelImpl(method);
	}

	/** @inheritDoc */
	@Override
	public void breadthFirstTravelImpl(Method<Statement, Statement> method, Queue<Statement> queue) {
		method.invoke(this);
		decored.breadthFirstTravelImpl(method, queue);
	}

	/** @inheritDoc */
	@Override
	void clearMemorizerImpl() {
		this.decored.clearMemorizerImpl();
	}
}
