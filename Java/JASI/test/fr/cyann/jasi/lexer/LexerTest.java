/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import fr.cyann.jasi.exception.InvalidSymbolException;
import fr.cyann.utils.Reflect;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class LexerTest {

	Lexer lexer = null;
	Term root = null;
	List<Token> tokens;
	Set<Integer> positions;

	public LexerTest() {
	}

	@Before
	public void setUp() throws Exception {
		// term = [( 'a' | 'b' )] | ' '

		root = new Alternation();
		lexer = new Lexer(root);

		Term w = new CharListAlternation("ab");

		root.add(new LexerBuilder(new Sequence().add(w).add(new RepeatTerm(w)), TokenType.SYMBOL, lexer));
		root.add(new Char(' '));

		tokens = (List<Token>) Reflect.getPrivateFieldValue(lexer, "tokens");
		positions = (Set<Integer>) Reflect.getPrivateFieldValue(lexer, "positions");
	}

	/**
	 * Test of getRoot method, of class Lexer.
	 */
	@Test
	public void testGetRoot() {
		assertSame(root, lexer.getRoot());
	}

	/**
	 * Test of match method, of class Lexer.
	 */
	@Test
	public void testMatch() {
		lexer.match("abababaaba");
		assertEquals(2, tokens.size());

		assertEquals("abababaaba", tokens.get(0).getText());
		assertEquals(TokenType.EOF, tokens.get(1).getType());
	}

	/**
	 * Test of lastTried method, of class Lexer.
	 */
	@Test
	public void testLastTried() {
		try {
			lexer.match("abababaabac");
			fail("Expected exception !");
		} catch (InvalidSymbolException ex) {
			assertEquals("abababaaba", lexer.lastTried().getText());
		}
	}

	/**
	 * Test of first method, of class Lexer.
	 */
	@Test
	public void testFirst() {
		lexer.match("ab ab ba baab");
		assertEquals(5, tokens.size());

		assertEquals("ab", lexer.first().getText());
	}

	/**
	 * Test of current method, of class Lexer.
	 */
	@Test
	public void testCurrent() {
		lexer.match("ab ab ba baab");

		assertEquals("ab", lexer.current().getText());
		lexer.next();
		assertEquals("ab", lexer.current().getText());
		lexer.next();
		assertEquals("ba", lexer.current().getText());
		lexer.next();
		assertEquals("baab", lexer.current().getText());
	}

	/**
	 * Test of next method, of class Lexer.
	 */
	@Test
	public void testNext() {
		lexer.match("ab ba");

		assertEquals("ab", lexer.current().getText());
		lexer.next();
		assertEquals("ba", lexer.current().getText());
	}

	/**
	 * Test of remove method, of class Lexer.
	 */
	@Test
	public void testRemove() {
		lexer.match("ab ba");

		try {
			lexer.remove();
			fail("Exception expected");
		} catch (Exception ex) {
		}
	}

	/**
	 * Test of resumeLookahead method, of class Lexer.
	 */
	@Test
	public void testResumeLookahead() {
		lexer.match("ab ba");
		assertEquals(0, Reflect.getPrivateFieldValue(lexer, "lookaheadIndex"));

		lexer.nextLookahead();
		assertEquals(1, Reflect.getPrivateFieldValue(lexer, "lookaheadIndex"));
		
		lexer.resumeLookahead();
		assertEquals(0, Reflect.getPrivateFieldValue(lexer, "lookaheadIndex"));
	}

	/**
	 * Test of currentLookahead method, of class Lexer.
	 */
	@Test
	public void testCurrentLookahead() {
		lexer.match("ab ba");
		lexer.nextLookahead();
		assertEquals("ba", lexer.currentLookahead().getText());
	}

	/**
	 * Test of nextLookahead method, of class Lexer.
	 */
	@Test
	public void testNextLookahead() {
		lexer.match("ab ba");
		lexer.nextLookahead();
		assertEquals(1, Reflect.getPrivateFieldValue(lexer, "lookaheadIndex"));
	}

	/**
	 * Test of hasNext method, of class Lexer.
	 */
	@Test
	public void testHasNext() {
		assertFalse(lexer.hasNext());
		lexer.match("ab ba");
		assertTrue(lexer.hasNext());
	}

	/**
	 * Test of getPosition method, of class Lexer.
	 */
	@Test
	public void testGetPosition() {
		lexer.match("ab ba");
		assertEquals(0, lexer.getPosition());
		assertEquals(0, lexer.getLookaheadPosition());
		lexer.next();
		assertEquals(1, lexer.getPosition());
		assertEquals(1, lexer.getLookaheadPosition());
	}

	/**
	 * Test of getLookaheadPosition method, of class Lexer.
	 */
	@Test
	public void testGetLookaheadPosition() {
		lexer.match("ab ba");
		assertEquals(0, lexer.getLookaheadPosition());
		assertEquals(0, lexer.getPosition());
		lexer.nextLookahead();
		assertEquals(1, lexer.getLookaheadPosition());
		assertEquals(0, lexer.getPosition());
	}

	/**
	 * Test of setLookaheadPosition method, of class Lexer.
	 */
	@Test
	public void testSetLookaheadPosition() {
		lexer.match("ab ba");
		assertEquals(0, lexer.getLookaheadPosition());
		lexer.next();
		lexer.setLookaheadPosition(2);
		assertEquals(2, lexer.getLookaheadPosition());
	}

	/**
	 * Test of toString method, of class Lexer.
	 */
	@Test
	public void testToString() {
		assertEquals("lexer{index=0, lookaheadIndex=0}", lexer.toString());
		lexer.match("abbaaba aaaa");
		lexer.next();
		lexer.nextLookahead();
		assertEquals("lexer{index=1, lookaheadIndex=1, current=aaaa, currentLookahead=}", lexer.toString());
		
	}
}
