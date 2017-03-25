/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.utils.UnitTesting;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class PEGTest {

	Tools.MockPEG peg = null;
	InterpreterBuilder builder = null;

	@Before
	public void setUp() {
		peg = new Tools.MockPEG();
		builder = new Tools.MockBuilder();

		peg.initalize();
	}

	/**
	 * Test of initalize method, of class PEG.
	 */
	@Test
	public void testInitalize() {
		assertEquals("initializeLexer(), initializeParser()", Tools.consumeResult());
	}

	/**
	 * Test of getLexer method, of class PEG.
	 */
	@Test
	public void testGetLexer() {
		assertNotNull(peg.lexer);
		assertEquals(peg.lexer, peg.getLexer());
	}

	/**
	 * Test of getParser method, of class PEG.
	 */
	@Test
	public void testGetParser() {
		assertNotNull(peg.stmt);
		assertEquals(peg.stmt, peg.getParser());
	}

	/**
	 * Test of parse method, of class PEG.
	 */
	@Test
	public void testParse1() {
		Tools.consumeResult();
		peg.parse("ab", builder);
		// test sequence
		assertEquals("factory(a), factory(b), build(grammar)", Tools.consumeResult());
	}

	@Test
	public void testParse2() {
		Tools.consumeResult();
		peg.parse("abab", builder);
		// test sequence
		assertEquals("factory(a), factory(b), build(grammar), factory(a), factory(b), build(grammar)", Tools.consumeResult());
	}

	@Test
	public void testParse3() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				peg.parse("abba", builder);
			}
		});
	}

	@Test
	public void testParse4() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				peg.parse("abc", builder);
			}
		});
	}
}
