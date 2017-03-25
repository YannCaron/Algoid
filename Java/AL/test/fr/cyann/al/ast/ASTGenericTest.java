/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.ast;

import fr.cyann.al.ast.declaration.Declaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.interfaces.Conditionable;
import fr.cyann.jasi.ast.interfaces.Scopeable;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.scope.AbstractScope;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.EmptyContext;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.utils.Reflect;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author caronyn
 */
public class ASTGenericTest extends TestCase {

    private Class<? extends AST> cls;

    /**
     * Default constructor. Set the class to test
     *
     * @param cls the class to test.
     */
    public ASTGenericTest(Class<? extends AST> cls) {
	super(cls.getSimpleName() + "GenericTest");
	this.cls = cls;
    }

    /**
     * Get the composite test case.
     *
     * @return the test suite.
     * @throws Exception exception
     */
    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite("ASTAllTest");

	for (String className : Reflect.getClassNamesFromPackage("fr.cyann.al.ast")) {
	    Class<? extends AST> cls = (Class<? extends AST>) Class.forName(className);
	    if (!Modifier.isAbstract(cls.getModifiers())) {
		suite.addTest(new ASTGenericTest(cls));
	    }
	}

	return suite;
    }

    /**
     * Test the specific AST.
     *
     * @throws Exception if exception occured
     */
    @Override
    public void runTest() throws Exception {

	AST ast = instancingAST(cls);

	List<String> found = new ArrayList<String>();
	final List<String> visited = new ArrayList<String>();

	found.add(ast.getToken().getText());

	instancingFields(ast, found);
	injectVisitor(ast, visited);

	// check that no field can be null
	for (Field field : ast.getClass().getDeclaredFields()) {
	    field.setAccessible(true);

	    Object o = field.get(ast);
	    if (!AbstractScope.class.isAssignableFrom(field.getType())) {
		assertNotNull(String.format("Field [%s] is null FAILED!", field.getName()), o);
	    }
	}

	// visite
	ast.visite(new EmptyContext());

	// verify that all sub ast are visited too
	assertEquals("All sub AST are not visited FAILD!", found, visited);
    }

    /**
     * Create new instance of AST.
     *
     * @param cls the AST class.
     * @return the AST instance.
     */
    public static AST instancingAST(Class<? extends AST> cls) {
	Token token = new Token(TokenType.SYMBOL, cls.getSimpleName());

	if (Conditionable.class.isAssignableFrom(cls) && Modifier.isInterface(cls.getModifiers())) {
	    return new If(token);
	} else if (Scopeable.class.isAssignableFrom(cls) && Modifier.isInterface(cls.getModifiers())) {
	    return new Block(token);
	} else if (Expression.class.isAssignableFrom(cls) && Modifier.isAbstract(cls.getModifiers())) {
	    return new Variable(token);
	} else if (Declaration.class.isAssignableFrom(cls) && Modifier.isAbstract(cls.getModifiers())) {
	    return new VariableDeclaration(token);
	} else if (Expression.class.isAssignableFrom(cls) && Modifier.isAbstract(cls.getModifiers())) {
	    return new StringExpression(token);
	} else {
	    try {
		return (AST) cls.getConstructor(Token.class).newInstance(token);
	    } catch (Exception ex) {
		try {
		    // 0 is the neutral value for all constructor (NumberExpression, BooleanExpression and StringExpression).
		    return (AST) cls.getConstructor(Token.class).newInstance(new Token(TokenType.SYMBOL, "0"));
		} catch (Exception ex1) {
		    try {
			return (AST) cls.getConstructor().newInstance();
		    } catch (Exception ex2) {
			return null;
		    }
		}
	    }
	}
    }

    /**
     * Instancing all the tree sub elements to test.
     *
     * @param ast the ast to instancing all sub tree elements.
     * @param found the found elements are setted into list to compare with
     * visited one.
     * @throws Exception all exceptions that can occured.
     */
    static void instancingFields(AST ast, List<String> found) throws Exception {
	// instancing all sub element of the tree
	for (Field field : ast.getClass().getDeclaredFields()) {
	    field.setAccessible(true);

	    Object o = field.get(ast);
	    if (AST.class.isAssignableFrom(field.getType())) {
		// All others
		Token t = new Token(TokenType.SYMBOL, field.getType().getSimpleName());
		AST sub = instancingAST((Class<? extends AST>) field.getType());
		found.add(sub.getToken().getText());
		field.set(ast, sub);

	    } else if (o != null && List.class.isAssignableFrom(field.getType())) {
		// List of AST
		for (int i = 0; i < 2; i++) {
		    AST sub = new StringExpression(new Token(TokenType.SYMBOL, field.getName() + "_" + i));
		    List<AST> list = (List<AST>) field.get(ast);
		    list.add(sub);
		    found.add(sub.getToken().getText());
		}
	    }
	}
    }

    /**
     * Inject visitor that store all the AST visited into list.
     *
     * @param ast the ast to inject lister visitor.
     * @param visited the visited ast result to compare.
     */
    static void injectVisitor(AST ast, final List<String> visited) {

	// inject visitor
	ast.injectVisitor(new MethodVisitorInjector() {

	    @Override
	    public void visite(AST ast, Context context) {
		visited.add(ast.getToken().getText());
		for (Field field : ast.getClass().getDeclaredFields()) {
		    field.setAccessible(true);

		    try {
			Object o = field.get(ast);
			if (o != null && AST.class.isAssignableFrom(field.getType())) {
			    ((AST) o).visite(context);
			} else if (List.class.isAssignableFrom(field.getType())) {
			    List<AST> list = (List<AST>) field.get(ast);
			    for (AST sub : list) {
				sub.visite(context);
			    }
			}
		    } catch (Exception ex) {
		    }
		}
	    }
	});
    }

}
