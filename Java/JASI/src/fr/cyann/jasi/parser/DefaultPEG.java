
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

import fr.cyann.jasi.lexer.Alternation;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.Term;

/**
 * The DefaultPEG class.<br> creation date : Jul 13, 2012.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class DefaultPEG extends PEG {

	private Term rootTerm;
	private Lexer lexer;
	private Statement rootStatement;

	/**
	 * Default constructor
	 */
	public DefaultPEG() {
		rootTerm = new Alternation();
		lexer = new Lexer(rootTerm);
		rootStatement = new AssumptionStatement("root");
	}

	/**
	 * The default lexer builder.
	 * @return the lexer.
	 */
	@Override
	protected Lexer constructLexer() {
		return lexer;
	}

	/**
	 * The default parser builder.
	 * @return the statement root node.
	 */
	@Override
	protected Statement constructParser() {
		return rootStatement;
	}

	/**
	 * Get the lexer.
	 * @return the lexer.
	 */
	@Override
	public Lexer getLexer() {
		return lexer;
	}

	/**
	 * Add term to lexer root node.
	 * @param e the term to add
	 */
	public void addTerm(Term e) {
		rootTerm.add(e);
	}

	/**
	 * Remove term from root node.
	 * @param e the term to remove.
	 */
	public void removeTerm(Term e) {
		rootTerm.remove(e);
	}

	/**
	 * Add Statement to grammar root node.
	 * @param e the statement to add.
	 */
	public void addStatement(Statement e) {
		rootStatement.add(e);
	}

	/**
	 * Remove statement to grammar root node.
	 * @param e the statement to remove.
	 */
	public void removeStatement(Statement e) {
		rootStatement.remove(e);
	}
}
