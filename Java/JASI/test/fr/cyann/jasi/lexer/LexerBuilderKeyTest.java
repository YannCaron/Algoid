/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import java.util.Set;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class LexerBuilderKeyTest {
	
	CharacterIterator right = null, wrong = null;
	Term term = null;
	Lexer lexer = new Lexer(term);

	@Before
	public void setUp() throws Exception {
		right = new StringCharacterIterator("key1key2"); 
		wrong = new StringCharacterIterator("key1key3");
		
		Term keys = new Alternation().add(new Word("key1")).add(new Word("key2"));
		Set<String> keywords = new LinkedHashSet<String>();
		keywords.add("key2");
		
		term = new LexerBuilderKey(keys, keywords, TokenType.SYMBOL, lexer);
	}

	/**
	 * Test of getTerm method, of class LexerBuilder.
	 */
	@Test
	public void testGetTerm() {
		assertTrue(term.find(right));
		assertEquals("key1", term.getTerm());
		assertTrue(term.find(right));
		assertEquals("key2", term.getTerm());
	}

	/**
	 * Test of find method, of class LexerBuilder.
	 */
	@Test
	public void testFind() {
		assertTrue(term.find(wrong));
		assertEquals("key1", term.getTerm());
		
		assertFalse(term.find(wrong));
	}
}
