/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import fr.cyann.utils.Reflect;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author CyaNn
 */
public class TokenTypeTest {

	/**
	 * Test of exist method, of class TokenType.
	 */
	@Test
	public void testExist() {
		assertTrue(TokenType.exist("SYMBOL"));
		assertTrue(TokenType.exist("EOF"));
		assertFalse(TokenType.exist("CTRL"));
		assertFalse(TokenType.exist("ANOTHER"));
	}

	/**
	 * Test of valueOf method, of class TokenType.
	 */
	@Test
	public void testValueOf() {
		assertEquals(TokenType.SYMBOL, TokenType.valueOf("SYMBOL"));
		TokenType myType = TokenType.valueOf("ANOTHER");
		assertEquals(myType, TokenType.valueOf("ANOTHER"));
	}

	/**
	 * Test of compareTo method, of class TokenType.
	 */
	@Test
	public void testCompareTo() {
		TokenType symb = TokenType.valueOf("SYMBOL");
		assertEquals(0, Reflect.getPrivateFieldValue(symb, "ordinal"));
		TokenType myType = TokenType.valueOf("ANOTHER");
		assertEquals(2, Reflect.getPrivateFieldValue(myType, "ordinal"));
		
		
		assertEquals(0, TokenType.valueOf("ANOTHER").compareTo(TokenType.valueOf("ANOTHER")));
		assertEquals(-1, TokenType.valueOf("SYMBOL").compareTo(TokenType.valueOf("ANOTHER")));
	}

	/**
	 * Test of toString method, of class TokenType.
	 */
	@Test
	public void testToString() {
		assertEquals("SYMBOL", TokenType.SYMBOL.toString());
	}
}
