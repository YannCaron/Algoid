/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class CallTest2 {

	@Test
	public void expressionTest1() {

		String source = "print();";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest2() {

		String source = "print(\"Hi univers!\");";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest4() {

		String source = "print( math.random(25 + 25));";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest5() {

		String source = "print(math.random(25) + 25);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest7() {

		String source = "print(math.random(math.random(math.random(25) + 25) + 25) + 25);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest8() {

		String source = "" +
			"unit.assertTrue(true);" +
			"set a = 5;" +
			"debug.printScope();" +
			"" +
			"unit.assertExists(unit);" +
			"unit.assertNotExists(another);" +
			"";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		System.out.println("");

	}

	@Test
	public void expressionTest9() {
		String source = "math.random((25)).addition(25);";
		AL.execute(new Syntax(), new UnitTestRuntime(), "");

		System.out.println("");

	}
}
