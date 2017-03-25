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
public class LambdaTest {

	@Test
	public void lambdaTest1() {
		String source = ""
		  + "set res = 0;"
		  + "set f = function (g) {"
		  + "	g();"
		  + "};"
		  + ""
		  + "f ({res = 7});"
		  + "unit.assertEquals(7, res);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void lambdaTest2() {
		String source = ""
		  + "set res = 0;"
		  + "set f = function (g) {"
		  + "	g(7);"
		  + "};"
		  + ""
		  + "f ((v){res = v});"
		  + "unit.assertEquals(7, res);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void lambdaTest3() {
		String source = ""
		  + "set a = array{}.create(5, { return 8})"
		  + "unit.assertEquals(array {8, 8, 8, 8, 8}, a)"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void lambdaTest4() {
		String source = ""
		  + "set a = array{}.create(5, (v) { return v * 2})"
		  + "unit.assertEquals(array {0, 2, 4, 6, 8}, a)"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}

	@Test
	public void notALambdaTest() {
		String source = ""
		  + "set res = 0;"
		  + "set f = function (v) {"
		  + "	res = v;"
		  + "};"
		  + ""
		  + "f ((7+7));"
		  + "unit.assertEquals(14, res);"
		  + "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);;
	}
}
