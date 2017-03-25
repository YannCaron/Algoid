/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.scope;

import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class ParameterScopeTest {

	Scope root;
	ParameterScope scope;

	@Before
	public void setUp() throws Exception {
		root = new Scope("root");
		scope = new ParameterScope("params", root);
	}

	/**
	 * Test of resolve method, of class ParameterScope.
	 */
	@Test
	public void testResolve() throws Exception {
		root.define(Identifiers.getID("a"), new MutableVariant("a var"));
		scope.define(Identifiers.getID("b"), new MutableVariant("b var"));

		assertEquals(new MutableVariant("a var"), scope.resolve(Identifiers.getID("a")));
		assertEquals(new MutableVariant("b var"), scope.resolve(Identifiers.getID("b")));

		try {
			scope.resolve(Identifiers.getID("c"), true);
			fail("Exception must be thrown");
		} catch (Exception ex) {
		}

		scope.define(Identifiers.getID("a"), new MutableVariant("a overriding var"));
		assertEquals(new MutableVariant("a overriding var"), scope.resolve(Identifiers.getID("a")));
	}

	/**
	 * Test of toString method, of class ParameterScope.
	 */
	@Test
	public void testToString() {
		root.define(Identifiers.getID("a"), new MutableVariant("a var"));
		scope.define(Identifiers.getID("b"), new MutableVariant("b var"));

		assertEquals("param (params) [b] => scope (root) [a]", scope.toString());
	}
}
