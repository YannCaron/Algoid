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
public class NullTest {

	@Test
	public void testNull1() throws Exception {

		String source = "" +
				"unit.assertEquals (nil, nil);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testNull2() throws Exception {

		String source = "" +
				"set a;" +
				"unit.assertEquals (nil, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
