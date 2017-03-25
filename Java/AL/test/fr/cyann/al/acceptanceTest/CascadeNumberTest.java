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
 * The EvalTest class.<br> creation date : 8 mai 2012.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class CascadeNumberTest {

	@Test
	public void type1Test() throws Exception {
		String source = "" +
			"unit.assertEquals(al.types.NUMBER, (7.8).getType());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void type2Test() throws Exception {
		String source = "" +
			"set a = 0;" +
			"" +
			"unit.assertTrue((2.7).is(al.types.NUMBER));" + "\n" +
			"unit.assertFalse((2.7).isInteger());" + "\n" +
			"unit.assertTrue((2.7).isReal());" + "\n" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
			"unit.assertTrue((7).equals(7));" +
			"unit.assertFalse((7).equals(8));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add1Test() throws Exception {
		String source = "" +
			"set n = 7;" +
			"n.add(1);" +
			"" +
			"unit.assertEquals (array {7, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add2Test() throws Exception {
		String source = "" +
			"set n = 7;" +
			"n.add(1, 3);" +
			"" +
			"unit.assertEquals (array {7, nil, nil, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll1Test() throws Exception {
		String source = "" +
			"set n = 7;" +
			"n.addAll(array {1, 2});" +
			"" +
			"unit.assertEquals (array {7, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll2Test() throws Exception {
		String source = "" +
			"set n = 7;" +
			"n.addAll(array {1, 2}, 3);" +
			"" +
			"unit.assertEquals (array {7, nil, nil, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void minusTest() throws Exception {
		String source = "" +
			"set n = (7).minus();" +
			"" +
			"unit.assertEquals (-7, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void increment1Test() throws Exception {
		String source = "" +
			"set n = (7).increment();" +
			"" +
			"unit.assertEquals (8, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void increment2Test() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set n = 7;" +
			"}" +
			"unit.assertEquals (9, o.n.increment(2));" +
			"unit.assertEquals (9, o.n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decrement1Test() throws Exception {
		String source = "" +
			"set n = (8).decrement();" +
			"" +
			"unit.assertEquals (7, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decrement2Test() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set n = 9;" +
			"}" +
			"unit.assertEquals (7, o.n.decrement(2));" +
			"unit.assertEquals (7, o.n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void additionTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"n = n.addition(8);" +
			"" +
			"unit.assertEquals (15, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void substractTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"n = n.substract(8);" +
			"" +
			"unit.assertEquals (-1, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void multiplyTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"n = n.multiply(8);" +
			"" +
			"unit.assertEquals (7 * 8, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void divideTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"n = n.divide(8);" +
			"" +
			"unit.assertEquals (7 / 8, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void greatedThanTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"unit.assertTrue (n.greaterThan(5));" +
			"unit.assertFalse (n.greaterThan(7));" +
			"unit.assertFalse (n.greaterThan(8));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void smallerThanTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"unit.assertFalse (n.smallerThan(5));" +
			"unit.assertFalse (n.greaterThan(7));" +
			"unit.assertTrue (n.smallerThan(8));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void greaterThanOrEqualsTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"unit.assertTrue (n.greaterOrEquals(5));" +
			"unit.assertTrue (n.greaterOrEquals(7));" +
			"unit.assertFalse (n.greaterOrEquals(8));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void smallerThanOrEqualsTest() throws Exception {
		String source = "" +
			"set n = 7;" +
			"" +
			"unit.assertFalse (n.smallerOrEquals(5));" +
			"unit.assertTrue (n.greaterOrEquals(7));" +
			"unit.assertTrue (n.smallerOrEquals(8));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void betweenTest() throws Exception {
		String source = "" +
			"unit.assertTrue ((7).between(1, 10));" +
			"unit.assertFalse ((11).between(1, 10));" +
			"unit.assertTrue ((1).between(1, 10));" +
			"unit.assertTrue ((10).between(1, 10));" +
			"unit.assertFalse ((0).between(1, 10));" +
			"unit.assertFalse ((7+7).between(1, 10));" +
			"unit.assertFalse ((-1).between(1, 10));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopForTest() throws Exception {
		String source = "" +
			"set n = 0;" +
			"" +
			"(7).loopFor (function () {" +
			"	n++;" +
			"});" +
			"" +
			"unit.assertEquals (7, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopFor1Test() throws Exception {
		String source = "" +
			"set n = 0;" +
			"" +
			"(8).loopFor (function (i) {" +
			"	n += i;" +
			"});" +
			"" +
			"unit.assertEquals (28, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopFor2Test() throws Exception {
		String source = "" +
			"set n = 0;" +
			"set a = array {};" +
			"" +
			"(8).loopFor (function (i) {" +
			"	n++;" +
			"	a.add(i);" +
			"}, 0, 2);" +
			"print (a);" +
			"unit.assertEquals (4, n);" +
			"unit.assertEquals (array {0, 2, 4, 6}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopFor3Test() throws Exception {
		String source = "" +
			"set n = 0;" +
			"set a = array {};" +
			"" +
			"(8).loopFor (function (i) {" +
			"	n++;" +
			"	a.add(i);" +
			"}, 0, -1);" +
			"" +
			"unit.assertEquals (8, n);" +
			"unit.assertEquals (array {8, 7, 6, 5, 4, 3, 2, 1}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopFor4Test() throws Exception {
		String source = "" +
			"set n = 0;" +
			"set a = array {};" +
			"" +
			"(-8).loopFor (function (i) {" +
			"	n++;" +
			"	a.add(i);" +
			"}, 0, -2);" +
			"" +
			"unit.assertEquals (4, n);" +
			"unit.assertEquals (array {0, -2, -4, -6}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void loopFor5Test() throws Exception {
		String source = "" +
			"set executor = function (f) {" +
			"	f();" +
			"};" +
			"" +
			"set f = function () {" +
			"	set j = 0;" +
			"	(2).loopFor (function (i) {" +
			"		j += i;" +
			"	});" +
			"};" +
			"" +
			"executor(f);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void limitTest() throws Exception {
		String source = "" +
			"set a = 7;"
		  + ""
		  + "unit.assertEquals (7, a);"
		  + "unit.assertEquals (5, a.limit(0, 5));"
		  + "unit.assertEquals (5, a.limit(5, 0));"
		  + "unit.assertEquals (8, a.limit(8, 10));"
		  + "unit.assertEquals (8, a.limit(10, 8));"
		  + "unit.assertEquals (7, a.limit(0, 10));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

}
