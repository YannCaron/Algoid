/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import org.junit.Before;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class LexerBuilderTest {

	CharacterIterator right = null, wrong = null;
	Term term = null;
	Lexer lexer = new Lexer(term);

	@Before
	public void setUp() throws Exception {
		right = new StringCharacterIterator("1234");
		wrong = new StringCharacterIterator("1a2a");
		Term digit = new CharListAlternation(".0123456789");
		term = new LexerBuilder(digit, TokenType.SYMBOL, lexer);
	}

	/**
	 * Test of getTerm method, of class LexerBuilder.
	 */
	@Test
	public void testGetTerm() {
		assertTrue(term.find(right));
		assertEquals("1", term.getTerm());
		assertTrue(term.find(right));
		assertEquals("2", term.getTerm());
		assertTrue(term.find(right));
		assertEquals("3", term.getTerm());
		assertTrue(term.find(right));
		assertEquals("4", term.getTerm());
	}

	/**
	 * Test of find method, of class LexerBuilder.
	 */
	@Test
	public void testFind() {
		assertTrue(term.find(wrong));
		assertEquals("1", term.getTerm());
		assertFalse(term.find(wrong));
		wrong.next();
		assertTrue(term.find(wrong));
		assertEquals("2", term.getTerm());
		assertFalse(term.find(wrong));
	}
}
