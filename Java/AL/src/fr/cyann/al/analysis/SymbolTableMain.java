/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.AL;
import fr.cyann.al.symbolTable.PrettyPrinter;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.jasi.builder.ASTBuilder;

/**
 * <p>
 * @author CARONYN
 */
public class SymbolTableMain {

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	/*String source = "" +
	 "set o = object () {" +
	 "	set method1 = function() {};" +
	 "	set method2 = function() {};" +
	 "}" +
	 "";*/
	String source = ""
		+ "unit.assertEquals(7, 7);"
		+ "";

	/*String source = ""
	 + "set a = 5;"
	 + "a;"
	 + "";*/
	SymbolTableContext context = parse(source);
	new PrettyPrinter().println(context.getRootScope());

	System.out.println("\n");

	TypeInferer ti = new TypeInferer(context.getRootScope());
	ti.infer();

	System.out.println("\n");

	new PrettyPrinter().println(context.getRootScope());

    }

}
