
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
import fr.cyann.jasi.exception.InvalidGrammarException;
import fr.cyann.jasi.exception.InvalidTokenException;
import fr.cyann.jasi.lexer.Lexer;

/**
 * The PEG abstract class.<br>
 * Define the entire syntaxe of the language.<br>
 * First define the lexer hierarchy, and second the parser.<br>
 * @author CARONYN
 */
public abstract class PEG {

	private Lexer lexer;
	private Statement parser;
	
	/**
	 * The syntax processor get the root lexer term.<br>
	 * Based on GoF template methode.
	 * @return the root lexer term.
	 */
	protected abstract Lexer constructLexer();

	/**
	 * The syntax processor get the root parser statement.<br>
	 * Based on GoF template methode.
	 * @return the root lexer term.
	 */
	protected abstract Statement constructParser();

	/**
	 * Initialize the instance variables.
	 */
	public void initalize() {
		// lazy initialization
		if (lexer == null) {
			lexer = constructLexer();
		}

		if (parser == null) {
			parser = constructParser();
		}
	}

	/**
	 * The Lexer accessor.
	 * @return the lexer
	 */
	public Lexer getLexer() {
		return lexer;
	}

	/**
	 * The parser accessor.
	 * @return the parser
	 */
	public Statement getParser() {
		return parser;
	}

	/**
	 * The parser entry point.<br>
	 * Parse the source code and build the result via builder.<br>
	 * Builder is a factory that be able to interprete the langage directly, or building AST.
	 * @param source the source code
	 * @param builder the result builder
	 */
	public void parse(String source, InterpreterBuilder builder) {
		initalize();

		// initialize static resources
		parser.clearMemorizer();

		lexer.match(source);

		while (lexer.hasNext()) {
			int i = lexer.getPosition();
			boolean res = parser.parse(lexer, builder);
			if (!res) {
				throw new InvalidGrammarException(lexer.lastTried());
			}
			if (res && i == lexer.getPosition()) {
				throw new InvalidTokenException(lexer.current(), PEG.class);
			}
		}
	}
	
	/**
	 * Build a list with separator.
	 * @param stmt the statement to list
	 * @param separator the separator
	 * @return the statement
	 */
	public static Statement buildList (Statement stmt, Statement separator){
		Statement multi = new CompoundStatement(stmt.getName() + "_m");
		Statement part = new CompoundStatement(stmt.getName() + "_p");

		part.add(separator).add(stmt);

		multi.add(stmt);
		multi.add(new OptionalStatement(new RepeatStatement(part)));
		
		return multi;
	}
	
	/**
	 * Build an aggregation of a statement.
	 * @param name the aggregate name
	 * @param stmt the list statement
	 * @param begin the beginning symbol
	 * @param end the ed symbol
	 * @return 
	 */
	public static Statement buildAggregate (String name, Statement stmt, Statement begin, Statement end) {
		Statement aggregate = new CompoundStatement(name);
		aggregate.add(begin);
		aggregate.add(stmt);
		aggregate.add(end);
		return aggregate;
	}
}
