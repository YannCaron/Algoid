/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.builder;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class VoidAgregatorTest {

	/**
	 * Test of build method, of class VoidAgregator.
	 */
	@Test
	public void testBuild() {
		VoidAgregator agregator = new VoidAgregator();
		agregator.build(null);
		assertTrue(true); // no error
	}
}
