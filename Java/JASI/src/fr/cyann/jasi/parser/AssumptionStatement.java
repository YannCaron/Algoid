
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
import fr.cyann.jasi.lexer.Term;
import fr.cyann.jasi.lexer.Token;

/**
 * The Assumption statement class.<br> Used to teste some grammar e.g. 'a' | 'b'
 * | 'c'
 *
 * @author CARONYN
 */
public class AssumptionStatement extends StatementNode {

	private Memorizer memorizer;
	private String name;

	/**
	 * Default constructor.<br> Use jasy.parser.mode to determine backtracking.
	 * <br> Set jasy.parser.mode=ll1 to desactivate backtracking and set value to
	 * backtrack to activate it.<br>
	 *
	 * @param name The mendatory and immutable assumption name.
	 */
	public AssumptionStatement(String name) {
		this.name = name;
		this.memorizer = new Memorizer();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean tryParse(BacktrackingIterator<Token> iterator) {
		int pos = iterator.getLookaheadPosition();

		int size = super.children.size();
		for (int i = 0; i < size; i++) {
			Statement child = children.get(i);
			iterator.setLookaheadPosition(pos);

			// speculate
			if (child.tryParse(iterator)) {
				// memorize
				memorizer.store(pos, child);
				return true;
			}
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
		Statement memo = memorizer.get(iterator.getPosition());

		if (memo != null) {
			return memo.parse(iterator, builder);
		} else {
			int size = super.children.size();
			for (int i = 0; i < size; i++) {
				Statement child = children.get(i);
				iterator.resumeLookahead();
				if (child.tryParse(iterator)) {
					if (child.parse(iterator, builder)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	void clearMemorizerImpl() {
		super.clearMemorizerImpl();
		memorizer.clear();
	}

	/**
	 * Return the string representation of the object.<br> Use is reserved to
	 * debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toBNFString() {
		StringBuilder sb = new StringBuilder();

		int size = super.children.size();
		for (int i = 0; i < size; i++) {
			Statement child = children.get(i);
			if (sb.length() > 0) {
				sb.append(" | ");
			}

			sb.append(child.getName());
		}

		sb.insert(0, "= ");
		sb.insert(0, getName());

		return sb.toString();
	}
}
