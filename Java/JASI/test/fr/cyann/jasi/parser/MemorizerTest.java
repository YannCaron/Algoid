/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.utils.Reflect;
import java.util.Map;
import fr.cyann.utils.UnitTesting;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class MemorizerTest {

	Memorizer memorizer = null;
	Statement stmt1 = null, stmt2 = null;
	Map<Integer, Statement> memo = null;

	@Before
	public void setUp() {
		memorizer = new Memorizer();
		stmt1 = new KeyStatement("keyword1");
		stmt1 = new KeyStatement("keyword2");

		memo = (Map<Integer, Statement>) Reflect.getPrivateFieldValue(memorizer, "memo");
	}

	/**
	 * Test of getEfficiency method, of class Memorizer.
	 */
	@Test
	public void testGetEfficiency() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				Memorizer.getEfficiency();
			}
		});

		memorizer.store(0, stmt1);
		memorizer.store(1, stmt2);
		memorizer.store(2, stmt1);

		memorizer.get(0);
		assertEquals(100, Memorizer.getEfficiency());
		memorizer.get(7);
		assertEquals(50, Memorizer.getEfficiency());
		memorizer.get(7);
		assertEquals(33, Memorizer.getEfficiency());
		memorizer.get(1);
		assertEquals(50, Memorizer.getEfficiency());

	}

	/**
	 * Test of clear method, of class Memorizer.
	 */
	@Test
	public void testClear() {

		assertTrue(memo.isEmpty());
		memorizer.store(0, stmt1);
		memorizer.store(1, stmt2);
		memorizer.store(2, stmt1);
		assertFalse(memo.isEmpty());
		memorizer.clear();
		assertTrue(memo.isEmpty());

	}

	/**
	 * Test of store method, of class Memorizer.
	 */
	@Test
	public void testStore() {
		assertTrue(memo.isEmpty());
		memorizer.store(0, stmt1);
		assertEquals(stmt1, memo.get(0));
	}

	/**
	 * Test of get method, of class Memorizer.
	 */
	@Test
	public void testGet() {
		assertTrue(memo.isEmpty());
		memorizer.store(0, stmt1);
		assertEquals(stmt1, memorizer.get(0));
	}

	/**
	 * Test of isEmpty method, of class Memorizer.
	 */
	@Test
	public void testIsEmpty() {
		assertTrue(memorizer.isEmpty());
		memorizer.store(0, stmt1);
		memorizer.store(1, stmt2);
		memorizer.store(2, stmt1);
		assertFalse(memorizer.isEmpty());
		memorizer.clear();
		assertTrue(memorizer.isEmpty());
	}

	/**
	 * Test of size method, of class Memorizer.
	 */
	@Test
	public void testSize() {
		assertTrue(memorizer.isEmpty());
		memorizer.store(0, stmt1);
		memorizer.store(1, stmt2);
		memorizer.store(2, stmt1);
		assertEquals(memo.size(), memorizer.size());
	}

	/**
	 * Test of toString method, of class Memorizer.
	 */
	@Test
	public void testToString() {
		assertEquals("Memorizer{memo={}}", memorizer.toString());
	}
}
