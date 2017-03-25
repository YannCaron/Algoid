/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.builder;

import fr.cyann.al.syntax.ALTools;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class ToolsTest {

	public ToolsTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of lazyBody method, of class Tools.
	 */
	@Test
	public void testLazyBlock() {
		AST stmt = new BooleanExpression(new Token(TokenType.SYMBOL, "1"));
		AST result = ALTools.lazyBlock(stmt);
		assertNotSame(stmt, result);
		assertTrue(result instanceof Block);

		stmt = new Block(new Token(TokenType.SYMBOL, "1"));
		result = ALTools.lazyBlock(stmt);
		assertEquals(stmt, result);
		assertTrue(result instanceof Block);

	}
}
