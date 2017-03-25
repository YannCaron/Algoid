/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.AL;
import static fr.cyann.al.AL.setProgramToBlock;
import fr.cyann.al.ast.Program;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.symbolTable.PrettyPrinter;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.ToStringContext;
import fr.cyann.al.visitor.ToStringTreeVisitor;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * The EvalTest class.<br>
 * creation date : 8 mai 2012.
 * <p>
 * @author Yann Caron
 * @version v0.1
 */
public class StaticTransformerTest {

    public static final Syntax SYNTAX = new Syntax();
    public static final AbstractRuntime RUNTIME = new UnitTestRuntime();
    public static final RuntimeContext CONTEXT = AL.createContext(SYNTAX, RUNTIME);

    public static final ASTBuilder parse(String source) {

	// parse
	// create parser and visitor
	ASTBuilder builder = new ASTBuilder();

	SYNTAX.parse(source, builder);

	// dependency injection
	SymbolTableContext symbolTable = new SymbolTableContext(CONTEXT);

	builder.injectVisitor(new SymbolTableGeneratorVisitor()).visite(symbolTable);

	// type inference
	new TypeInferer(symbolTable.getRootScope()).infer();

	new PrettyPrinter().print(symbolTable.getRootScope());

	// transform code
	setProgramToBlock(builder);

	// static analysis
	builder.injectVisitor(new StaticTransformerVisitor()).visite(new TransformContext(symbolTable));

	return builder;
    }

    public static final void execute(ASTBuilder builder) {

	// execution phase
	System.out.println("");
	System.gc(); // clear gc before execution
	builder.injectVisitor(RUNTIME).visite(CONTEXT);

    }

    public static final String toStringTree(ASTBuilder builder) {
	ToStringContext toStringContext = new ToStringContext();
	builder.injectVisitor(new ToStringTreeVisitor());
	builder.visite(toStringContext);

	return toStringContext.getSource();
    }

    public static final List<AST> getProgram(ASTBuilder builder) {
	Program block = (Program) builder.getProgram().get(0);
	return block.getStatements();
    }

    @Test
    public void AutomaticProperty1Test() {
	String source = ""
		+ "set o = object() {"
		+ " set pa;"
		+ " set getA = function () {return pa};"
		+ " set setA = function (a) {pa = a};"
		+ "};"
		+ ""
		+ "o.a;"
		+ "o.a = 7;"
		+ ""
		+ "";

	ASTBuilder builder = parse(source);
	System.out.println(toStringTree(builder));

	List<AST> program = getProgram(builder);
	Variable v1 = (Variable) program.get(1);
	Assert.assertEquals("o.getA()", v1.getChainName());

	Variable v2 = (Variable) program.get(2);
	Assert.assertEquals("o.setA()", v2.getChainName());

	System.out.println("");
    }

    @Test
    public void AutomaticProperty2Test() {
	String source = ""
		+ "set o = object() {"
		+ " set pp = object () {"
		+ "	set pa;"
		+ "	set getA = function () {return pa};"
		+ "	set setA = function (a) {pa = a};"
		+ " };"
		+ ""
		+ "set getP = function () {"
		+ " return pp;"
		+ "}"
		+ ""
		+ "};"
		+ ""
		+ "o.p.a;"
		+ "o.p.a = 7;"
		+ "o"
		+ ""
		+ "";

	ASTBuilder builder = parse(source);
	System.out.println(toStringTree(builder));

	List<AST> program = getProgram(builder);
	Variable v1 = (Variable) program.get(1);
	Assert.assertEquals("o.getP().getA()", v1.getChainName());

	Variable v2 = (Variable) program.get(2);
	Assert.assertEquals("o.getP().setA()", v2.getChainName());

	System.out.println("");
    }

    @Test
    public void TO_BE_CONTINUED_AutomaticMagicMethod1Test() {
	// TODO Set the function type sensitive
	String source = ""
		+ "set addWith = function (a, b) { return a + b; };"
		+ ""
		+ "set a = (7).addWith(8);"
		+ "unit.assertEquals(15, a);"
		+ ""
		+ "";

	ASTBuilder builder = parse(source);
	System.out.println(toStringTree(builder));

	execute(builder);

	System.out.println("");
    }

    @Test
    public void TO_BE_CONTINUED_AutomaticMagicMethod2Test() {
	// TODO Set the function type sensitive
	String source = ""
		+ "set f = function (a) {"
		+ " return a;"
		+ "};"
		+ ""
		+ "set o = object () {"
		+ " set f = function (a) {"
		+ "	return a * 2;"
		+ " };"
		+ "};"
		+ ""
		+ "unit.assertEquals((7).f(), 7);"
		+ "unit.assertEquals(o.f(7), 14);"
		+ "";

	ASTBuilder builder = parse(source);
	System.out.println(toStringTree(builder));

	execute(builder);

	System.out.println("");
    }

}
