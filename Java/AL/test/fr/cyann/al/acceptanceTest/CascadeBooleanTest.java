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
public class CascadeBooleanTest {

	@Test
	public void devTes1() throws Exception {
		String source = "" +
				"set a = false;" +
				"print (a.not());" +
				"unit.assertFalse(a)" +
				"unit.assertFalse(false.not().and(false.and(true)));" +
				"a.not();" +
				"unit.assertFalse(a)" +
				"a = a.not();" +
				"unit.assertTrue(a)" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void devTest2() throws Exception {
		String source = "" +
				"unit.assertEquals (7, true.ifTrue(function () {" +
				"	return 7;" +
				"}));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void typeTest() throws Exception {
		String source = "" +
				"unit.assertEquals(al.types.BOOL, true.getType());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isTest() throws Exception {
		String source = "" +
				"unit.assertTrue(true.is(al.types.BOOL));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isNull() throws Exception {
		String source = "" +
				"unit.assertFalse(true.isNull());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
				"unit.assertTrue(true.equals(true));" +
				"unit.assertFalse(false.equals(true));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void notTest() throws Exception {
		String source = "" +
				"" +
				"unit.assertFalse(true.not());" +
				"unit.assertTrue(true.not().not());" +
				"unit.assertFalse(true.not().not().not());" +
				"unit.assertTrue(true.not().not().not().not());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void notvarTest() throws Exception {
		String source = "" +
				"set b = true;" +
				"unit.assertFalse(b.not());" +
				"unit.assertTrue(b.not().not());" +
				"unit.assertFalse(true.not().not().not());" +
				"unit.assertTrue(true.not().not().not().not());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void andTest() throws Exception {
		String source = "" +
				"" +
				"unit.assertTrue(true.and(true));" +
				"unit.assertFalse(true.and(false));" +
				"unit.assertFalse(false.and(true));" +
				"unit.assertFalse(false.and(false));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void orTest() throws Exception {
		String source = "" +
				"" +
				"unit.assertTrue(true.or(true));" +
				"unit.assertTrue(true.or(false));" +
				"unit.assertTrue(false.or(true));" +
				"unit.assertFalse(false.or(false));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void xorTest() throws Exception {
		String source = "" +
				"" +
				"unit.assertFalse(true.xor(true));" +
				"unit.assertTrue(true.xor(false));" +
				"unit.assertTrue(false.xor(true));" +
				"unit.assertFalse(false.xor(false));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void xnorTest() throws Exception {
		String source = "" +
				"" +
				"unit.assertTrue(true.xor(true).not());" +
				"unit.assertFalse(true.xor(false).not());" +
				"unit.assertFalse(false.xor(true).not());" +
				"unit.assertTrue(false.xor(false).not());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ifTrueTest() throws Exception {
		String source = "" +
				"" +
				"set a = 0;" +
				"set f = function () {" +
				"	a++;" +
				"};" +
				"" +
				"true.ifTrue(f);" +
				"(!false).ifTrue(f);" +
				"" +
				"unit.assertEquals(2, a);" +
				"" +
				"false.ifTrue(f);" +
				"(!true).ifTrue(f);" +
				"" +
				"unit.assertEquals(2, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ifTrue2Test() throws Exception {
		String source = "" +
				"" +
				"set a = 0;" +
				"set f = function () {" +
				"	a++;" +
				"};" +
				"" +
				"(true && true).ifTrue(f);" +
				"" +
				"unit.assertEquals(1, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void ifFalseTest() throws Exception {
		String source = "" +
				"" +
				"set a = 0;" +
				"set f = function () {" +
				"	a++;" +
				"};" +
				"" +
				"false.ifFalse(f);" +
				"(!true).ifFalse(f);" +
				"" +
				"unit.assertEquals(2, a);" +
				"" +
				"true.ifFalse(f);" +
				"(!false).ifFalse(f);" +
				"" +
				"unit.assertEquals(2, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void misc1Test() throws Exception {
		String source = "" +
				"" +
				"set a = false;" +
				"set f = function () {" +
				"	return !a;" +
				"};" +
				"" +
				"unit.assertTrue(a.ifFalse(f));" +
				"" +
				"unit.assertEquals(false, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void misc2Test() throws Exception {
		String source = "" +
				"" +
				"set a = false;" +
				"set f = function () {" +
				"	return !a;" +
				"};" +
				"" +
				"a.ifFalse(f).ifTrue(f);" +
				"" +
				"unit.assertEquals(false, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void whileTrue1Test() throws Exception {
		String source = "" +
				"set a;" +
				"" +
				"(a < 7).whileDo(function () {" +
				"	a++;" +
				"}).ifFalse(function () {" +
				"	a++;" +
				"});" +
				"" +
				"unit.assertEquals(8, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void untilFalseTest() throws Exception {
		String source = "" +
				"set a;" +
				"" +
				"(a > 7).untilDo(function () {" +
				"	a++;" +
				"});" +
				"" +
				"unit.assertEquals(8, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void whileTrue2Test() throws Exception {
		String source = "" +
				"set a;" +
				"" +
				"set f = function (e) {" +
				"	e(function () {" +
				"		a++;" +
				"	}).ifFalse(function () {" +
				"		a++;" +
				"	});" +
				"};" +
				"" +
				"f((a < 7).whileDo);" +
				"" +
				"unit.assertEquals(8, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void untilTrue2Test() throws Exception {
		String source = "" +
				"set a;" +
				"" +
				"set f = function (e) {" +
				"	e(function () {" +
				"		a++;" +
				"	}).ifFalse(function () {" +
				"		a++;" +
				"	});" +
				"};" +
				"" +
				"f((a > 7).untilDo);" +
				"" +
				"unit.assertEquals(8, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
