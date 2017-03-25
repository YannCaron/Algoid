/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.factory;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.ast.Expression;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.MethodVisitor;

/**
 * The DeclarationFactory class. Creation date: 21 déc. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public final class DeclarationFactory {

	private DeclarationFactory() {
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, Expression<? extends Expression, RuntimeContext> expr) {
		// set [name] = [defaultValue];

		// create assignation
		Token token = new Token(Syntax.IDENT, name);

		// declaring variable
		VariableDeclaration decl = new VariableDeclaration(token);

		// declaring variable
		Variable variable = new Variable(token);

		// aggregate
		decl.setVar(variable);
		decl.setExpr(expr);

		return decl;
	}

	public static VariableDeclaration<RuntimeContext> factoryVar(String name) {
		// set [name] = [defaultValue];

		// create assignation
		Token token = new Token(Syntax.IDENT, name);

		// declaring variable
		VariableDeclaration decl = new VariableDeclaration(token);

		// declaring variable
		Variable variable = new Variable(token);

		// aggregate
		decl.setVar(variable);

		return decl;
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, boolean value) {
		return factory(name, ExpressionFactory.bool(value));
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, float value) {
		return factory(name, ExpressionFactory.number(value));
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, String value) {
		return factory(name, ExpressionFactory.string(value));
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor) {
		return factory(name, ExpressionFactory.function(name, visitor));
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, String... params) {
		return factory(name, visitor, false, params);
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, boolean lazyArgEvaluation, String... params) {
		FunctionDeclaration<RuntimeContext> f = ExpressionFactory.function(name, visitor, params);
		f.setLazyArgEvaluation(lazyArgEvaluation);
		return factory(name, f);
	}

	public static VariableDeclaration<RuntimeContext> factory(ObjectDeclaration object) {
		return factory(object.getChainName(), object);
	}

	public static VariableDeclaration<RuntimeContext> factory(String name) {
		return factory(name, ExpressionFactory.object(name));
	}

	public static VariableDeclaration<RuntimeContext> factory(String name, MutableVariant value) {
		return factory(name, ExpressionFactory.expression(value));
	}
}
