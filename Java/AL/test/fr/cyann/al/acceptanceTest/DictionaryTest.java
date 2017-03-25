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
public class DictionaryTest {

	@Test
	public void dictionaryTest1() throws Exception {
		String source = "" +
				"set a = array {\"one\" : 1, \"two\" : 2, 3, \"four\" : 4};" + "\n" +
				"unit.assertEquals (1, a[\"one\"]);" + "\n" +
				"unit.assertEquals (2, a[\"two\"]);" + "\n" +
				"unit.assertEquals (nil, a[\"three\"]);" + "\n" +
				"unit.assertEquals (4, a[\"four\"]);" + "\n" +
				"unit.assertEquals (nil, a[nil]);" + "\n" +
				"unit.assertEquals (1, a[0]);" + "\n" +
				"unit.assertEquals (2, a[1]);" + "\n" +
				"unit.assertEquals (3, a[2]);" + "\n" +
				"unit.assertEquals (4, a[3]);" + "\n" +
				"unit.assertEquals (nil, a[4]);" + "\n" +
				"" +
				"print (a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void dictionaryTest2() throws Exception {
		String source = "" +
				"" +
				"set x = function () {" +
				"};" +
				"set y = function () {" +
				"	return 7;" +
				"};" +
				"" +
				"set z = x;" +
				"set a = array {1, x : y, 1 : 3, x};" +
				"print (a);" +
				"unit.assertEquals(3, a[1]);" + "\n" +
				"unit.assertEquals(y, a[z]);" + "\n" +
				"unit.assertEquals(1, a[0]);" + "\n" +
				"unit.assertEquals(x, a[3]);" + "\n" +
				"unit.assertEquals(x, a[a[1]]);" + "\n" +
				"unit.assertEquals(y, a[a[3]]);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void dictionaryTest3() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 0, \"b\" : 1, 8 + 7 : 2, \"d\" : 3};" +
				"unit.assertEquals(0, a[\"a\"]);" +
				"unit.assertEquals(1, a[\"b\"]);" +
				"" +
				"a[\"a\"] = 85;" +
				"unit.assertEquals(85, a[\"a\"]);" +
				"unit.assertEquals(2, a[15]);" +
				"" +
				"a[\"g\"] = 8;" +
				"print (a);" +
				"unit.assertEquals(85, a[0]);" +
				"unit.assertEquals(8, a[\"g\"]);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
