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
public class CaseInsensitive {

    @Test
    public void forTest1() throws Exception {
        String source = "Loop (10) { Print (1); }";

        AL.execute(new Syntax(), new UnitTestRuntime(), source);

    }
}
