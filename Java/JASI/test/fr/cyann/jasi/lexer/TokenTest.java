/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import org.junit.Before;
import fr.cyann.utils.UnitTesting;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TokenTest {

    Token token = null;

    @Before
    public void setUp() throws Exception {
	token = new Token(TokenType.SYMBOL, ".", 10, 7, 45);
    }

    /**
     * Test of getType method, of class Token.
     */
    @Test
    public void testType() {
	UnitTesting.assertAccessorAndMutator(token, "type", TokenType.SYMBOL, true);
    }

    /**
     * Test of getText method, of class Token.
     */
    @Test
    public void testText() {
	UnitTesting.assertAccessorAndMutator(token, "text", ".", true);
    }

    /**
     * Test of getLine method, of class Token.
     */
    @Test
    public void testLine() {
	UnitTesting.assertAccessorAndMutator(token, "line", 10, true);
    }

    /**
     * Test of getCol method, of class Token.
     */
    @Test
    public void testCol() {
	UnitTesting.assertAccessorAndMutator(token, "col", 7, true);
    }

    /**
     * Test of getPos method, of class Token.
     */
    @Test
    public void testPos() {
	UnitTesting.assertAccessorAndMutator(token, "pos", 45, true);
    }

    /**
     * Test of getLength method, of class Token.
     */
    @Test
    public void testGetLength() {
	assertEquals(1, token.getLength());
    }

    /**
     * Test of equals method, of class Token.
     */
    @Test
    public void testEquals() {
	assertTrue(token.equals(new Token(TokenType.SYMBOL, ".")));
	assertTrue(token.equals(new Token(TokenType.SYMBOL, ".")));
	assertFalse(token.equals(new Token(TokenType.SYMBOL, "..")));
    }

    /**
     * Test of hashCode method, of class Token.
     */
    @Test
    public void testHashCode() {
	assertEquals(token.hashCode(), new Token(TokenType.SYMBOL, ".").hashCode());
	assertNotSame(token.hashCode(), new Token(TokenType.SYMBOL, ".").hashCode());
	assertNotSame(token.hashCode(), new Token(TokenType.SYMBOL, "..").hashCode());
    }

    /**
     * Test of toString method, of class Token.
     */
    @Test
    public void testToString() {
	assertEquals("Token{type=SYMBOL, text='.', line=10, col=7, pos=45}", token.toString());
    }
}
