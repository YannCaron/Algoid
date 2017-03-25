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
 *
 * @author CARONYN
 */
public class ObjectPatternTest {

	@Test
	public void templateMethod1Test() {
		String source = "" +
			"set tm = object () {" +
			"	set s = \"\";" +
			"	set m1 = function () {" +
			"		s = s.append(\"before\");" + "\n" +
			"		m2();" +
			"		s = s.append(\"after\");" + "\n" +
			"	};" + "\n" +
			"" + "\n" +
			"	set m2 = function () {" + "\n" +
			"		s = s.append(\" treatment \");" + "\n" +
			"	};" + "\n" +
			"};" + "\n" +
			"" +
			"set o = object (tm) {" +
			"	set m2 = function () {" +
			"		s = s.append(\" another \");" + "\n" +
			"	};" +
			"};" +
			"" +
			"tm.m1();" +
			"unit.assertEquals (\"before treatment after\", tm.s);" + "\n" +
			"o.m1();" +
			"unit.assertEquals (\"before another after\", o.s);" + "\n" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void templateMethod2Test() {
		String source = "" +
			"set tm = object () {" + "\n" +
			"	set s = \"\";" +
			"	set m1 = function () {" + "\n" +
			"		s = s.append(\"before\");" + "\n" +
			"		m2();" + "\n" +
			"		s = s.append(\"after\");" + "\n" +
			"	};" + "\n" +
			"" + "\n" +
			"	set m2 = function () {" + "\n" +
			"		s = s.append(\" treatment \");" + "\n" +
			"	};" + "\n" +
			"};" + "\n" +
			"" +
			"set o = object (tm) {" +
			"" +
			"	set entry = function () {" +
			"		m1();" +
			"	};" +
			"" +
			"	set m2 = function () {" +
			"		s = s.append(\" another \");" + "\n" +
			"	};" +
			"};" +
			"" +
			"tm.m1();" +
			"unit.assertEquals (\"before treatment after\", tm.s);" + "\n" +
			"o.entry();" +
			"unit.assertEquals (\"before another after\", o.s);" + "\n" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Aspect1Test() {
		String source = "" +
			"set p = object () {" +
			"	set pm = function (){" +
			"	};" +
			"};" +
			"" +
			"set o = object (p) {" +
			"	set om = function () {" +
			"	};" +
			"};" +
			"" +
			"set deco = function (f) {};" +
			"" +
			"o.pm = o.pm.decorate(deco);" +
			"" +
			"set q = new o;" +
			"q.om = q.pm.decorate(deco);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
