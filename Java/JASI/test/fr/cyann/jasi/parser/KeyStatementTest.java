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
public class KeyStatementTest {

	KeyStatement stmt = null;
	
	@Before
	public void setUp() {
		stmt = new KeyStatement(TokenType.SYMBOL, "keyword");
	}

	/**
	 * Test of getName method, of class KeyStatement.
	 */
	@Test
	public void testGetName() {
		assertEquals("keyword", stmt.getName());
	}

	/**
	 * Test of getTokenType method, of class KeyStatement.
	 */
	@Test
	public void testGetTokenType() {
		assertEquals(TokenType.SYMBOL, stmt.getTokenType());
	}

	/**
	 * Test of isToken method, of class KeyStatement.
	 */
	@Test
	public void testIsToken() {
		assertFalse(stmt.isToken(new Token(TokenType.EOF, "keyword")));
		assertFalse(stmt.isToken(new Token(TokenType.SYMBOL, "other")));
		assertTrue(stmt.isToken(new Token(TokenType.SYMBOL, "keyword")));
	}

	/**
	 * Test of toBNFString method, of class KeyStatement.
	 */
	@Test
	public void testToBNFString() {
		assertEquals("'keyword'", stmt.toBNFString());
	}
}
