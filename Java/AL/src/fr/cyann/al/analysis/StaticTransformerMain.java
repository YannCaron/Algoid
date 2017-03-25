/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.AL;
import static fr.cyann.al.AL.setProgramToBlock;
import fr.cyann.al.symbolTable.PrettyPrinter;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.al.visitor.ToStringContext;
import fr.cyann.al.visitor.ToStringTreeVisitor;
import fr.cyann.jasi.builder.ASTBuilder;

/**
 * <p>
 * @author CARONYN
 */
public class StaticTransformerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

	String source = ""
		+ "set addWith = function (a, b) { return a + b; };"
		+ ""
		+ "set a = (7).addWith(8);"
		+ "print (a);"
		+ ""
		+ "";

	// parse
	// create parser and visitor
	ASTBuilder builder = new ASTBuilder();
	Syntax syntax = new Syntax();
	AbstractRuntime runtime = new RuntimeVisitor();
	RuntimeContext context = AL.createContext(syntax, runtime);

	syntax.parse(source, builder);

	// transform code
	setProgramToBlock(builder);

	// dependency injection
	SymbolTableContext symbolTable = new SymbolTableContext(context);

	builder.injectVisitor(new SymbolTableGeneratorVisitor()).visite(symbolTable);
	new PrettyPrinter().println(symbolTable.getRootScope());
	System.out.println("\n");

	// static analysis
	builder.injectVisitor(new StaticTransformerVisitor()).visite(new TransformContext(symbolTable));

	// execution phase
	System.out.println("");
	System.gc(); // clear gc before execution
	builder.injectVisitor(runtime).visite(context);

	// generate transformed
	System.out.println("");
	ToStringContext toStringContext = new ToStringContext();
	builder.injectVisitor(new ToStringTreeVisitor()).visite(toStringContext);

	System.out.println(toStringContext.getSource());

    }

}
