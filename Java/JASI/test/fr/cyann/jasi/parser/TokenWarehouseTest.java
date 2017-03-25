/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.utils.Reflect;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TokenWarehouseTest {

	TokenWarehouse tw = null;
	Map<String, Set<Token>> warehouse = null;

	@Before
	public void setUp() {
		tw = TokenWarehouse.getInstance();
		warehouse = (Map<String, Set<Token>>) Reflect.getPrivateFieldValue(tw, "warehouse");
	}

	/**
	 * Test of store method, of class TokenWarehouse.
	 */
	@Test
	public void testStore() {
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol1"));
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol2"));
		tw.store("CLASS", new Token(TokenType.SYMBOL, "class"));

		assertFalse(warehouse.isEmpty());
		assertTrue(warehouse.get("KEY").contains(new Token(TokenType.SYMBOL, "symbol1")));
		assertTrue(warehouse.get("KEY").contains(new Token(TokenType.SYMBOL, "symbol2")));
		assertFalse(warehouse.get("KEY").contains(new Token(TokenType.SYMBOL, "class")));
		assertTrue(warehouse.get("CLASS").contains(new Token(TokenType.SYMBOL, "class")));
		assertFalse(warehouse.get("CLASS").contains(new Token(TokenType.SYMBOL, "symbol1")));
	}

	/**
	 * Test of contains method, of class TokenWarehouse.
	 */
	@Test
	public void testContains() {
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol1"));
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol2"));
		tw.store("CLASS", new Token(TokenType.SYMBOL, "class"));

		assertTrue(tw.contains("KEY", new Token(TokenType.SYMBOL, "symbol1")));
		assertTrue(tw.contains("KEY", new Token(TokenType.SYMBOL, "symbol2")));
		assertFalse(tw.contains("KEY", new Token(TokenType.SYMBOL, "class")));
		assertTrue(tw.contains("CLASS", new Token(TokenType.SYMBOL, "class")));
		assertFalse(tw.contains("CLASS", new Token(TokenType.SYMBOL, "symbol1")));
	}

	/**
	 * Test of toString method, of class TokenWarehouse.
	 */
	@Test
	public void testToString() {
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol1"));
		tw.store("KEY", new Token(TokenType.SYMBOL, "symbol2"));
		tw.store("CLASS", new Token(TokenType.SYMBOL, "class"));
		assertEquals("TokenWarehouse{warehouse={CLASS=[Token{type=SYMBOL, text='class', line=-1, col=-1, pos=0}], KEY=[Token{type=SYMBOL, text='symbol2', line=-1, col=-1, pos=0}, Token{type=SYMBOL, text='symbol1', line=-1, col=-1, pos=0}]}}", tw.toString());
	}

	/**
	 * Test of getInstance method, of class TokenWarehouse.
	 */
	@Test
	public void testGetInstance() {
		assertSame(tw, TokenWarehouse.getInstance());
	}
}
