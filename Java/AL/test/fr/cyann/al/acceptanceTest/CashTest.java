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
public class CashTest {
	
	@Test
	public void rootScopeTest() {
		String source = "" +
			"debug.printScope();" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
	@Test
	public void staticScopeTest() {
		String source = "" +
			"set o = object () {" +
			"	set a = 10;" +
			"};" +
			"" +
			"set a = 7;" +
			"" +
			"set getA = function (j) {" +
			"	set i;" +
			"	i = 10 + j / math.random(10);" +
			"	return a;" +
			"};" +
			"set o.setA = function () {};" +
			"set o.getA = getA;" +
			"" +
			"unit.assertEquals (7, getA(5));" +
			"unit.assertEquals (10, o.getA(5));" +
			"" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

}
