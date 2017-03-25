/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.runtime;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;

/**
 *
 * @author CARONYN
 */
public class ALTest {

	@Test
	public void getRootTreeTest() throws Exception {
		String source = ""
			+ "al.allObjects().each(print)" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void getScopeTreeTest() throws Exception {
		String source = ""
			+ "set f = function () {"
			+ "	set a = object(){};"
			+ "	al.allLocalObjects().each(print);"
			+ "	unit.assertEquals (\"a\", al.allLocalObjects()[0]);"
			+ "};"
			+ ""
			+ "f();"
			+ "" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

}
