/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.utils.UnitTesting;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class AssumptionStatementTest {

	private Statement stmt = null;

	@Before
	public void setUp() {
		stmt = new AssumptionStatement("assumption");
		stmt.clearMemorizer();
		stmt.add(new Tools.MockStatement("+"));
		stmt.add(new Tools.MockStatement("-"));
	}

	/**
	 * Test of getName method, of class AssumptionStatement.
	 */
	@Test
	public void testGetName() {
		UnitTesting.assertAccessorAndMutator(stmt, "name", "assumption", true);
	}

	/**
	 * Test of tryParse method, of class AssumptionStatement.
	 */
	@Test
	public void testTryParse1() {
		assertTrue(stmt.tryParse(Tools.getMockLexer("+")));
		assertEquals("isToken(+)", Tools.consumeResult());
	}

	@Test
	public void testTryParse2() {
		assertTrue(stmt.tryParse(Tools.getMockLexer("-", ";")));
		assertEquals("isToken(-), isToken(-)", Tools.consumeResult());
	}

	@Test
	public void testTryParse3() {

		assertFalse(stmt.tryParse(Tools.getMockLexer("27", "4")));
		assertEquals("isToken(27), isToken(27)", Tools.consumeResult());
	}

	/**
	 * Test of parse method, of class AssumptionStatement.
	 */
	@Test
	public void testParse1() {
		assertTrue(stmt.parse(Tools.getMockLexer("+"), null));
		assertEquals("isToken(+), isToken(+)", Tools.consumeResult());
	}

	@Test
	public void testParse2() {

		assertTrue(stmt.parse(Tools.getMockLexer("-", "4"), null));
		assertEquals("isToken(-), isToken(-), isToken(-)", Tools.consumeResult());
	}

	@Test
	public void testParse3() {

		assertFalse(stmt.parse(Tools.getMockLexer("27", "4"), null));
		assertEquals("isToken(27), isToken(27)", Tools.consumeResult());
	}

	/**
	 * Test of toString method, of class AssumptionStatement.
	 */
	@Test
	public void testToString() {
		assertEquals("assumption", stmt.toString());
	}

	/**
	 * Test of toBNFString method, of class AssumptionStatement.
	 */
	@Test
	public void testToBNFString() {
		assertEquals("assumption= + | -", stmt.toBNFString());
	}
}
