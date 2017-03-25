/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.lexer.Token;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TypedStatementTest {

	TypedStatement stmt = null;
	
	@Before
	public void setUp() {
		stmt = new TypedStatement(TokenType.SYMBOL);
	}

	/**
	 * Test of getName method, of class KeyStatement.
	 */
	@Test
	public void testGetName() {
		assertEquals("SYMBOL", stmt.getName());
	}

	/**
	 * Test of isToken method, of class KeyStatement.
	 */
	@Test
	public void testIsToken() {
		assertFalse(stmt.isToken(new Token(TokenType.EOF, "keyword")));
		assertTrue(stmt.isToken(new Token(TokenType.SYMBOL, "other")));
		assertTrue(stmt.isToken(new Token(TokenType.SYMBOL, "keyword")));
	}

	/**
	 * Test of toBNFString method, of class KeyStatement.
	 */
	@Test
	public void testToBNFString() {
		assertEquals("SYMBOL", stmt.toBNFString());
	}
}
