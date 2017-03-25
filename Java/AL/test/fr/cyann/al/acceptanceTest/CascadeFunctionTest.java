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
public class CascadeFunctionTest {

	@Test
	public void typeTest() throws Exception {
		String source = "" +
			"unit.assertEquals(al.types.FUNCTION, function() {}.getType());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
			"set a = function () {};" + "\n" +
			"set b = a;" + "\n" +
			"set c = function () {};" + "\n" +
			"set d = a.clone();" + "\n" +
			"unit.assertTrue(a.equals(b));" + "\n" +
			"unit.assertFalse(a.equals(c));" + "\n" +
			"unit.assertFalse(b.equals(c));" + "\n" +
			"unit.assertTrue(d.equals(a));" + "\n" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cloneTest() throws Exception {
		String source = "" +
			"set v = 0;" +
			"set f = function (a) {" +
			"	v = a;" +
			"};" +
			"" +
			"f.clone()(10);" +
			"" +
			"unit.assertEquals(10, v);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add1Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"set n = f;" +
			"n.add(1);" +
			"" +
			"unit.assertEquals (array {f, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add2Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"set n = f;" +
			"n.add(1, 3);" +
			"" +
			"unit.assertEquals (array {f, nil, nil, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll1Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"set n = f;" +
			"n.addAll(array {1, 2});" +
			"" +
			"unit.assertEquals (array {f, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll2Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"set n = f;" +
			"n.addAll(array {1, 2}, 3);" +
			"" +
			"unit.assertEquals (array {f, nil, nil, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cascade1Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"" +
			"" +
			"unit.assertEquals (true, f.is(al.types.FUNCTION).is(al.types.BOOL));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cascade2Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"" +
			"" +
			"unit.assertEquals (true, f.isFunction.is(al.types.FUNCTION));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cascade3Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"" +
			"" +
			"unit.assertEquals (true, f.is(al.types.FUNCTION).is(al.types.OBJECT).is(al.types.BOOL));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decorateTest1() throws Exception {
		String source = "" +
			"set result = 0;" +
			"set f = function (a, b) {" +
			"	result += a + b;" +
			"};" +
			"" +
			"set g = f.decorate(function (a, b, fun) {" +
			"	result *= 10;" +
			"	fun (a, b);" +
			"	result /= 5;" +
			"});" +
			"" +
			"f(7, 10);" +
			"unit.assertEquals (17, result);" +
			"result = 1;" +
			"g(7, 10);" +
			"unit.assertEquals (5.4, result);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void aopTest() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set log = \"\";" +
			"	set f = function (p) {" +
			"		log ..= p;" +
			"	};" +
			"	set g = function (p) {" +
			"		log ..= \"another\";" +
			"	};" +
			"};" +
			"" +
			"set deco = function (p, f) {" +
			"	log = \"before \";" +
			"	f (p);" +
			"	log ..= \" after\";" +
			"};" +
			"o.f = o.f.decorate(deco);" +
			"o.g = o.g.decorate(deco);" +
			"" +
			"o.f(\"during\");" +
			"unit.assertEquals (\"before during after\", o.log);" +
			"o.g(\"useless\");" +
			"unit.assertEquals (\"before another after\", o.log);" +
			"o.f(\"other\");" +
			"unit.assertEquals (\"before other after\", o.log);" +
			"o.g(\"other\");" +
			"unit.assertEquals (\"before another after\", o.log);" +
			"" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decorateTest2() throws Exception {
		String source = "" +
			"set a = 0;" +
			"" +
			"set f = function (a, b) {" +
			"	return a + b;" +
			"};" +
			"" +
			"set g = function (f) {" +
			"	a = f();" +
			"};" +
			"" +
			"f.setParameter(\"a\", 7).setParameter(\"b\", 8);" +
			"" +
			"g(f);" +
			"" +
			"unit.assertEquals(15, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decorateTest3() throws Exception {
		String source = "" +
			"set a = 0;" +
			"" +
			"set f = function (a, b) {" +
			"	return a + b;" +
			"};" +
			"" +
			"set g = function (f) {" +
			"	a = f();" +
			"};" +
			"" +
			"f.setParameters(array {7, 8});" +
			"" +
			"g(f);" +
			"" +
			"unit.assertEquals(15, a);" +
			"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void decorateTest4() throws Exception {
		String source = "" +
			"set a = 0;" +
			"" +
			"function (p){" +
			"	a = p;" +
			"}.setParameter(\"p\", 7)();" +
			"" +
			"unit.assertEquals(7, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void concatTest() throws Exception {
		String source = "" +
			"set n = 0;" +
			"" +
			"set f = function (a) {" +
			"	n += a;" +
			"};" +
			"" +
			"set g = function (b) {" +
			"	n *= b;" +
			"};" +
			"" +
			"f = f.concat(g);" +
			"f(4, 2);" +
			"" +
			"unit.assertEquals (8, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void setParametersTest() throws Exception {
		String source = "" +
			"set result = 0;" +
			"" +
			"set deco = function (f) {" +
			"	result = f ();" +
			"};" +
			"" +
			"set f = function (a, b) {" +
			"	return a + b;" +
			"};" +
			"" +
			"deco (f.setParameters (array {7, 8}));" +
			"" +
			"unit.assertEquals (15, result);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void setParameterTest() throws Exception {
		String source = "" +
			"set result = 0;" +
			"set f = function () {" +
			"	result = a + b;" +
			"};" +
			"" +
			"f.setParameter (\"a\", 7);" +
			"f.setParameter (\"b\", 8);" +
			"" +
			"f();" +
			"" +
			"unit.assertEquals (15, result);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void parameterExistsTest() throws Exception {
		String source = "" +
			"set f = function (a, b) {};" +
			"" +
			"unit.assertTrue(f.parameterExists(\"a\"));" +
			"unit.assertFalse(f.parameterExists(\"c\"));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void getParametersNames() throws Exception {
		String source = "" +
			"set f = function (a, b) {};" +
			"" +
			"unit.assertEquals(array {\"a\", \"b\"}, f.getParametersNames());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void removeParameterTest() throws Exception {
		String source = "" +
			"set f = function (a, b, c) {};" +
			"" +
			"f.removeParameter(\"b\");" +
			"" +
			"unit.assertEquals(array {\"a\", \"c\"}, f.getParametersNames());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
