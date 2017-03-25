/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.Lexer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class OptionalStatementTest {

    OptionalStatement stmt = null;

    @Before
    public void setUp() {
	stmt = new OptionalStatement(new Tools.MockStatement("keyword"));
    }

    /**
     * Test of getName method, of class OptionalStatement.
     */
    @Test
    public void testGetName() {
	assertEquals("[keyword]", stmt.getName());
	assertEquals("getName()", Tools.consumeResult());
    }

    /**
     * Test of tryParse method, of class AssumptionStatement.
     */
    @Test
    public void testTryParse1() {
	Lexer lexer = Tools.getMockLexer("key");
	assertTrue(stmt.tryParse(lexer)); // always return true
	assertEquals("isToken(key)", Tools.consumeResult());
	assertEquals(0, lexer.getLookaheadPosition()); // not found
    }

    @Test
    public void testTryParse2() {
	Lexer lexer = Tools.getMockLexer("keyword");
	assertTrue(stmt.tryParse(lexer));
	assertEquals("isToken(keyword)", Tools.consumeResult());
	assertEquals(1, lexer.getLookaheadPosition()); // found
    }

    /**
     * Test of parse method, of class AssumptionStatement.
     */
    @Test
    public void testParse1() {
	Lexer lexer = Tools.getMockLexer("key");
	assertTrue(stmt.parse(lexer, null));
	assertEquals("isToken(key)", Tools.consumeResult());
	assertEquals(0, lexer.getPosition()); // not found
    }

    @Test
    public void testParse2() {
	Lexer lexer = Tools.getMockLexer("keyword");
	assertTrue(stmt.parse(lexer, null));
	assertEquals("isToken(keyword)", Tools.consumeResult());
	assertEquals(1, lexer.getPosition()); // found
    }

}
