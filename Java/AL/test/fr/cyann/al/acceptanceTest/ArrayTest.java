/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.utils.UnitTesting;
import org.junit.Test;

/**
 * <p>
 * @author CARONYN
 */
public class ArrayTest {

    @Test
    public void cacheTest() throws Exception {
        String source = ""
                + "set a;"
                + "loop (1000) {"
                + "	a = array {1, 2, 3, 4};"
                + "	a.add(5);"
                + "}"
                + "unit.assertEquals (array {1, 2, 3, 4, 5}, a)"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void optionalParenthesisFTest() throws Exception {
        String source = ""
                + "set a = array () {1, 2, 3, 4};"
                + "unit.assertEquals (array {1, 2, 3, 4}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void listTest1() throws Exception {
        String source = ""
                + "set r; set s; set t;"
                + "set a = array {1, 2, 3};" //+ "t = len(a);"
                + "unit.assertEquals (2, a[1]);"
                + "a[1] = 5;"
                + "unit.assertEquals (5, a[1]);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void listSetTest1() throws Exception {
        String source = ""
                + "set f = function (v) {"
                + "	print (v);"
                + "};"
                + "set a = array {1, 2, 3};"
                + ""
                + "unit.assertEquals (array {1, 2, 3}, a);"
                + "a[0] = 5;"
                + "unit.assertEquals (5, a[0]);"
                + "unit.assertEquals (array {5, 2, 3}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listTest2() throws Exception {
        String source = ""
                + "set a = 1;"
                + "a[1] = 2;"
                + "unit.assertEquals (1, a[0]);"
                + "unit.assertEquals (2, a[1]);"
                + "unit.assertEquals (array {1, 2}, a);"
                + "unit.assertEquals (2, a.length());"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listTest3() throws Exception {
        String source = ""
                + "set a = 1;"
                + "a[4] = 2;"
                + "unit.assertEquals (1, a[0]);"
                + "unit.assertEquals (2, a[4]);"
                + "unit.assertEquals (array {1, nil, nil, nil, 2}, a);"
                + "unit.assertEquals (5, a.length());"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfListTest() throws Exception {
        String source = ""
                + "set a = array {{11, 12}, {21, 22}};"
                + "set r = a.clone();"
                + "print(a[0][1]);"
                + "print(a[0][4]);"
                + "print(a);"
                + "print(r);"
                + ""
                + "unit.assertEquals (array {{11, 12, nil, nil, nil}, {21, 22}}, a);"
                + "unit.assertEquals (array {{11, 12}, {21, 22}}, r);"
                + "unit.assertEquals (2, a.length());"
                + "unit.assertEquals (\"{{11, 12, nil, nil, nil}, {21, 22}}\", \"\" .. a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void listOfListCreationTest1() throws Exception {
        String source = ""
                + "set a = 0;"
                + "a[0] = 1;"
                + "print (a);"
                + "a[1][1] = 5;"
                + "print (a);"
                + "set b;"
                + "b[0][0] = 10;"
                + "b[0][1][5] = 10;"
                + "print(b);"
                + ""
                + "unit.assertEquals (array {1, {nil, 5}}, a);"
                + "unit.assertEquals (array {{10, {nil, nil, nil, nil, nil, 10}}}, b);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfListCreationTest2() throws Exception {
        String source = ""
                + "set b;"
                + "b[0][1][0] = 10;"
                + "print(b.getType());"
                + ""
                + "unit.assertEquals (array {{nil, {10}}}, b);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfObject1Test() {
        String source = ""
                + "set o = object {"
                + "	set a = 7;"
                + "	set f = function () {"
                + "		unit.assertEquals(a, 8);"
                + "	};"
                + "	set g = function () {};"
                + "};"
                + "set a;"
                + "a[0] = new o;"
                + "a[0].a = 8;"
                + "print (a[0].f);"
                + "a[0].f();"
                + ""
                + "unit.assertEquals(7, o.a);"
                + "unit.assertEquals(8, a[0].a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfObject2Test() {
        String source = ""
                + "set o = object {"
                + "	set f = function () {};"
                + "};"
                + "set a;"
                + "a[0] = new o;"
                + "a[0].f();"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfFunctionTest() {
        String source = ""
                + "set f = function () {"
                + "	return 8;"
                + "};"
                + "set a;"
                + "a[0] = f;"
                + "unit.assertEquals(8, a[0]());"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void forListTest() {
        String source = ""
                + "set a = array {1, 2};"
                + ""
                + "for (set i = 0; i<a.length(); i++) {"
                + "	print(a[i]);"
                + "}"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfObject3Test() {
        String source = ""
                + "set o = object() {"
                + "	set a = 0;"
                + "};"
                + "set a = array {new o, new o};"
                + ""
                + "for (set i = 0; i<a.length(); i++) {"
                + "	a[i].a = i;"
                + "}"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfObject4Test() {
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
                + "	a[i].f(j);"
                + "	print (a[i].a);"
                + "	unit.assertEquals(7, a[i].a);"
                + "}"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfFunctionalObject1Test() {
        String source = ""
                + "set o = object() {"
                + "	set x = 0;"
                + "};"
                + "set a = array {new o, new o};"
                + ""
                + "a[0].x = 7;"
                + "a[1].x = 8;"
                + ""
                + "set o = a[0];"
                + "set o.test = function () {"
                + "	print (\"test \" .. 7 .. \" = \" .. x);"
                + "	unit.assertEquals(7, x);"
                + "};"
                + ""
                + "set o = a[1];"
                + "set o.test = function () {"
                + "	print (\"test \" .. 8 .. \" = \" .. x);"
                + "	unit.assertEquals(8, x);"
                + "};"
                + ""
                + "a[0].test();"
                + "a[1].test();"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void listOfFunctionalObject2Test() {
        String source = ""
                + "set o = object() {"
                + "	set x = 0;"
                + "};"
                + "set a = array {new o, new o};"
                + ""
                + "a[0].x = 7;"
                + "a[1].x = 8;"
                + ""
                + ""
                + "set a[0].test = function () {"
                + "	print (\"test \" .. 7 .. \" = \" .. x);"
                + "	unit.assertEquals(7, x);"
                + "};"
                + ""
                + "set a[1].test = function () {"
                + "	print (\"test \" .. 8 .. \" = \" .. x);"
                + "	unit.assertEquals(8, x);"
                + "};"
                + ""
                + "a[0].test();"
                + "a[1].test();"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void optionalArrayTest() {
        String source = ""
                + "set a = array {"
                + "	1, "
                + "	2"
                + "};"
                + ""
                + "unit.assertEquals(2, a[1]);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void compareTest() {
        final String source = ""
                + "set a1 = array {1, 2, 3};"
                + "set a2 = array {1, 2, 4};"
                + ""
                + "unit.assertFalse (a1 == a2);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void setTest() {
        final String source = ""
                + "(10).loopFor (function (i) {"
                + "	set a = array{};"
                + "	a.add(1);"
                + "	unit.assertEquals (1, a.length());"
                + "	print (a);"
                + "});"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void swapTest() throws Exception {
        String source = ""
                + "set a = array {\"a\" : 7, \"b\" : 8};"
                + "unit.assertEquals (7, a[\"a\"]);"
                + "unit.assertEquals (8, a[\"b\"]);"
                + ""
                + "unit.assertEquals (array {7, 8}, a);"
                + "a.swap (\"a\", \"b\");"
                + "unit.assertEquals (array {8, 7}, a);"
                + ""
                + "unit.assertEquals (7, a[\"a\"]);"
                + "unit.assertEquals (8, a[\"b\"]);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void generateTest() {
        final String source = ""
                + "set a = array {};"
                + ""
                + "for (set i = 0; i <= 3; i++) {"
                + "	a.add (array {i, 0});"
                + "};"
                + ""
                + "print (a);"
                + "unit.assertEquals (array {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void NOT_YET_objectIndexTest() {
        final String source = ""
                + "set o = object () {};"
                + ""
                + "set p = new o;"
                + "set q = new o;"
                + ""
                + "set a = array {p : p, q : q};"
                + ""
                + "unit.assertEquals (array {p, q}, a);"
                + "a.each (function (item) {"
                + "	print (\"remove \" .. item);"
                + "	a.remove (item);"
                + "});"
                + ""
                + "print (a);"
                + "unit.assertEquals (array {}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void assoTestTest() {
        final String source = ""
                + "set a = array {};"
                + ""
                + "\"abc\".each (function (c, i) {"
                + "	a[c] = i + 1;"
                + "});"
                + ""
                + "unit.assertEquals(array {1, 2, 3}, a);"
                + "unit.assertEquals(1, a[\"a\"]);"
                + "unit.assertEquals(2, a[\"b\"]);"
                + "unit.assertEquals(3, a[\"c\"]);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void createTest() {
        final String source = ""
                + "set a = array {}.create (2, "
                + "	function () { "
                + "		return array{}.create (2, function (i) {"
                + "			return i * 2;"
                + "		}); "
                + "	}"
                + ");"
                + ""
                + "unit.assertEquals(array {{0, 2}, {0, 2}}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void removeTest() {
        final String source = ""
                + "set a = array {\"a\", \"b\", \"c\"};"
                + "a.remove(\"b\");"
                + ""
                + "unit.assertEquals(array {\"a\", \"c\"}, a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

}
