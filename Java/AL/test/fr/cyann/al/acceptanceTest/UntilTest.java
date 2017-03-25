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
public class UntilTest {

	@Test
	public void untilTest1() throws Exception {
		String source = "" +
			 "set a;" +
			 "do {" +
			 "	a++;" +
			 "} until (a > 4)" +
			 "unit.assertEquals(5, a);" +
			"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
