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
 * The StringTest class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class StringTest {

    @Test
    public void testTranstype() throws Exception {

        String source = ""
                + "set a;"
                + "a = \"test 'bla' \\ 5\" + true;"
                + "unit.assertEquals(1, a);";
        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void testConcatenation() throws Exception {

        String source = ""
                + "set a;"
                + "a = \"test 'bla' \" .. 5 .. \" \" .. true;"
                + "unit.assertEquals(\"test 'bla' 5 true\", a);";
        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void testConcatenation2() throws Exception {

        String source = ""
                + "set a;"
                + "a = 7 .. 8;"
                + "unit.assertEquals(\"78\", a);";
        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void testConcatenation3() throws Exception {

        String source = ""
                + "set a;"
                + "a = true .. false;"
                + "unit.assertEquals(\"truefalse\", a);";
        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }

    @Test
    public void testEscape1() throws Exception {

        String source = ""
                + "set a = \"special\\tstring\\n\\nyop\";"
                + "print(a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }
    
    
    @Test
    public void testEscape2() throws Exception {

        String source = ""
                + "set a = \"special \\\"string\\\" \";"
                + "print(a);"
                + "";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }
}
