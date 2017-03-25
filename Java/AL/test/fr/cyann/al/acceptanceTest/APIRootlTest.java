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
 * The EvalTest class.<br>
 * creation date : 8 mai 2012.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class APIRootlTest {

	@Test
	public void Eval1Test() {
		String source = "" +
				"util.eval (\"set a = 2 + 2;\");" +
				"print (\"result = \" .. a);" +
				"unit.assertEquals (4, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Eval2Test() {
		String source = "" +
				"set a;" +
				"loop (10) {" +
				"	util.eval (\"a++\");" +
				"}" +
				"print (\"result = \" .. a);" +
				"unit.assertEquals (10, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Exists1Test() {
		String source = "" +
				"unit.assertFalse(exists(toto));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Exists2Test() {
		String source = "" +
				"set toto;" +
				"unit.assertTrue(exists(toto));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Exists3Test() {
		String source = "" +
				"{ set toto; }" +
				"unit.assertFalse(exists(toto));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

}
