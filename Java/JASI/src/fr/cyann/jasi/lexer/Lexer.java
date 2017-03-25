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

import fr.cyann.jasi.exception.InvalidSymbolException;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Lexer class.<br>
 * The entry point of the lexing part of the PEG.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class Lexer implements BacktrackingIterator<Token> {

	private Term root;
	private List<Token> tokens;
	private Set<Integer> positions;
	private int index, lookaheadIndex, lastTried;
	private StringCharacterIterator it;

	/**
	 * Default constructor.
	 *
	 * @param root
	 */
	public Lexer(Term root) {
		this.root = root;
		tokens = new ArrayList<Token>();
		positions = new HashSet<Integer>();
		it = new StringCharacterIterator("");
	}

	/**
	 * The root term accessor.
	 *
	 * @return the root term
	 */
	public Term getRoot() {
		return root;
	}

	/**
	 * The entry point of the lexer.<br>
	 * Truncate the source code from String representation to list of token
	 * one.<br>
	 * This class was designed to add more functionality than Java framework
	 * tokenizer.
	 *
	 * @param source
	 */
	public void match(String source) {
		this.index = 0;
		this.lookaheadIndex = 0;
		this.lastTried = 0;

		tokens.clear();
		positions.clear();

		//CharacterIterator it = new StringCharacterIterator(source);
		it.setText(source);

		Term.initialize();

		int pos = 0;
		while (pos < it.getEndIndex()) {
			root.find(it);
			if (pos == it.getIndex()) {
				if (tokens.size() > 0) {
					throw new InvalidSymbolException(it.current(), pos, tokens.get(tokens.size() - 1));
				} else {
					throw new InvalidSymbolException(it.current(), pos);
				}
			}
			pos = it.getIndex();
		}

		tokens.add(new Token(TokenType.EOF, ""));

	}

	/**
	 * Add a new token to the token list. Part of the parse methode.<br>
	 * package modifier to permeet to LexerBuilder and LexerBuilderKey only to
	 * add a token.
	 *
	 * @param e the token to add.
	 * @return false if already token already exists in the list.
	 */
	boolean add(Token e) {
		// only one term in position, to enforce the chain of responcipility
		if (!positions.contains(e.getPos())) {
			positions.add(e.getPos());
			tokens.add(e);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the last tried token in lookahead parsing.
	 *
	 * @return the last token tried index.
	 */
	public Token lastTried() {
		if (lastTried > 0) {
			return tokens.get(lastTried);
		} else {
			return current();
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Token first() {
		index = 0;
		lookaheadIndex = 0;
		return current();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Token current() {
		return tokens.get(index);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Token next() {
		Token current = current();

		index++;
		return current;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Token cannot been removed.");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void resumeLookahead() {
		lookaheadIndex = 0;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Token currentLookahead() {
		return tokens.get(index + lookaheadIndex);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Token nextLookahead() {
		lookaheadIndex++;

		if (getLookaheadPosition() > lastTried) {
			lastTried = getLookaheadPosition();
		}
		return currentLookahead();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean hasNext() {
		return (index < tokens.size() - 1);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getPosition() {
		return index;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getLookaheadPosition() {
		return index + lookaheadIndex;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setLookaheadPosition(int pos) {
		lookaheadIndex = pos - index;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("lexer{");
		sb.append("index=");
		sb.append(index);
		sb.append(", lookaheadIndex=");
		sb.append(lookaheadIndex);
		if (index != 0) {
			sb.append(", current=");
			sb.append(current().getText());
			sb.append(", currentLookahead=");
			sb.append(currentLookahead().getText());
		}
		sb.append('}');

		return sb.toString();
	}

}
