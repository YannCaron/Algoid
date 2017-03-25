/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.builder;

import java.util.Stack;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.ToStringTreeVisitor;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.visitor.ToStringContext;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.Reflect;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class ASTBuilderTest {

	ASTBuilder builder = null;
	Stack<AST> stack = null;

	@Before
	public void setUp() {
		builder = new ASTBuilder();
		stack = (Stack<AST>) Reflect.getPrivateFieldValue(builder, "stack");
	}

	/**
	 * Test of getInstance method, of class ASTBuilder.
	 */
	@Test
	public void testGetInstance() {
		assertSame(builder, new ASTBuilder());
	}

	/**
	 * Test of initialize method, of class ASTBuilder.
	 */
	@Test
	public void testInitialize() {
		assertNotNull(stack);
	}

	/**
	 * Test of push method, of class ASTBuilder.
	 */
	@Test
	public void testPush() {
		assertEquals(0, stack.size());
		builder.push(new Block(null));
		assertEquals(1, stack.size());
	}

	/**
	 * Test of pop method, of class ASTBuilder.
	 */
	@Test
	public void testPop() {
		Block block = new Block(null);
		builder.push(block);

		assertEquals(1, stack.size());
		assertEquals(block, builder.pop());
		assertEquals(0, stack.size());

	}

	/**
	 * Test of peek method, of class ASTBuilder.
	 */
	@Test
	public void testPeek() {
		Block block = new Block(null);
		builder.push(block);

		assertEquals(1, stack.size());
		assertEquals(block, builder.peek());
		assertEquals(1, stack.size());
	}

	/**
	 * Test of empty method, of class ASTBuilder.
	 */
	@Test
	public void testEmpty() {
		assertTrue(builder.isEmpty());
		builder.push(new Block(null));
		assertFalse(builder.isEmpty());
	}

	/**
	 * Test of injectVisitor method, of class ASTBuilder.
	 */
	@Test
	public void testInjectVisitor() {
		VisitorInjector mockInjector = new MethodVisitorInjector() {

			@Override
			public void visite(AST ast, Context context) {
				// do nothing
			}
		};

		Block block = new Block(null);
		BinaryOperator expr = new BinaryOperator(new Token(TokenType.SYMBOL, "+"));

		expr.setLeft(new NumberExpression(new Token(TokenType.SYMBOL, "1")));
		expr.setRight(new NumberExpression(new Token(TokenType.SYMBOL, "2")));

		assertNull(Reflect.getPrivateFieldValue(block, "visitor"));
		assertNull(Reflect.getPrivateFieldValue(expr, "visitor"));
		assertNull(Reflect.getPrivateFieldValue(expr.getLeft(), "visitor"));
		assertNull(Reflect.getPrivateFieldValue(expr.getRight(), "visitor"));

		builder.push(block);
		builder.push(expr);
		builder.injectVisitor(mockInjector);

		assertNotNull(Reflect.getPrivateFieldValue(block, "visitor"));
		assertNotNull(Reflect.getPrivateFieldValue(expr, "visitor"));
		assertNotNull(Reflect.getPrivateFieldValue(expr.getLeft(), "visitor"));
		assertNotNull(Reflect.getPrivateFieldValue(expr.getRight(), "visitor"));

	}

	/**
	 * Test of visite method, of class ASTBuilder.
	 */
	@Test
	public void testVisite() {
		final StringBuilder sb = new StringBuilder();

		VisitorInjector mockInjector = new MethodVisitorInjector() {

			@Override
			public void visite(AST ast, Context context) {
				if (sb.length() != 0) sb.append(", ");
				sb.append(ast.getClass().getName());
			}
		};

		Block block = new Block(null);
		BinaryOperator expr = new BinaryOperator(new Token(TokenType.SYMBOL, "+"));
		expr.setLeft(new NumberExpression(new Token(TokenType.SYMBOL, "1")));
		expr.setRight(new NumberExpression(new Token(TokenType.SYMBOL, "2")));

		builder.push(block);
		builder.push(expr);
		builder.injectVisitor(mockInjector).visite(null);

		assertEquals("fr.cyann.jasi.ast.Block, fr.cyann.jasi.ast.BinaryOperator", sb.toString());
	}

	public String toStringTree() {
		ToStringTreeVisitor visitor = new ToStringTreeVisitor();
		ToStringContext context = new ToStringContext();
		builder.injectVisitor(visitor).visite(context);

		return context.getSource();
	}

	/**
	 * Test of toStringTree method, of class ASTBuilder.
	 */
	@Test
	public void testToStringTree() {
		Block block = new Block(null);
		BinaryOperator expr = new BinaryOperator(new Token(TokenType.SYMBOL, "+"));
		expr.setLeft(new NumberExpression(new Token(TokenType.SYMBOL, "1")));
		expr.setRight(new NumberExpression(new Token(TokenType.SYMBOL, "2")));

		builder.push(block);
		builder.push(expr);

		assertEquals(" ()(+ 1 2)", toStringTree());
	}

	/**
	 * Test of toString method, of class ASTBuilder.
	 */
	@Test
	public void testToString() {
		builder.push(new Block(null));
		System.out.println(builder.toString());
		assertEquals("ASTBuilder{stack=[Block{statements=[], scope=null}]}", builder.toString());
	}
}
