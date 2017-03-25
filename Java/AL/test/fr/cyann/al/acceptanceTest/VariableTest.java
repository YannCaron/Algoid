/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class VariableTest {

	@Test
	public void var1Test() {

		String source = "" +
				"set a;" +
				"set b = 7;" +
				"a = b++ - 7;" +
				"print (a);" +
				"print (b);" +
				"print ()";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void functionTest() {

		String source = "" +
				"set add = function (a, b) {" +
				"	return a + b;" +
				"};" +
				"" +
				"set sub = function (a, b) {" +
				"	return a - b;" +
				"};" +
				"" +
				"set a = add (7, sub (8, 3));" +
				"print (a);" +
				"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}
}
