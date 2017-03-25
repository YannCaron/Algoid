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
 <p>
 @author CARONYN
 */
public class ArithmeticTest {

	@Test
	public void justExpressionTest1() {

		String source = "unit.assertEquals (4, 2 + 2);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void justExpressionTest2() {

		String source = "unit.assertEquals (7, 2 + 2 + 3);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void expressionTest1() {

		String source = "unit.assertEquals (-57, -5 + 3 + 4 + -7 - (4 + 15) * 3 + 5);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void expressionTest2() {

		String source = "unit.assertEquals (11, 3 * (7 / 3) + 4;)";
	}

	@Test
	public void expressionTest3() {

		String source = ""
			+ "set b;"
			+ "unit.assertEquals (15, 3 * (b = 5));";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void assignTest1() {

		String source = ""
			+ "set a = 7;"
			+ "a = a * 3;"
			+ "unit.assertEquals (21, a);";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void assignTest2() {

		String source = ""
			+ "set a = 7;"
			+ "a += 3;"
			+ "unit.assertEquals(10, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void assignTest3() {

		String source = ""
			+ "set a = 7;"
			+ "a -= 3;"
			+ "unit.assertEquals(4, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void assignTest4() {

		String source = ""
			+ "set a = 8;"
			+ "a /= 2;"
			+ "unit.assertEquals(4, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void assignTest5() {

		String source = ""
			+ "set a = 7;"
			+ "a %= 2;"
			+ "unit.assertEquals (1, a);";

	}

	@Test
	public void assignStringTest() {

		String source = ""
			+ "set a = \"hello\";"
			+ "a ..= \" world\";"
			+ "unit.assertEquals (\"hello world\", a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void plusplusTest() {

		String source = ""
			+ "set a = 0;"
			+ "a++;"
			+ "a = 7 + a;"
			+ "unit.assertEquals (8, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void mistTest() {

		String source = ""
			+ "unit.assertEquals (8, 7++);"
			+ "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void moinsmoinsTest() {

		String source = ""
			+ "set a = 0;"
			+ "a--;"
			+ "a = 8 + a;"
			+ "unit.assertEquals (7, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void callTest() {

		String source = ""
			+ "unit.assertEquals(1, 1);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void parenthesis1Test() {

		String source = ""
			+ "set a = 5 * (4 + 1);"
			+ "unit.assertEquals(25, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void parenthesis2Test() {

		String source = ""
			+ "set a = -(4 + 1);"
			+ "unit.assertEquals(-5, a);"
			+ "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void NaNTest() {

		String source = ""
			+ "set a = 0 / 0;"
			+ "unit.assertEquals (nan, a);"
			+ "unit.assertTrue (a.isNan());";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void InfinityTest1() {

		String source = ""
			+ "set a = 1 / 0;"
			+ "unit.assertEquals (infinity, a);"
			+ "unit.assertTrue (a.isInfinite());";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
	
	
	@Test
	public void InfinityTest2() {

		String source = ""
			+ "set a = -1 / 0;"
			+ "unit.assertEquals (-infinity, a);"
			+ "unit.assertTrue (a.isInfinite());";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
