/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.utils.UnitTesting;
import org.junit.Test;

/**
 *
 * @author CARONYN
 */
public class ObjectReferenceTest {
//TODO

	@Test
	public void thisObject() {
		String source = "" +
				"set o = object() {" +
				"		set a;" +
				"		set getA = function() {" +
				"				return this.a;" +
				"		};" +
				"		set setA = function(a) {" +
				"				this.a = a;" +
				"		};" +
				"		set clone = function(a) {" +
				"				set clone = new this;" +
				"				clone.setA(a);" +
				"				return clone;" +
				"		};" +
				"};" +
				"" +
				"unit.assertEquals(7, o.clone(7).getA());";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void superObject() {
		String source = "" +
				"set p = object() {" +
				"	set a = 7;" +
				"	set getA = function () {" +
				"		return 7;" +
				"	};" +
				"};" +
				"" +
				"set o = object (p) {" +
				"	set getSuperA = function() {" +
				"		return supers[0].getA();" +
				"	};" +
				"};" +
				"" +
				"print (o.supers[0].a);" +
				"print (p);" +
				"unit.assertEquals(7, o.getSuperA());";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void supersObjects() {
		String source = "" +
				"set p = object() {" +
				"	set getA = function () {" +
				"		return 7;" +
				"	};" +
				"};" +
				"set q = object() {" +
				"	set getA = function () {" +
				"		return 8;" +
				"	};" +
				"};" +
				"" +
				"set o = object (p, q) {" +
				"	set getSuper0A = function() {" +
				"		return supers[0].getA();" +
				"	};" +
				"	set getSuper1A = function() {" +
				"		return supers[1].getA();" +
				"	};" +
				"};" +
				"" +
				"unit.assertEquals(7, o.getSuper0A());" +
				"unit.assertEquals(8, o.getSuper1A());";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void thisSuperPath() {
		String source = "" +
				"set q = object() {" +
				"	set ret7 = function () {" +
				"		return 7;" +
				"	};" +
				"};" +
				"" +
				"set p1 = object() {};" +
				"" +
				"set p2 = object(q) {};" +
				"" +
				"set o = object (p1, p2) {" +
				"	set test = function () {" +
				"		return this.supers[1].supers[0].ret7();" +
				"	};" +
				"};" +
				"" +
				"o.test();";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void newThisPath() {
		String source = "" +
				"set o = object() {" +
				"	set a;" +
				"	set getA = function () {" +
				"		return this.a;" +
				"	};" +
				"};" +
				"" +
				"o.a = 8;" +
				"set p = new o;" +
				"p.a = 7;" +
				"" +
				"print (o.getA);" +
				"print (p.getA);" +
				"" +
				"unit.assertEquals (8, o.getA());" +
				"unit.assertEquals (7, p.getA());" +
				"";


		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void thisSuperErrorPath() {
		final String source = "" +
				"set q = object() {" +
				"	set ret7 = function () {" +
				"		return 7;" +
				"	};" +
				"};" +
				"" +
				"set p1 = object() {};" +
				"" +
				"set p2 = object(q) {};" +
				"" +
				"set o = object (p1, p2) {" +
				"};" +
				"" +
				"unit.assertEquals(7, o.this.super.supers[0].ret7());";

		UnitTesting.assertException(new Runnable() {
			@Override
			public void run() {
				AL.execute(new Syntax(), new UnitTestRuntime(), source);
			}
		});
	}

	@Test
	public void thisSuperError2Path() {
		final String source = "" +
				"set m = object() {" +
				"	set ret7 = function() {" +
				"		return 7;" +
				"	};" +
				"};" +
				"set n = object(m) {};" +
				"set o = object(n) {};" +
				"" +
				"unit.assertEquals(7, o.this.notExists.ret7());";

		UnitTesting.assertException(new Runnable() {
			@Override
			public void run() {
				AL.execute(new Syntax(), new UnitTestRuntime(), source);
			}
		});
	}

}
