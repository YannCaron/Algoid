/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import java.text.StringCharacterIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class SequenceTest {

	Term term = null;

	@Before
	public void setUp() {
		// term = 'a' | 'b'
		term = new Sequence();
		term.add(new Char('a'));
		term.add(new Char('b'));
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm1_Find() {
		assertTrue(term.find(new StringCharacterIterator("abab")));
		assertEquals("ab", term.getTerm());
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm2_Find() {
		assertFalse(term.find(new StringCharacterIterator("baba")));
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm3_Find() {
		assertFalse(term.find(new StringCharacterIterator("c")));
	}
	
}
