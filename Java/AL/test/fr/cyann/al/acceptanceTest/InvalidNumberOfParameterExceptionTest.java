/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.exception.InvalidNumberOfParameterException;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class InvalidNumberOfParameterExceptionTest {
//TODO
	// TODO reflechir si call strict ou pas
	@Test
	public void textException1() {

		String source = "" +
			"set f = function (a, b) {" +
			"	print (a);" +
			"	print (b);" +
			"};" +
			"f (1);" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (InvalidNumberOfParameterException ex) {
			fail("expected not exception");
			//System.out.println(ex.getMessage());
			//assertEquals("Invalid number of parameter for function f(), expecter: 2, found: 1 !", ex.getMessage());
		}
	}

	@Test
	public void textException2() {

		String source = "" +
			"set f = function (a, b) {" +
			"	print (a);" +
			"	print (b);" +
			"};" +
			"f (1, 2, 3);" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (InvalidNumberOfParameterException ex) {
			fail("expected not exception");
//			System.out.println(ex.getMessage());
//			assertEquals("Invalid number of parameter for function f(), expecter: 2, found: 3 !", ex.getMessage());
		}
	}

	@Test
	public void textException3() {

		String source = "" +
			"set f = function (a, b) {" +
			"	print (a);" +
			"	print (b);" +
			"};" +
			"f (1, 2);" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (Exception ex) {
			fail("expected not exception");
		}
	}
}
