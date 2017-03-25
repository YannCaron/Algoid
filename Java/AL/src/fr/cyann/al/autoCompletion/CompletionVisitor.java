/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.autoCompletion;

import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.Break;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.EvalBlock;
import fr.cyann.al.ast.For;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.Loop;
import fr.cyann.al.ast.NullExpression;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.ast.Program;
import fr.cyann.al.ast.Return;
import fr.cyann.al.ast.StringExpression;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.Until;
import fr.cyann.al.ast.While;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Index;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * @author caronyn
 */
public class CompletionVisitor extends ClassMapVisitorInjector {

    /**
     * @inheritDoc
     */
    @Override
    protected VisitorInjector getDefault(AST ast) {
	return new MethodVisitorInjector<AST, Context>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(AST ast, Context context) {
		System.out.println("ERROR NO VISITOR FOUND FOR AST [" + ast.getClass().getName() + "]");
	    }
	};
    }

    private static boolean isIn(int pos, AST ast) {
	if (ast.getEndToken() == null) {
	    return false;
	}
	return pos >= ast.getToken().getPos() && pos <= ast.getEndToken().getPos();
    }

    private static void shouldContinue(int pos, AST ast) {
	if (pos <= ast.getEndToken().getPos()) {
	    throw new CompletionFinishedException();
	}
    }

    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
	Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

	// misc
	map.put(Comment.class, new MethodVisitorInjector<Comment<CompletionContext>, CompletionContext>() {

	    /**
	     * Comment visitor. Skip expression.
	     */
	    @Override
	    public void visite(Comment<CompletionContext> ast, CompletionContext context) {
		// do nothing
	    }
	});

	// structure description
	map.put(VariableDeclaration.class, new MethodVisitorInjector<VariableDeclaration<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(VariableDeclaration<CompletionContext> ast, CompletionContext context) {
		context.addKeywordPart(ast.var.partName);

		if (ast.hasNext()) {
		    ast.next.visite(context);
		} else {
		    context.clearKeywordPart();
		}

		if (ast.expr != null) {
		    ast.expr.visite(context);
		}
	    }
	});

	map.put(FunctionDeclaration.class, new MethodVisitorInjector<FunctionDeclaration<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(FunctionDeclaration<CompletionContext> ast, CompletionContext context) {
		// context
		StringBuilder sb = new StringBuilder();
		sb.append("function (");

		boolean first = true;
		for (VariableDeclaration<CompletionContext> param : ast.params) {
		    if (!first) {
			sb.append(", ");
		    }
		    first = false;
		    sb.append(param.var.partName);
		}
		sb.append(")");
		context.addDescription(sb.toString());

		// add variables
		if (isIn(context.getPos(), ast.statement)) {

		    for (VariableDeclaration<CompletionContext> param : ast.params) {
			context.addKeyword(param.var.partName);
		    }

		    ast.statement.visite(context);
		}

		shouldContinue(context.getPos(), ast.statement);
	    }
	});

	map.put(ObjectDeclaration.class, new MethodVisitorInjector<ObjectDeclaration<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(ObjectDeclaration<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(ArrayDeclaration.class, new MethodVisitorInjector<ArrayDeclaration<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(ArrayDeclaration<CompletionContext> ast, CompletionContext context) {
	    }
	});

	// structure using
	map.put(Variable.class, new MethodVisitorInjector<Variable<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Variable<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Call.class, new MethodVisitorInjector<Call<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Call<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Index.class, new MethodVisitorInjector<Index<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Index ast, CompletionContext context) {
	    }
	});

	// expression
	map.put(NullExpression.class, new MethodVisitorInjector<NullExpression<CompletionContext>, CompletionContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NullExpression<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(BooleanExpression.class, new MethodVisitorInjector<BooleanExpression<CompletionContext>, CompletionContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(BooleanExpression<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(NumberExpression.class, new MethodVisitorInjector<NumberExpression<CompletionContext>, CompletionContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NumberExpression<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(StringExpression.class, new MethodVisitorInjector<StringExpression<CompletionContext>, CompletionContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(StringExpression<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Assignation.class, new MethodVisitorInjector<Program<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Program<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(UnaryOperator.class, new MethodVisitorInjector<Program<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Program<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(BinaryOperator.class, new MethodVisitorInjector<Program<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Program<CompletionContext> ast, CompletionContext context) {
	    }
	});

	// scope
	map.put(Program.class, new MethodVisitorInjector<Program<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Program<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Block.class, new MethodVisitorInjector<Block<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(Block<CompletionContext> ast, CompletionContext context) {
		if (!isIn(context.getPos(), ast)) {
		    return;
		}

		for (AST<? extends AST, CompletionContext> statement : ast.statements) {
		    statement.visite(context);

		    if (statement.getToken().getPos() >= context.getPos()) {
			throw new CompletionFinishedException();
		    }
		}
	    }
	});

	map.put(EvalBlock.class, new MethodVisitorInjector<EvalBlock<CompletionContext>, CompletionContext>() {

	    @Override
	    public void visite(EvalBlock<CompletionContext> ast, CompletionContext context) {
	    }
	});

	// imperative statements
	map.put(If.class, new MethodVisitorInjector<If<CompletionContext>, CompletionContext>() {

	    /**
	     * If statement visitor.
	     */
	    @Override
	    public void visite(If<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Loop.class, new MethodVisitorInjector<Loop<CompletionContext>, CompletionContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Loop<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(For.class, new MethodVisitorInjector<For<CompletionContext>, CompletionContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(For<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(While.class, new MethodVisitorInjector<While<CompletionContext>, CompletionContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(While<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Until.class, new MethodVisitorInjector<Until<CompletionContext>, CompletionContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Until<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Break.class, new MethodVisitorInjector<Break<CompletionContext>, CompletionContext>() {

	    /**
	     * Break statement visitor. Throw break exception.
	     */
	    @Override
	    public void visite(Break<CompletionContext> ast, CompletionContext context) {
	    }
	});

	map.put(Return.class, new MethodVisitorInjector<Return<CompletionContext>, CompletionContext>() {

	    /**
	     * Return statement visitor. Throw return exception.
	     */
	    @Override
	    public void visite(Return<CompletionContext> ast, CompletionContext context) {
	    }
	});

	return map;
    }

}
