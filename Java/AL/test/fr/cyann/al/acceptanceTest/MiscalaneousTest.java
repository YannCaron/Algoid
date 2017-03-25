/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class MiscalaneousTest {

	@Test
	public void functionIncrementTest1() throws Exception {
		String source = "" +
				"set a = function (b) {" +
				"	set d = function (e, f){" +
				"		return e + f;" +
				"	};" +
				"return d;" +
				"};" +
				"set c = a(1);" +
				"set d = (c(2, 3)++)++;" +
				"unit.assertEquals (al.types.FUNCTION, c.getType());" +
				"unit.assertEquals (7, d);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void functionIncrementTest2() {
		String source = "" +
				"set a = function (b) {" +
				"	set d = function (e, f){" +
				"		return e + f;" +
				"	};" +
				"return d;" +
				"};" +
				"set c = a(1)++;" +
				"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
			fail("Exception expected");
		} catch (RuntimeException ex) {
			System.out.println("Expected exception : " + ex);
		}
	}

	@Test
	public void ObjectTest() throws Exception {
		String source = "" +
				"set o = object () {" +
				"	set a = 5;" +
				"	set op = function () {" +
				"		a *= 2;" +
				"	};" +
				"};" +
				"" +
				"o.op();" +
				"set a = o.a;" +
				"o.op();" +
				"set b = o.a;" +
				"" +
				"unit.assertEquals (10, a);" +
				"unit.assertEquals (20, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void AssignationTest() throws Exception {
		String source = "" +
				"set a = 5;" +
				"set op = function () {" +
				"	a *= 2;" +
				"};" +
				"" +
				"op();" +
				"set b = a;" +
				"op();" +
				"set d = a;" +
				"" +
				"unit.assertEquals (10, b);" +
				"unit.assertEquals (20, d);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Dynamic1Test() {
		String source = "" +
				"set val = function () {" +
				"	return object () {" +
				"		set a = function () {" +
				"			return 7;" +
				"		};" +
				"	};" +
				"}().a();" +
				"" +
				"unit.assertEquals (7, val);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Dynamic2Test() throws Exception {
		String source = "" +
				"set o = object() {" +
				"	set r;" +
				"};" +
				"set a = array {{0, new o}, {1, new o}};" +
				"" +
				"set a[0][1].f = function () {" +
				"	r = 7;" +
				"};" +
				"" +
				"a[0][1].f();" +
				"set result = a[0][1].r;" +
				"" +
				"unit.assertEquals (7, result);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Parser1Test() {
		String source = "" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Parser2Test() {
		String source = "" +
				";";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	/*
	 @Test
	 public void SmallTalkTest1() throws Exception {
	 String source = ""
	 + "true.print();"
	 + "";

	 Scope<MutableVariant> test = AL.execute(new Syntax(), new JVMRuntime(), source).getScopeManager().getCurrent();
	 System.out.println(ASTBuilder.getInstance().toStringTree());
	 }*/
}
