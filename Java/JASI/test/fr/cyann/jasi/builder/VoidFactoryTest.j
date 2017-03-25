/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.builder;

import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.parser.TypedStatement;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class VoidFactoryTest {

	/**
	 * Test of buildLeaf method, of class VoidFactory.
	 */
	@Test
	public void testBuildLeaf() {
		VoidFactory factory = new VoidFactory();
		factory.buildLeaf(new TypedStatement(TokenType.EOF));
		assertTrue(true); // no error
	}
}
