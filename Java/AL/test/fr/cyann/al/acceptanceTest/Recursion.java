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
 * <p>
 * @author caronyn
 */
public class Recursion {

	@Test
	public void testRecursive1() throws Exception {

		int n = 10;

		String source = "" +
				"set fibo = function (n) {" +
				"	if (n == 0) return 0;" +
				"	else if (n == 1) return 1;" +
				"	else {" +
				"		return fibo (n - 1) + fibo (n - 2);" +
				"	}" +
				"};" +
				"" +
				"set res = fibo(" + n + ");" +
				"unit.assertEquals(" + fibo(n) + ", res);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testRecursive2() throws Exception {

		String source = "" +
				"set res = 0;" +
				"set f = function (a) {" +
				"   res += a;" +
				"   if (a < 4) " +
				"     f(a + 1);" +
				"};" +
				"" +
				"f(0);" +
				"unit.assertEquals(10, res);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void recursiveTest1() throws Exception {
		String source = "" +
				"set s = \"\";" +
				"" +
				"set f = function (i) {" +
				"	s ..= \"recursive \" .. i .. \", \";" +
				"	if (i < 5) {" +
				"		f (i+1);" +
				"	} else {" +
				"		s ..= \"recursive end\";" +
				"	}" +
				"};" +
				"" +
				"f(0);" +
				"print (s);" +
				"unit.assertEquals(\"recursive 0, recursive 1, recursive 2, recursive 3, recursive 4, recursive 5, recursive end\", s);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void factorialTest() throws Exception {
		String source = "" +
				"set fact = function (n) {" +
				"	if (n == 0) return 1;" +
				"	return n * fact (n - 1);" +
				"};" +
				"" +
				"set r = fact (10);" +
				"" +
				"unit.assertEquals(3628800, r);" +
				"" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void miscTest() throws Exception {
		String source = "" +
				"" +
				"set f = function (n) {" +
				"	if (n == 0) {" +
				"		return 1;" +
				"	} else {" +
				"		print (n);" +
				"		return n * f(n - 1);" +
				"	}" +
				"};" +
				"" +
				"print (\"result \" .. f (10));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	int f(int n) {
		if (n == 0) {
			return 1;
		} else {
			System.out.println("run with " + n);
			return f(n - 1) * n;
		}
	}

	@Test
	public void miscJava1Test() throws Exception {
		System.out.println(f(10));
	}

	int fibo(int n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			return fibo(n - 1) + fibo(n - 2);
		}
	}

	@Test
	public void miscJava2Test() throws Exception {
		System.out.println(fibo(10));
	}

	@Test
	public void kochFractalTest() throws Exception {
		String source = "" +
				"set res = 0;" +
				"" +
				"set kochFractal = function (it) {" +
				"	if (it == 3) res++;" +
				"	if (it > 0) {" +
				"		it--;" +
				"		kochFractal (it);" +
				"		kochFractal (it);" +
				"		kochFractal (it);" +
				"	}" +
				"};" +
				"" +
				"for (set a; a<3; a++) {" +
				"	print (\"CALL\");" +
				"	kochFractal (3);" +
				"}" +
				"" +
				"print (\"Iteration : \" .. res);" +
				"" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void tailRecursionTest() throws Exception {
		String source = "" +
				"set exp = function (v) {" +
				"	print (\"V = \" .. v);" +
				"	if (v <= 1) {" +
				"		print (\"END !!!!!!!!!!!!!!!!!!!!!!!!!!!\")" +
				"		return 0;" +
				"	}" +
				"	return exp (v - 1);" +
				"};" +
				"" +
				"set str = \"EXP = \" .. exp(5000);" +
				"" +
				"print (str)" +
				"print (\"yeah ! it'works\");" +
				"" +
				"unit.assertEquals(\"EXP = 0\", str);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void tailMultiRecursionTest() throws Exception {
		String source = "" +
				"set exp = function (v) {" +
				"	print (\"uncount \" .. v);" +
				"	if (v <= 1) {" +
				"		set x = exp2(v + 1);" +
				"		return x;" +
				"	}" +
				"	return exp (v - 1);" +
				"};" +
				"" +
				"set exp2 = function (w) {" +
				"	print (\"uncount \" .. w);" +
				"	if (w >= 100) return w;" +
				"	return exp2(w * 2);" +
				"}" +
				"" +
				"print (\"EXP = \" .. exp(5000));" +
				"print (\"yeah ! it'works\");" +
				"print (\"after block\");" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void tailRecursion2Test() throws Exception {
		String source = "" +
				"set g = function (v) {" +
				"	print (\"g(\" .. v .. \")\");" +
				"	if (v >= 10) return v; else return f (v + 2);" +
				"};" +
				"" +
				"set f = function (v) {" +
				"	print (\"f(\" .. v .. \")\");" +
				"	if (v >= 10) return v; else return f(g (v + 1));" +
				"};" +
				"" +
				"f (1);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void tailRecursion3Test() throws Exception {
		String source = "" +
				"set f = function (v) {" +
				"	if (v > 1) {" +
				"		return f (v-1);" +
				"	}" +
				"}" +
				"" +
				"f(5000);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
