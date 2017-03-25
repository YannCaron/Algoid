/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import java.util.HashMap;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.NameMapVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class NameMapVisitorInjectorTest {

	private static class MockNameMapVisitorInjector extends NameMapVisitorInjector {

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
		public Map<String, VisitorInjector> getVisitorMap() {
			Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

			map.put("+", new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append("+");
				}
			});

			map.put("-", new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append("-");
				}
			});

			map.put("++", new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append("++");
				}
			});

			map.put("--", new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append("--");
				}
			});

			return map;
		}

		@Override
		protected VisitorInjector getDefault(AST ast) {
			return new MethodVisitorInjector() {

				@Override
				public void visite(AST ast, Context context) {
					append("default");
				}
			};
		}
	}

	NameMapVisitorInjector injector = null;

	@Before
	public void setUp() {
		injector = new MockNameMapVisitorInjector();
	}

	/**
	 * Test of getVisitor method, of class ClassMapVisitorInjector.
	 */
	@Test
	public void testGetVisitor() {
		injector.getVisitor(new BinaryOperator(new Token(TokenType.SYMBOL, "+"))).visite(null, null);
		injector.getVisitor(new BinaryOperator(new Token(TokenType.SYMBOL, "-"))).visite(null, null);
		injector.getVisitor(new UnaryOperator(new Token(TokenType.SYMBOL, "++"))).visite(null, null);
		injector.getVisitor(new UnaryOperator(new Token(TokenType.SYMBOL, "--"))).visite(null, null);
		assertEquals("+, -, ++, --", injector.toString());
	}
}
