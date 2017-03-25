/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.builder;

import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.al.ast.Block;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.lexer.Token;
import org.junit.Before;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.parser.StatementLeafToken;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class ClassFactoryTest {

	private static class MockStatementLeafToken extends StatementLeafToken {

		public MockStatementLeafToken() {
			super(null);
		}

		@Override
		public Token getToken() {
			return new Token(TokenType.SYMBOL, "Null");
		}

		@Override
		public boolean isToken(Token token) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getName() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String toBNFString() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public ClassFactoryTest() {
	}

	@Before
	public void setUp() throws Exception {
		ASTBuilder bd = new ASTBuilder();
		assertTrue("Builder is not empty ?", bd.isEmpty());
	}

	/**
	 * Test of buildLeaf method, of class ClassFactory.
	 */
	@Test
	public void testBuildLeafForSimpleConstructor() {
		ASTBuilder bd = new ASTBuilder();

		ClassFactory factory = new ClassFactory(NumberExpression.class);
		factory.buildLeaf(bd, new StatementLeafToken(factory) {

			@Override
			public Token getToken() {
				return new Token(TokenType.SYMBOL, "0");
			}


			@Override
			public boolean isToken(Token token) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public String getName() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public String toBNFString() {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
		assertFalse("Builder is empty ?", bd.isEmpty());

		AST ast = bd.pop();
		assertTrue("Builder is not empty ?", bd.isEmpty());

		assertNotNull("Built object is null ?", ast);
		assertTrue("Not an AST ?", ast instanceof AST);
		assertTrue("Not a Null AST?", ast instanceof NumberExpression);

	}

	/**
	 * Test of buildLeaf method, of class ClassFactory.
	 */
	@Test
	public void testBuildLeafForTokenConstructor() {
		ASTBuilder bd = new ASTBuilder();

		ClassFactory factory = new ClassFactory(Block.class);
		factory.buildLeaf(bd, new MockStatementLeafToken());
		assertFalse("Builder is empty ?", bd.isEmpty());

		AST ast = bd.pop();
		assertTrue("Builder is not empty ?", bd.isEmpty());

		assertNotNull("Built object is null ?", ast);
		assertTrue("Not an AST ?", ast instanceof AST);
		assertTrue("Not a Null AST?", ast instanceof Block);

	}
}
