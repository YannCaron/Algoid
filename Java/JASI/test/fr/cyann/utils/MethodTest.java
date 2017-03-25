/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class MethodTest {

    private static Method<Integer, Integer> add;
    private static Method<Integer, Integer> substract;

    public MethodTest() {
        add = new Method<Integer, Integer>() {

            @Override
            public Integer invoke(Integer... args) {
                Integer result = 0;
                for (Integer arg : args) {
                    result += arg;
                }
                return result;
            }
        };

        substract = new Method<Integer, Integer>() {

            @Override
            public Integer invoke(Integer... args) {
                Integer result = null;
                for (Integer arg : args) {
                    if (result == null) {
                        result = arg;
                    } else {
                        result -= arg;
                    }
                }
                return result;
            }
        };
    }

    /**
     * Test of invoke method, of class Method.
     */
    @Test
    public void testInvoke() {
        assertEquals("5 + 5 != 10 ?", Integer.valueOf(10), add.invoke(5, 5));
        assertEquals("5 - 5 != 0 ?", Integer.valueOf(0), substract.invoke(5, 5));
        assertEquals("5 + 2 != 7 ?", Integer.valueOf(7), add.invoke(5, 2));
        assertEquals("5 - 2 != 3 ?", Integer.valueOf(3), substract.invoke(5, 2));
        assertEquals("5 + 4 - 1 != 8 ?", Integer.valueOf(8), add.invoke(5, substract.invoke(4, 1)));
    }
}
