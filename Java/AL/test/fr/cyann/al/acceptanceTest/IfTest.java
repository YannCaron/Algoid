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
public class IfTest {

    @Test
    public void ifTest() throws Exception {
        String source = ""
                + "set a = 0;"
                + "set b = 0;"
                + "if (a == 0) {"
                + "	print (\"Enter\");"
                + "	b = b + 1;"
                + "}"
                + "if (a == 1) {"
                + "	print (\"Not enter\");"
                + "	b = b + 1;"
                + "}"
                + "a = 1;"
                + "if (a == 1) {"
                + "	print (\"Enter now\");"
                + "	b = b + 1;"
                + "}"
                + "unit.assertEquals(2, b);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void elseTest() throws Exception {
        String source = ""
                + "set a = 0;"
                + "set b = 0;"
                + "if (a == 0) {"
                + "	print (\"Enter\");"
                + "	b = 1;"
                + "} else {"
                + "	print (\"Not Enter\");"
                + "	b = 2;"
                + "}"
                + "if (a == 1) {"
                + "	print (\"Not enter\");"
                + "	b = b + 10;"
                + "} else {"
                + "	print (\"Enter now\");"
                + "	b = b + 20;"
                + "}"
                + "unit.assertEquals(21, b);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void elseIfTest() throws Exception {
        String source = ""
                + "set f = function(a) {"
                + "	if (a == 0) {"
                + "		return \"zero\";"
                + "	} elseif (a == 1) {"
                + "		return \"un\";"
                + "	} elseif (a == 2) {"
                + "		return \"deux\";"
                + "	} else {"
                + "		return \"other\";"
                + "	}"
                + "};"
                + "print (f (1));"
                + "unit.assertEquals(\"zero\", f(0));"
                + "unit.assertEquals(\"un\", f(1));"
                + "unit.assertEquals(\"deux\", f(2));"
                + "unit.assertEquals(\"other\", f(3));"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void nestedIfTest() throws Exception {
        String source = ""
                + "set f = function (a) {" + "\n"
                + "	if (a >= 0) {" + "\n"
                + "		if (a == 1) {" + "\n"
                + "			return \"un\";" + "\n"
                + "		} elseif (a == 2) {" + "\n"
                + "			return \"deux\";" + "\n"
                + "		} else {" + "\n"
                + "			return \"other than 1\";" + "\n"
                + "		}" + "\n"
                + "	} elseif (a == -1) {" + "\n"
                + "		return \"moins un\";" + "\n"
                + "	} else {" + "\n"
                + "		return \"other\";" + "\n"
                + "	}" + "\n"
                + "};" + "\n"
                + "unit.assertEquals(\"other\", f(-2));" + "\n"
                + "unit.assertEquals(\"moins un\", f(-1));" + "\n"
                + "unit.assertEquals(\"other than 1\", f(0));" + "\n"
                + "unit.assertEquals(\"un\", f(1));" + "\n"
                + "unit.assertEquals(\"deux\", f(2));" + "\n"
                + "unit.assertEquals(\"other than 1\", f(3));" + "\n"
                + "unit.assertEquals(\"un\", f(1));" + "\n"
                + "" + "\n";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void condTest1() throws Exception {
        String source = ""
                + "set a = true;"
                + "set b = false;"
                + "set c = false;"
                + "if (a == true && b == false && (a && b == false) && (a || b == true)) {"
                + "	c = true;"
                + "}"
                + "unit.assertTrue(c);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void condTest2() throws Exception {
        String source = ""
                + "set c;"
                + "if (0) {"
                + "	c = true;"
                + "}"
                + "unit.assertFalse(c);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void functionTest() throws Exception {
        String source = ""
                + "set i = 5;"
                + "set test = function (j) {"
                + "	if (j == 5) {"
                + "		i = 2;"
                + "		return;"
                + "	}"
                + "	i = 7;"
                + "};"
                + "test (i);"
                + "unit.assertEquals(2, i);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void ifSemicolonTest() throws Exception {
        String source = ""
                + "if (true) {"
                + "	print (\"working\");"
                + "};"
                + ""
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void ifExpr1() throws Exception {
        String source = ""
                + "set a = 0;"
                + "set i = if (a == 0) {7} else {8};"
                + "print (\"i=\" .. i);"
                + "unit.assertEquals (7, i);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void ifExpr2() throws Exception {
        String source = ""
                + "set a = 1;"
                + "set i = if (a == 0) 7 else 8;"
                + "print (\"i=\" .. i);"
                + "unit.assertEquals (8, i);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void ifExpr3() throws Exception {
        String source = ""
                + "unit.assertTrue( if (true) true )"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

    @Test
    public void ifExpr4() throws Exception {
        String source = ""
                + "set a = 7;"
                + "if (a == 7) { function () { print (\"it works !\")} }()"
                + //"f ();" +
                "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);
    }

}
