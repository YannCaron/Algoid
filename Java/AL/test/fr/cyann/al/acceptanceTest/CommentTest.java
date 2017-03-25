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
 * @author caronyn
 */
public class CommentTest {

	@Test
	public void comment1Test() {
		String source = "" +
			 "// comment = 5;\n" +
			 "for (set i = 0; i < 5; i++) {" +
			 "	// print(i);\n" +
			 "}" //+ "unit.assertNull(comment);"
			+
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void comment2Test() {
		String source = "" +
			 "for (set i = 0; i<5; i++) {" +
			 "	/*print(i);*/" +
			 "}" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void comment3Test() {
		String source = "" +
			 "for (set i = 0; i<5; i++) {" +
			 "	/* / * owuoiudfler */" +
			 "}" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void comment4Test() {
		String source = "" +
			 "for (set i = 0; i<5; i++) {" +
			 "	/* /* owuoiudfler */" +
			 "}" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void comment7Test() {
		String source = "" +
			 "for (set i = 0; i<5 /* comment here */; i++) {" +
			 "	/* /* owuoiudfler */" +
			 "}" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void comment8Test() {
		String source = "" +
			"set a = array {" +
			"	// test\n" +
			"	7" +
			"};" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
		@Test
	public void comment9Test() {
		String source = "" +
			"set f = function (/* no args */) {" +
			"	// test\n" +
			"	7" +
			"};" +
			 "";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
