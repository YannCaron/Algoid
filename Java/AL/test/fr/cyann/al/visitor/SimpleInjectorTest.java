/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.utils.Reflect;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class SimpleInjectorTest {

	MethodVisitorInjector<NumberExpression, Context> injector;

	@Before
	public void setUp() {
		injector = new MethodVisitorInjector<NumberExpression, Context>() {

			private String result;

			@Override
			public void visite(NumberExpression ast, Context context) {
				result = "run";
			}
		};
	}

	/**
	 * Test of getVisitor method, of class MethodVisitorInjector.
	 */
	@Test
	public void testGetVisitor() {
		MethodVisitor visitor = injector.getVisitor(null);
		visitor.visite(null, null);
		assertEquals("run", Reflect.getPrivateFieldValue(visitor, "result"));
	}

	/**
	 * Test of toString method, of class MethodVisitorInjector.
	 */
	@Test
	public void testToString() {
		assertEquals("SimpleInjector#NumberExpression", injector.toString());
	}

}
