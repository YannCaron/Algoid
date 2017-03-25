/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 <p>
 @author CARONYN
 */
public class FunctionTest {

	@Test
	public void anonymousFunction1Test() {
		String source = ""
		  + "set a = function() {"
		  + "	return 7;"
		  + "};"
		  + ""
		  + "unit.assertEquals(8, a() + 1);"
		  + "set b = a;"
		  + //"unit.assertEquals(7, b());" +
		  "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void anonymousFunction2Test() {
		String source = ""
		  + ""
		  + "unit.assertEquals (7, "
		  + "	function (a) {"
		  + "		return a;"
		  + "	}(7)"
		  + ");"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void anonymousFunction3Test() {
		String source = ""
		  + ""
		  + "unit.assertEquals (8, "
		  + "	function (a) {"
		  + "		return function () {"
		  + "			return a + 1;"
		  + "		};"
		  + "	}(7)()"
		  + ");"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void functionTest() throws Exception {
		String source = ""
		  + "set r; set s; set t;"
		  + "set test = function (a, b, c) {"
		  + "	r = a; s = b; t = c;"
		  + "	print (a);" + '\n'
		  + "	print (b);" + '\n'
		  + "	print (c);" + '\n'
		  + "};" + '\n'
		  + "test (1, 2, 4);"
		  + "unit.assertEquals (1, r);"
		  + "unit.assertEquals (2, s);"
		  + "unit.assertEquals (4, t);"
		  + "";
		//+ "test();" + '\n';

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionErrorTest() throws Exception {
		String source = ""
		  + "set r;"
		  + "set test = function (a, b, c) {" + '\n'
		  + "	print (a);" + '\n'
		  + "	print (b);" + '\n'
		  + "	print (c);"
		  + "	r = c;" + '\n'
		  + "};" + '\n'
		  + "test (1, 2);"
		  + ""
		  + "unit.assertEquals (nil, r);"
		  + "";
		//+ "test();" + '\n';

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void functionReturnTest() throws Exception {
		String source = ""
		  + "set sum = function (a, b, c) {" + '\n'
		  + "	return a + b + c;" + '\n'
		  + "};"
		  + "set r = \"result : \" .. sum (1, 2, 4);" + '\n'
		  + "print(r);"
		  + ""
		  + "unit.assertEquals (\"result : 7\", r);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionReturnTest2() throws Exception {
		String source = ""
		  + "set sum = function (a, b, c) {" + '\n'
		  + "	return a + b + c;" + '\n'
		  + "};"
		  + "set r = sum(1, 2, 4);"
		  + "print(r);"
		  + ""
		  + "unit.assertEquals (7, r);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionDefaultParameterTest() throws Exception {
		String source = ""
		  + "set r = 0;"
		  + "set test = function (a, b = 7) {"
		  + "	r = b;"
		  + "	print (b);"
		  + "};"
		  + "test (1);"
		  + "unit.assertEquals (7, r);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
//TODO

	@Test
	public void TODO_functionAttrScopeTest() throws Exception {
		String source = ""
		  + "set a = 0;"
		  + ""
		  + "set double = function (f) {"
		  + "	f ();"
		  + "	f ();"
		  + "};"
		  + ""
		  + "set doubleInc = function () {"
		  + "	double (increment);"
		  + "};"
		  + ""
		  + "set increment = function () {"
		  + "	a = a + 1;"
		  + "};"
		  + ""
		  + "double (doubleInc);"
		  + "unit.assertEquals (4, a);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void embededFunctionTest() {
		String source = ""
		  + "set f = function () {"
		  + "	return function () {"
		  + "		return 8;"
		  + "	};"
		  + "};"
		  + "unit.assertEquals(8, f()());"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void NOT_YET_listOfFunctionTest() {
		String source = ""
		  + "set f = function () {"
		  + "	return function () {"
		  + "		return 8;"
		  + "	};"
		  + "};"
		  + "unit.assertEquals(8, f.f.f.f.f.f.f()());"
		  + "";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);;
			fail("No exception ?");
		} catch (ALRuntimeException ex) {
		}
	}

	@Test
	public void listOfObjectTest() {
		String source = ""
		  + "set o = object () {"
		  + "	set a = 8;"
		  + "};"
		  + "unit.assertEquals(8, o.o.o.o.a);"
		  + "";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);;
			fail("No exception ?");
		} catch (ALRuntimeException ex) {
		}
	}

	@Test
	public void invalidMethodTest() {
		String source = ""
		  + "set o = algo.doesNotExists.clone(array {1, 2, 3, 4}, 4);"
		  + "";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);;
			fail("No exception ?");
		} catch (ALRuntimeException ex) {
		}
	}

	@Test
	public void functionAndObject() {
		String source = ""
		  + "set f = function () {"
		  + "	return object () {"
		  + "		set g = function () {"
		  + "			return 8;"
		  + "		};"
		  + "	};"
		  + "	"
		  + "};"
		  + "f().g();"
		  + ""
		  + //"unit.assertEquals(8, f().g());" +
		  "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void functionStringObject() {
		String source = ""
		  + "set f = function () {"
		  + "};"
		  + "print (f);"
		  + "print (f);"
		  + ""
		  + //"unit.assertEquals(8, f().g());" +
		  "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void arrayFunctionStringObject() {
		String source = ""
		  + "set a = array {"
		  + "	function () {"
		  + "		print (\"hi\");"
		  + "	},"
		  + "	function () {"
		  + "		print (\"I\");"
		  + "	}"
		  + "};"
		  + ""
		  + "set i=0;"
		  + "while (i < a.length()) {"
		  + "	a[i]();"
		  + "	i++;"
		  + "}"
		  + ""
		  + //"unit.assertEquals(8, f().g());" +
		  "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void returnTest() {
		String source = ""
		  + "set add = function (a, b) {"
		  + "	return a + b;"
		  + "};"
		  + "set sub = function (a, b) {"
		  + "	return a - b;"
		  + "};"
		  + ""
		  + "unit.assertEquals (-1, sub (7, 8));"
		  + "unit.assertEquals (3, add (1, 2));"
		  + "unit.assertEquals (4, sub (3, -1));"
		  + "print (\"Result is \" .. sub (add (1, 2), sub (7, 8)));"
		  + "unit.assertEquals (4, sub (add (1, 2), sub (7, 8)));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	// ISSUE #3 function scope to be cloned
	@Test
	public void storeFunction() {
		String source = ""
		  + "set multWith = function (a) {"
		  + "	return function (b) {"
		  + "		return a * b;"
		  + "	}"
		  + "}"
		  + ""
		  + "set multWith2 = multWith(2)"
		  + "set multWith4 = multWith(4)"
		  + "set multWith8 = multWith(8)"
		  + ""
		  + "print (multWith2);"
		  + "print (multWith4);"
		  + "print (multWith8);"
		  + ""
		  + "print (\"Result \" .. multWith2 (2))"
		  + "print (\"Result \" .. multWith4 (2))"
		  + "print (\"Result \" .. multWith8 (2))"
		  + "unit.assertEquals(4, multWith2 (2));"
		  + "unit.assertEquals(8, multWith4 (2));"
		  + "unit.assertEquals(16, multWith8 (2));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

}
