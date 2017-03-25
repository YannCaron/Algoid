/*
 * Copyright (C) 2012 CyaNn
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
 * @author CyaNn
 */
public class ScopeTest {

	Scope root;

	@Before
	public void setUp() throws Exception {
		root = new Scope("root");
	}

	/**
	 * Test of getName method, of class ScopeImpl.
	 */
	@Test
	public void testGetName() {
		assertEquals("root", root.getName());
	}

	/**
	 * Test of getRoot method, of class ScopeImpl.
	 */
	@Test
	public void testGetRoot() {
		assertEquals(root, root.getRoot());
	}

	/**
	 * Test of getVariables method, of class ScopeImpl.
	 */
	@Test
	public void testGetVariables() {
		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		root.define(Identifiers.getID("b"), new MutableVariant("test b"));
		root.define(Identifiers.getID("c"), new MutableVariant("test c"));

		assertEquals(3, root.getVariables().size());
		assertEquals(new MutableVariant("test c"), root.getVariables().get(Identifiers.getID("c")));
	}

	/**
	 * Test of isAlreadyDefined method, of class ScopeImpl.
	 */
	@Test
	public void testIsAlreadyDefined() {
		assertFalse(root.isAlreadyDefined(Identifiers.getID("a")));
		root.define(Identifiers.getID("a"), new MutableVariant("test"));
		assertTrue(root.isAlreadyDefined(Identifiers.getID("a")));
	}

	/**
	 * Test of define method, of class ScopeImpl.
	 */
	@Test
	public void testDefine() {
		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		assertEquals(new MutableVariant("test a"), root.getVariables().get("a"));
	}

	/**
	 * Test of resolve method, of class ScopeImpl.
	 */
	@Test
	public void testResolve_String() throws Exception {
		try {
			root.resolve(Identifiers.getID("a"));
			fail("exception expected !");
		} catch (Exception ex) {
		}

		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		assertEquals(new MutableVariant("test a"), root.resolve(Identifiers.getID("a")));

	}

	/**
	 * Test of resolve method, of class ScopeImpl.
	 */
	@Test
	public void testResolve_String_boolean() throws Exception {
		try {
			root.resolve(Identifiers.getID("a"), true);
			fail("exception expected !");
		} catch (Exception ex) {
		}

		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		assertEquals(new MutableVariant("test a"), root.resolve(Identifiers.getID("a"), true));
	}

	/**
	 * Test of remove method, of class ScopeImpl.
	 */
	@Test
	public void testRemove() throws Exception {
		try {
			root.resolve(Identifiers.getID("a"));
			fail("exception expected !");
		} catch (Exception ex) {
		}

		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		root.remove(Identifiers.getID("a"));

		try {
			root.resolve(Identifiers.getID("a"));
			fail("exception expected !");
		} catch (Exception ex) {
		}
	}

	/**
	 * Test of reset method, of class ScopeImpl.
	 */
	@Test
	public void testClear() throws Exception {
		try {
			root.resolve(Identifiers.getID("a"));
			fail("exception expected !");
		} catch (Exception ex) {
		}

		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		root.define(Identifiers.getID("d"), new MutableVariant("test d"));
		//root.reset();

		try {
			root.resolve(Identifiers.getID("a"));
			fail("exception expected !");
		} catch (Exception ex) {
		}
	}

	/**
	 * Test of toString method, of class ScopeImpl.
	 */
	@Test
	public void testToString() {
		root.define(Identifiers.getID("a"), new MutableVariant("test a"));
		root.define(Identifiers.getID("b"), new MutableVariant("test b"));

		System.out.println(root);
		assertEquals("scope (root) [a, b]", root.toString());
	}
}
