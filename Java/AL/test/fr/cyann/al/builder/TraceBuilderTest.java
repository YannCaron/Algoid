/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.builder;

import fr.cyann.al.ast.NumberExpression;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.parser.CompoundStatement;
import fr.cyann.jasi.parser.StatementLeafToken;
import fr.cyann.jasi.parser.StatementNode;
import fr.cyann.jasi.parser.TypedStatement;
import fr.cyann.utils.UnitTesting;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class TraceBuilderTest {

	InterpreterBuilder builder;

	/**
	 * Test of getInstance method, of class TraceBuilder.
	 */
	@Test
	public void testGetInstance() {
		assertSame(builder, new ASTBuilder());
	}

	/**
	 * Test of build method, of class TraceBuilder.
	 */
	@Test
	public void testBuild_StatementLeafToken_FactoryStrategy() {
		builder = new ASTBuilder();
		final StatementLeafToken statement = new TypedStatement(TokenType.SYMBOL);

		builder.build(statement, new ClassFactory(NumberExpression.class));

		UnitTesting.assertOutput("Build leaf: SYMBOL => ClassFactory\r\n", new Runnable() {
			@Override
			public void run() {
				builder.build(statement, new ClassFactory(NumberExpression.class));
			}
		});

	}

	/**
	 * Test of build method, of class TraceBuilder.
	 */
	@Test
	public void testBuild_StatementNode_AgregatorStrategy() {
		builder = new ASTBuilder();
		final StatementNode statement = new CompoundStatement("compount");

		class MockAgregator implements AgregatorStrategy {

			@Override
			public void build(InterpreterBuilder builder, StatementNode node) {
				// do nothing
			}
		}

		UnitTesting.assertOutput("Build node : compount => MockAgregator\r\n", new Runnable() {
			@Override
			public void run() {
				builder.build(statement, new MockAgregator());
			}
		});

	}
}
