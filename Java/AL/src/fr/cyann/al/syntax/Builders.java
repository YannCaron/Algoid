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
package fr.cyann.al.syntax;

import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.For;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.Return;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.Until;
import fr.cyann.al.ast.While;
import fr.cyann.al.ast.declaration.Declaration;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.interfaces.Conditionable;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Index;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.ast.EOG;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.FactoryStrategy;
import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.parser.StatementLeafToken;
import fr.cyann.jasi.parser.StatementNode;

/**
 * <p>
 * @author ops
 */
public final class Builders {

    private static void ignoreComment(InterpreterBuilder builder) {

	while (builder.peek() instanceof Comment) {
	    builder.pop();
	}
    }
    /**
     * ID format string.
     */
    public static final String ID_FORMAT = "#ID%d";

    // hide constructor according sonar rule
    private Builders() {
    }

    /**
     * Create the StringExpression AST
     */
    public static class StringFactory implements FactoryStrategy {

	/**
	 * @inheritDoc
	 */
	@Override
	public void buildLeaf(InterpreterBuilder builder, StatementLeafToken statement) {

	    String value = statement.getToken().getText();
	    value = value.substring(1, value.length() - 1);
	    value = value.replace("\\n", "\n");
	    value = value.replace("\\t", "\t");
	    value = value.replace("\\\\", "\\");

	    // clone
	    Token tk = statement.getToken();
	    Token token = new Token(tk.getType(), value, tk.getLine(), tk.getCol() + 1, tk.getPos() + 1);

	    AST str = new StringExpression(token);
	    builder.push(str);
	}
    }

    /**
     * Agregate the ASTs into Block one
     */
    public static class BlockAgregator implements AgregatorStrategy {

	/**
	 * @inheritDoc
	 */
	@Override
	public void build(InterpreterBuilder builder, StatementNode node) {
	    AST ast = builder.pop();
	    Block block = (Block) builder.pop();

	    block.addStatement(ast);
	    builder.push(block);

	}
    }

    public static class BlockTerminator implements AgregatorStrategy {

	/**
	 * @inheritDoc
	 */
	@Override
	public void build(InterpreterBuilder builder, StatementNode node) {
	    EOG eog = (EOG) builder.pop();
	    Block block = (Block) builder.pop();

	    //if (block.statements.size() > 0) {
	    block.setEndToken(eog.getToken());
	    //}

	    builder.push(block);
	}
    }

    /**
     * Used to fold
     */
    public static class Function {

	/**
	 * Agregate the ASTs into Call one
	 */
	public static class CallAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Call call = (Call) builder.pop();
		Expression symb = (Expression) builder.pop();

		symb.setNext(call);

		builder.push(symb);
	    }
	}

	/**
	 * Agregate the ASTs into Function declaration one
	 */
	public static class FunctionAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {
		Block block = (Block) builder.pop();
		FunctionDeclaration function = (FunctionDeclaration) builder.pop();

		function.setStatement(block);
		builder.push(function);

	    }
	}

	/**
	 * Agregate the ASTs into Parameter one
	 */
	public static class ParameterAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		VariableDeclaration decl = (VariableDeclaration) builder.pop();
		FunctionDeclaration function = (FunctionDeclaration) builder.pop();

		function.addDeclaration(decl);
		builder.push(function);

	    }
	}

	/**
	 * Agregate the ASTs into Lambda declaration one
	 */
	public static class LambdaAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Block block = (Block) builder.pop();
		AST ast = builder.peek();

		FunctionDeclaration function;
		if (ast != null && ast instanceof FunctionDeclaration) {
		    function = (FunctionDeclaration) builder.pop();
		} else {
		    function = new FunctionDeclaration(block.getToken());
		}

		function.setStatement(block);
		builder.push(function);

	    }
	}

	/**
	 * Agregate the ASTs into Parameter one
	 */
	public static class LambdaParameterAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		VariableDeclaration decl = (VariableDeclaration) builder.pop();
		AST ast = builder.peek();

		FunctionDeclaration function;
		if (ast != null && ast instanceof FunctionDeclaration) {
		    function = (FunctionDeclaration) builder.pop();
		} else {
		    function = new FunctionDeclaration(decl.getToken());
		}

		function.addDeclaration(decl);
		builder.push(function);

	    }
	}

	/**
	 * Agregate the ASTs into Return one
	 */
	public static class ReturnAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Return ret = (Return) builder.pop();

		ret.setExpr(expr);
		builder.push(ret);

	    }
	}
    }

    /**
     * Used to fold
     */
    public static class Obj {

	/**
	 * Agregate the ASTs into Attribute declaration one
	 */
	public static class AttributeDeclarationAgregator implements AgregatorStrategy {

	    /**
	     * Agregate the ASTs into Return one
	     */
	    private Declaration getDefaultDeclaration(Variable var) {
		VariableDeclaration decl = new VariableDeclaration(var.getToken());
		decl.setVar(var);
		return decl;
	    }

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST ast = builder.pop();

		if (!builder.isEmpty()) {
		    AST op = builder.pop();

		    if (op instanceof VariableDeclaration) {
			VariableDeclaration decl = ((VariableDeclaration) op);
			Variable var = (Variable) builder.pop();

			decl.setVar(var);
			decl.setExpr((Expression) ast);
			builder.push(decl);
		    } else {
			builder.push(op);
			builder.push(getDefaultDeclaration((Variable) ast));
		    }
		} else {
		    builder.push(getDefaultDeclaration((Variable) ast));
		}
	    }
	}

	/**
	 * Agregate the ASTs into Attribute list one
	 */
	public static class AttributeListAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		VariableDeclaration decl = (VariableDeclaration) builder.pop();

		ignoreComment(builder);
		ObjectDeclaration class_ = (ObjectDeclaration) builder.pop();

		class_.addDeclaration(decl);
		builder.push(class_);

	    }
	}

	/**
	 * Agregate the ASTs into Inheritance one
	 */
	public static class InheritanceAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Variable superClass = (Variable) builder.pop();
		ObjectDeclaration class_ = (ObjectDeclaration) builder.pop();

		class_.addSuperClass(superClass);
		builder.push(class_);
	    }
	}

    }

    /**
     * Used to fold
     */
    public static class Expr {

	/**
	 * Agregate the ASTs into Function declaration one
	 */
	public static class VariableAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Variable var = (Variable) builder.pop();
		VariableDeclaration variable = (VariableDeclaration) builder.pop();

		variable.setVar(var);

		builder.push(variable);

	    }
	}

	/**
	 * Agregate the optional assignment of variable
	 */
	public static class VariableAssignAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		ignoreComment(builder);
		Expression expr = (Expression) builder.pop();
		Variable var = (Variable) builder.pop();
		VariableDeclaration variable = (VariableDeclaration) builder.pop();

		variable.setExpr(expr);

		builder.push(variable);
		builder.push(var);

	    }
	}

	/**
	 * Agregate the ASTs into Argument one
	 */
	public static class ArgumentAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Call call = (Call) builder.pop();
		call.addArg(expr);
		builder.push(call);

	    }
	}

	/**
	 * Agregate the ASTs into Assignment one
	 */
	public static class AssignmentAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Assignation assign = (Assignation) builder.pop();
		Variable var = (Variable) builder.pop();

		assign.setVar(var);
		assign.setExpr(expr);

		builder.push(assign);

	    }
	}

	/**
	 * Agregate the ASTs into Binary operation one
	 */
	public static class BinaryAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression right = (Expression) builder.pop();
		BinaryOperator op = (BinaryOperator) builder.pop();
		Expression left = (Expression) builder.pop();

		// construct
		op.setLeft(left);
		op.setRight(right);

		// set on top
		builder.push(op);
	    }
	}

	/**
	 * Agregate the ASTs into Unary left operation one
	 */
	public static class UnaryLeftAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		UnaryOperator op = (UnaryOperator) builder.pop();

		op.setRight(expr);
		builder.push(op);
	    }
	}

	/**
	 * Agregate the ASTs into Unary right operation one
	 */
	public static class UnaryRightAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST ast = builder.pop();

		if (ast instanceof UnaryOperator) {
		    UnaryOperator op = (UnaryOperator) ast;
		    if (op.getRight() == null) {
			Expression expr = (Expression) builder.pop();
			op.setRight(expr);
		    }
		    builder.push(op);
		} else {
		    builder.push(ast);
		}

	    }
	}

	/**
	 * Agregate the ASTs into Variable index (array) one
	 */
	public static class VariableIndexAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Expression var = (Expression) builder.pop();

		Index index = new Index(var.getToken());
		index.setExpr(expr);

		var.setNext(index);
		builder.push(var);

	    }
	}

	/**
	 * Agregate the ASTs into Variable next for variable chain one
	 */
	public static class VariableNextAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression var2 = (Expression) builder.pop();
		Expression var1 = (Expression) builder.pop();

		var1.setNext(var2);

		builder.push(var1);
	    }
	}

	/**
	 * Agregate the ASTs into ArrayDeclaration chain one
	 */
	public static class VariableArrayAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();

		ArrayDeclaration array = (ArrayDeclaration) builder.pop();
		array.addElement(expr);

		builder.push(array);
	    }
	}

	/**
	 * Agregate the ASTs into ArrayDeclaration chain one
	 */
	public static class VariableDictAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Expression key = (Expression) builder.pop();

		ArrayDeclaration array = (ArrayDeclaration) builder.pop();
		array.addKey(key);

		builder.push(array);
		builder.push(expr);
	    }
	}
    }

    /**
     * Used to fold
     */
    public static class Condition {

	/**
	 * Agregate the ASTs into Conditionable one
	 */
	public static class ConditionableAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		Conditionable cond = (Conditionable) builder.pop();

		cond.setCondition(expr);

		builder.push((AST) cond);

	    }
	}

	/**
	 * Agregate the ASTs into Else one
	 */
	public static class ElseAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		If _if = (If) builder.pop();

		_if.setElseStatement(ALTools.lazyBlock(statement));

		builder.push(_if);

	    }
	}

	/**
	 * Agregate the ASTs into ElseIf one
	 */
	public static class ElseIfAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		If elseif_ = (If) builder.pop();
		If if_ = (If) builder.pop();

		if_.addElseIf(elseif_);

		builder.push(if_);

	    }
	}

	/**
	 * Agregate the ASTs into If false statement one
	 */
	public static class IfFalseAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		If _if = (If) builder.pop();

		_if.setElseStatement(ALTools.lazyBlock(statement));

		builder.push(_if);

	    }
	}

	/**
	 * Agregate the ASTs into If true statement one
	 */
	public static class IfTrueAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		If _if = (If) builder.pop();

		_if.setStatement(ALTools.lazyBlock(statement));

		builder.push(_if);

	    }
	}
    }

    /**
     * Used to fold
     */
    public static class Loop {

	/**
	 * Agregate the ASTs into Loop one
	 */
	public static class LoopAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		fr.cyann.al.ast.Loop loop = (fr.cyann.al.ast.Loop) builder.pop();

		loop.setStatement(ALTools.lazyBlock(statement));
		builder.push(loop);

	    }
	}

	/**
	 * Agregate the ASTs into Loop limit one
	 */
	public static class LoopLimitAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		fr.cyann.al.ast.Loop loop = (fr.cyann.al.ast.Loop) builder.pop();

		loop.setLimit(expr);
		builder.push(loop);

	    }
	}

	/**
	 * Agregate the ASTs into For one
	 */
	public static class ForAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		For for_ = (For) builder.pop();

		for_.setStatement(ALTools.lazyBlock(statement));

		builder.push(for_);
	    }
	}

	/**
	 * Agregate the ASTs into For assignment one
	 */
	public static class ForAssignmentAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		For for_ = (For) builder.pop();

		for_.setDecl(expr);
		builder.push(for_);

	    }
	}

	/**
	 * Agregate the ASTs into For increment one
	 */
	public static class ForIncrementAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Expression expr = (Expression) builder.pop();
		For for_ = (For) builder.pop();

		for_.setIncrement(expr);
		builder.push(for_);

	    }
	}

	/**
	 * Agregate the ASTs into Until one
	 */
	public static class UntilAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		Until until_ = (Until) builder.pop();
		AST statement = builder.pop();

		until_.setStatement(ALTools.lazyBlock(statement));
		builder.push(until_);

	    }
	}

	/**
	 * Agregate the ASTs into While one
	 */
	public static class WhileAgregator implements AgregatorStrategy {

	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void build(InterpreterBuilder builder, StatementNode node) {

		AST statement = builder.pop();
		While while_ = (While) builder.pop();

		while_.setStatement(ALTools.lazyBlock(statement));
		builder.push(while_);

	    }
	}
    }
}
