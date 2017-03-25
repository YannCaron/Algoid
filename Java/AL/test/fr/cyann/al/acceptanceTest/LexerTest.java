/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.syntax.Syntax;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.Token;
import java.util.List;

import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class LexerTest {

	/**
	 * Test of remove method, of class Lexer.
	 */
	@Test
	public void testLexer1() {

		List<Token> tokens = new LinkedList<Token>();

		Lexer lx = new Syntax().constructLexer();
		lx.match("++ const int a=1078.5 +;\n"
				+ "set i = 7 + 5;");

		while (lx.hasNext()) {
			tokens.add(lx.current());
			System.out.println(lx.next());
		}

		assertEquals(15, tokens.size());
		assertEquals("++", tokens.get(0).getText());
		assertEquals(0, tokens.get(0).getPos());
		assertEquals(0, tokens.get(0).getCol());
		assertEquals(0, tokens.get(0).getLine());
		assertEquals(";", tokens.get(14).getText());
		assertEquals(38, tokens.get(14).getPos());
		assertEquals(13, tokens.get(14).getCol());
		assertEquals(1, tokens.get(14).getLine());

		System.out.println("");

	}

	/**
	 * Test of remove method, of class Lexer.
	 */
	@Test
	public void testLexer2() {
		List<Token> tokens = new LinkedList<Token>();

		Lexer lx = new Syntax().constructLexer();
		lx.match("i=5;\n"
				+ "print(i);\n");

		while (lx.hasNext()) {
			tokens.add(lx.current());
			System.out.println(lx.next());
		}

		assertEquals(9, tokens.size());
		assertEquals("i", tokens.get(0).getText());
		assertEquals(0, tokens.get(0).getPos());
		assertEquals(0, tokens.get(0).getCol());
		assertEquals(0, tokens.get(0).getLine());
		assertEquals(";", tokens.get(8).getText());
		assertEquals(13, tokens.get(8).getPos());
		assertEquals(8, tokens.get(8).getCol());
		assertEquals(1, tokens.get(8).getLine());

		System.out.println("");

	}
}
