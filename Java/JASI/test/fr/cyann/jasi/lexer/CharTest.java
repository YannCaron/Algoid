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
public class CharTest {

	Term term = null;

	@Before
	public void setUp() {
		// term = 'a'
		term = new Char('a');
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
		assertFalse(term.find(new StringCharacterIterator("b")));
	}
}
