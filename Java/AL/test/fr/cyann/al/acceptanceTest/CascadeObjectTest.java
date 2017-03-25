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
public class CascadeObjectTest {

	@Test
	public void typeTest() throws Exception {
		String source = "" +
				"unit.assertEquals(al.types.OBJECT, object() {}.getType());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
				"set a = object () {};" +
				"set b = object () {};" +
				"set c = a;" +
				"set d = a.clone();" +
				"unit.assertFalse(a.equals(b));" +
				"unit.assertTrue(a.equals(c));" +
				"unit.assertFalse(b.equals(c));" +
				"unit.assertFalse(d.equals(a));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isA1Test() throws Exception {
		String source = "" +
				"set a = object () {};" + "\n" +
				"set b = object () {};" + "\n" +
				"set c = new a;" + "\n" +
				"set d = new b;" + "\n" +
				"set e = new c;" + "\n" +
				"" + "\n" +
				"unit.assertTrue(c.isA(a));" + "\n" +
				"unit.assertTrue(a.isA(c));" + "\n" +
				"unit.assertTrue(d.isA(b));" + "\n" +
				"unit.assertTrue(b.isA(d));" + "\n" +
				"unit.assertTrue(e.isA(c));" + "\n" +
				"unit.assertTrue(e.isA(a));" + "\n" +
				"unit.assertFalse(a.isA(b));" + "\n" +
				"unit.assertFalse(b.isA(a));" + "\n" +
				"unit.assertFalse(d.isA(a));" + "\n" +
				"unit.assertFalse(d.isA(e));" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isA2Test() throws Exception {
		String source = "" +
				"set a = object () {};" + "\n" +
				"set b = object (a) {};" + "\n" +
				"set c = new a;" + "\n" +
				"set e = new b;" + "\n" +
				"" + "\n" +
				"unit.assertFalse(b.isA(a));" + "\n" +
				"unit.assertTrue(c.isA(a));" + "\n" +
				"unit.assertFalse(c.isA(b));" + "\n" +
				"unit.assertFalse(e.isA(a));" + "\n" +
				"unit.assertTrue(e.isA(b));" + "\n" +
				"unit.assertFalse(c.isA(e));" + "\n" +
				"unit.assertFalse(e.isA(c));" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cascadeTest() throws Exception {
		String source = "" +
				"set a = object () {" +
				"	set s = \"\";" +
				"	set m = function () {" +
				"		s ..= \"m\";" +
				"	};" +
				"	set n = function () {" +
				"		s ..= \"n\";" +
				"	};" +
				"" +
				"	set getS = function () {" +
				"		return s;" +
				"	};" +
				"};" +
				"" +
				"a.m().n().m();" +
				"unit.assertEquals (\"mnm\", a.getS());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void magicMethodTest() throws Exception {
		String source = "" +
				"set r = object () {" +
				"	set f = function () {" +
				"		return this.getType() == al.types.OBJECT;" +
				"	};" +
				"}.f();" +
				"" +
				"unit.assertTrue (r);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void nested1Test() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = array {};" +
				"	set f = function (p) {" +
				"		set obj = new this;" +
				"		this.a = p;" +
				"	};" +
				"};" +
				"o.f(1);" +
				"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void nested2Test() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a;" +
				"	set f = function (grid, p) {" +
				"		a.add (function () {" +
				"			return grid[p];" +
				"		});" +
				"	};" +
				"};" +
				"" +
				"o.f(array {1, 2, 3, 4}, 1);" +
				"print (o.a[0]());";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void mergeTest() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = 0;" +
				"	set setA = function (a) {" +
				"		this.a = a;" +
				"	};" +
				"};" +
				"" +
				"set p = object () {" +
				"	set b = 0;" +
				"	set setB = function (b) {" +
				"		this.b = b;" +
				"	};" +
				"};" +
				"" +
				"set q = o.merge(p);" +
				"" +
				"q.setA (7);" +
				"q.setB (8);" +
				"" +
				"unit.assertEquals (7, q.a);" +
				"unit.assertEquals (8, q.b);" +
				"" +
				"print (\"o properties : \" .. q.a .. \", \" .. q.b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void clone1Test() {
		String source = "" +
				"set o = object () {" +
				"	set a;" +
				"	set f = function () {};" +
				"	set b = 5;" +
				"	set c = function () {};" +
				"};" +
				"" +
				"set p = o.clone(1, 2, 7);" +
				"unit.assertEquals (1, p.a);" +
				"unit.assertTrue (p.f.isFunction());" +
				"unit.assertEquals (2, p.b);" +
				"unit.assertTrue (p.c.isFunction());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void clone2Test() {
		String source = "" +
				"set o = object () {" +
				"	set a = 7;" +
				"	set f = function () {};" +
				"	set b = 5;" +
				"	set c = function () {};" +
				"};" +
				"" +
				"set p = o.clone();" +
				"unit.assertEquals (7, p.a);" +
				"unit.assertTrue (p.f.isFunction());" +
				"unit.assertEquals (5, p.b);" +
				"unit.assertTrue (p.c.isFunction());" +
				"set q = o.clone(1);" +
				"unit.assertEquals (1, q.a);" +
				"unit.assertTrue (q.f.isFunction());" +
				"unit.assertEquals (5, q.b);" +
				"unit.assertTrue (q.c.isFunction());" +
				"set r = q.clone(7, 8);" +
				"unit.assertEquals (7, r.a);" +
				"unit.assertTrue (r.f.isFunction());" +
				"unit.assertEquals (8, r.b);" +
				"unit.assertTrue (r.c.isFunction());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void setAttributeTest1() throws Exception {
		String source = "" +
				"set o = object () {};" +
				"" +
				"o.setAttribute (\"a\", 8);" +
				"o.setAttribute (\"f\", function () {" +
				"	a = 7;" +
				"});" +
				"" +
				"set fun = function () {};" +
				"set obj = object () {set a = 7;};" +
				"o.setAttribute (\"as\", array {1, 2, 3, {4, 7, 8}});" +
				"o.setAttribute (\"obj\", obj);" +
				"" +
				"set p = new o;" +
				"" +
				"unit.assertEquals (8, o.a);" +
				"unit.assertEquals (8, p.a);" +
				"" +
				"o.f();" +
				"p.f();" +
				"unit.assertEquals (7, o.a);" +
				"unit.assertEquals (7, p.a);" +
				"unit.assertEquals (array {1, 2, 3, {4, 7, 8}}, o.as);" +
				"unit.assertEquals (array {1, 2, 3, {4, 7, 8}}, p.as);" +
				"unit.assertEquals (obj.a, o.obj.a);" +
				"unit.assertEquals (obj.a, p.obj.a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void setAttributeTest2() throws Exception {
		String source = "" +
				"" +
				"set o = object () {" +
				"	set n = 0;" +
				"	set meth = function () {" +
				"		n--;" +
				"	};" +
				"};" +
				"" +
				"o.setAttribute(\"meth\", function () {" +
				"	n++;" +
				"});" +
				"" +
				"o.setAttribute(\"a\", 8);" +
				"" +
				"o.meth();" +
				"print(\"o.n should be equals to 1 ? Result : \" .. o.n);" +
				"" +
				"unit.assertEquals(1, o.n);" +
				"unit.assertEquals(8, o.a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void setAttributeTest3() throws Exception {
		String source = "" +
				"set res;" +
				"set a = 7;" +
				"set f = function () {" +
				"		res = a;" +
				"		print (a);" +
				"};" +
				"f();" +
				"" +
				"set o = object() {" +
				"	set res;" +
				"	set a = 8;" +
				"};" +
				"" +
				"o.setAttribute(\"f\", f);" +
				"o.f();" +
				"" +
				"unit.assertEquals(7, res);" +
				"unit.assertEquals(8, o.res);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void removeAttributeTest() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set meth1 = function () {};" +
				"	set meth2 = function () {};" +
				"};" +
				"" +
				"o.removeAttribute(\"meth2\");" +
				"" +
				"unit.assertExists(o.meth1);" +
				"unit.assertNotExists(o.meth2);" +
				"" +
				"set p = new o;" +
				"unit.assertExists(p.meth1);" +
				"unit.assertNotExists(p.meth2);" +
				"" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void attributeExistsTest() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set method = function () {};" +
				"};" +
				"" +
				"unit.assertTrue(o.attributeExists(\"method\"));" +
				"unit.assertFalse(o.attributeExists(\"superMethod\"));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void getAttributesNamesTest() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set method1 = function () {};" +
				"	set method2 = function () {};" +
				"	set method3 = function () {};" +
				"};" +
				"" +
				"unit.assertEquals(array {\"method1\", \"method2\", \"method3\"}, o.getAttributesNames());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cloneArrayCascadeTest() throws Exception {
		String source = "" +
				"set pt3d = object () {" +
				"	set x; set y; set z;" +
				"};" +
				"" +
				"set face = object () {" +
				"	set pts1; set pts2;" +
				"}" +
				"" +
				"set pts = array {" +
				"	pt3d.clone (1, 2, 3)," +
				"	pt3d.clone (7, 8, 9)," +
				"	pt3d.clone (10, 11, 12)," +
				"	pt3d.clone (20, 21, 22)" +
				"};" +
				"" +
				"print (pts[0].toString());" +
				"" +
				"set faces = array {" +
				"	face.clone (array {pts[0], pts[1]}, array {pts[2], pts[3]})" +
				"}" +
				"" +
				"unit.assertEquals (array {pts[0], pts[1]}, faces[0].pts1);" +
				"unit.assertEquals (array {pts[2], pts[3]}, faces[0].pts2);" +
				"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
