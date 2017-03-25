
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

import fr.cyann.utils.Method;
import java.util.Queue;

/**
 * The TermLeafDecorator class.<br>
 * The decorator base class.<br>
 * Based on GoF Decorator design pattern.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class TermLeafDecorator extends TermLeaf {

	/** The decored term */
	protected Term decored;

	/**
	 * Default constructor.
	 * @param decored the decored term is mandatory.
	 */
	public TermLeafDecorator(Term decored) {
		this.decored = decored;
	}

	/** @inheritDoc */
	@Override
	public void initTravel() {
		decored.initTravel();
	}

	/** @inheritDoc */
	@Override
	public void depthFirstTravelImpl(Method<Term, Term> method) {
		method.invoke(this);
		decored.depthFirstTravelImpl(method);
	}

	/** @inheritDoc */
	@Override
	public void breadthFirstTravelImpl(Method<Term, Term> method, Queue<Term> queue) {
		method.invoke(this);
		decored.breadthFirstTravelImpl(method, queue);
	}
}
