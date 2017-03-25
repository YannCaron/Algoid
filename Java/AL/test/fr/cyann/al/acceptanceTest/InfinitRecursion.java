/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class InfinitRecursion {

	@Test
	public void testInfinit1() throws Exception {

		String source = ""
				+ "set f = function () {"
				+ "	f();"
				+ "};"
				+ ""
				+ "f();"
				+ "";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
			fail("Stack overflow error expected !");
		} catch (StackOverflowError err) {
			System.out.println(err);
		}
	}

	@Test
	public void testInfinit2() throws Exception {

		String source = ""
				+ "set f = function () {"
				+ "	g();"
				+ "};"
				+ ""
				+ "set g = function () {"
				+ "	f();"
				+ "};"
				+ ""
				+ "f();"
				+ "";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
			fail("Stack overflow error expected !");
		} catch (StackOverflowError err) {
			System.out.println(err);
		}
	}
}