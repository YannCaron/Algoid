/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class ExceptionTest {

	@Test
	public void Exception1Test() throws Exception {
		String source = "" +
			"true.and (function () {});" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (ALRuntimeException ex) {
			System.out.println(ex);
			assertEquals(ex.getToken().getText(), "function");
		}

	}

	@Test
	public void Exception2Test() throws Exception {
		String source = "" +
			"set f = function () {};" +
			"1 + f;" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (ALRuntimeException ex) {
			System.out.println(ex);
			assertEquals(ex.getToken().getText(), "f");
		}

	}
	
	@Test
	public void Exception3Test() throws Exception {
		String source = "" +
			"1 + f;" +
			"";

		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);
		} catch (ALRuntimeException ex) {
			System.out.println(ex);
			assertEquals(ex.getToken().getText(), "f");
		}

	}
}
