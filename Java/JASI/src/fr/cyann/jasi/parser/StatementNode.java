
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

import fr.cyann.jasi.exception.InfinitRecursionException;
import fr.cyann.utils.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * The node of the interpreter design pattern tree.
 *
 * @author CARONYN
 */
public abstract class StatementNode extends Statement {

	List<Statement> children;
	private boolean lock;

	/**
	 * Default constructor.
	 */
	public StatementNode() {
		children = new ArrayList<Statement>();
		lock = false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Statement get(int index) {
		return children.get(index);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public StatementNode add(Statement e) {
		children.add(e);
		return this;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public StatementNode remove(Statement e) {
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
	public void depthFirstTravelImpl(Method<Statement, Statement> method) {

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
	public void breadthFirstTravelImpl(Method<Statement, Statement> method, Queue<Statement> queue) {

		// verify if already travel
		if (lock) {
			throw new InfinitRecursionException();
		}
		lock = true;

		method.invoke(this);
		int size = children.size();
		for (int i = 0; i < size; i++) {
			queue.add(children.get(i));
		}

		while (!queue.isEmpty()) {
			Statement stmt = queue.poll();

			try {
				stmt.breadthFirstTravelImpl(method, queue);
			} catch (Exception ex) {
				// do nothing
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	void clearMemorizerImpl() {
		// verify if already travel
		if (!lock) {
			lock = true;
		int size = children.size();
		for (int i = 0; i < size; i++) {
				children.get(i).clearMemorizerImpl();
			}
		}
	}
}
