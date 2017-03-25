/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.factory;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.exception.UnexpectedTypeException;
import fr.cyann.al.scope.NestedScope;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.scope.ParameterScope;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.StaticMethodVisitorInjector;
import java.util.List;

/**
 * The ExpressionFactory class. Creation date: 20 déc. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public final class ExpressionFactory {

	private ExpressionFactory() {
	}

	public static Expression<? extends Expression, RuntimeContext> expression(MutableVariant var) {
		switch (var.getType()) {
			case BOOL:
				return bool(var.getBool());
			case NUMBER:
				return number(var.getNumber());
			case STRING:
				return string(var.getString(null));
			case FUNCTION:
				FunctionInstance f = var.getFunction();
				f.decl.isCloned = true;
				return f.decl;
			case OBJECT:
				return var.getObject().decl;
			case ARRAY:
				return array(var);
		}

		throw new UnexpectedTypeException(var.getType());
	}

	/**
	 * Build boolean expression from value
	 *
	 * @param context the al context
	 * @param value the value to build with
	 * @return the boolean expression
	 */
	public static BooleanExpression<RuntimeContext> bool(boolean value) {
		BooleanExpression<RuntimeContext> expr = new BooleanExpression<RuntimeContext>(new Token(Syntax.BOOLEAN, String.valueOf(value)));
		return expr;
	}

	/**
	 * Build number expression from value
	 *
	 * @param context the al context
	 * @param value the value to build with
	 * @return the number expression
	 */
	public static NumberExpression<RuntimeContext> number(float value) {
		NumberExpression<RuntimeContext> expr = new NumberExpression<RuntimeContext>(new Token(Syntax.NUMBER, String.valueOf(value)));
		return expr;
	}

	/**
	 * Build string expression from value
	 *
	 * @param context the al context
	 * @param value the value to build with
	 * @return the string expression
	 */
	public static StringExpression<RuntimeContext> string(String value) {
		StringExpression<RuntimeContext> expr = new StringExpression<RuntimeContext>(new Token(Syntax.STRING, value));
		return expr;
	}


	public static ArrayDeclaration<RuntimeContext> array() {
		return new ArrayDeclaration<RuntimeContext>(new Token(Syntax.STRING, "array"));
	}

	public static ArrayDeclaration<RuntimeContext> array(MutableVariant var) {
		ArrayDeclaration expr = new ArrayDeclaration<RuntimeContext>(new Token(Syntax.STRING, "array"));
		for (int i = 0; i < var.size(); i++) {
			expr.addElement(expression(var.getValue(i)));
		}
		return expr;
	}

	public static FunctionDeclaration<RuntimeContext> function(final String name, final MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, String... params) {
		FunctionDeclaration<RuntimeContext> function = new FunctionDeclaration<RuntimeContext>(new Token(Syntax.IDENT, name));
		function.system = true;

		// params
		int size = params.length;
		for (int i = 0; i < size; i++) {
			String paramName = params[i];

			VariableDeclaration param = new VariableDeclaration(new Token(Syntax.IDENT, paramName));
			param.setVar(new Variable(new Token(Syntax.IDENT, paramName)));
			param.var.mv = new MutableVariant(); // ADD
			function.addDeclaration(param);

		}

		// statement
		function.setStatement(new Block(new Token(Syntax.IDENT, "block")));
		function.getStatement().injectVisitor(new StaticMethodVisitorInjector<Block, RuntimeContext>() {
			@Override
			public void visite(Block ast, RuntimeContext context) {
				// backup
				Scope backup = context.scope;
				if (ast.scope == null) {
					ast.scope = new NestedScope("nested", context.scope);
				}
				context.scope = ast.scope;

				// execute visitor
				visitor.visite(ast, context);

				// restore
				context.scope = backup;
			}
		});

		return function;
	}

	public static FunctionInstance functionInstance(RuntimeContext context, final String name, final MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, String... params) {
		return new FunctionInstance(function(name, visitor, params), context.root);
	}

	public static FunctionDeclaration<RuntimeContext> function(RuntimeContext context, Scope enclosing, final String name, final MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, String... params) {
		final FunctionDeclaration<RuntimeContext> function =  ExpressionFactory.function(name, visitor, params);
		function.scope = new ParameterScope(name, enclosing);
		return function;
	}

	public static FunctionInstance functionInstance(RuntimeContext context, Scope enclosing, final String name, final MethodVisitor<Block<RuntimeContext>, RuntimeContext> visitor, String... params) {
		return new FunctionInstance(function(context, enclosing, name, visitor, params), enclosing);
	}
	public static ObjectDeclaration<RuntimeContext> object(String name) {
		return new ObjectDeclaration<RuntimeContext>(new Token(Syntax.IDENT, name));
	}

	public static ObjectInstance objectInstance(RuntimeContext context, final String name) {
		return new ObjectInstance(object(name), context.root);
	}
}
