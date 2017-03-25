
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

/**
 * The syntax token representation.
 * @author CARONYN
 */
public class Token {

	private TokenType type;
	private String text;
	private int line, col, pos;

	/**
	 * Type getter.
	 * @return the token's type.
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Text getter.
	 * @return the token's text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * The text mutator.
	 * @param text the text to change.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The line number getter.<br>
	 * On witch line the token was found.
	 * @return the line number.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * The column number getter.<br>
	 * On witch column the token was found.
	 * @return the column number.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * The position number getter.<br>
	 * The absolute position of where the token was found.
	 * @return te position number.
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * Get the token length
	 * @return the token length
	 */
	public int getLength() {
		return text.length();
	}

	/**
	 * Verify if object are equals.
	 * @param obj the object to compare.
	 * @return true if tokentype and name are identical else false.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final Token other = (Token) obj;
		if (this.type != other.type) {
			return false;
		}
		if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
			return false;
		}
		return true;
	}

	/**
	 * Calculate the hashcode of the object by hashing the type and text parameters.
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
		hash = 71 * hash + (this.text != null ? this.text.hashCode() : 0);
		return hash;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return
	 */
	@Override
	public String toString() {
		return "Token{" + "type=" + type + ", text='" + text + "', line=" + line + ", col=" + col + ", pos=" + pos + '}';
	}

	/**
	 * Default constructor.
	 * (Immutable pattern, only constructor initialization).
	 * @param type Immutable type.
	 * @param text Immutable text.
	 * @param line source code line containing token.
	 * @param col line column where is the token.
	 * @param pos the global string position.
	 */
	public Token(TokenType type, String text, int line, int col, int pos) {
		this.type = type;
		this.text = text;
		this.line = line;
		this.col = col;
		this.pos = pos;
	}

	public Token(TokenType type, String text, Token token) {
		this.type = type;
		this.text = text;
		this.line = token.line;
		this.col = token.col;
		this.pos = token.pos;
	}

	/**
	 * Constructor without position.
	 * Must only be used in Unit testing context.
	 * (Immutable pattern, only constructor initialization).
	 * @param type Immutable type.
	 * @param text Immutable text.
	 */
	public Token(TokenType type, String text) {
		this.type = type;
		this.text = text;
		this.line = -1;
		this.col = -1;
	}

	/**
	 * Clone constructor.
	 * Based on [GoF] prototype factory
	 * @param token the token to clone
	 */
	public Token(Token token) {
		this.col = token.col;
		this.line = token.line;
		this.pos = token.pos;
		this.text = token.text;
		this.type = token.type;
	}
}