
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
package fr.cyann.al.visitor;

import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.Break;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.EvalBlock;
import fr.cyann.al.ast.declaration.Declaration;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.For;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.Loop;
import fr.cyann.al.ast.NullExpression;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.ast.Program;
import fr.cyann.al.ast.Return;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.Until;
import fr.cyann.al.ast.ValueExpression;
import fr.cyann.al.ast.While;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Index;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.HashMap;
import java.util.Map;

/**
 * The ToStringTreeVisitor class.
 *
 * @author Yann Caron
 * @version v0.1
 */
public class ToStringTreeVisitor extends ClassMapVisitorInjector {

    // TODO Manage EXPRESSION.NEXT !!!!
    
    /**
     * Default constructor.
     */
    public ToStringTreeVisitor() {
    }

    /**
     * @inheritDoc
     */
    @Override
    protected VisitorInjector getDefault(AST ast) {
	return new MethodVisitorInjector<AST, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(AST ast, ToStringContext context) {
		context.append(String.format("NO VISITOR AVAILABLE for class [%s] !", ast.getClass().getName()));
	    }
	};
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
	Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

	map.put(Comment.class, new MethodVisitorInjector<Comment<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Comment<ToStringContext> ast, ToStringContext context) {
		// do nothing
	    }
	});

	map.put(BooleanExpression.class, new MethodVisitorInjector<BooleanExpression<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(BooleanExpression<ToStringContext> ast, ToStringContext context) {
		context.append(ast.getValue());
	    }
	});

	map.put(NumberExpression.class, new MethodVisitorInjector<NumberExpression<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(NumberExpression<ToStringContext> ast, ToStringContext context) {
		context.append(ast.getValue());
	    }
	});

	map.put(StringExpression.class, new MethodVisitorInjector<StringExpression<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(StringExpression<ToStringContext> ast, ToStringContext context) {
		context.append("\"");
		context.append(ast.getValue());
		context.append("\"");
	    }
	});

	map.put(VariableDeclaration.class, new MethodVisitorInjector<VariableDeclaration<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(VariableDeclaration<ToStringContext> ast, ToStringContext context) {
		context.append("(devar");
		if (ast.getVar() != null) {
		    context.append(" ");

		    if (ast.expr != null) {
			context.append("(= ");
		    }

		    context.append(ast.getVar().getChainName());

		    if (ast.expr != null) {
			context.append(" ");
			ast.expr.visite(context);
			context.append(")");
		    }
		}
		context.append(")");
	    }
	});

	map.put(Variable.class, new MethodVisitorInjector<Variable<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Variable<ToStringContext> ast, ToStringContext context) {
		boolean isCall = false;
		if (ast.hasPrevious()) {
		    context.append(".");
		} else if (ast.getLast() instanceof Call) {
		    isCall = true;
		}

		if (isCall) {
		    context.append('(');
		}
		context.append(ast.getToken().getText());
		if (ast.hasNext()) {
		    ast.next().visite(context);
		}
		if (isCall) {
		    context.append(')');
		}
	    }
	});

	map.put(Call.class, new MethodVisitorInjector<Call<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Call<ToStringContext> ast, ToStringContext context) {
		for (Expression<? extends Expression, ToStringContext> arg : ast.getArgs()) {
		    context.append(" ");
		    arg.visite(context);
		}

		if (ast.hasNext()) {
		    ast.next().visite(context);
		}
	    }
	});

	map.put(Index.class, new MethodVisitorInjector<Index<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Index<ToStringContext> ast, ToStringContext context) {
		context.append("[");
		ast.getExpr().visite(context);
		context.append("]");
		if (ast.hasNext()) {
		    ast.next().visite(context);
		}
	    }
	});

	map.put(Return.class, new MethodVisitorInjector<Return<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Return<ToStringContext> ast, ToStringContext context) {
		context.append("(return ");
		AST.visite(ast.getExpr(), context);
		context.append(")");
	    }
	});

	map.put(ObjectDeclaration.class, new MethodVisitorInjector<ObjectDeclaration<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(ObjectDeclaration<ToStringContext> ast, ToStringContext context) {
		context.append("(decl ");
		context.append(ast.getChainName());
		context.append(" (");
		boolean space = false;
		for (Variable superClass : ast.getSuperClasses()) {
		    if (space) {
			context.append(" ");
		    }
		    space = true;
		    superClass.visite(context);
		}
		context.append(")");
		context.append(" (");
		space = false;
		for (Declaration decl : ast.getDeclarations()) {
		    if (space) {
			context.append(" ");
		    }
		    space = true;
		    decl.visite(context);
		}
		context.append(")");
		context.append(")");
	    }
	});

	map.put(FunctionDeclaration.class, new MethodVisitorInjector<FunctionDeclaration<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(FunctionDeclaration<ToStringContext> ast, ToStringContext context) {
		context.append("(defun ");
		//context.append(ast.getName());
		context.append(" (");
		boolean space = false;
		for (Declaration param : ast.getDeclarations()) {
		    if (space) {
			context.append(" ");
		    }
		    space = true;
		    param.visite(context);
		}
		context.append(")");
		ast.getStatement().visite(context);
		context.append(")");
	    }
	});

	map.put(If.class, new MethodVisitorInjector<If<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(If<ToStringContext> ast, ToStringContext context) {
		context.append("(if ");
		ast.getCondition().visite(context);
		context.append(" ");
		ast.getStatement().visite(context);
		for (If elseIf : ast.getElseIfs()) {
		    elseIf.visite(context);
		}
		if (ast.getElseStatement() != null) {
		    context.append(" ");
		    ast.getElseStatement().visite(context);
		}
		context.append(")");
	    }
	});

	map.put(Loop.class, new MethodVisitorInjector<Loop<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Loop<ToStringContext> ast, ToStringContext context) {
		context.append("(loop ");
		AST.visite(ast.getLimit(), context);
		AST.visite(ast.getStatement(), context);
		context.append(")");
	    }
	});

	map.put(For.class, new MethodVisitorInjector<For<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(For<ToStringContext> ast, ToStringContext context) {
		context.append("(for ");
		AST.visite(ast.getDecl(), context);
		AST.visite(ast.getCondition(), context);
		AST.visite(ast.getIncrement(), context);
		AST.visite(ast.getStatement(), context);
		context.append(")");
	    }
	});

	map.put(While.class, new MethodVisitorInjector<While<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(While<ToStringContext> ast, ToStringContext context) {
		context.append("(while ");
		ast.getCondition().visite(context);
		ast.getStatement().visite(context);
		context.append(")");
	    }
	});

	map.put(Until.class, new MethodVisitorInjector<Until<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Until<ToStringContext> ast, ToStringContext context) {
		context.append("(until ");
		ast.getCondition().visite(context);
		ast.getStatement().visite(context);
		context.append(")");
	    }
	});

	map.put(Break.class, new MethodVisitorInjector<Break<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Break<ToStringContext> ast, ToStringContext context) {
		context.append("(break ");
		context.append(")");
	    }
	});

	map.put(Block.class, new MethodVisitorInjector<Block<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Block<ToStringContext> ast, ToStringContext context) {
		boolean space = false;
		context.append(" (");
		for (AST line : ast.getStatements()) {
		    if (space) {
			context.append(" ");
		    }
		    space = true;
		    line.visite(context);
		}
		context.append(")");
	    }
	});

	map.put(Program.class, new MethodVisitorInjector<Program<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Program<ToStringContext> ast, ToStringContext context) {
		boolean newLine = false;
		for (AST line : ast.getStatements()) {
		    if (newLine) {
			context.append('\n');
		    }
		    newLine = true;
		    line.visite(context);
		}
	    }
	});

	map.put(EvalBlock.class, new MethodVisitorInjector<EvalBlock<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(EvalBlock<ToStringContext> ast, ToStringContext context) {
		boolean newLine = false;
		for (AST line : ast.getStatements()) {
		    if (newLine) {
			context.append('\n');
		    }
		    newLine = true;
		    line.visite(context);
		}
	    }
	});

	map.put(ArrayDeclaration.class, new MethodVisitorInjector<ArrayDeclaration<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(ArrayDeclaration<ToStringContext> ast, ToStringContext context) {
		context.append("[");
		boolean sep = false;
		for (int i = 0; i < ast.getElements().size(); i++) {
		    if (sep) {
			context.append(", ");
		    } else {
			sep = true;
		    }

		    if (i < ast.getKeys().size()) {
			ast.getKeys().get(i).visite(context);
			context.append(":");
		    }

		    ast.getElements().get(i).visite(context);
		}
		context.append("]");
	    }
	});

	map.put(Assignation.class, new MethodVisitorInjector<Assignation<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(Assignation<ToStringContext> ast, ToStringContext context) {
		context.append("(=");
		if (ast.getVar() != null) {
		    context.append(" ");
		    ast.getVar().visite(context);
		}
		if (ast.getExpr() != null) {
		    context.append(" ");
		    ast.getExpr().visite(context);
		}
		context.append(")");
	    }
	});

	map.put(UnaryOperator.class, new MethodVisitorInjector<UnaryOperator<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(UnaryOperator<ToStringContext> ast, ToStringContext context) {
		context.append("(");
		context.append(ast.getSign());
		if (ast.getRight() != null) {
		    context.append(" ");
		    context.append(ast.getRight().getChainName());
		}
		context.append(")");
	    }
	});

	map.put(BinaryOperator.class, new MethodVisitorInjector<BinaryOperator<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(BinaryOperator<ToStringContext> ast, ToStringContext context) {
		context.append("(");
		context.append(ast.getSign());
		if (ast.getLeft() != null) {
		    context.append(" ");
		    context.append(ast.getLeft().getChainName());
		}
		if (ast.getRight() != null) {
		    context.append(" ");
		    context.append(ast.getRight().getChainName());
		}
		context.append(")");
	    }
	});

	// do nothing with them
	map.put(ValueExpression.class, new MethodVisitorInjector<ValueExpression<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(ValueExpression<ToStringContext> ast, ToStringContext context) {
	    }
	});

	map.put(NullExpression.class, new MethodVisitorInjector<NullExpression<ToStringContext>, ToStringContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(NullExpression<ToStringContext> ast, ToStringContext context) {
	    }
	});

	return map;
    }
}
