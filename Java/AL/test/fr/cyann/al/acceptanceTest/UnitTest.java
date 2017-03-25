/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.exception.AssertException;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.jasi.builder.ASTBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CARONYN
 */
public class UnitTest {

	@Test
	public void unitTestEqualTrue() {
		String source = "" +
			"unit.assertEquals(1, 1);" +
			"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestEqualFalse() {
		String source = "" +
			"unit.assertEquals(1, 2);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestNotEqualTrue() {
		String source = "" +
			"unit.assertNotEquals(1, 2);" +
			"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestNotEqualFalse() {
		String source = "" +
			"unit.assertNotEquals(1, 1);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestNullTrue() {
		String source = "" +
			"set a;" +
			"unit.assertNull(a);" +
			"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestNullFalse() {
		String source = "" +
			"set a = 5;" +
			"unit.assertNull(a);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestExistsTrue() {
		String source = "" +
			"set a;" +
			"unit.assertExists(a);" +
			"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestExistsFalse() {
		String source = "" +
			"set a = 5;" +
			"unit.assertExists(b);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestNotExistsTrue() {
		String source = "" +
			"set a;" +
			"unit.assertNotExists(b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestNotExistsFalse() {
		String source = "" +
			"set a = 5;" +
			"unit.assertNotExists(a);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestNotNullTrue() {
		String source = "" +
			"set a = 7;" +
			"unit.assertNotNull(a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void unitTestNotNullFalse() {
		String source = "" +
			"set a;" +
			"unit.assertNotNull(a);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestTrueTrue() {
		String source = "" +
			"set a = 7;" +
			"unit.assertTrue(true);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void unitTestTrueFalse() {
		String source = "" +
			"unit.assertTrue(false);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}

	@Test
	public void unitTestFalseTrue() {
		String source = "" +
			"set a = 7;" +
			"unit.assertFalse(false);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unitTestFalseFalse() {
		String source = "" +
			"unit.assertFalse(true);" +
			"";


		try {
			AL.execute(new Syntax(), new UnitTestRuntime(), source);

			fail("Exception expected");
		} catch (AssertException ex) {
			System.out.println("Expected exception : " + ex.getMessage());
		}

	}
}
