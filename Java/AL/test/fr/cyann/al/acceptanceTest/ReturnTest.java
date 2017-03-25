
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
public class ReturnTest {

	@Test
	public void returnTest1() throws Exception {
		String source = "" +
			"set a = function (b) {" +
			"	set c = 1;" +
			"	if (b == 0) {" +
			"		c = 2;" +
			"		{" +
			"			c = 3;" +
			"			return 5;" +
			"		}" +
			"	}" +
			"};" +
			"set d = a(0);" +
			"print (a(0));" +
			"" +
			"unit.assertNotExists(c);" +
			"unit.assertEquals(5, d);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void returnTest2() throws Exception {
		String source = "" +
			"set f = function () {" +
			"	set g = function (a) {" +
			"		return a * 2;" +
			"	};" +
			"	return g;" +
			"};" +
			"set d = f()(5);" +
			"unit.assertEquals(10, d);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
	
		@Test
	public void returnTest3() throws Exception {
		String source = "" +
			"set a = 0;" +
			"set b = 0;" +
			"" +
			"set f = function () {" +
			"	loop (10) {" +
			"		a++;" +
			"		return;" +
			"		b++" +
			"	}" +
			"};" +
			"f();" +
			"unit.assertEquals(1, a);" +
			"unit.assertEquals(0, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
