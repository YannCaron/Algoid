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
 * <p>
 * @author Yann Caron
 * @version v0.1
 */
public class CascadeArrayTest {
//TODO

	@Test
	public void typeTest() throws Exception {
		String source = "" +
				"unit.assertEquals(al.types.ARRAY, array {}.getType());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void isEmptyTest() throws Exception {
		String source = "" +
				"unit.assertTrue (array {}.isEmpty());" +
				"unit.assertFalse (array {1, 2, 3, 4}.isEmpty());" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void equalsTest() throws Exception {
		String source = "" +
				"unit.assertTrue(array {1, 2, 3, 4}.equals(array {1, 2, 3, 4}));" +
				"unit.assertFalse(array {1, 2, 3, 4}.equals(array {1, 2, 3}));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void lengthTest() throws Exception {
		String source = "" +
				"unit.assertEquals(4, array {1, 2, 3, 4}.length());" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void get1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"unit.assertEquals(1, a.getItem(0));" + "\n" +
				"unit.assertEquals(2, a.getItem(1));" + "\n" +
				"unit.assertEquals(3, a.getItem(2));" + "\n" +
				"unit.assertEquals(4, a.getItem(3));" + "\n" +
				"unit.assertNull(a.getItem(4));" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void get2Test() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2, \"a\" : 3, \"d\" : 4};" +
				"unit.assertEquals(3, a.getItem(\"a\"));" + "\n" +
				"unit.assertEquals(4, a.getItem(\"d\"));" +
				"" +
				"print (a);" +
				"" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void getTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"unit.assertEquals(1, a.getFirst());" + "\n" +
				"unit.assertEquals(4, a.getLast());" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void set1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"a.setItem(0, 4);" +
				"a.setItem(1, 3);" +
				"a.setItem(2, 2);" +
				"a.setItem(3, 1);" +
				"unit.assertEquals(array {4, 3, 2, 1}, a);" +
				"a.setItem(4, 5);" +
				"unit.assertEquals(array {4, 3, 2, 1, 5}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void set2Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"a.setItem(\"a\", 8);" +
				"" +
				"unit.assertEquals (array {1, 2, 3, 4, 8}, a);" +
				"unit.assertEquals (8, a[\"a\"]);" +
				"print (a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add1Test() throws Exception {
		String source = "" +
				"set a = array {};" +
				"a.add(1);" +
				"a.add(2);" +
				"a.add(3);" +
				"" +
				"unit.assertEquals (array {1, 2, 3}, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void add2Test() throws Exception {
		String source = "" +
				"set a = array {};" +
				"a.add(1);" +
				"a.add(3);" +
				"a.add(2, 1);" +
				"" +
				"unit.assertEquals (array {1, 2, 3}, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void associativeAddTest() throws Exception {
		String source = "" +
				"set a = array {};" +
				"a.add(1);" +
				"a.add(3);" +
				"a.add(2, \"a\");" +
				"" +
				"print (a);" +
				"" +
				"unit.assertEquals (array {1, 3, 2}, a);" +
				"unit.assertEquals (2, a[\"a\"]);" +
				"unit.assertEquals (nil, a[\"b\"]);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3};" +
				"a.addAll(array {4, 5, 6, 7});" + "\n" +
				"" +
				"unit.assertEquals (array {1, 2, 3, 4, 5, 6, 7}, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll2Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3};" +
				"a.addAll(array {4, 5, 6, 7}, 1);" + "\n" +
				"" +
				"unit.assertEquals (array {1, 4, 5, 6, 7, 2, 3}, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void addAll3Test() throws Exception {
		String source = "" +
				"set a = array {1};" +
				"a.addAll(array {4, 5, 6, 7}, 3);" + "\n" +
				"" +
				"unit.assertEquals (array {1, nil, nil, 4, 5, 6, 7}, a);";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void clearTest() throws Exception {
		String source = "" +
				"set a = array {};" +
				"a.add(1);" +
				"a.add(2);" +
				"a.add(3);" +
				"" +
				"unit.assertEquals (array {1, 2, 3}, a);" +
				"a.clear();" +
				"unit.assertEquals (array {}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void containsTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"" +
				"unit.assertTrue (a.contains(2));" +
				"unit.assertFalse (a.contains(5));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"a.remove(2);" +
				"" +
				"unit.assertEquals (array {1, 2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove2Test() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2, \"c\" : 3, \"d\" : 4};" +
				"a.remove(\"c\");" +
				"" +
				"unit.assertEquals (array {1, 2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void remove3Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set rem = function (p, i) {" +
				"	p.remove(i);" +
				"};" +
				"rem(a, 2);" +
				"" +
				"unit.assertEquals (array {1, 2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void pop1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"unit.assertEquals (4, a.pop());" +
				"unit.assertEquals (array {1, 2, 3}, a);" +
				"unit.assertEquals (3, a.pop());" +
				"unit.assertEquals (array {1, 2}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void pop2Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"unit.assertEquals (1, a.pop(0));" +
				"unit.assertEquals (array {2, 3, 4}, a);" +
				"unit.assertEquals (2, a.pop(0));" +
				"unit.assertEquals (array {3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void pop3Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, \"c\" : 3, \"d\" : 4};" +
				"unit.assertEquals (4, a.pop(\"d\"));" +
				"unit.assertEquals (array {1, 2, 3}, a);" +
				"unit.assertEquals (3, a.pop(\"c\"));" +
				"unit.assertEquals (array {1, 2}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void indexOfTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"unit.assertEquals (1, a.indexOf(2));" +
				"unit.assertEquals (2, a.indexOf(3));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void countTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 1, 4, 7, 1, 5, 1};" +
				"unit.assertEquals (4, a.count(1));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void swap1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4}.swap(1, 2);" +
				"unit.assertEquals (array {1, 3, 2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void swap2Test() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2, \"c\" : 3, \"d\" : 4};" +
				"" +
				"unit.assertEquals (1, a[\"a\"]);" +
				"unit.assertEquals (2, a[\"b\"]);" +
				"unit.assertEquals (3, a[\"c\"]);" +
				"unit.assertEquals (4, a[\"d\"]);" +
				"" +
				"a.swap(1, 2);" +
				"unit.assertEquals (array {1, 3, 2, 4}, a);" +
				"" +
				"unit.assertEquals (1, a[\"a\"]);" +
				"unit.assertEquals (2, a[\"b\"]);" +
				"unit.assertEquals (3, a[\"c\"]);" +
				"unit.assertEquals (4, a[\"d\"]);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void cloneTest() throws Exception {
		String source = "" +
				"set a1 = array {4, 2, 1, 3};" +
				"set a2 = a1.clone().sort();" +
				"" +
				"unit.assertEquals (array {4, 2, 1, 3}, a1);" +
				"unit.assertEquals (array {1, 2, 3, 4}, a2);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void build1Test() throws Exception {
		String source = "" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"a.eachOnRow(1, b.add);" +
				"unit.assertEquals (array {4, 5, 6}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void build2Test() throws Exception {
		String source = "" +
				"set al.order.getCol = function(a, col) {" +
				"	set result = array {};" +
				"	a.eachOnCol (col, result.add);" +
				"	return result;" +
				"};" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = al.order.getCol (a, 1);" +
				"unit.assertEquals (array {2, 5, 8}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void noCloneTest() throws Exception {
		String source = "" +
				"set a1 = array {4, 2, 1, 3};" +
				"set a2 = a1.sort();" +
				"" +
				"unit.assertEquals (array {1, 2, 3, 4}, a1);" +
				"unit.assertEquals (array {1, 2, 3, 4}, a2);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void create1Test() throws Exception {
		String source = "" +
				"set a = array {}.create(5);" + "\n" +
				"unit.assertEquals(array {0, 1, 2, 3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void create2Test() throws Exception {
		String source = "" +
				"set a = array {}.create(5, function (i) {return i * 2});" + "\n" +
				"unit.assertEquals(array {0, 2, 4, 6, 8}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void findTest() throws Exception {
		String source = "" +
				"set o = object() {" +
				"	set a;" +
				"	set b;" +
				"	set clone = function (a, b) {" +
				"		set c = new this;" +
				"		c.a = a;" +
				"		c.b = b;" +
				"		return c;" +
				"	};" +
				"};" +
				"set a = array {o.clone(1, 1), o.clone(2, 2), o.clone(3, 3), o.clone(2, 4)};" +
				"" +
				"set find2 = function (item) {" +
				"	return item.a == 2;" +
				"};" +
				"unit.assertEquals(2, a.find (find2).b);" +
				"unit.assertEquals(4, a.find (find2, 2).b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void findKeyTest() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2};" +
				"" +
				"set find2 = function (item, index, key, compar) {" +
				"	return key == compar;" +
				"};" +
				"" +
				"find2.setParameter(\"compar\", \"a\");" +
				"unit.assertEquals(1, a.find (find2));" +
				"find2.setParameter(\"compar\", \"b\");" +
				"unit.assertEquals(2, a.find (find2));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each1Test() throws Exception {
		String source = "" +
				"set i = 0;" +
				"array {1, 2, 3, 4}.each(function (item) {i = i.addition(item);});" + "\n" +
				"unit.assertEquals(10, i);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each2Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4}.each(function (item) {return item * 2;});" + "\n" +
				"unit.assertEquals(array {2, 4, 6, 8}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each3Test() throws Exception {
		String source = "" +
				"set i = 1;" +
				"set a = array {1, 2, 3, 4};" +
				"a.each(function (item, index) {i += index;});" + "\n" +
				"unit.assertEquals(7, i);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each4Test() throws Exception {
		String source = "" +
				"set i = 1;" +
				"array {1, 2, 3, 4}.each(function (item, index) {i += index;});" + "\n" +
				"unit.assertEquals(7, i);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void each5Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b;" +
				"a.each(b.add);" +
				"" +
				"unit.assertEquals(array {1, 2, 3, 4}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachKeyTest() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2, 3, \"d\" : 4};" +
				"set b;" +
				"set f = function (item, index, key) {" +
				"	b.add (key);" +
				"print (a);" +
				"};" +
				"a.each(f);" +
				"" +
				"unit.assertEquals(array {\"a\", \"b\", nil, \"d\"}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachOnRow1Test() throws Exception {
		String source = "" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"a.eachOnRow(1, function (item) {" +
				"	b.add(item);" +
				"});" +
				"unit.assertEquals (array {4, 5, 6}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachOnRow2Test() throws Exception {
		String source = "" +
				"set i = 0;" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"a.eachOnRow(1, function (item, index) {" +
				"	i += index;" +
				"});" +
				"unit.assertEquals (3, i);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachOnRowKeyTest() throws Exception {
		String source = "" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{\"a\" : 4, 5, \"c\" : 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"set c = array {};" +
				"a.eachOnRow(1, function (item, index, key) {" +
				"	b.add(key);" +
				"	c.add(item);" +
				"});" +
				"" +
				"print (b);" +
				"print (c);" +
				"unit.assertEquals (array {\"a\", nil, \"c\"}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachOnCol1Test() throws Exception {
		String source = "" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"a.eachOnCol(1, b.add);" +
				"unit.assertEquals (array {2, 5, 8}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachOnCol2Test() throws Exception {
		String source = "" +
				"set i = 0;" +
				"set a = array {" +
				"	{1, 2, 3}," +
				"	{4, 5, 6}," +
				"	{7, 8, 9}" +
				"};" +
				"set b = array {};" +
				"a.eachOnCol(1, function (item, index) {" +
				"	i += index;" +
				"});" +
				"unit.assertEquals (3, i);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void eachItemTest() throws Exception {
		String source = "" +
				"set a = array {1, {2, 3}, {{4, 5}, 6, {7, 8}}" +
				"};" +
				"set b;" +
				"a.eachItem(b.add);" +
				"unit.assertEquals (array {1, 2, 3, 4, 5, 6, 7, 8}, b);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Sort1Test() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort();" + "\n" +
				"unit.assertEquals (array {1, 2, 3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Sort2Test() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort(" +
				"	function (a, b) {" +
				"		if (a > b) return true;" +
				"	}" +
				");" + "\n" +
				"unit.assertEquals (array {1, 2, 3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void Sort3Test() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort(" +
				"	function (a, b) {" +
				"		if (a < b) return true;" +
				"	}" +
				");" + "\n" +
				"unit.assertEquals (array {4, 3, 2, 1}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SortReverseTest() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort(al.order.reverse);" +
				"print (a);" + "\n" +
				"unit.assertEquals (array {1, 2, 4, 3}, a);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SortReverseStringTest() throws Exception {
		String source = "" +
				"set a = \"Al'O world !\".split(\"\").sort(al.order.reverse).toString();" + "\n" +
				"unit.assertEquals (\"! dlrow O'lA\", a);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SortAscendingTest() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort(al.order.ascending);" + "\n" +
				"unit.assertEquals (array {1, 2, 3, 4}, a);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SortDescendingTest() throws Exception {
		String source = "" +
				"set a = array {3, 4, 2, 1}.sort(al.order.descending);" + "\n" +
				"unit.assertEquals (array {4, 3, 2, 1}, a);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SOMETIME_SortRandom1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};" +
				"set b = a.clone().sort(al.order.random);" + "\n" +
				"set c = a.clone().sort(al.order.random);" + "\n" +
				"unit.assertNotEquals (b, c);" +
				"print (b .. \" != \" .. c);" + "\n" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void SOMETIME_SortRandom2Test() throws Exception {
		String source = "" +
				"set a = array {\"a\" : 1, \"b\" : 2, \"c\" : 3, \"d\" : 4};" +
				"set keys1;" +
				"set keys2;" +
				"a.each (function (item, index, key) {" +
				"	keys1.add(key);" +
				"});" +
				"" +
				"a.sort(al.order.random);" +
				"" +
				"a.each (function (item, index, key) {" +
				"	keys2.add(key);" +
				"});" +
				"" +
				"print (keys1 .. \" == \" .. keys2);" + "\n" +
				"" +
				"unit.assertEquals (1, a[\"a\"]);" +
				"unit.assertEquals (2, a[\"b\"]);" +
				"unit.assertEquals (3, a[\"c\"]);" +
				"unit.assertEquals (4, a[\"d\"]);" +
				"" +
				"unit.assertNotEquals (keys1, keys2);" +
				"" +
				"unit.assertEquals (1, a[\"a\"]);" +
				"unit.assertEquals (2, a[\"b\"]);" +
				"unit.assertEquals (3, a[\"c\"]);" +
				"unit.assertEquals (4, a[\"d\"]);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void minTest() throws Exception {
		String source = "" +
				"set a = array {2, 4, 1, 3};" +
				"set fsort = function (item1, item2) {" +
				"	if (item1 < item2) return true;" +
				"};" +
				"unit.assertEquals (1, a.min());" +
				"unit.assertEquals (4, a.min(fsort));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void maxTest() throws Exception {
		String source = "" +
				"set a = array {2, 4, 1, 3};" +
				"set fsort = function (item1, item2) {" +
				"	if (item1 < item2) return true;" +
				"};" +
				"unit.assertEquals (4, a.max());" +
				"unit.assertEquals (1, a.max(fsort));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void filter1Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4}.filter();" + "\n" +
				"unit.assertEquals(array {1, 2, 3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void filter2Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4}.filter(function(index, item) {return true;});" + "\n" +
				"unit.assertEquals(array {1, 2, 3, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void filter3Test() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4}.filter(function(item) {return (item % 2 == 0);});" + "\n" +
				"unit.assertEquals(array {2, 4}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void filter4Test() throws Exception {
		String source = "" +
				"set a = array {}.create(11).filter(function(item) {return (item % 3 == 0);});" + "\n" +
				"unit.assertEquals(array {0, 3, 6, 9}, a);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void join1Test() throws Exception {
		String source = "" +
				"set n = array {}.create(10, (v) {return v + 1}).join(function (a, b, index) {return a + b;});" + "\n" +
				"unit.assertEquals(55, n);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void join2Test() throws Exception {
		String source = "" +
				"set a = array {\"hi\", \"world\", \"I am\", \"algoid!\"};" +
				"set s = a.join (function (a, b, index) {return a .. \" \" .. b;});" + "\n" +
				"unit.assertEquals(\"hi world I am algoid!\", s);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void joinConcatTest() throws Exception {
		String source = "" +
				"set a = array {\"hi\", \"world\", \"I am\", \"algoid!\"};" +
				"set s = a.join (al.combine.concat.setParameter(\"separator\", \" \"));" + "\n" +
				"unit.assertEquals(\"hi world I am algoid!\", s);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void joinSumTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set r = a.join (al.combine.sum);" + "\n" +
				"unit.assertEquals(10, r);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void joinProductTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set r = a.join (al.combine.product);" + "\n" +
				"unit.assertEquals(24, r);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void merge1Test() throws Exception {
		String source = "" +
				"set a1 = array {1, 2, 3, 4}.merge (array {1, 2, 1, 2}, function (item1, item2, index) {return item1 * item2;});" +
				"unit.assertEquals(array {1, 4, 3, 8}, a1);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void merge2Test() throws Exception {
		String source = "" +
				"set a1 = array {1, 2, 3, 4}.merge (array {\"odd\", \"event\"}, function (item1, item2, index) {return item1 .. \" is \" .. item2;});" +
				"unit.assertEquals(array {\"1 is odd\", \"2 is event\", \"3 is odd\", \"4 is event\"}, a1);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void merge3Test() throws Exception {
		String source = "" +
				"set a1 = array {1, 2, 3, 4}.merge (array {1, 2, 0, 0}, function (item1, item2, index) {return item1 * item2;});" +
				"unit.assertEquals(array {1, 4, 0, 0}, a1);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void merge4Test() throws Exception {
		String source = "" +
				"set a1 = array {\"a\" : 1, \"b\" : 2, \"c\" : 3, \"d\" : 4};" +
				"set a2 = array {\"c\" : 1, \"b\" : 2, \"a\" : 0, \"d\" : 0};" +
				"a1 = a1.merge (a2, function (item1, item2, index, key) {" +
				"	return a1[key] * a2[key];" +
				"});" +
				"unit.assertEquals(array {0, 4, 3, 0}, a1);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void event1Test() throws Exception {
		String source = "" +
				"set r;" +
				"set a = array {1, 2, };" +
				"" +
				"a.onChanged ( (v) {r = v} );" +
				"" +
				"a.add(3);" +
				"unit.assertEquals(array {1, 2, 3}, r);" +
				"" +
				"a.remove(2);" +
				"unit.assertEquals(array {1, 2}, r);" +
				"" +
				"a.addAll(array {3, 4})" +
				"unit.assertEquals(array {1, 2, 3, 4}, r);" +
				"" +
				"a.pop()" +
				"unit.assertEquals(array {1, 2, 3}, r);" +
				"" +
				"a.swap(0, 1)" +
				"unit.assertEquals(array {2, 1, 3}, r);" +
				"" +
				"a.each( {return 0;} )" +
				"unit.assertEquals(array {0, 0, 0}, r);" +
				"" +
				"a.clear()" +
				"unit.assertEquals(array {}, r);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void event2Test() throws Exception {
		String source = "" +
				"set r;" +
				"set a = array {1, 2, 3};" +
				"" +
				"a.onChanged ( (v) {r = v} );" +
				"a = 7" +
				"" +
				"unit.assertEquals(7, r);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void containsAllTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"" +
				"unit.assertEquals(true, a.containsAll( array {2, 3} ));" +
				"unit.assertEquals(true, a.containsAll( array {1, 2, 4, 3} ));" +
				"unit.assertEquals(false, a.containsAll( array {2, 7} ));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void containsAnyTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"" +
				"unit.assertEquals(true, a.containsAny( array {2, 3, 7} ));" +
				"unit.assertEquals(true, a.containsAny( array {1, 2, 4, 3, 7} ));" +
				"unit.assertEquals(false, a.containsAny( array {7, 8} ));" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void unionTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b = array {3, 5, 7, 8};" +
				"set c = a.union(b);" +
				"" +
				"unit.assertEquals(array {1, 2, 3, 4, 5, 7, 8}, c);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void intersectionTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b = array {3, 2, 7, 8};" +
				"set c = a.intersection(b);" +
				"" +
				"unit.assertEquals(array {2, 3}, c);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void complementTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b = array {3, 2, 7, 8};" +
				"set c = a.complement(b);" +
				"set d = b.complement(a);" +
				"" +
				"unit.assertEquals(array {1, 4}, c);" +
				"unit.assertEquals(array {7, 8}, d);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void differenceTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b = array {3, 2, 7, 8};" +
				"set c = a.difference(b);" +
				"" +
				"unit.assertEquals(array {1, 4, 7, 8}, c);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}

	@Test
	public void productTest() throws Exception {
		String source = "" +
				"set a = array {1, 2, 3, 4};" +
				"set b = array {true, false};" +
				"set c = a.product(b);" +
				"" +
				"unit.assertEquals(array {{1, true}, {1, false}, {2, true}, {2, false}, {3, true}, {3, false}, {4, true}, {4, false}}, c);" +
				"";

		AL.execute(new Syntax(), new UnitTestRuntime(), source);
	}
}
