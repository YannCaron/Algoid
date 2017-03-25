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
package fr.cyann.al.reflexion;

import fr.cyann.al.AL;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.libs.Reflexion;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import java.util.Arrays;
import java.util.List;
import javax.script.ScriptException;
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

	public void testStringBuilder2() throws ScriptException {
		String source = ""
			+ "set sb = java.newObject (\"java.lang.StringBuilder\");"
			+ ""
			+ "sb.append(\"AL'O\");"
			+ "sb.append(\" \");"
			+ "sb.append(\"World !\");"
			+ ""
			+ "print (sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testMath() throws InterruptedException {
		String source = ""
			+ "java.import (\"java.lang.Math\");"
			+ "set Math = java.newObject (\"Math\");"
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
//java.util.Arrays.copyOfRange(strings, from, to)
		String source = ""
			+ "print (java.staticMethod(\"java.util.Arrays\", \"asList\", array {1, 2, 3, 4}).toString());"
			+ "java.staticMethod(\"java.util.Arrays\", \"sort\", array {4,3,5,1});"
			+ "java.staticMethod(\"java.util.Arrays\", \"sort\", array {\"a\", \"b\", \"c\"});"
			+ "set r1 = java.staticMethod(\"java.util.Arrays\", \"copyOfRange\", array {1, 2, 3, 4}, 1, 3);"
			+ "unit.assertEquals (array {2, 3}, r1);"
			+ /*"print (r1);" +
			 "set r2 = java.staticMethod(\"java.util.Arrays\", \"copyOfRange\", array {\"a\", \"b\", \"c\", \"d\"}, 1, 3);" +
			 "unit.assertEquals (array {\"b\", \"c\"}, r2);" +
			 "print (r2);" +*/ "";

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
			+ "set interface = java.newObject(\"fr.cyann.al.reflexion.Interface\", o);"
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
			+ ""
			+ "set o = object {"
			+ "	set result = 0;"
			+ "	set doIt = function () {"
			+ "		result++;"
			+ "		print (\"It works ! \");"
			+ "	}"
			+ "}"
			+ "set abstract = java.newObject(\"fr.cyann.al.reflexion.Abstract\", o);"
			+ ""
			+ "print (al.allObjects());"
			+ ""
			+ "unit.assertEquals (0, o.result);"
			+ "abstract.doIt();"
			+ "unit.assertEquals (1, o.result);"
			+ "abstract.doIt();"
			+ "unit.assertEquals (2, o.result);"
			+ ""
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testImportError() throws InterruptedException {

		String source = ""
			+ "" + '\n'
			+ "java.import (\"java.lang\");" + '\n'
			+ "" + '\n';

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		// execute
		try {
			AL.execute(syntax, runtime, context, source);
			fail("Exception expected !");
		} catch (ALRuntimeException ex) {
			assertEquals(1, ex.getToken().getLine());
			assertEquals(12, ex.getToken().getCol());
		}

	}

	public void testImport() throws InterruptedException {

		String source = ""
			+ "java.import (\"java.lang.StringBuilder\");"
			+ "set sb = java.newObject (\"StringBuilder\");"
			+ ""
			+ "sb.append(1);"
			+ "sb.append(2);"
			+ "sb.append(3);"
			+ ""
			+ "unit.assertEquals (\"123\", sb.toString());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

	public void testListObject() throws InterruptedException {

		String source = ""
			+ "set list = (al.allObjects().filter( function (item) { return item.contains(\"java\") } ));"
			+ "unit.assertNotEquals (0, list.length());"
			+ "";

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new UnitTestRuntime();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, new Reflexion());

		AL.execute(syntax, runtime, context, source);

	}

}
