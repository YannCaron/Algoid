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
 * The EvalTest class.<br> creation date : 8 mai 2012.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class CascadeStringTest {

	@Test
	public void typeTest() throws Exception {
		String source = "" +
			"unit.assertEquals(al.types.STRING, \"\".getType());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isEmptyTest() throws Exception {
		String source = "" +
			"unit.assertTrue (\"\".isEmpty());" +
			"unit.assertFalse (\"I am am Algoid\".isEmpty());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
			"set a = \"abc\";" +
			"set b = \"abc\";" +
			"set c = \"defg\";" +
			"unit.assertTrue(a.equals(b));" +
			"unit.assertFalse(a.equals(c));" +
			"unit.assertFalse(b.equals(c));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add1Test() throws Exception {
		String source = "" +
			"set n = \"abc\";" +
			"n = n.add(1);" +
			"" +
			"unit.assertEquals (array {\"abc\", 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add2Test() throws Exception {
		String source = "" +
			"set n = \"abc\";" +
			"n.add(1, 3);" +
			"" +
			"unit.assertEquals (array {\"abc\", nil, nil, 1}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll1Test() throws Exception {
		String source = "" +
			"set n = \"abc\";" +
			"n.addAll(array {1, \"defg\"});" +
			"" +
			"unit.assertEquals (array {\"abc\", 1, \"defg\"}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll2Test() throws Exception {
		String source = "" +
			"set n = \"abc\";" +
			"n.addAll(array {1, 2}, 3);" +
			"" +
			"unit.assertEquals (array {\"abc\", nil, nil, 1, 2}, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void lengthTest() throws Exception {
		String source = "" +
			"set n = \"abcdefg\".length();" +
			"unit.assertEquals (7, n);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void containsTest() throws Exception {
		String source = "" +
			"unit.assertTrue(\"abcdefg\".contains(\"cd\"));" +
			"unit.assertFalse(\"abcdefg\".contains(\"gh\"));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void concatTest() throws Exception {
		String source = "" +
			"set s = \"abc\".concat(\"defg\");" +
			"unit.assertEquals (\"abcdefg\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void getCharTest() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"unit.assertEquals (\"H\", s.getChar(0));" +
			"unit.assertEquals (\"a\", s.getChar(7));" +
			"unit.assertEquals (\"m\", s.getChar(8));" +
			"unit.assertEquals (\"!\", s.getChar(s.length() - 1));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void split1Test() throws Exception {
		String source = "" +
			"set a = \"1 2 3 4 5\".split(\" \");" +
			"unit.assertEquals (array {\"1\", \"2\", \"3\", \"4\", \"5\"}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void split2Test() throws Exception {
		String source = "" +
			"set s = \"1 2 3 4 5\".split(\" \").join(function (a, b) {return a .. \":\" .. b;});" +
			"unit.assertEquals (\"1:2:3:4:5\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void replaceAt1Test() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"s = s.replaceAt (\"umb\", 7);" +
			"print (s);" +
			"unit.assertEquals (\"Hi my numb is Algoid !\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void replaceAt2Test() throws Exception {
		String source = "" +
			"set s = \"Hi my na\";" +
			"s = s.replaceAt (\"umber is 7\", 7);" +
			"print (s);" +
			"unit.assertEquals (\"Hi my number is 7\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove1Test() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"s = s.remove (0);" +
			"unit.assertEquals (\"i my name is Algoid !\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove2Test() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"s = s.remove (2, 11);" +
			"unit.assertEquals (\"Hi Algoid !\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove3Test() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"s = s.remove (2, 100);" +
			"unit.assertEquals (\"Hi\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void indexOfTest() throws Exception {
		String source = "" +
			"set s = \"Hi my name is Algoid !\";" +
			"unit.assertEquals (0, s.indexOf(\"Hi\"));" +
			"unit.assertEquals (3, s.indexOf(\"my\"));" +
			"unit.assertEquals (6, s.indexOf(\"name\"));" +
			"unit.assertEquals (11, s.indexOf(\"is\"));" +
			"unit.assertEquals (14, s.indexOf(\"Algoid\"));" +
			"unit.assertEquals (nil, s.indexOf(\"not found\"));" +
			"unit.assertEquals (nil, s.indexOf(\"is\", 14));" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each1Test() throws Exception {
		String source = "" +
			"set i = 0;" +
			"set s;" +
			"" +
			"\"hi I am algoid!\".each(function (char, index) {" +
			"	print(index .. \" \" .. char);" +
			"	i += index;" +
			"	s = char;" +
			"});" +
			"" +
			"unit.assertEquals(105, i);" +
			"unit.assertEquals(\"!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

		@Test
	public void each2Test() throws Exception {
		String source = "" +
			"set i = 0;" +
			"set s;" +
			"" +
			"\"hi I am algoid!\".each(function (char, index) {" +
			"	print(index .. \" \" .. char);" +
			"	i += index;" +
			"	s = char;" +
			"}, 3);" +
			"" +
			"unit.assertEquals(30, i);" +
			"unit.assertEquals(\"id!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each3Test() throws Exception {
		String source = "" +
			"" +
			"set s = \"hi I am algoid!\".each(function (char, index) {" +
			"	return char .. \"0\";" +
			"});" +
			"" +
			"unit.assertEquals (\"h0i0 0I0 0a0m0 0a0l0g0o0i0d0!0\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}


	@Test
	public void each4Test() throws Exception {
		String source = "" +
			"" +
			"set s = \"hi I am algoid!\".each(function (char, index) {" +
			"	return char .. \"0\";" +
			"}, 4);" +
			"" +
			"unit.assertEquals (\"hi I0 am 0algo0id!0\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void splitAtTest() throws Exception {
		String source = "" +
			"set a = \"hi I am algoid!\".splitAt (array {3, 5, 8, 50});" +
			"" +
			"unit.assertEquals(array {\"hi \", \"I \", \"am \", \"algoid!\"}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void uppercaseTest() throws Exception {
		String source = "" +
			"set s = \"Hi I Am Algoid!\".upper();" +
			"" +
			"unit.assertEquals(\"HI I AM ALGOID!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void lowercaseTest() throws Exception {
		String source = "" +
			"set s = \"Hi I Am Algoid!\".lower();" +
			"" +
			"unit.assertEquals(\"hi i am algoid!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void append1Test() throws Exception {
		String source = "" +
			"set s = \"Hi I am\".append(\" algoid!\");" +
			"" +
			"unit.assertEquals(\"Hi I am algoid!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void append2Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid!\".append(\"I \", 3);" +
			"" +
			"unit.assertEquals(\"Hi I am algoid!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void replaceTest() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".replace(\"algoid\", \"android\");" +
			"" +
			"unit.assertEquals(\"Hi am android, the android programm!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void appendSepTest() throws Exception {
		String source = "" +
			"set s = \"Hi I am\".appendSep(\"algoid!\", \" \");" +
			"" +
			"unit.assertEquals(\"Hi I am algoid!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void subString1Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".subString(12);" +
			"" +
			"unit.assertEquals(\", the algoid programm!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void subString2Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".subString(0, 12);" +
			"" +
			"unit.assertEquals(\"Hi am algoid\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void subStringOf1Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".subStringOf(\"am\", \"the\");" +
			"" +
			"unit.assertEquals(\" algoid, \", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void subStringOf2Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".subStringOf(\"am\");" +
			"" +
			"unit.assertEquals(\" algoid, the algoid programm!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void subStringOf3Test() throws Exception {
		String source = "" +
			"set s = \"Hi am algoid, the algoid programm!\".subStringOf(\"test\");" +
			"" +
			"unit.assertEquals(\"Hi am algoid, the algoid programm!\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void trimTest() throws Exception {
		String source = "" +
			"set s = \"    algoid       \".trim();" +
			"" +
			"unit.assertEquals(\"algoid\", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void createTest() throws Exception {
		String source = "" +
			"set s = \"\".create(\"Hi \", 4);" +
			"" +
			"unit.assertEquals(\"Hi Hi Hi Hi \", s);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void countTest() throws Exception {
		String source = "" +
			"set i = \"Hi I am algoid, the programm algoid, yes it is me algoid!\".count(\"algoid\");" +
			"set J = \"Hi I am algoid, the programm algoid, yes it is me algoid!\".count(\"algoiR\");" +
			"" +
			"unit.assertEquals(3, i);" +
			"unit.assertEquals(0, J);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
	
	@Test
	public void splitTest() throws Exception {
		String source = "" +
			"set s = \"my string\";" +
			"" +
			"set a = s.split(\" \");" +
			"" +
			"unit.assertEquals (\"my string\", s);" +
			"unit.assertEquals (array {\"my\", \"string\"}, a);" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
		
	@Test
	public void toAsciiTest() throws Exception {
		String source = "" +
			"set s = \"algoid\";" +
			"set a = s.encodePoint();" +
			"" +
			"unit.assertEquals (\"algoid\", s);" +
			"unit.assertEquals (array {97, 108, 103, 111, 105, 100}, a);" +
			"" +
			"unit.assertEquals (109, \"m\".encodePoint());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
			
	@Test
	public void fromAsciiTest() throws Exception {
		String source = "" +
			"set a = array {97, 108, 103, 111, 105, 100};" +
			"set s = a.decodePoint();" +
			"" +
			"unit.assertEquals (\"algoid\", s);" +
			"unit.assertEquals (array {97, 108, 103, 111, 105, 100}, a);" +
			"unit.assertEquals (\"m\", (109).decodePoint());" +
			"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
