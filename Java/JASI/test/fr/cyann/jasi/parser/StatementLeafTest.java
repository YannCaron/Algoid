/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.parser;

import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.utils.Method;
import fr.cyann.utils.UnitTesting;
import java.util.Queue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author CyaNn
 */
public class StatementLeafTest {

	StatementLeaf stmt = null;
	
	@Before
	public void setUp() {
		stmt = new StatementLeaf() {

			@Override
			public String getName() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean tryParse(BacktrackingIterator<Token> iterator) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public String toBNFString() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			void clearMemorizerImpl() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void initTravel() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void depthFirstTravelImpl(Method<Statement, Statement> method) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void breadthFirstTravelImpl(Method<Statement, Statement> method, Queue<Statement> queue) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}

	/**
	 * Test of get method, of class StatementLeaf.
	 */
	@Test
	public void testGet() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				stmt.get(0);
			}
		});
	}

	/**
	 * Test of add method, of class StatementLeaf.
	 */
	@Test
	public void testAdd() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				stmt.add(new KeyStatement("keyword"));
			}
		});
	}

	/**
	 * Test of remove method, of class StatementLeaf.
	 */
	@Test
	public void testRemove() {
		UnitTesting.assertException(new Runnable() {

			@Override
			public void run() {
				stmt.remove(new KeyStatement("keyword"));
			}
		});
	}

}
