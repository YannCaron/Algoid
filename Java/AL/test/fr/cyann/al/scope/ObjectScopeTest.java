/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.scope;

import fr.cyann.jasi.lexer.Token;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.TokenType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class ObjectScopeTest {

	ObjectDeclaration ast;
	Scope root;
	ObjectScope scope, p1, p2;

	@Before
	public void setUp() throws Exception {
		ast = new ObjectDeclaration(new Token(TokenType.SYMBOL, "object"));

		root = new Scope("root");
		scope = new ObjectScope("ast", root);
		p1 = new ObjectScope("ast", root);
		p2 = new ObjectScope("ast", root);
	}

	/**
	 * Test of resolve method, of class ObjectScope.
	 */
	@Test
	public void testResolve() throws Exception {
		root.define(Identifiers.getID("a"), new MutableVariant("a var"));
		scope.define(Identifiers.getID("b"), new MutableVariant("b var"));

		assertEquals("a var", scope.resolve(Identifiers.getID("a")));
		assertEquals("b var", scope.resolve(Identifiers.getID("b")));

		try {
			scope.resolve(Identifiers.getID("a"), true);
			fail("Exception must be thrown");
		} catch (Exception ex) {
		}

		scope.define(Identifiers.getID("a"), new MutableVariant("a overriding var"));
		assertEquals("a overriding var", scope.resolve(Identifiers.getID("a")));
	}

	/**
	 * Test of addParent method, of class ObjectScope.
	 */
	@Test
	public void testAddParent() throws Exception {
		scope.addParent(p1);
		scope.addParent(p2);

		p1.define(Identifiers.getID("a"), new MutableVariant("a from p1"));
		assertEquals("a from p1", scope.resolve(Identifiers.getID("a")));

		p2.define(Identifiers.getID("a"), new MutableVariant("a from p2"));
		assertEquals("a from p1", scope.resolve(Identifiers.getID("a")));

		p2.define(Identifiers.getID("b"), new MutableVariant("b from p2"));
		assertEquals("b from p2", scope.resolve(Identifiers.getID("b")));

		scope.define(Identifiers.getID("b"), new MutableVariant("b over"));
		assertEquals("b over", scope.resolve(Identifiers.getID("b")));
		assertEquals("b from p2", p2.resolve(Identifiers.getID("b")));

	}

	/**
	 * Test of getParents method, of class ObjectScope.
	 */
	@Test
	public void testGetParents() {
		scope.addParent(p1);
		scope.addParent(p2);

		assertEquals(2, scope.getParents().size());
		assertEquals(p1, scope.getParents().get(0));
		assertEquals(p2, scope.getParents().get(1));
	}

	/**
	 * Test of reset method, of class ObjectScope.
	 */
	@Test
	public void testClear() throws Exception {
		scope.addParent(p1);
		scope.addParent(p2);

		p1.define(Identifiers.getID("a"), new MutableVariant("a from p1"));
		scope.define(Identifiers.getID("b"), new MutableVariant("b over"));

		//scope.reset();

		try {
			scope.resolve(Identifiers.getID("a"), true);
			fail("Exception must be thrown");
		} catch (Exception ex) {
		}

		try {
			scope.resolve(Identifiers.getID("b"), true);
			fail("Exception must be thrown");
		} catch (Exception ex) {
		}
	}

	/**
	 * Test of toString method, of class ObjectScope.
	 */
	@Test
	public void testToString() {
		scope.addParent(p1);
		scope.addParent(p2);

		p1.define(Identifiers.getID("a"), new MutableVariant("a from p1"));
		p2.define(Identifiers.getID("a"), new MutableVariant("a from p2"));
		p2.define(Identifiers.getID("b"), new MutableVariant("b from p2"));
		scope.define(Identifiers.getID("b"), new MutableVariant("b over"));

		System.out.println(scope);
		assertEquals("object (object) [b] => [object (object) [a] => [] => scope (root) []), object (object) [a, b] => [] => scope (root) [])] => scope (root) [])", scope.toString());
	}
}
