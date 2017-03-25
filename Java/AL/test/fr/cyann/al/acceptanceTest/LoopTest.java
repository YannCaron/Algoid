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
public class LoopTest {

	@Test
	public void loopTest1() throws Exception {
		String source = "" +
			"set a = 0;" +
			"loop (7) a++;" +
			"unit.assertEquals(7, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void loopTest2() throws Exception {
		String source = "" +
			"set a = 0;" +
			"loop (8) {" +
			"	a++;" +
			"}" +
			"unit.assertEquals (8, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void loopTest3() throws Exception {
		String source = "" +
			"set a = 0;" +
			"loop (\"8\") {" +
			"	a++;" +
			"}" +
			"unit.assertEquals(8, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void loopTest4() throws Exception {
		String source = "" +
			"set a = 0;" +
			"loop (true) {" +
			"	a++;" +
			"}" +
			"unit.assertEquals (1, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void loopTest5() throws Exception {
		String source = "" +
			"set a = 0;" +
			"set b = 7;" +
			"loop (b) {" +
			"	a++;" +
			"}" +
			"unit.assertEquals (7, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void breakTest() throws Exception {
		String source = "" +
			"set a = 0;" +
			"loop (1000) {" +
			"	if (a == 10) break;" +
			"	a++;" +
			"}" +
			"unit.assertEquals(10, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
