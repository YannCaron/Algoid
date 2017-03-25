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
public class ForTest {

	private float javaFor() {
		float b = 0F;
		for (float a = 0F; a < 5F; a++) {
			b++;
		}

		return b;
	}

	@Test
	public void forTest1() throws Exception {
		String source = "" +
			"set b=0;" +
			"set c=0;" +
			"for (set a = 0; a < 5; a++) {" +
			"	c++;" +
			"	if (a == 4) b=c;" +
			"}" +
			"unit.assertNotExists (a);" +
			"unit.assertEquals(" + javaFor() + ", b);" +
			"unit.assertEquals(" + javaFor() + ", c);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void forTest2() throws Exception {
		String source = "" +
			"set b=0;" +
			"for (set a = 0; a < 5;) {" +
			"	b++;" +
			"	a++;" +
			"}" +
			"unit.assertNotExists (a);" +
			"unit.assertEquals(" + javaFor() + ", b);" +
			"";
		AL.execute(new Syntax(), new UnitTestRuntime(), source);

	}

	@Test
	public void forTest3() throws Exception {
		String source = "" +
			"set a=0;" +
			"set b=0;" +
			"for (;;) {" +
			"	b++;" +
			"	a++;" +
			"	print (a);" +
			"	if (a >= 5) break;" +
			"}" +
			"unit.assertEquals(" + javaFor() + ", a);" +
			"unit.assertEquals(" + javaFor() + ", b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);


	}

	@Test
	public void forTest4() throws Exception {
		String source = "" +
			"set b=0;" +
			"for(set a=0;a<5;a++)" +
			"	for(set c=0;c<5;c++){" +
			"		b++;" +
			"	}" +
			"unit.assertEquals (25, b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);


	}

	@Test
	public void forTest5() throws Exception {
		String source = "" +
			"set b=0;" +
			"set a=0;" +
			"for (a=0; a<5; a++) b++;" +
			"unit.assertEquals(" + javaFor() + ", b);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);


	}
}
