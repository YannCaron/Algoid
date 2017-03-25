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
 * @author CARONYN
 */
public class FunctionalTest {

	@Test
	public void functionalTest1() throws Exception {
		String source = "" +
				"set add = function (a, b) {" +
				"	return a + b;" +
				"};" +
				"set sub = function (a, b) {" +
				"	return a - b;" +
				"};" +
				"" +
				"print (\"add \" .. add (4, 3));" +
				"print (\"sub \" .. sub (4, 3));" +
				"" +
				"unit.assertEquals (7, add(4, 3));" +
				"unit.assertEquals (1, sub(4, 3));" +
				"" +
				"set op = add;" +
				"unit.assertEquals (7, op(4, 3));" +
				"" +
				"op = sub;" +
				"unit.assertEquals (1, op(4, 3));" +
				"" +
				"unit.assertEquals (7, add(4, 3));" +
				"unit.assertEquals (1, sub(4, 3));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionalTest2() throws Exception {
		String source
				= "set d = 0;" +
				"set test = function (f, a, b) {" +
				"	print (\"result of \" .. f .. \": \" .. f (a, b));" +
				"	d = f (a, b);" + '\n' +
				"};" + '\n' +
				"" + '\n' +
				"set add = function (x, y) {" +
				"	return x + y;" + '\n' +
				"};" + '\n' +
				"set minus = function (x, y) {" +
				"	return x - y;" + '\n' +
				"};" + '\n' +
				"" + '\n' +
				"test (add, 7, 8);" +
				"" +
				"unit.assertEquals (15, d);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	
	@Test
	public void TODO_functionalTest() throws Exception {
		String source
				= ""
		  + "set res = 0;"
		  + "set f = function (g) {"
		  + "	return g;"
		  + "};"
		  + ""
		  + "set g = f(function () {res = 7});"
		  + "g();"
		  + "unit.assertEquals(7, res);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
