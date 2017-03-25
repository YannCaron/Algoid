/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import fr.cyann.utils.Method;
import fr.cyann.utils.UnitTesting;
import java.text.CharacterIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TermLeafTest {

	public static class MockTermLeaf extends TermLeaf {

		private String name;

		public MockTermLeaf(String name) {
			this.name = name;
		}
		

		@Override
		public String getTerm() {
			return name;
		}

		@Override
		boolean find(CharacterIterator it) {
			return true;
		}
	}
	TermLeaf term = null;

	@Before
	public void setUp() throws Exception {
		term = new MockTermLeaf("leaf");
	}

	/**
	 * Test of add method, of class TermLeaf.
	 */
	@Test
	public void testAdd() {
		UnitTesting.assertException(new Runnable() {
			@Override
			public void run() {
				term.add(term);
			}
		});
	}

	/**
	 * Test of remove method, of class TermLeaf.
	 */
	@Test
	public void testRemove() {
		UnitTesting.assertException(new Runnable() {
			@Override
			public void run() {
				term.remove(null);
			}
		});
	}

	/**
	 * Test of depthFirstTravelImpl method, of class TermLeaf.
	 */
	@Test
	public void testDepthFirstTravelImpl() {
		final StringBuilder sb = new StringBuilder();
		
		term.depthFirstTravel(new Method<Term, Term>() {
			@Override
			public Term invoke(Term... args) {
				sb.append(args[0].getTerm());
				return args[0];
			}
		});
		
		assertEquals("leaf", sb.toString());
		
	}

	/**
	 * Test of breadthFirstTravelImpl method, of class TermLeaf.
	 */
	@Test
	public void testBreadthFirstTravelImpl() {
		final StringBuilder sb = new StringBuilder();
		
		term.breadthFirstTravel(new Method<Term, Term>() {
			@Override
			public Term invoke(Term... args) {
				sb.append(args[0].getTerm());
				return args[0];
			}
		});
		
		assertEquals("leaf", sb.toString());
		
	}
}
