
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
 * The TermLeaf class.<br>
 * The base class of avoid composite behaviours for leafs.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class TermLeaf extends Term {

	/** @inheritDoc */
	@Override
	public Term add(Term e) {
		throw new UnsupportedOperationException("Leaf cannot have child.");
	}

	/** @inheritDoc */
	@Override
	public Term remove(Term e) {
		throw new UnsupportedOperationException("Leaf cannot have child.");
	}
	
	/** @inheritDoc */
	@Override
	public void initTravel() {
		// do nothing
	}
	
	/** @inheritDoc */
	@Override
	public void depthFirstTravelImpl(Method<Term, Term> method) {
		method.invoke(this);
	}

	/** @inheritDoc */
	@Override
	public void breadthFirstTravelImpl(Method<Term, Term> method, Queue<Term> queue) {
		method.invoke(this);
	}
}
