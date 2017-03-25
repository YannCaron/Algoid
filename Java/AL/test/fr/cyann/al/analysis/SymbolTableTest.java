/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.AL;
import fr.cyann.al.analysis.SymbolTableGeneratorVisitor;
import fr.cyann.al.analysis.SymbolTableContext;
import fr.cyann.al.symbolTable.PrettyPrinter;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.jasi.builder.ASTBuilder;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;

/**
 * The EvalTest class.<br>
 * creation date : 8 mai 2012.
 * <p>
 * @author Yann Caron
 * @version v0.1
 */
public class SymbolTableTest {

    private static final ASTBuilder builder = new ASTBuilder();
    private static final SymbolTableGeneratorVisitor runtime = new SymbolTableGeneratorVisitor();

    public static final SymbolTableContext parse(String source) {
	Syntax syntax = new Syntax();
	RuntimeContext api = AL.createContext(syntax, new UnitTestRuntime());
	SymbolTableContext context = new SymbolTableContext(api);

	syntax.parse(source, builder);

	// dependency injection
	builder.injectVisitor(runtime).visite(context);

	return context;
    }

    @Test
    public void SymbolTable1Test() {
	String source = ""
		+ "set a = 7;"
		+ "set c = 8 + 7;"
		+ "{"
		+ "	set g = function (a, b, c) {"
		+ "		set no;"
		+ "	}"
		+ "	set f = \"f\""
		+ "};"
		+ ""
		+ "set b;"
		+ "";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }

    @Test
    public void SymbolTableFunction1Test() {
	String source = ""
		+ "set f = function (a) {"
		+ "	set i = a;"
		+ "	return i;"
		+ "};"
		+ "return;";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }

    @Test
    public void SymbolTableObjectTest() {
	String source = ""
		+ "set s = object () {}"
		+ "set o = object (s) {"
		+ "	set i;"
		+ "};";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }

    @Test
    public void SymbolTable4Test() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	String source = ""
		+ "set a = 7;" + '\n'
		+ "set b = false;" + '\n'
		+ "set c = a + 7;";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }

    @Test
    public void SymbolTable5Test() {
	String source = ""
		+ "if (a == 5) {"
		+ "} else {"
		+ "}"
		+ ""
		+ "loop (5) {}"
		+ "";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }

    @Test
    public void SymbolTableIdentifier1Test() {
	String source = ""
		+ "set o.a[7](a, b);"
		+ "";

	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());
    }
}
