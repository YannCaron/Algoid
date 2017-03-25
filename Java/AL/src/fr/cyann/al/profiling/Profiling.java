/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.profiling;

import fr.cyann.al.AL;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.UnitTestRuntime;

/**
 * The Profiling main class. Creation date: 6 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Profiling {

	public static final String SRC_LOOP = "" +
		"loop (100000) {" +
		"	1 + 2 + 3 + 4" +
		"};" +
		"";
	public static final String SRC_FOR = "" +
		"for (set i = 0; i<100000; i++) {" +
		"	1 + 2 + 3 + 4" +
		"};" +
		"";
	public static final String SRC_CALL = "" +
		"set j;" +
		"for (set i = 0; i<1000000; i++) {" +
		"	j = math.random(100);" +
		"};" +
		"";
	public static final String SRC_EVAL = "" +
		"set i = 0;" +
		"set j = 0;" +
		"while (i < 100" +
		") {" +
		"	eval(\"i = \" .. j);" +
		"}" +
		"";
	public static final String SRC_NEW = "" +
		"set parent = object() {};" +
		"set o = object(parent) {" +
		"	set p = object() {};" +
		"	set f = function (n) {" +
		"		return n * 2;" +
		"	};" +
		"};" +
		"" +
		"set j;" +
		"for (set i; i<10; i++) {" +
		"	set p = new o;" +
		"	j += p.f(1);" +
		"}" +
		"";

	/**
	 * Main methode, programm entry point.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		AL.execute(new Syntax(), new UnitTestRuntime(), SRC_EVAL);
	}
}
