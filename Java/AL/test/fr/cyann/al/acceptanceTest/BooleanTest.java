/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;

/**
 * The StringTest class.
 * @author Yann Caron
 * @version v0.1
 */
public class BooleanTest {

	@Test
	public void testTranstypage1() throws Exception {

		String source = "" +
			"set a = true && 2 + 2;" +
			"unit.assertTrue(a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testTranstypage2() throws Exception {

		String source = "" +
			"set a = true && 2 + 2 .. \"4\";" +
			"unit.assertEquals (\"true4\", a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testBoolean1() throws Exception {

		String source = "" +
			"set a = true && true;" +
			"unit.assertEquals(true, a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testBoolean2() throws Exception {

		String source = "" +
			"set a = true && false;" +
			"unit.assertFalse(a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void testBooleanTranstyping1() throws Exception {

		String source = "" +
			"set a = \"true\" || false;" +
			"unit.assertTrue(a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void testBooleanArith1() throws Exception {

		String source = "" +
			"set a = 1 == 1 && 2 == 2;" +
			"set b = 1 == 1 && 2 == 3;" +
			"unit.assertTrue(a);" +
			"unit.assertFalse(b);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void testBooleanArith2() throws Exception {

		String source = "" +
			"set b = \"b\";\n" +
			"set a = \"a\" == b;" +
			"unit.assertFalse(b);" +
			"unit.assertFalse(a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void testBooleanArith3() throws Exception {

		String source = "" +
			"set a = true == 1;" + "\n" +
			"set b = false == 0;" + "\n" +
			"set c = true == \"a\";" + "\n" +
			"set d = false == \"false\";" + "\n" +
			"" +
			"unit.assertTrue(a);" +
			"unit.assertTrue(b);" +
			"unit.assertFalse(c);" +
			"unit.assertTrue(d);" +
			"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);


	}

	@Test
	public void testBooleanArith4() throws Exception {

		String source = "" +
			"set b = (true == true || false != true);" +
			"set c = !(true == true || false != true);" +
			"set d = !b;" +
			"unit.assertEquals(c, d);" +
			"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
