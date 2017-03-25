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
 * The EvalTest class.<br> creation date : 8 mai 2012.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class CascadeVoidTest {

	@Test
	public void typeTest() throws Exception {
		String source = "" +
			"unit.assertEquals(al.types.VOID, nil.getType());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

		@Test
	public void isTest() throws Exception {
		String source = "" +
			"unit.assertTrue(nil.is(al.types.VOID));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isNullTest() throws Exception {
		String source = "" +
			"set a;" +
			"unit.assertTrue(a.isNull());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
			"set a;" +
			"set b;" +
			"set c = true;" +
			"unit.assertTrue(a.equals(b));" +
			"unit.assertFalse(a.equals(c));" +
			"unit.assertFalse(b.equals(c));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add1Test() throws Exception {
		String source = "" +
			"set n;" +
			"n.add(1);" +
			"" +
			"unit.assertEquals (array {1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add2Test() throws Exception {
		String source = "" +
			"set n;" +
			"n.add(1, 3);" +
			"" +
			"unit.assertEquals (array {nil, nil, nil, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll1Test() throws Exception {
		String source = "" +
			"set n;" +
			"n.addAll(array {1, 2});" +
			"" +
			"unit.assertEquals (array {1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll2Test() throws Exception {
		String source = "" +
			"set n;" +
			"n.addAll(array {1, 2}, 3);" +
			"" +
			"unit.assertEquals (array {nil, nil, nil, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
