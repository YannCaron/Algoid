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
 * @author caronyn
 */
public class BlockTest {

	@Test
	public void testScope1() throws Exception {

		String source = "" +
			"set a = 8;" +
			"{" +
			"	set a = 7;" +
			"	unit.assertEquals (7, a);" +
			"}" +
			"unit.assertEquals (8, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void testScope2() throws Exception {

		String source = "" +
			"set a;" +
			"{" +
			"	unit.assertExists (a);" +
			"	unit.assertNotExists (b);" +
			"	set b;" +
			"	unit.assertExists (a);" +
			"	unit.assertExists (b);" +
			"	unit.assertNotExists (c);" +
			"	{" +
			"		unit.assertExists (a);" +
			"		unit.assertExists (b);" +
			"		unit.assertNotExists (c);" +
			"		set c;" +
			"		unit.assertExists (a);" +
			"		unit.assertExists (b);" +
			"		unit.assertExists (c);" +
			"	}" +
			"}" +
			"unit.assertExists (a);" +
			"unit.assertNotExists (b);" +
			"unit.assertNotExists (c);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
