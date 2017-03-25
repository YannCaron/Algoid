/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.utils.UnitTesting;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class StatementNodeTest {

	StatementNode stmt = null;
	StatementNode n1 = null;
	StatementNode n2 = null;
	StatementNode n3 = null;

	@Before
	public void setUp() {
		stmt = new Tools.MockNodeStatement("root");
		n1 = new Tools.MockNodeStatement("n1").add(new Tools.MockNodeStatement("l1.1")).add(new Tools.MockNodeStatement("l1.2"));
		stmt.add(n1);
		n2 = new Tools.MockNodeStatement("n2").add(new Tools.MockNodeStatement("l2.1")).add(new Tools.MockNodeStatement("n2.2").add(new Tools.MockNodeStatement("l2.2.1"))).add(new Tools.MockNodeStatement("l2.3"));
		stmt.add(n2);
		n3 = new Tools.MockNodeStatement("n3").add(new Tools.MockNodeStatement("l3.1")).add(stmt);
		stmt.add(n3);
	}

	/**
	 * Test of get method, of class StatementNode.
	 */
	@Test
	public void testGet() {
		assertEquals(n1, stmt.get(0));
		assertEquals(n2, stmt.get(1));
		assertEquals(n3, stmt.get(2));
	}

	/**
	 * Test of add method, of class StatementNode.
	 */
	@Test
	public void testAdd() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				assertNotNull(n3.get(2));
			}
		});

		n3.add(n1);
		assertEquals(n1, n3.get(2));
	}

	/**
	 * Test of remove method, of class StatementNode.
	 */
	@Test
	public void testRemove() {
		n3.add(n1);
		assertEquals(n1, n3.get(2));
		n3.remove(n1);

		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				assertNotNull(n3.get(2));
			}
		});
	}
}
