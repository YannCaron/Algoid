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
 <p>
 @author CARONYN
 */
public class FunctionalScopeTest {

	@Test
	public void functionalScopeTest1() throws Exception {
		String source = ""
		  + "set a = function (b) {"
		  + "	return b * 2;"
		  + "};"
		  + "set c = a(1);"
		  + "print (c);"
		  + "unit.assertEquals (2, c);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionalScopeTest2() throws Exception {
		String source = ""
		  + "set a = function (b) {"
		  + "	{"
		  + "		return b * 3;"
		  + "	}"
		  + "};"
		  + "set c = a(1);"
		  + "print (c);"
		  + "unit.assertEquals (3, c);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionalScopeTest3() throws Exception {
		String source = ""
		  + "set a = function (b) {"
		  + "	5;"
		  + "};"
		  + "set c = a(1);"
		  + "print (c);"
		  + "unit.assertNull(c);"
		  + "";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionalScopeTest4() throws Exception {
		String source = ""
		  + "set a = function (b) {"
		  + "	set d = function (e, f){"
		  + "		return e + f;"
		  + "	};"
		  + "return d;"
		  + "};"
		  + "set c = a(1);"
		  + "set d = c(2, 3);"
		  + "print (d);"
		  + "unit.assertEquals (al.types.FUNCTION, c.getType());"
		  + "unit.assertEquals (5, d);";
		//+ "test();" + '\n';

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void functionalScopeTest5() throws Exception {
		String source = ""
		  + ""
		  + "set f = function () {"
		  + "	set b = math.random(7);"
		  + "	return function (p) {"
		  + "		return p * 5;"
		  + "	}"
		  + "};"
		  + ""
		  + "set g = function () {"
		  + "	set a = 5;"
		  + "	print (f()(a));"
		  + "};"
		  + ""
		  + "g();"
		  + "";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
