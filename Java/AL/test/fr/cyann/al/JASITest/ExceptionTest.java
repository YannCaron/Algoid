/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.JASITest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.jasi.exception.InvalidGrammarException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class ExceptionTest {

    @Test
    public void testJASIException1() {

        String source = ""
                + "set a = object () {" + "\n"
                + "" + "\n"
                + " bla " + "\n"
                + "" + "\n"
                + " a = 5;" + "\n"
                + "};" + "\n"
                + "";

        try {
            AL.execute(new Syntax(), new RuntimeVisitor(), source);
            fail("Must throw an exception !");
        } catch (InvalidGrammarException ex) {
            System.out.println(ex.toString());
            assertEquals("bla", ex.getToken().getText());
            assertEquals(22, ex.getToken().getPos());
            assertEquals(1, ex.getToken().getCol());
            assertEquals(2, ex.getToken().getLine());
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains("Unexpected grammar 'bla' at line 3, column 2"));
        }
    }

}
