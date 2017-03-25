/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.Lexer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class RepeatStatementTest {

	RepeatStatement stmt = null;

	@Before
	public void setUp() {
		stmt = new RepeatStatement(new Tools.MockStatement("keyword"));
	}

	/**
	 * Test of getName method, of class OptionalStatement.
	 */
	@Test
	public void testGetName() {
		assertEquals("{keyword}", stmt.getName());
		assertEquals("getName()", Tools.consumeResult());
	}

	/**
	 * Test of tryParse method, of class AssumptionStatement.
	 */
	@Test
	public void testTryParse1() {
		Lexer lexer = Tools.getMockLexer("key");
		assertFalse(stmt.tryParse(lexer)); // always return true
		assertEquals("isToken(key)", Tools.consumeResult());
		assertEquals(0, lexer.getLookaheadPosition()); // not found
	}

	@Test
	public void testTryParse2() {
		Lexer lexer = Tools.getMockLexer("keyword");
		assertTrue(stmt.tryParse(lexer));
		assertEquals("isToken(keyword), isToken()", Tools.consumeResult());
		assertEquals(1, lexer.getLookaheadPosition()); // found
	}

	@Test
	public void testTryParse3() {
		Lexer lexer = Tools.getMockLexer("keyword", "keyword");
		assertTrue(stmt.tryParse(lexer));
		assertEquals("isToken(keyword), isToken(keyword), isToken()", Tools.consumeResult());
		assertEquals(2, lexer.getLookaheadPosition()); // found
	}

	
	@Test
	public void testTryParse4() {
		Lexer lexer = Tools.getMockLexer("keyword", "key");
		assertTrue(stmt.tryParse(lexer));
		assertEquals("isToken(keyword), isToken(key)", Tools.consumeResult());
		assertEquals(1, lexer.getLookaheadPosition()); // found
	}

	/**
	 * Test of parse method, of class AssumptionStatement.
	 */
	@Test
	public void testParse1() {
		Lexer lexer = Tools.getMockLexer("key");
		assertFalse(stmt.parse(lexer, null));
		assertEquals("isToken(key)", Tools.consumeResult());
		assertEquals(0, lexer.getPosition()); // not found
	}

	@Test
	public void testParse2() {
		Lexer lexer = Tools.getMockLexer("keyword");
		assertTrue(stmt.parse(lexer, null));
		assertEquals("isToken(keyword), isToken()", Tools.consumeResult());
		assertEquals(1, lexer.getPosition()); // found
	}
	
		@Test
	public void testParse3() {
		Lexer lexer = Tools.getMockLexer("keyword", "keyword", "key");
		assertTrue(stmt.parse(lexer, null));
		assertEquals("isToken(keyword), isToken(keyword), isToken(key)", Tools.consumeResult());
		assertEquals(2, lexer.getPosition()); // found
	}

}
