/*
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.al.libs;

import fr.cyann.al.AL;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 <p>
 @author cyann
 */
public class ReflexionTest extends TestCase {

	public void testStringBuilder() throws InterruptedException {

		String source = ""
			+ "set sb = java.newObject (\"java.lang.StringBuilder\");"
			+ "set sb2 = java.newObject (\"java.lang.StringBuilder\");"
			+ ""
			+ "sb2.append(\"native\")"
			+ "sb.append(\"test\");"
			+ "sb.append(8);"
			+ "sb.append(sb2);"
			+ ""
			+ "print (sb.toString());"
			+ "unit.assertEquals(\"test8native\", sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}
	
	public void testClone1() throws InterruptedException {

		String source = ""
			+ "set sb = java.newObject (\"java.lang.StringBuilder\");"
			+ "set sb2 = sb.clone(\"new string builder \");"
			+ ""
			+ "sb2.append(\"native\")"
			+ "sb.append(\"test\");"
			+ "sb.append(8);"
			+ "sb.append(sb2);"
			+ ""
			+ "print (sb.toString());"
			+ "unit.assertEquals(\"test8native\", sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}
	
	public void testClone2() throws InterruptedException {

		String source = ""
			+ "set sb = java.newObject (\"java.lang.StringBuilder\");"
			+ "sb.append(\"test\");"
			+ "sb.append(8);"
			+ ""
			+ "set sb2 = sb.clone();"
			+ ""
			+ "sb2.append(\"native\")"
			+ "sb.append(sb2);"
			+ ""
			+ "print (sb.toString());"
			+ "unit.assertEquals(\"test8test8native\", sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}
	
	public void testNew() throws InterruptedException {

		String source = ""
			+ "set sb = java.newObject (\"java.lang.StringBuilder\");"
			+ "sb.append(\"test\");"
			+ "sb.append(8);"
			+ ""
			+ "set sb2 = new sb;"
			+ ""
			+ "sb2.append(\"native\")"
			+ "sb.append(sb2);"
			+ ""
			+ "print (sb.toString());"
			+ "unit.assertEquals(\"test8test8native\", sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}
	public void testMath() throws InterruptedException {
		String source = ""
			+ "set Math = java.newObject (\"java.lang.Math\");"
			+ "unit.assertEquals (Math.acos(0.2), " + Math.acos(0.2) + ");"
			+ "unit.assertEquals (Math.abs(-10), " + Math.abs(-10) + ");"
			+ "unit.assertEquals (Math.PI, " + Math.PI + ");"
			+ "unit.assertEquals (java.staticField (\"java.lang.Math\", \"PI\"), " + Math.PI + ");"
			+ "unit.assertEquals (java.staticMethod (\"java.lang.Math\", \"acos\", 0.2), " + Math.acos(0.2) + ");"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testArray() throws InterruptedException {

		Integer[] strings = new Integer[]{1, 2, 3, 4, 5};
		List<Integer> list = Arrays.asList(strings);
		System.out.println(list.toString());

		String source = ""
			+ "print (java.staticMethod(\"java.util.Arrays\", \"asList\", array {1, 2, 3, 4}).toString());"
			+ "java.staticMethod(\"java.util.Arrays\", \"sort\", array {4,3,5,1});"
			+ "java.staticMethod(\"java.util.Arrays\", \"sort\", array {\"a\", \"b\", \"c\"});"
			+ ""
			+ "set r1 = java.staticMethod(\"java.util.Arrays\", \"copyOfRange\", array {1, 2, 3, 4}, 1, 3);"
			+ "unit.assertEquals (array {2, 3}, r1);"
			+ "print (r1);"
			+ ""
			+ "set r2 = java.staticMethod(\"java.util.Arrays\", \"copyOfRange\", array {\"a\", \"b\", \"c\", \"d\"}, 1, 3);"
			+ "unit.assertEquals (array {\"b\", \"c\"}, r2);"
			+ "print (r2);"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testInterface() throws InterruptedException {

		String source = ""
			+ "set o = object {"
			+ "	set result = 0;"
			+ "	set doIt = function (i) {"
			+ "		result += i;"
			+ "		print (\"It works ! \" .. i);"
			+ "	}"
			+ "}"
			+ "set interface = java.newObject(\"fr.cyann.al.libs.Interface\", o);"
			+ ""
			+ "interface.doIt(8);"
			+ "unit.assertEquals (8, o.result);"
			+ "interface.doIt(9);"
			+ "unit.assertEquals (17, o.result);"
			+ ""
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testAbstract() throws InterruptedException {

		String source = ""
			+ "set abstract = java.newObject(\"fr.cyann.al.libs.Abstract\", object {"
			+ "	set doIt = function (method) {"
			+ "		print (\"It works ! \" .. method);"
			+ "	}"
			+ "});"
			+ ""
			+ "print (al.allObjects());"
			+ ""
			+ "abstract.doIt();"
			+ "abstract.doIt();"
			+ ""
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testStackOverflow() {
		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		//try {
			// create context
			RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());
			ObjectInstance o = Reflexion.buildObject(context, InfinitLoopType.class, new InfinitLoopType());

		/*} catch (StackOverflowError ex) {
			fail("Stack overflow shouldn't been thrown !");
		}*/

	}

}
