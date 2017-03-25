/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class ClassMapVisitorInjectorTest {

	private static class MockClassMapVisitorInjector extends ClassMapVisitorInjector {

		StringBuilder sb = new StringBuilder();

		@Override
		public String toString() {
			return sb.toString();
		}

		private void append(String text) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(text);
		}

		@Override
		public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
			Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

			map.put(BooleanExpression.class, new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append(BooleanExpression.class.getSimpleName());
				}
			});

			map.put(NumberExpression.class, new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append(NumberExpression.class.getSimpleName());
				}
			});

			map.put(StringExpression.class, new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append(StringExpression.class.getSimpleName());
				}
			});


			return map;
		}
	}
	ClassMapVisitorInjector injector = null;

	@Before
	public void setUp() {
		injector = new MockClassMapVisitorInjector();
	}

	/**
	 * Test of getVisitorMap method, of class ClassMapVisitorInjector.
	 */
	@Test
	public void testGetVisitorMap() {
		assertNotNull(injector.getVisitorMap());
		((MethodVisitorInjector) injector.getVisitorMap().get(NumberExpression.class)).getVisitor(null).visite(null, null);
		assertEquals("NumberExpression", injector.toString());
	}

	/**
	 * Test of getVisitor method, of class ClassMapVisitorInjector.
	 */
	@Test
	public void testGetVisitor() {
		injector.getVisitor(new BooleanExpression(new Token(TokenType.SYMBOL, "1"))).visite(null, null);
		injector.getVisitor(new NumberExpression(new Token(TokenType.SYMBOL, "1"))).visite(null, null);
		injector.getVisitor(new StringExpression(new Token(TokenType.SYMBOL, "1"))).visite(null, null);
		assertEquals("BooleanExpression, NumberExpression, StringExpression", injector.toString());
	}
}
