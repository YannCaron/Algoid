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
public class ObjectTest {

    @Test
    public void staticScopeTest() {
        String source = ""
                + "set o = object () {"
                + "	set a = 7;"
                + "	set g = function () {"
                + "		print (a);"
                + "	};"
                + "};"
                + ""
                + "set exec = function(f) {"
                + "	f();"
                + "};"
                + "exec (o.g);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newTest() {
        String source = ""
                + "set o = object () {"
                + "	set a = 5;"
                + "	set setA = function (a) {"
                + "		a = a;"
                + "	};"
                + "};"
                + "set p = new o;"
                + ""
                + "print (o.a);"
                + "print (p.a);"
                + ""
                + "o.a = 7;"
                + "print (o.a);"
                + "print (p.a);"
                + ""
                + "o.setA(8);"
                + "p.setA(9);"
                + "print (o.a);"
                + "print (p.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectTest() {
        String source = ""
                + "set obj = object () {};"
                + "set obj.a = function () {};"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void object1Test() {
        String source = ""
                + "set obj = object () {"
                + "	set i = 0;"
                + "	set f = function () {"
                + "		i = 7;"
                + "	};"
                + "};"
                + "unit.assertExists(obj.f);"
                + "unit.assertEquals (0, obj.i);"
                + "obj.f();"
                + "unit.assertEquals (7, obj.i);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectDeclarationTest() {
        String source = ""
                + "set obj = object () {};"
                + "unit.assertExists(obj);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectComment1Test() {
        String source = ""
                + "set obj = object () {"
                + "	/* comment */"
                + "};"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectComment2Test() {
        String source = ""
                + "set obj = object () {"
                + "	// comment \n"
                + "	set f = function () {};"
                + "};"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void anonymousObjectTest() {
        String source = "" + //"unit.assertEquals(7, " +
                "	object () {"
                + "		set f = function() {"
                + "			return 7;"
                + "		};"
                + "	}.f();" + //");" +
                //"unit.assertTrue(true);" +
                "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectCallTest() {
        String source = ""
                + "set obj = object {"
                + "	set attr1 = 7;"
                + "};"
                + "unit.assertEquals(7, obj.attr1);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectGetterSetterTest() throws Exception {
        String source = ""
                + "set obj = object {"
                + "	set attr1;"
                + "	set setAttr1 = function (attr1) {"
                + "		print (attr1);"
                + "		attr1 = attr1;"
                + "	};"
                + "	set getAttr1 = function () {"
                + "		return attr1;"
                + "	};"
                + "};"
                + ""
                + "unit.assertNull(obj.getAttr1());"
                + "obj.setAttr1(7);"
                + "unit.assertEquals(7, obj.getAttr1());"
                + "set res = obj.getAttr1();"
                + ""
                + "unit.assertEquals (7, res);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectAddAttributesTest() {
        String source = ""
                + "set obj = object {};"
                + "set obj.attr1;"
                + "set obj.setAttr1 = function(attr1) {"
                + "	attr1 = attr1;"
                + "};"
                + "set obj.getAttr1 = function () {"
                + "	return attr1;"
                + "};"
                + ""
                + "unit.assertNull(obj.getAttr1());"
                + "obj.setAttr1(7);"
                + "unit.assertEquals(7, obj.getAttr1());"
                + "set res = obj.getAttr1();"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

        //assertEquals(Float.valueOf(7F), Float.valueOf(test.resolve("res").getNumber()));
        //assertEquals(Float.valueOf(0F), Float.valueOf(test.resolve("attr1").getNumber()));
    }

    @Test
    public void pointerTest1() {
        String source = ""
                + "set obj = object {"
                + "	set attr1 = 7;"
                + "};"
                + "set a = obj;"
                + ""
                + "unit.assertEquals(7, a.attr1);"
                + ""
                + "a.attr1 = 8;"
                + "unit.assertEquals(8, obj.attr1);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void pointerTest2() {
        String source = ""
                + "set o = object {"
                + "	set a = 7;"
                + "};"
                + "set p = object {"
                + "	set b = 4;"
                + "	set setB = function (b) {"
                + "		b = b;"
                + "	};"
                + "};"
                + "p.setB(o);"
                + "unit.assertEquals(7, p.b.a);"
                + ""
                + "set o.b = 9;"
                + "unit.assertEquals(9, p.b.b);"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newTest1() {
        String source = ""
                + "set obj = object {"
                + "	set a = 1;"
                + "};"
                + "set instance = new obj;"
                + "unit.assertEquals(1, obj.a);"
                + "unit.assertEquals(1, instance.a);"
                + "obj.a = 5;"
                + "unit.assertEquals(5, obj.a);"
                + "unit.assertEquals(1, instance.a);"
                + "instance.a = 7;"
                + "unit.assertEquals(5, obj.a);"
                + "unit.assertEquals(7, instance.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void newTest2() {
        String source = ""
                + "set ref = object {"
                + "	set a = 0;"
                + "	set b = 0;"
                + "};"
                + "set obj = object {"
                + "	set a = new ref;"
                + "};"
                + "set instance = new obj;"
                + "unit.assertEquals(0, obj.a.a);"
                + "obj.a.b = 7;"
                + "unit.assertEquals(0, obj.a.a);"
                + "unit.assertEquals(7, obj.a.b);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

        // TODO
//		String result = ASTBuilder.getInstance().toStringTree();
//		System.out.println(result);
    }

    @Test
    public void newTest3() {
        String source = ""
                + "set parent = object {"
                + "};"
                + "set parent.a = 1;"
                + "set parent.setA = function(a) {"
                + "	a = a;"
                + "};"
                + "set obj = object (parent) {"
                + "};"
                + "unit.assertEquals(1, obj.a);"
                + "set obj2 = new obj;"
                + "unit.assertEquals(1, obj2.a);"
                + "obj2.setA(2);"
                + "unit.assertEquals(2, obj2.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newTest4() {
        String source = ""
                + "set parent = object {};"
                + "set parent.inner = object {};"
                + "set parent.inner.f = function() {return 7;};"
                + "set obj = object (parent) {};"
                + ""
                + "unit.assertEquals(7, obj.inner.f());"
                + ""
                + "set obj2 = new obj;"
                + "" //+ "unit.assertEquals(7, obj2.inner.f());"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newTest5() {
        String source = ""
                + "set init = object {};"
                + "set init.a = 5;"
                + "unit.assertEquals(5, init.a);"
                + "set init.a = 7;"
                + "unit.assertEquals(7, init.a);"
                + ""
                + "set redefined = new init;"
                + "unit.assertEquals(7, redefined.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void cloneTest1() {
        String source = ""
                + "set pt = object {"
                + "	set x;"
                + "	set y;"
                + "	set clone = function (x, y) {"
                + "		set o = new pt;"
                + "		o.x = x;"
                + "		o.y = y;"
                + "		return o;"
                + "	};"
                + "};"
                + ""
                + "set pt1 = pt.clone(1, 2);"
                + "set pt2 = pt.clone(3, 4);"
                + ""
                + "unit.assertEquals (1, pt1.x);"
                + "unit.assertEquals (2, pt1.y);"
                + "unit.assertEquals (3, pt2.x);"
                + "unit.assertEquals (4, pt2.y);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void cloneTest2() {
        String source = ""
                + "set pt = object {"
                + "	set x;"
                + "	set y;"
                + "};"
                + ""
                + "set pt1;"
                + "set pt2;"
                + ""
                + "{"
                + "	set a = 1; set b = 2;"
                + "	pt1 = pt.clone(a, b);"
                + "	pt2 = pt.clone(3, 4);"
                + ""
                + "}"
                + "unit.assertEquals (1, pt1.x);"
                + "unit.assertEquals (2, pt1.y);"
                + "unit.assertEquals (3, pt2.x);"
                + "unit.assertEquals (4, pt2.y);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void cloneTest3() {
        String source = ""
                + "set pt = object {"
                + "	set x;"
                + "	set y;"
                + "};"
                + ""
                + "set pt1;"
                + "set pt2;"
                + ""
                + "	set a = 1; set b = 2;"
                + ""
                + "set point = array {"
                + "	pt.clone(a, b),"
                + "	pt.clone(3, 4)"
                + "}"
                + ""
                + "{"
                + "	pt1 = pt.clone(a, b);"
                + "	pt2 = pt.clone(3, 4);"
                + ""
                + "}"
                + "unit.assertEquals (1, point[0].x);"
                + "unit.assertEquals (2, point[0].y);"
                + "unit.assertEquals (3, point[1].x);"
                + "unit.assertEquals (4, point[1].y);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void cloneAndScopeTest() {
        final String source = ""
                + "set a = 7;"
                + ""
                + "set o = object() {"
                + " set a = 8;"
                + "}"
                + ""
                + "set p = o.clone(9);"
                + ""
                + "unit.assertEquals(7, a);"
                + "unit.assertEquals(8, o.a);"
                + "unit.assertEquals(9, p.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newAndInheritanceTest() {
        String source = ""
                + "set parent = object {"
                + "	set a = 1;"
                + "};"
                + "set obj = object (parent) {"
                + "};"
                + "set instance = new obj;"
                + "unit.assertEquals(1, obj.a);"
                + "unit.assertEquals(1, instance.a);"
                + "obj.a = 5;"
                + "unit.assertEquals(5, obj.a);"
                + "unit.assertEquals(1, instance.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void newToExternalTest() {
        String source = ""
                + "set o = object () {"
                + "	set dbg = function () { debug.printScope(); };"
                + "};"
                + ""
                + "set p = object () {"
                + "	set f = function () { return 7; };"
                + "};"
                + ""
                + "set o.p = new p;"
                + ""
                + "o.dbg();"
                + "unit.assertEquals (7, o.p.f());"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void DeclarationClone1Test() {
        String source = ""
                + "set o = object {"
                + "	set a = 5;"
                + "	set f = function (v) {"
                + "		a = v;"
                + "	};"
                + "};"
                + "set q = object (o) {};" + // inherits with initial o definition (a = 5)
                "o.f(7);"
                + "unit.assertEquals(7, o.a);"
                + "unit.assertEquals(5, q.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void DeclarationClonexTest() {
        String source = ""
                + "set o = object () {"
                + "	set a = 1;"
                + "	set c;"
                + "};"
                + ""
                + "set p = object () {};"
                + "set p.a = 2;"
                + "set q = new o;"
                + "set r = new p;"
                + ""
                + "unit.assertEquals (o.a, q.a);"
                + "unit.assertEquals (p.a, r.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void DeclarationClone2Test() {
        String source = ""
                + "set o = object {"
                + "};"
                + "set o.a = 5;"
                + "set o.f = function (aVar) {"
                + "debug.printScope();"
                + "	print (\"param = \" .. aVar);"
                + "	print (\"a = \" .. a);"
                + "	a = aVar;"
                + "	print (\"a = \" .. a .. \" = \" .. aVar);"
                + "};"
                + ""
                + "o.f(7);" + // set p.a = 7
                "unit.assertEquals(7, o.a);"
                + ""
                + "set p = new o;" + // new p with initial o definition (initialy a = 5)
                "unit.assertEquals(5, p.a);"
                + "p.f(8);" + // set 8 to o.a
                "print (p.a);"
                + "unit.assertEquals(8, p.a);"
                + ""
                + "set q = object (o) {};" // inherits with initial o definition (a = 5)
                + "unit.assertEquals(5, q.a);"
                + "o.a = 7;" // modify the parent dont change the inherited definition
                + "o.f(7);"
                + "unit.assertEquals(5, q.a);"
                + ""
                + "q.a = 8;"
                + "q.f(8);"
                + "unit.assertEquals(8, q.a);"
                + "unit.assertEquals(7, o.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void DuckTyping() {
        String source = ""
                + "set duck = object {"
                + "	set quack = function () {"
                + "		return \"quack!\";"
                + "	};"
                + "};"
                + "set cow = object {"
                + "	set quack = function () {"
                + "		return \"Cow's dont quack, they Mooo!\";"
                + "	};"
                + "};"
                + "set quackQuack = function (animal) {"
                + "	return animal.quack();"
                + "};"
                + ""
                + "unit.assertEquals(\"quack!\", quackQuack(duck));"
                + "unit.assertEquals(\"Cow's dont quack, they Mooo!\", quackQuack(cow));"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void miscTest1() {
        String source = ""
                + "set comp = object {"
                + "	set name = \"\";"
                + "	set next;"
                + "	set setNext = function (next) {"
                + "		next = next;"
                + "	};"
                + "	set pr = function () {"
                + "		print(name);"
                + "	};"
                + "};"
                + "set a = new comp;"
                + "a.name = \"Hello\";"
                + "unit.assertEquals(\"Hello\", a.name);"
                + "set b = new comp;"
                + "set c = new comp;"
                + ""
                + "a.setNext(b);"
                + "b.setNext(c);"
                + ""
                + "a.pr();"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void nestedObjects() {
        String source = ""
                + "set o = object () {"
                + "	set p = object () {"
                + "		set q = object () {"
                + "			set r = function() {"
                + "				return 8;"
                + "			};"
                + "		};"
                + "	};"
                + "};"
                + "unit.assertEquals(8, o.p.q.r());"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void nestedObjects1Test() {
        String source = ""
                + "set o = object () {"
                + "	set p = object () {"
                + "		set a = 7;"
                + "	};"
                + "};"
                + ""
                + "set q = new o;"
                + "unit.assertEquals (7, o.p.a);"
                + "unit.assertEquals (7, q.p.a);"
                + "o.p.a = 10;"
                + "unit.assertEquals (10, o.p.a);"
                + "unit.assertEquals (7, q.p.a);"
                + "q.p.a = 5;"
                + "unit.assertEquals (10, o.p.a);"
                + "unit.assertEquals (5, q.p.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void nestedObjects2Test() {
        String source = ""
                + "set pp = object () {"
                + "	set a = 7;"
                + "};"
                + "set o = object () {"
                + "	set p = pp;"
                + "};"
                + ""
                + "set q = new o;"
                + "unit.assertEquals (7, o.p.a);"
                + "unit.assertEquals (7, q.p.a);"
                + "o.p.a = 10;"
                + "unit.assertEquals (10, o.p.a);"
                + "unit.assertEquals (10, q.p.a);"
                + "q.p.a = 5;"
                + "unit.assertEquals (5, o.p.a);"
                + "unit.assertEquals (5, q.p.a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectAccessTest() throws Exception {
        String source = ""
                + "set p = object() {"
                + "	set a = 7;"
                + "};"
                + ""
                + "set o = object () {"
                + "	set f = function () {"
                + "		set myP = new p;"
                + "		return myP.a;"
                + "	};"
                + "};"
                + "unit.assertEquals (7, o.f());"
                + "";
        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void equalTest() {
        String source = ""
                + "set o = object () {"
                + "};"
                + ""
                + "unit.assertTrue (o == o);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void objectExternalDefinitionTest() throws Exception {
        String source = ""
                + "set o = object {"
                + "	set a = 7;"
                + "}"
                + ""
                + "set o.setA = function (a) {"
                + "	this.a = a;"
                + "}"
                + ""
                + "set p = o.clone();"
                + "p.setA(8);"
                + "unit.assertEquals (7, o.a);"
                + "unit.assertEquals (8, p.a);";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void objectToStringTest() throws Exception {
        String source = ""
                + "set o = object () {"
                + "	set toString = function () {"
                + "		return \"my to string!\""
                + "	}"
                + "}"
                + ""
                + "unit.assertEquals (\"my to string!\", \"\" .. o);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void returnObjectAndTailCallTest() throws Exception {
        String source = ""
                + "set o = object () {"
                + "	set f = function () {"
                + "		return 7;"
                + "	};"
                + "};"
                + ""
                + "set f = function () {"
                + "	return o.clone();"
                + "};"
                + ""
                + "set res = f().f()"
                + "print(res);"
                + ""
                + "unit.assertEquals (7, res);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void miscTest2() {
        String source = ""
                + "set o = object () {"
                + " set x;"
                + " set y;"
                + " set f = function () { print (y) }"
                + ""
                + " set clone = function (x, y) {"
                + "     set o = new this"
                + "     o.x = x;"
                + "     o.y = y;"
                + "     return o;"
                + " }"
                + "}"
                + ""
                + "set os = array {"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + " o.clone (1, 2),"
                + " o.clone (3, 4),"
                + "}"
                + ""
                + "os.each ((item) { item.f(); })"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

        @Test
    public void collectionTest() {
        String source = ""
                + "set o = object () {"
                + " set a = 7;"
                + " set is = function () {"
                + "     if (a == 7) { print (\"OK\"); }"
                + " }"
                + "};"
                + ""
                + "set a = array () {"
                + " o.clone(), o.clone()"
                + "}"
                + "a.each((item) { if (true) { item.is(); } });"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

}
