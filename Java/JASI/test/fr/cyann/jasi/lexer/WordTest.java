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
public class WordTest {

	Term term = null;

	@Before
	public void setUp() {
		// term = [( * )] '"'
		term = new Word("abcd");
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm1_Find() {
		assertTrue(term.find(new StringCharacterIterator("abcdabcd")));
		assertEquals("abcd", term.getTerm());
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm2_Find() {
		assertTrue(term.find(new StringCharacterIterator("abcd")));
		assertEquals("abcd", term.getTerm());
	}

	/**
	 * Test of getTerm and find methods, of class Alternation.
	 */
	@Test
	public void testGetTerm3_Find() {
		assertFalse(term.find(new StringCharacterIterator("abce")));
	}
	
}
