/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class CallTest1 {

	@Test
	public void expressionTest1() {

		String source = "" +
			"print(\"hello\" .. \" \" .. \"world\");\n" +
			"print(\"I am JASI\");";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest2() {

		String source = "" +
			"set a;" +
			"print (a = 5);" +
			"unit.assertEquals(5, a);" +
			"print (a = 10+4);" +
			"unit.assertEquals(14, a);" +
			"print (a = \"toto\" .. 58);" +
			"unit.assertEquals(\"toto58\", a);" +
			"print (a = true || false);" +
			"unit.assertEquals(true, a);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest3() {

		String source = "" +
			"set a = 1 + 1;\n" +
			"print (\"a = \" .. a);" +
			"unit.assertEquals (2, a);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest4() {

		String source = "" +
			"set i = 0;" +
			"i = i + 1;" +
			"unit.assertEquals(1, i);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest5() {

		String source = "" +
			"set a = 1 + 1;\n" +
			"print (a);" +
			"unit.assertEquals(2, a);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest7() {

		String source = "" +
			"set a;" +
			"print(a); print(a);" +
			"unit.assertEquals(nil, a);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest8() {

		String source = "" +
			"set i;" +
			"i = i + 1; i = i + 1; print(i);" +
			"unit.assertEquals(2, i);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}

	@Test
	public void expressionTest9() {

		String source = "" +
			"set i;" +
			"print(i = - i + 1);" +
			"unit.assertEquals(1, i);";
		System.out.println(source);
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

		assertTrue(true);
		System.out.println("");

	}
}