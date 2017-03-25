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
public class CompoundStatementTest {

	private Statement stmt = null;

	@Before
	public void setUp() {
		stmt = new CompoundStatement("compound");
		stmt.clearMemorizer();
		stmt.add(new Tools.MockStatement("+"));
		stmt.add(new Tools.MockStatement("-"));
	}

	/**
	 * Test of getName method, of class AssumptionStatement.
	 */
	@Test
	public void testGetName() {
		UnitTesting.assertAccessorAndMutator(stmt, "name", "compound", true);
	}

	/**
	 * Test of tryParse method, of class AssumptionStatement.
	 */
	@Test
	public void testTryParse1() {
		assertFalse(stmt.tryParse(Tools.getMockLexer("+")));
		assertEquals("isToken(+), isToken()", Tools.consumeResult());
	}

	@Test
	public void testTryParse2() {
		Tools.consumeResult();
		assertTrue(stmt.tryParse(Tools.getMockLexer("+", "-")));
		assertEquals("isToken(+), isToken(-)", Tools.consumeResult());
	}

	@Test
	public void testTryParse3() {
		Tools.consumeResult();
		assertFalse(stmt.tryParse(Tools.getMockLexer("27", "4")));
		assertEquals("isToken(27)", Tools.consumeResult());
	}

	/**
	 * Test of parse method, of class AssumptionStatement.
	 */
	@Test
	public void testParse1() {
		Tools.consumeResult();
		assertFalse(stmt.parse(Tools.getMockLexer("+"), new Tools.MockBuilder()));
		assertEquals("isToken(+), getName()(+), isToken()", Tools.consumeResult());
	}

	@Test
	public void testParse2() {
		Tools.consumeResult();
		assertTrue(stmt.parse(Tools.getMockLexer("+", "-"), new Tools.MockBuilder()));
		assertEquals("isToken(+), getName()(+), isToken(-), getName()(-)(compound)", Tools.consumeResult());
	}

	@Test
	public void testParse3() {
		Tools.consumeResult();
		assertFalse(stmt.parse(Tools.getMockLexer("27", "4"), new Tools.MockBuilder()));
		assertEquals("isToken(27)", Tools.consumeResult());
	}

	/**
	 * Test of toString method, of class AssumptionStatement.
	 */
	@Test
	public void testToString() {
		assertEquals("compound", stmt.toString());
	}

	/**
	 * Test of toBNFString method, of class AssumptionStatement.
	 */
	@Test
	public void testToBNFString() {
		assertEquals("compound= + -", stmt.toBNFString());
	}
}
