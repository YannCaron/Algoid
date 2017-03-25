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
 *
 * @author CARONYN
 */
public class ScopeTest {

	@Test
	public void parameterScopeTest1() throws Exception {
		String source = "" +
				"set a = 10;" +
				"set fun = function (a) {" +
				"	a+= 1;" +
				"};" +
				"" +
				"fun (20);" +
				"unit.assertEquals (10, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void parameterScopeTest2() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = 10;" +
				"	set setA = function (a) {" +
				"		a += 1;" +
				"	};" +
				"};" +
				"" +
				"o.setA(20);" +
				"set res = o.a;" +
				"unit.assertEquals (11, res);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void arrayInParameterScope1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"" +
				"set f = function (p) {" +
				"	unit.assertEquals (p[0], 1);" +
				"	p.remove (2);" +
				"};" +
				"" +
				"f (a);" +
				"" +
				"unit.assertEquals (array {1, 2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void arrayInParameterScope2Test() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = 10;" +
				"};" +
				"" +
				"set q = new o;" +
				"unit.assertEquals (10, q.a);" +
				"" +
				"set a = array {new o, new o, q};" +
				"" +
				"set f = function (p) {" +
				"	unit.assertEquals (10, p[0].a);" +
				"	p.remove (2);" +
				"};" +
				"" +
				"f (a);" +
				"" +
				"unit.assertEquals (10, q.a);" +
				"unit.assertEquals (2, a.length());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void arrayInParameterScope3Test() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = 0;" +
				"	set clone = function (a) {" +
				"		set obj = new this;" +
				"		obj.a = a;" +
				"		return obj;" +
				"	};" +
				"};" +
				"" +
				"set q = object () {" +
				"	set o;" +
				"	set clone = function (o) {" +
				"		set obj = new this;" +
				"		obj.o = o;" +
				"		return obj;" +
				"	};" +
				"	set mult = function (factor) {" +
				"		o.a *= 10;" +
				"	};" +
				"};" +
				"set f = function (o, v) {" +
				"	o.a = v;" +
				"};" +
				"set a = array {" +
				"	o.clone (1)," +
				"	o.clone (2)," +
				"	o.clone (3)," +
				"	o.clone (4)" +
				"};" +
				"" +
				"unit.assertEquals (1, a[0].a);" +
				"unit.assertEquals (2, a[1].a);" +
				"unit.assertEquals (3, a[2].a);" +
				"unit.assertEquals (4, a[3].a);" +
				"" +
				"set b = array {" +
				"	q.clone (a[0])," +
				"	q.clone (a[1])," +
				"	q.clone (a[2])," +
				"	q.clone (a[3])," +
				"	q.clone (a[0])," +
				"	q.clone (a[1])" +
				"};" +
				"" +
				"set trans = function (a) {" +
				"	a.each (function (item) {" +
				"		item.mult(10);" +
				"	});" +
				"};" +
				"trans (b);" +
				"unit.assertEquals (100, b[0].o.a);" +
				"unit.assertEquals (200, b[1].o.a);" +
				"unit.assertEquals (30, b[2].o.a);" +
				"unit.assertEquals (40, b[3].o.a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
	
	@Test
	public void parameterObjectTest() throws Exception {
		String source = "" +
			"set o = object () {" +
			"	set x = 10;" +
			"};" +
			"" +
			"set p = object () {" +
			"	set f = function (obj) {" +
			"		print (obj.x);" +
			"		obj.x = 20;" +
			"	};" +
			"};" +
			"" +
			"unit.assertEquals(10, o.x);" +
			"" +
			"p.f (o);" +
			"" +
			"unit.assertEquals(20, o.x);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
