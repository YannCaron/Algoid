/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.analysis.StaticTransformerVisitor;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.utils.Reflect;
import java.lang.reflect.Modifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author CyaNn
 */
public class StaticTransformerVisitorGenericTest extends TestCase {

    private static final String PACKAGE = "fr.cyann.al.ast";
    private static ClassMapVisitorInjector visitor;
    private Class<? extends AST> cls;

    static {
	visitor = new StaticTransformerVisitor();
    }

    /**
     * Default constructor. Set the class to test
     *
     * @param cls the class to test.
     */
    public StaticTransformerVisitorGenericTest(Class<? extends AST> cls) {
	super(cls.getSimpleName() + "ExecutionTest");
	this.cls = cls;
    }

    /**
     * Get the composite test case.
     *
     * @return the test suite.
     * @throws Exception exception
     */
    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite("ExecutionAllTest");

	for (String className : Reflect.getClassNamesFromPackage(PACKAGE)) {
	    Class<? extends AST> cls = (Class<? extends AST>) Class.forName(className);
	    if (!Modifier.isAbstract(cls.getModifiers()) && Modifier.isPublic(cls.getModifiers()) && !cls.isEnum()) {
		suite.addTest(new StaticTransformerVisitorGenericTest(cls));
	    }
	}

	return suite;
    }

    /**
     * The unit test for all execution visitor.
     */
    @Override
    public void runTest() throws Exception {
	// verify visitor is defined
	assertTrue(String.format("Visitor must be defined for [%s] ! ", cls.getName()), visitor.getVisitorMap().containsKey(cls));

	Token token = null;

	if ("Assignation".equals(cls.getSimpleName())) {
	    token = new Token(TokenType.SYMBOL, "=");
	} else if ("BinaryOperator".equals(cls.getSimpleName())) {
	    token = new Token(TokenType.SYMBOL, "+");
	} else if ("NumberExpression".equals(cls.getSimpleName())) {
	    token = new Token(TokenType.SYMBOL, "78");
	} else if ("UnaryOperator".equals(cls.getSimpleName())) {
	    token = new Token(TokenType.SYMBOL, "-");
	} else {
	    token = new Token(TokenType.SYMBOL, cls.getSimpleName());
	}

	AST ast = (AST) cls.getConstructor(Token.class).newInstance(token);

	MethodVisitor injector = visitor.getVisitor(ast);
	assertNotNull(String.format("Visitor must not be null for [%s] ! ", cls.getName()), injector);

    }
}
