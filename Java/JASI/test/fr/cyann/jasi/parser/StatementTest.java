/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.utils.Method;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class StatementTest {

	Statement stmt = null;
	
	@Before
	public void setUp() {
		stmt = new Tools.MockNodeStatement("root");
		stmt.add(new Tools.MockNodeStatement("n1").add(new Tools.MockNodeStatement("l1.1")).add(new Tools.MockNodeStatement("l1.2")));
		stmt.add(new Tools.MockNodeStatement("n2").add(new Tools.MockNodeStatement("l2.1")).add(new Tools.MockNodeStatement("n2.2").add(new Tools.MockNodeStatement("l2.2.1"))).add(new Tools.MockNodeStatement("l2.3")));
		stmt.add(new Tools.MockNodeStatement("n3").add(new Tools.MockNodeStatement("l3.1")).add(stmt));
	}

	/**
	 * Test of depthFirstTravel method, of class Statement.
	 */
	@Test
	public void testDepthFirstTravel() {
		final StringBuilder sb = new StringBuilder();
		
		stmt.depthFirstTravel(new Method<Statement, Statement>() {

			@Override
			public Statement invoke(Statement... args) {
				if (sb.length() > 0) sb.append(", ");
				sb.append(args[0].getName());
				return args[0];
			}
		});
		
		assertEquals("root, n1, l1.1, l1.2, n2, l2.1, n2.2, l2.2.1, l2.3, n3, l3.1", sb.toString());
	}

	/**
	 * Test of breadthFirstTravel method, of class Statement.
	 */
	@Test
	public void testBreadthFirstTravel() {
		final StringBuilder sb = new StringBuilder();
		
		stmt.breadthFirstTravel(new Method<Statement, Statement>() {

			@Override
			public Statement invoke(Statement... args) {
				if (sb.length() > 0) sb.append(", ");
				sb.append(args[0].getName());
				return args[0];
			}
		});

		assertEquals("root, n1, n2, n3, l1.1, l1.2, l2.1, n2.2, l2.3, l3.1, l2.2.1", sb.toString());
	}

}
