/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.builder.FactoryStrategy;
import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.Alternation;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Char;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.LexerBuilder;
import fr.cyann.jasi.lexer.Term;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.utils.Reflect;
import java.util.ArrayList;
import java.util.List;

/**
 * The StatementTools class. Creation date: 18 mai 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Tools {

	private static StringBuilder sb = new StringBuilder();

	;

	private static void append(String text) {
		if (sb.length() > 0) {
			sb.append(", ");
		}
		sb.append(text);
	}

	public static String consumeResult() {
		String result = sb.toString();
		sb = new StringBuilder();
		return result;
	}

	public static class MockStatement extends StatementLeafToken {

		private String text;

		public MockStatement(String text) {
			super(new ClassFactory(BinaryOperator.class));
			this.text = text;
		}

		@Override
		public boolean isToken(Token token) {
			append("isToken(" + token.getText() + ")");
			return (text.equals(token.getText()));
		}

		@Override
		public String getName() {
			append("getName()");
			return text;
		}

		@Override
		public String toBNFString() {
			return text;
		}
	}

	public static class MockNodeStatement extends StatementNode {

		private String name;

		public MockNodeStatement(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean tryParse(BacktrackingIterator<Token> iterator) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String toBNFString() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public static Lexer getMockLexer(String... tokenTexts) {
		List<Token> tokens = new ArrayList<Token>();

		int size = tokenTexts.length;
		for (int i = 0; i < size; i++) {
			tokens.add(new Token(TokenType.SYMBOL, tokenTexts[i]));
		}

		tokens.add(new Token(TokenType.EOF, ""));

		Lexer lexer = new Lexer(null);
		Reflect.setPrivateFieldValue(lexer, "tokens", tokens);
		Reflect.setPrivateFieldValue(lexer, "index", 0);
		Reflect.setPrivateFieldValue(lexer, "lookaheadIndex", 0);
		Reflect.setPrivateFieldValue(lexer, "lastTried", 0);

		return lexer;
	}

	public static class MockBuilder implements InterpreterBuilder {

		@Override
		public void build(StatementLeafToken statement, FactoryStrategy strategy) {
			strategy.buildLeaf(this, statement);
			sb.append("(" + statement.getName() + ")");
		}

		@Override
		public void build(StatementNode statement, AgregatorStrategy strategy) {
			strategy.build(this, statement);
			sb.append("(" + statement.getName() + ")");
		}

		@Override
		public void push(AST item) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public AST poll() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public AST pop() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public AST peek() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public static class MockPEG extends PEG {

		public Term term = null;
		public Lexer lexer = null;
		public Statement stmt = null;

		@Override
		protected Lexer constructLexer() {
			append("initializeLexer()");

			term = new Alternation();
			lexer = new Lexer(term);

			Term alt1 = new Alternation();
			alt1.add(new Char('a'));
			alt1.add(new Char('b'));

			term.add(new LexerBuilder(alt1, TokenType.SYMBOL, lexer));

			// term = 'a' | 'b'

			return lexer;
		}

		@Override
		protected Statement constructParser() {
			append("initializeParser()");
			stmt = new CompoundStatement("grammar", new MockAgregator());
			stmt.add(new KeyStatement("a", new MockFactory()));
			stmt.add(new KeyStatement("b", new MockFactory()));

			// grammar = a b

			return stmt;

		}
	}

	public static class MockAgregator implements AgregatorStrategy {

		@Override
		public void build(InterpreterBuilder builder, StatementNode node) {
			append("build");
		}
	}

	public static class MockFactory implements FactoryStrategy {

		@Override
		public void buildLeaf(InterpreterBuilder builder, StatementLeafToken statement) {
			append("factory");
		}
	}
}
