/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 YANN CARON CONFIDENTIAL
 __________________

 Yann Caron Copyright (c) 2011
 All Rights Reserved.
 __________________

 NOTICE:  All information contained herein is, and remains
 the property of Yann Caron and its suppliers, if any.
 The intellectual and technical concepts contained
 herein are proprietary to Yann Caron
 and its suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Yann Caron.
 */
package fr.cyann.al;

import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.declaration.Declaration;
import fr.cyann.al.ast.interfaces.Conditionable;
import fr.cyann.jasi.ast.interfaces.Scopeable;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.lang.reflect.Modifier;
import java.util.EmptyStackException;
import java.util.List;
import static org.junit.Assert.*;

/**
 *
 * @author ops
 */
public class ASTUnitTesting {

	/**
	 * The default expression class implementation. Null object.
	 */
	public static class Null extends Expression {

		/**
		 * Default constructor.
		 */
		public Null() {
			super(null);
		}

		/**
		 * No implementation.
		 *
		 * @param injector the injector.
		 */
		@Override
		public void injectVisitor(VisitorInjector injector) {
		}

		@Override
		public void depthFirstTraversal(TraversalFunctor func) {
		}

	}

	/**
	 * Build AST list according the agregator needs.
	 *
	 * @param cls the AST type
	 * @return the AST instance
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
			return new Null();
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
	 * Constructor AST list from strategy. Used to reverse and generated unit
	 * tests.<br>
	 * Try the agregator to find the needed AST.
	 *
	 * @param agregator the agregator to analyse
	 * @param outASTList the list
	 */
	public static void buildASTList(AgregatorStrategy agregator, List<String> outASTList) {
		try {
			agregator.build(null, null);
			return;
		} catch (EmptyStackException ex) {
			outASTList.add(0, StringExpression.class.getCanonicalName());
			pushAllCls(outASTList);
			// recursive
			buildASTList(agregator, outASTList);
		} catch (ClassCastException ex) {
			String msg = ex.getMessage();
			String className = msg.substring(msg.lastIndexOf(' ') + 1);
			outASTList.remove(0);
			outASTList.add(0, className);
			pushAllCls(outASTList);
			// recursive
			buildASTList(agregator, outASTList);
		}

	}

	/**
	 * Instancing all the AST object into the builder.
	 *
	 * @param outASTList the list.
	 */
	private static void pushAllCls(List<String> outASTList) {
		for (String className : outASTList) {
			try {
				AST ast = instancingAST((Class<? extends AST>) Class.forName(className));
				new ASTBuilder().push(ast);
			} catch (ClassNotFoundException ex) {
				fail(String.format("Cannot instancing [%s] class.", className));
			}
		}
	}
}
