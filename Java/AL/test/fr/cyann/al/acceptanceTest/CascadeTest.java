/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.utils.UnitTesting;
import org.junit.Test;

/**
 The EvalTest class.<br> creation date : 8 mai 2012.
 <p>
 @author Yann Caron
 @version v0.1
 */
public class CascadeTest {

	@Test
	public void Functional1Test() throws Exception {
		String source = ""
		  + "set a;"
		  + "set f = function (fun) {"
		  + "	a = fun(\" \");"
		  + "};"
		  + "f (\"a b c\".split);"
		  + "unit.assertEquals(array {\"a\", \"b\", \"c\"}, a);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Parenthesis1Test() throws Exception {
		String source = ""
		  + "set b = (false && false).is(al.types.BOOL);" + "\n"
		  + "unit.assertTrue(b);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Parenthesis2Test() throws Exception {
		String source = ""
		  + "set b;"
		  + "set b = (!true).is(al.types.BOOL);" + "\n"
		  + "unit.assertTrue(b);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ArrayAllObjectTest() throws Exception {
		String source = ""
		  + "set a = array {0, 1, 2, 3, 4};"
		  + "print (a.length());"
		  + "unit.assertEquals (5, a.length());"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ArrayAllObjectExceptionTest() throws Exception {
		final String source = ""
		  + "set a = array {0, 1, 2, 3, 4};"
		  + "a.length().length();"
		  + "";

		UnitTesting.assertException(new Runnable() {
			@Override
			public void run() {
				AL.execute(new Syntax(), new UnitTestRuntime(), source);
			}
		});
	}

	@Test
	public void String1Test() throws Exception {
		String source = ""
		  + "unit.assertEquals(4, \"test\".length());"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void String2Test() throws Exception {
		String source = ""
		  + "unit.assertEquals(4, (\"test\").length());"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Number1Test() throws Exception {
		String source = ""
		  + "print(\"addition 4 + 5 = \" .. (4).addition(5));"
		  + "unit.assertEquals(9, (4).addition(5));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void CascadeTest() throws Exception {
		String source = ""
		  + "unit.assertEquals(7, (\"test\").length().addition(3));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Boolean1Test() throws Exception {
		String source = ""
		  + "print(\"true and false = \" .. true.and(false));"
		  + "unit.assertEquals(false, true.and(false));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ArrayTest() throws Exception {
		String source = ""
		  + "unit.assertEquals(4, array {1, 2, 3, 4}.length());"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void BlockTest() throws Exception {
		String source = ""
		  + "{"
		  + "	array {}.clone();"
		  + "	print (\"hi I am algoid!\");"
		  + "}"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void DocTest() throws Exception {
		String source = ""
		  + ""
		  + "set list2 = array {};"
		  + "set list = array {\"Rod\", \"Carlos\", \"Chris\"};"
		  + "list.filter (function (item) {"
		  + "	return item.length () <= 4;"
		  + "}).each (list2.add);"
		  + ""
		  + "unit.assertEquals(array {\"Rod\"}, list2);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void ObjectObjectTest() throws Exception {
		String source = ""
		  + "set o = object() {"
		  + "	set f = function () {"
		  + "		print (this.is(al.types.OBJECT));"
		  + "	};"
		  + "};"
		  + ""
		  + "o.f();";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void scope1Test() throws Exception {
		String source = ""
		  + ""
		  + "set a = 0;"
		  + "set f = function () {"
		  + "	a++;"
		  + "};"
		  + "debug.printScope();"
		  + "(!false).ifTrue(f);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void scope2Test() throws Exception {
		String source = ""
		  + "set cell = object () {"
		  + "	set a;"
		  + "	set clone = function (grid, p) {"
		  + "		set o = new this;"
		  + "		o.a.add(grid [p]);"
		  + "	};"
		  + "};"
		  + "set a = array {};"
		  + "cell.clone(array {1, 2, 3, 4, 5}, 1);"
		  + "";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void scope3Test() throws Exception {
		String source = ""
		  + "set o = object () {"
		  + "	set a;"
		  + "	set f = function (grid, p) {"
		  + "		set u = p;"
		  + "		a.add (grid[u]);"
		  + "	};"
		  + "};"
		  + ""
		  + "o.f(array {1, 2, 3, 4}, 1);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void scope4Test() throws Exception {
		String source = ""
		  + "set n = 0;"
		  + "set a = array {};"
		  + ""
		  + "(-8).loopFor (function (i) {"
		  + "	n++;"
		  + "	a.add(i);"
		  + "}, 0, -2);"
		  + ""
		  + "unit.assertEquals (4, n);"
		  + "unit.assertEquals (array {0, -2, -4, -6}, a);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void scope5Test() {
		String source = ""
		  + "set o = object() {"
		  + "	set a = 0;"
		  + "	set f = function (a) {"
		  + "		a = a;"
		  + "	};"
		  + "};"
		  + "set a = array {new o, new o};"
		  + ""
		  + "for (set i = 0; i<a.length(); i++) {"
		  + "	set j = 7;"
		  + "	debug.printScope();"
		  + "	a[i].f(j);"
		  + "	print (a[i].a);"
		  + "	unit.assertEquals(7, a[i].a);"
		  + "}"
		  + ""
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void NOT_YET_stackTest() throws Exception {
		/*
		 TODO : If test failed, it is normal
		 Al tente de résoudre le variant avant d'essayer les magic methods
		 Problème de concepte
		 */
		String source = ""
		  + "set a = array {1, 2, 3, 4};"
		  + "set remove = function (p, i) {"
		  + "	p.remove(i);"
		  + "};"
		  + "remove(a, 2);"
		  + ""
		  + "unit.assertEquals (array {1, 2, 4}, a);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void nestedCascadeTest() throws Exception {
		String source = ""
		  + "set csv;"
		  + "csv   = \"data 1.1; data 1.2; data 1.3; data 1.4\\n\";"
		  + "csv ..= \"data 2.1; data 2.2; data 2.3; data 2.4\\n\";"
		  + "csv ..= \"data 3.1; data 3.2; data 3.3; data 3.4\\n\";"
		  + "csv ..= \"data 4.1; data 4.2; data 4.3; data 4.4\\n\";"
		  + ""
		  + "set cutCol = function (item) {"
		  + "	return item.trim();"
		  + "};"
		  + ""
		  + "set cutLine = function (item) {"
		  + "	return item.split (\";\").each(cutCol);"
		  + "}"
		  + ""
		  + "set values = csv.split (\"\\n\").each (cutLine);"
		  + ""
		  + "values.each(function (item) {"
		  + "	item.each (function (item) {"
		  + "		print (\"parsed data : [\" .. item .. \"]\");"
		  + "	});"
		  + "});"
		  + "" + "\n";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void lastCascadeTest() throws Exception {
		String source = ""
		  + "set a = array {"
		  + "	function (f) {"
		  + "		print (\"execute \" .. f());"
		  + "	}"
		  + "};"
		  + ""
		  + "set f = function () {"
		  + "	return \"me\";"
		  + "};"
		  + "f = f.decorate (a[0]);"
		  + "f();"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void toStringTest() throws Exception {
		String source = ""
		  + "set f = function () {};"
		  + "set o = object () {};"
		  + ""
		  + "set a = array {1, o, f, 4};"
		  + ""
		  + "print (a);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void toStringCascadeTest() throws Exception {
		String source = ""
		  + "set f = function (a, b, c = 7) {};"
		  + "set o = object () {"
		  + "	set a = 5;"
		  + "	set meth = function () {};"
		  + "};"
		  + ""
		  + "set a = array {\"a\":1, o, f, 4};"
		  + ""
		  + "print (a);"
		  + "print (a.toString());"
		  + "unit.assertEquals (\"nil\", nil.toString())"
		  + "unit.assertEquals (\"true\", true.toString())"
		  + "unit.assertEquals (\"7\", (7).toString())"
		  + "unit.assertEquals (\"test\", \"test\".toString())"
		  + "unit.assertEquals (\"1234\", array {1,2,3,4}.toString())"
		  + "unit.assertEquals (\"function#\", function {}.toString().subString(0, 9))"
		  + "unit.assertEquals (\"object#\", object {}.toString().subString(0, 7))"
		  //+ "unit.assertEquals (\"1object(a=5, meth=function())function(a, b, c=7)4\", a.toString())"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void ifNullTest() throws Exception {
		String source = ""
		  + "set s;"
		  + "unit.assertTrue(s.isNull());"
		  + "s = s.ifNull(\"my string\");"
		  + "unit.assertFalse(s.isNull());"
		  + "unit.assertEquals(\"my string\" ,s);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ifNullBreakTest() throws Exception {
		String source = ""
		  + "set a;"
		  + "unit.assertEquals(nil, a.ifNullBreak().add(5));"
		  + "a = 5;"
		  + "unit.assertEquals(array {5, 5}, a.ifNullBreak().add(5));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
	@Test
	public void ifNotBreakTest() throws Exception {
		String source = ""
		  + "set a;"
		  + "unit.assertEquals(nil, a.ifNotBreak(al.types.NUMBER).add(5));"
		  + "a = 5;"
		  + "unit.assertEquals(array {5, 5}, a.ifNotBreak(al.types.NUMBER).add(5));"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	
	@Test
	public void eventTest() throws Exception {
		String source = ""
		  + "set r;"
		  + "set a;"
		  + ""
		  + "a.onChanged( (v){ r = v } );"
		  + ""
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = true;"
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = 7;"
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = \"Algoid\";"
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = array() {1, 2, 3, 4};"
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = function () {};"
		  + "unit.assertEquals(a, r);"
		  + ""
		  + "a = object () {};"
		  + "unit.assertEquals(a, r);"

		  + ""
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

}
