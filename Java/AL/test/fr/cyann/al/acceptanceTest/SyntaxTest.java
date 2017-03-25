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
public class SyntaxTest {

	@Test
	public void danglingElseTest() throws Exception {
		String source = "" +
				"set a = if (true) if (false) 1 else 2 else 3;" +
				"unit.assertEquals(2, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void danglingExpressionTest() throws Exception {

		String source = "" +
				"set f = function () {};" +
				"\n" +
				"(5).loopFor (function () {})" +
				"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
