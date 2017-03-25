/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class StatementLeafTokenTest {

	StatementLeafToken stmt = null;

	@Before
	public void setUp() {
		stmt = new Tools.MockStatement("keyword");
	}

	/**
	 * Test of getToken method, of class StatementLeafToken.
	 */
	@Test
	public void testGetToken() {
		assertNull(stmt.getToken());
		stmt.parse(Tools.getMockLexer("keyword"), null);
		assertEquals(new Token(TokenType.SYMBOL, "keyword"), stmt.getToken());
	}

	/**
	 * Test of tryParse method, of class StatementLeafToken.
	 */
	@Test
	public void testTryParse1() {
		Tools.consumeResult();
		assertTrue(stmt.tryParse(Tools.getMockLexer("keyword")));
		assertNull(stmt.getToken());
		assertEquals("isToken(keyword)", Tools.consumeResult());
	}

	@Test
	public void testTryParse2() {
		Tools.consumeResult();
		assertFalse(stmt.tryParse(Tools.getMockLexer("key")));
		assertNull(stmt.getToken());
		assertEquals("isToken(key)", Tools.consumeResult());
	}

	@Test
	public void testTryParse3() {
		Tools.consumeResult();
		assertTrue(stmt.tryParse(Tools.getMockLexer("keyword", "key")));
		assertNull(stmt.getToken());
		assertEquals("isToken(keyword)", Tools.consumeResult());
	}

	/**
	 * Test of parse method, of class StatementLeafToken.
	 */
	@Test
	public void testParse1() {
		Tools.consumeResult();
		assertTrue(stmt.parse(Tools.getMockLexer("keyword"), null));
		assertEquals(new Token(TokenType.SYMBOL, "keyword"), stmt.getToken());
		assertEquals("isToken(keyword)", Tools.consumeResult());
	}

	@Test
	public void testParse2() {
		Tools.consumeResult();
		assertFalse(stmt.parse(Tools.getMockLexer("key"), null));
		assertEquals(new Token(TokenType.SYMBOL, "key"), stmt.getToken());
		assertEquals("isToken(key)", Tools.consumeResult());
	}

	/**
	 * Test of isToken method, of class StatementLeafToken.
	 */
	@Test
	public void testIsToken1() {
		assertTrue(stmt.isToken(new Token(TokenType.SYMBOL, "keyword")));
		assertTrue(stmt.isToken(new Token(TokenType.SYMBOL, "keyword")));
		assertFalse(stmt.isToken(new Token(TokenType.SYMBOL, "key")));
	}

	/**
	 * Test of toString method, of class StatementLeafToken.
	 */
	@Test
	public void testToString() {
		stmt.parse(Tools.getMockLexer("keyword"), null);
		assertEquals("keyword", stmt.toString());
	}
}
