
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

import fr.cyann.jasi.exception.InfinitRecursionException;
import fr.cyann.utils.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * The TermNode class.<br> Based on GoF composite.
 *
 * @author Yann Caron
 * @version v0.1
 */
public abstract class TermNode extends Term {

	protected List<Term> children;
	protected boolean lock;

	/**
	 * The default constructor.<br> Initialize composite list.
	 */
	public TermNode() {
		children = new ArrayList<Term>();
		lock = false;
	}

	/**
	 * The children iterator accessor.
	 *
	 * @return the list of children
	 */
	public List<Term> getChildren() {
		return children;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Term add(Term e) {
		children.add(e);
		return this;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Term remove(Term e) {
		children.remove(e);
		return this;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void initTravel() {

		// save
		boolean wasLocked = lock;

		// unlock
		lock = false;

		if (wasLocked) {
			int size = children.size();
			for (int i = 0; i < size; i++) {
				children.get(i).initTravel();
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void depthFirstTravelImpl(Method<Term, Term> method) {

		// verify if already travel
		if (lock) {
			throw new InfinitRecursionException();
		}
		lock = true;

		method.invoke(this);
		int size = children.size();
		for (int i = 0; i < size; i++) {
			try {
				children.get(i).depthFirstTravelImpl(method);
			} catch (Exception ex) {
				// do nothing
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void breadthFirstTravelImpl(Method<Term, Term> method, Queue<Term> queue) {

		// verify if already travel
		if (lock) {
			throw new InfinitRecursionException();
		}
		lock = true;

		method.invoke(this);
		int size = children.size();
		for (int i = 0; i < size; i++) {
			Term child = children.get(i);
			queue.add(children.get(i));
		}

		while (!queue.isEmpty()) {
			Term child = queue.poll();

			try {
				child.breadthFirstTravelImpl(method, queue);
			} catch (Exception ex) {
				// do nothing
			}
		}
	}
}
