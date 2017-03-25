/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import org.junit.Before;
import java.text.StringCharacterIterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class CharListAlternationTest {

	Term term = null;

	@Before
	public void setUp() {
		// term = 'a' | 'b'
		term = new CharListAlternation("ab");
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm1_Find() {
		assertTrue(term.find(new StringCharacterIterator("ab")));
		assertEquals("a", term.getTerm());
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm2_Find() {

		assertTrue(term.find(new StringCharacterIterator("b")));
		assertEquals("b", term.getTerm());
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm3_Find() {
		assertFalse(term.find(new StringCharacterIterator("c")));
	}
}
