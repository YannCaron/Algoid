/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import org.junit.Test;

/**
 *
 * @author CARONYN
 */
public class FunctionConcatenationTest {

	@Test
	public void FunctionConcatenation1Test() throws Exception {
		String source = "" +
			"set a = 0;" +
			"" +
			"set f = function () {" +
			"	a += 1;" +
			"};" +
			"" +
			"set g = function () {" +
			"	a += 1;" +
			"};" +
			"" +
			"set h = f -> g;" +
			"h ();" +
			"unit.assertEquals(2, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void FunctionConcatenation2Test() throws Exception {
		String source = "" +
			"set a = 0;" +
			"set f = function (p) {" +
			"	a+= p;" +
			"};" +
			"" +
			"set g = function (p) {" +
			"	a += p;" +
			"};" +
			"" +
			"set h = f -> g;" +
			"h (5);" +
			"unit.assertEquals (10, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void FunctionConcatenation3Test() throws Exception {
		String source = "" +
			"set a = 0;" +
			"set f = function (p) {" +
			"	a+= p;" +
			"};" +
			"" +
			"set g = function (q) {" +
			"	a += q;" +
			"};" +
			"" +
			"set h = f -> g;" +
			"h (5, 2);" +
			"unit.assertEquals (7, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Aspect1Test() throws Exception {
		String source = "" +
			"set a = \"\";" +
			"set o = object () {" +
			"	set f = function (p) {" +
			"		a ..= \"during \" .. p;" +
			"	};" +
			"};" +
			"" +
			"o.f = function() {" +
			"	a ..= \"before \" .. p .. \" \";" +
			"} -> o.f -> function (p) {" +
			"	a ..= \" after \" .. p;" +
			"};" +
			"" +
			"o.f(7);" +
			"print (a);" +
			"unit.assertEquals (\"before 7 during 7 after 7\", a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Aspect2Test() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set a = \"\";" +
			"	set f = function (p) {" +
			"		a ..= \"during \" .. p;" +
			"	};" +
			"};" +
			"" +
			"set o.f = function() {" +
			"	a ..= \"before \" .. p .. \" \";" +
			"} -> o.f -> function (p) {" +
			"	a ..= \" after \" .. p;" +
			"};" +
			"set p = new o;" +
			"p.f(7);" +
			"set b = p.a;" +
			"print (b);" +
			"unit.assertEquals (\"before 7 during 7 after 7\", b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void FunctionConcatenationAssignationTest() throws Exception {
		String source = "" +
			"set a = 5;" +
			"set f = function () {" +
			"	a+=1;" +
			"};" +
			"" +
			"f ->= function () {" +
			"	a+=1;" +
			"};" +
			"" +
			"f ();" +
			"unit.assertEquals (7, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ObjectFunctionConcatenationTest1() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set a = 5;" +
			"	set f = function () {" +
			"		a += 2;" +
			"	};" +
			"};" +
			"" +
			"set o.g = function() {" +
			"	a += 1;" +
			"};" +
			"" +
			"set o.f = o.g -> o.f;" +
			"o.f();" +
			"set a = o.a;" +
			"" +
			"set p = new o;" +
			"p.f();" +
			"set b = p.a;" +
			"" +
			"unit.assertEquals (8, a);" +
			"unit.assertEquals (8, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ObjectFunctionConcatenationTest2() throws Exception {
		String source = "" +
			"" +
			"set o = object () {" +
			"	set a = \"\";" +
			"	set f = function () {" +
			"		a ..= \"function\";" +
			"	};" +
			"	set g = function () {};" +
			"};" +
			"" +
			"set before = function () {" +
			"	a ..= \"before \";" +
			"};" +
			"set after = function () {" +
			"	a ..= \" after\";" +
			"};" +
			"" +
			"set o.f = before -> o.f -> after;" +
			"set o.g = before -> o.g -> after;" +
			"set p = new o;" +
			"" +
			"p.f();" +
			"set a = p.a;" +
			"unit.assertEquals (\"before function after\", a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
