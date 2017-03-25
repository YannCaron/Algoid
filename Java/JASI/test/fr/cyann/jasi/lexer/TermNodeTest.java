/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.lexer;

import fr.cyann.utils.Method;
import fr.cyann.utils.Reflect;
import java.util.List;
import java.text.CharacterIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TermNodeTest {

	public class MockTermNode extends TermNode {

		private String name;

		public MockTermNode(String name) {
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
	private TermNode term = null;
	List<Term> children = null;
	private Term t1, t2, t3 = null;

	@Before
	public void setUp() throws Exception {
		term = new MockTermNode("n1");
		children = (List<Term>) Reflect.getPrivateFieldValue(term, "children");

		t1 = new TermLeafTest.MockTermLeaf("l1.1");
		t2 = new MockTermNode("n2");
		t2.add(new TermLeafTest.MockTermLeaf("l2.1"));
		t2.add(new TermLeafTest.MockTermLeaf("l2.2"));
		t2.add(new MockTermNode("n3").add(new TermLeafTest.MockTermLeaf("l3.1")));

		t3 = new TermLeafTest.MockTermLeaf("l1.2");

		term.add(t1);
		term.add(t2);
		term.add(t3);
	}

	/**
	 * Test of getChildren method, of class TermNode.
	 */
	@Test
	public void testGetChildren() {
		assertEquals(3, term.getChildren().size());
		assertEquals("l1.1", term.getChildren().get(0).getTerm());
		assertEquals("n2", term.getChildren().get(1).getTerm());
		assertEquals("l1.2", term.getChildren().get(2).getTerm());
	}

	/**
	 * Test of add method, of class TermNode.
	 */
	@Test
	public void testAdd() {
		assertEquals(3, children.size());
		assertEquals("l1.1", children.get(0).getTerm());
		assertEquals("n2", children.get(1).getTerm());
		assertEquals("l1.2", children.get(2).getTerm());
	}

	/**
	 * Test of remove method, of class TermNode.
	 */
	@Test
	public void testRemove() {
		assertEquals(3, children.size());
		term.remove(t1);
		assertEquals(2, children.size());
		term.remove(t2);
		assertEquals(1, children.size());
		term.remove(t3);
		assertEquals(0, children.size());
	}

	/**
	 * Test of initTravel method, of class TermNode.
	 */
	@Test
	public void testInitTravel() {
		Reflect.setPrivateFieldValue(term, "lock", true);
		Reflect.setPrivateFieldValue(t2, "lock", true);

		term.initTravel();
		assertEquals(false, Reflect.getPrivateFieldValue(term, "lock"));
		assertEquals(false, Reflect.getPrivateFieldValue(t2, "lock"));
	}

	/**
	 * Test of depthFirstTravelImpl method, of class TermNode.
	 */
	@Test
	public void testDepthFirstTravelImpl() {
		final StringBuilder sb = new StringBuilder();
		term.depthFirstTravel(new Method<Term, Term>() {

			@Override
			public Term invoke(Term... args) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(args[0].getTerm());
				return args[0];
			}
		});

		assertEquals("n1 l1.1 n2 l2.1 l2.2 n3 l3.1 l1.2", sb.toString());
	}

	/**
	 * Test of breadthFirstTravelImpl method, of class TermNode.
	 */
	@Test
	public void testBreadthFirstTravelImpl() {
		final StringBuilder sb = new StringBuilder();
		term.breadthFirstTravel(new Method<Term, Term>() {

			@Override
			public Term invoke(Term... args) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(args[0].getTerm());
				return args[0];
			}
		});

		assertEquals("n1 l1.1 n2 l1.2 l2.1 l2.2 n3 l3.1", sb.toString());
	}
}
