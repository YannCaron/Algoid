/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class StatementAgregatorTest {
	
	StatementAgregator stmt = null;
	
	@Before
	public void setUp() {
		stmt = new StatementAgregator(new CompoundStatement("test").add(new KeyStatement("keyword")), new Tools.MockAgregator());
	}

	/**
	 * Test of parse method, of class StatementAgregator.
	 */
	@Test
	public void testParse1() {
		stmt.parse(Tools.getMockLexer("keyword"), new Tools.MockBuilder());
		assertEquals("(keyword)(test), build", Tools.consumeResult());
	}

	@Test
	public void testParse2() {
		stmt.parse(Tools.getMockLexer("key"), new Tools.MockBuilder());
		assertEquals("", Tools.consumeResult());
	}

}
