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
public class WhileTest {

	@Test
	public void whileTest1() throws Exception {
		String source = "" +
			"set a = 0;" +
			"set b = 0;" +
			"while (a < 5) {" +
			"	b = a * a;" +
			"	a = a + 1;" +
			"}" +
			"unit.assertEquals (5, a);" +
			"unit.assertEquals (16, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void whileTest2() throws Exception {
		String source = "" +
			"set b = 0;" +
			"set a = 0;" +
			"while (a++ < 5) {" +
			"	b++;" +
			"}" +
			"unit.assertEquals (4, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	
	@Test
	public void whileTest3() throws Exception {
		String source = "" +
			"set b = 0;" +
			"set a = 0;" +
			"while ((a+=1) < 5) {" +
			"	b++;" +
			"}" +
			"unit.assertEquals (4, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
	@Test
	public void whileScopeTest() throws Exception {
		String source = "" +
			"set b = 0;" +
			"set c = 0;" +
			"while (c < 5) {" +
			"	set a;" +
			"	b = c * c;" +
			"	a++;" +
			"	c++;" +
			"}" +
			"unit.assertNotExists (a);" +
			"unit.assertEquals (16, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
