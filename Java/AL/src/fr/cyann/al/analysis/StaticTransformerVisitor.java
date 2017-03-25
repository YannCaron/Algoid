/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.Break;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.EvalBlock;
import fr.cyann.al.ast.Expression;
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
import fr.cyann.al.ast.ValueExpression;
import fr.cyann.al.ast.While;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.declaration.Declaration;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Index;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.Types;
import fr.cyann.al.symbolTable.DeclarationSymbol;
import fr.cyann.al.symbolTable.FunctionSymbol;
import fr.cyann.al.symbolTable.ObjectSymbol;
import fr.cyann.al.symbolTable.ScopeSymbol;
import fr.cyann.al.symbolTable.Symbol;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.exception.InvalidTokenException;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.NameMapVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import fr.cyann.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author caronyn
 */
public class StaticTransformerVisitor extends ClassMapVisitorInjector {

    private Variable createVariable(AST orig, String name) {
	Variable ast = new Variable(new Token(Syntax.IDENT, name, orig.getToken()));
	ast.setVisitor(getVisitor(ast));
	return ast;
    }

    private Call createVariable(AST orig) {
	Call ast = new Call(new Token(TokenType.SYMBOL, "(", orig.getToken()));
	ast.setVisitor(getVisitor(ast));
	return ast;
    }

    private void replaceVariableBy(Variable ast, Variable by) {
	if (ast.next != null) {
	    by.getLast().setNext(ast.next);
	}

	ast.previous.next = null;
	ast.previous.setNext(by);
    }

    private void visiteUnary(UnaryOperator<TransformContext> ast, TransformContext context, Types type) {
	ast.right.visite(context);

	ast.visitNext(context);
    }

    private void visiteBinary(BinaryOperator<TransformContext> ast, TransformContext context, Types type) {
	ast.getLeft().visite(context);
	ast.getRight().visite(context);

	ast.visitNext(context);
    }

    private DeclarationSymbol tryAutoProperty(Variable ast, TransformContext context) {

	Expression var = ast.getFirst();

	DeclarationSymbol symbol = null;

	if (ast.previous != null) {

	    if (var.getParent() instanceof Assignation && !ast.hasNext()) {

		// setter
		String setterName = "set" + StringUtils.capitalize(ast.partName);
		symbol = context.getScope().find(setterName, ast.getToken().getPos(), false);

		if (symbol != null) {

		    // replace in ast
		    Variable setter = createVariable(ast, setterName);
		    Call call = createVariable(ast);
		    setter.setNext(call);

		    Assignation assign = (Assignation) var.getParent();
		    call.addArg(assign.expr);
		    assign.replaceBy(var);

		    replaceVariableBy(ast, setter);
		}

	    } else {

		// getter
		String getterName = "get" + StringUtils.capitalize(ast.partName);
		symbol = context.getScope().find(getterName, ast.getToken().getPos(), false);

		if (symbol != null) {

		    // replace in ast
		    Variable getter = createVariable(ast, getterName);
		    Call call = createVariable(ast);
		    getter.setNext(call);

		    replaceVariableBy(ast, getter);

		    if (symbol.getExpression() != null && symbol.getExpression() instanceof FunctionSymbol) {
			FunctionSymbol fun = (FunctionSymbol) symbol.getExpression();

			if (fun.getReturnSymbol() != null) {
			    symbol = context.getScope().find(fun.getReturnSymbol().getToken().getText(), ast.getToken().getPos(), false);
			}
		    }
		}

	    }
	}

	return symbol;
    }

    private void tryAutoMethod(Variable ast, TransformContext context) {
	// TODO CONTINUE HERE !!!! DOES NOT WORKS ON ALL CASES
	
	if (ast.hasPrevious() && ast.hasNext() && ast.next instanceof Call) {
	    Expression var = ast.getFirst();
	    Call call = (Call) ast.next;

	    // method
	    DeclarationSymbol symbol = context.getScope().find(ast.partName, ast.getToken().getPos(), false);
	    if (symbol != null && context.getRootScope().equals(symbol.getParent())) {
		var.next = null;
		ast.previous = null;

		call.args.add(0, var);

		var.replaceBy(ast);
	    }
	}
    }

    /**
     * @inheritDoc
     */
    @Override
    protected VisitorInjector getDefault(AST ast) {
	return new MethodVisitorInjector<AST, TransformContext>() {
	    /**
	     * @inheritDoc
	     */
	    @Override
	    public void visite(AST ast, TransformContext context) {
		System.out.println("ERROR NO VISITOR FOUND FOR AST [" + ast.getClass().getName() + "]");
	    }
	};
    }

    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {

	Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

	// misc
	map.put(Comment.class, new MethodVisitorInjector<Comment<TransformContext>, TransformContext>() {

	    /**
	     * Comment visitor. Skip expression.
	     */
	    @Override
	    public void visite(Comment<TransformContext> ast, TransformContext context) {
		// do nothing
	    }
	});

	// structure description
	map.put(VariableDeclaration.class, new MethodVisitorInjector<VariableDeclaration<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(VariableDeclaration<TransformContext> ast, TransformContext context) {

		ast.var.visite(context);

		if (ast.expr != null) {
		    ast.expr.visite(context);
		}

		ast.visitNext(context);
	    }
	});

	map.put(FunctionDeclaration.class, new MethodVisitorInjector<FunctionDeclaration<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(FunctionDeclaration<TransformContext> ast, TransformContext context) {

		for (Declaration parameter : ast.params) {
		    parameter.visite(context);
		}
		ast.statement.visite(context);

		ast.visitNext(context);
	    }
	});

	map.put(ObjectDeclaration.class, new MethodVisitorInjector<ObjectDeclaration<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(ObjectDeclaration<TransformContext> ast, TransformContext context) {
		for (Variable superClass : ast.superClasses) {
		    superClass.visite(context);
		}

		ObjectSymbol o = (ObjectSymbol) context.getScope().find(ast.getToken());
		context.enterScope(o);
		// TOTO To be continued

		for (Declaration attribute : ast.attributs) {
		    attribute.visite(context);
		}

		context.exitScope();

	    }
	});

	map.put(ArrayDeclaration.class, new MethodVisitorInjector<ArrayDeclaration<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(ArrayDeclaration<TransformContext> ast, TransformContext context) {
		for (Expression element : ast.elements) {
		    element.visite(context);
		}

		ast.visitNext(context);
	    }
	});

	// structure using
	map.put(Variable.class, new MethodVisitorInjector<Variable<TransformContext>, TransformContext>() {

	    /*public boolean isObject(DeclarationSymbol symbol) {
	     if (symbol == null || symbol.getExpression() == null) return false;
		
	     Symbol expr = symbol.getExpression();
		
	     // check native source
	     if (symbol.getExpression() instanceof ObjectSymbol) return true;
		
	     // check modified source
	     if (!(expr instanceof FunctionSymbol)) return false;
	     FunctionSymbol fun = (FunctionSymbol) expr;
		
	     if (fun.getReturnSymbol() == null) return false;
		
	     return fun.getReturnSymbol().getType() == Types.OBJECT;
	     }*/
	    @Override
	    public void visite(Variable ast, TransformContext context) {

		DeclarationSymbol symbol = tryAutoProperty(ast, context);
		tryAutoMethod(ast, context);

		Expression var = ast.getFirst();
		boolean isForWrite = (var.getParent() instanceof Assignation);

		if (symbol == null) {
		    symbol = context.getScope().find(ast.partName, ast.getToken().getPos(), isForWrite);
		}

		ObjectSymbol o = null;
		if (symbol != null && symbol.getExpression() != null && symbol.getExpression() instanceof ObjectSymbol) {
		    o = (ObjectSymbol) symbol.getExpression();
		}

		if (o != null) {
		    context.enterScope(o);
		}

		ast.visitNext(context);

		if (o != null) {
		    context.exitScope();
		}
	    }
	});

	map.put(Call.class, new MethodVisitorInjector<Call<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(Call<TransformContext> ast, TransformContext context) {

		for (Expression arg : ast.args) {
		    arg.visite(context);
		}

		ast.visitNext(context);
	    }
	});

	map.put(Index.class, new MethodVisitorInjector<Index<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(Index<TransformContext> ast, TransformContext context) {

		ast.expr.visite(context);

		ast.visitNext(context);
	    }
	});

	// expression
	map.put(NullExpression.class, new MethodVisitorInjector<NullExpression<TransformContext>, TransformContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NullExpression<TransformContext> ast, TransformContext context) {
		ast.visitNext(context);
	    }
	});

	map.put(ValueExpression.class, new MethodVisitorInjector<ValueExpression<TransformContext>, TransformContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(ValueExpression<TransformContext> ast, TransformContext context) {
		ast.visitNext(context);
	    }
	});

	map.put(BooleanExpression.class, new MethodVisitorInjector<BooleanExpression<TransformContext>, TransformContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(BooleanExpression<TransformContext> ast, TransformContext context) {
		ast.visitNext(context);
	    }
	});

	map.put(NumberExpression.class, new MethodVisitorInjector<NumberExpression<TransformContext>, TransformContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NumberExpression<TransformContext> ast, TransformContext context) {
		ast.visitNext(context);
	    }
	});

	map.put(StringExpression.class, new MethodVisitorInjector<StringExpression<TransformContext>, TransformContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(StringExpression<TransformContext> ast, TransformContext context) {
		ast.visitNext(context);
	    }
	});

	map.put(Assignation.class, new MethodVisitorInjector<Assignation<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(Assignation<TransformContext> ast, TransformContext context) {
		ast.expr.visite(context);

		ast.var.visite(context);

		ast.visitNext(context);
	    }
	});

	map.put(UnaryOperator.class, new NameMapVisitorInjector() {

	    /**
	     * UnaryOperation visitor. Mapped on ast.findName() value. Based on
	     * Visitor / Composite design patterns.
	     */
	    @Override
	    protected Map<String, VisitorInjector> getVisitorMap() {
		Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

		map.put("!", new MethodVisitorInjector<UnaryOperator<TransformContext>, TransformContext>() {

		    /**
		     * Unary not operator. ! BooleanExpression;
		     */
		    @Override
		    public void visite(UnaryOperator<TransformContext> ast, TransformContext context) {
			visiteUnary(ast, context, Types.BOOL);
		    }
		});

		map.put("-", new MethodVisitorInjector<UnaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(UnaryOperator<TransformContext> ast, TransformContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("++", new MethodVisitorInjector<UnaryOperator<TransformContext>, TransformContext>() {

		    /**
		     * Increment unary operator. NumberExpression ++
		     */
		    @Override
		    public void visite(UnaryOperator<TransformContext> ast, TransformContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("--", new MethodVisitorInjector<UnaryOperator<TransformContext>, TransformContext>() {

		    /**
		     * DeDcrement unary operator. NumberExpression ++
		     */
		    @Override
		    public void visite(UnaryOperator<TransformContext> ast, TransformContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("new", new MethodVisitorInjector<UnaryOperator, TransformContext>() {

		    @Override
		    public void visite(UnaryOperator ast, TransformContext context) {
			visiteUnary(ast, context, Types.OBJECT);
		    }
		});
		return map;
	    }

	    /**
	     * Executed in the case of value not found. Throws exception.
	     */
	    @Override
	    protected VisitorInjector getDefault(AST ast) {
		throw new InvalidTokenException(ast.getToken(), UnaryOperator.class);
	    }
	});

	map.put(BinaryOperator.class, new NameMapVisitorInjector() {

	    /**
	     * BinaryOperator visitor. Mapped on ast.findName() value. Based on
	     * Visitor / Composite design patterns.
	     */
	    @Override
	    protected Map<String, VisitorInjector> getVisitorMap() {
		Map<String, VisitorInjector> map = new HashMap<String, VisitorInjector>();

		map.put("&&", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("||", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("==", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("!=", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("<", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put(">", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("<=", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put(">=", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("+", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    /**
		     * The plus number binary operator
		     */
		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("-", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("*", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("/", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("%", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("..", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.STRING);
		    }
		});

		map.put("->", new MethodVisitorInjector<BinaryOperator<TransformContext>, TransformContext>() {

		    @Override
		    public void visite(BinaryOperator<TransformContext> ast, TransformContext context) {
			visiteBinary(ast, context, Types.FUNCTION);
		    }
		});

		return map;
	    }

	    /**
	     * Executed in the case of value not found. Throws exception.
	     */
	    @Override
	    protected VisitorInjector getDefault(AST ast) {
		throw new InvalidTokenException(ast.getToken(), BinaryOperator.class);
	    }
	});
	// scope
	map.put(Program.class, new MethodVisitorInjector<Program<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(Program<TransformContext> ast, TransformContext context) {
		for (AST statement : ast.statements) {
		    statement.visite(context);
		}
	    }
	});

	map.put(Block.class, new MethodVisitorInjector<Block<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(Block<TransformContext> ast, TransformContext context) {

		ScopeSymbol scope = (ScopeSymbol) context.getScope().find(ast.getToken());
		context.enterScope(scope);

		for (AST statement : ast.statements) {
		    statement.visite(context);
		}

		context.exitScope();
	    }
	});

	map.put(EvalBlock.class, new MethodVisitorInjector<EvalBlock<TransformContext>, TransformContext>() {

	    @Override
	    public void visite(EvalBlock<TransformContext> ast, TransformContext context) {
		for (AST statement : ast.statements) {
		    statement.visite(context);
		}
	    }
	});

	// imperative statements
	map.put(If.class, new MethodVisitorInjector<If<TransformContext>, TransformContext>() {

	    /**
	     * If statement visitor.
	     */
	    @Override
	    public void visite(If<TransformContext> ast, TransformContext context) {
		ast.condition.visite(context);
		ast.trueStatement.visite(context);
		for (If _if : ast.elseIfs) {
		    _if.visite(context);
		}
		ast.falseStatement.visite(context);

		ast.visitNext(context);
	    }
	});

	map.put(Loop.class, new MethodVisitorInjector<Loop<TransformContext>, TransformContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Loop<TransformContext> ast, TransformContext context) {
		ast.limit.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(For.class, new MethodVisitorInjector<For<TransformContext>, TransformContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(For<TransformContext> ast, TransformContext context) {
		ast.decl.visite(context);
		ast.condition.visite(context);
		ast.increment.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(While.class, new MethodVisitorInjector<While<TransformContext>, TransformContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(While<TransformContext> ast, TransformContext context) {
		ast.condition.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(Until.class, new MethodVisitorInjector<Until<TransformContext>, TransformContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Until<TransformContext> ast, TransformContext context) {
		ast.condition.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(Break.class, new MethodVisitorInjector<Break<TransformContext>, TransformContext>() {

	    /**
	     * Break statement visitor. Throw break exception.
	     */
	    @Override
	    public void visite(Break<TransformContext> ast, TransformContext context) {
	    }
	});

	map.put(Return.class, new MethodVisitorInjector<Return<TransformContext>, TransformContext>() {

	    /**
	     * Return statement visitor. Throw return exception.
	     */
	    @Override
	    public void visite(Return<TransformContext> ast, TransformContext context) {
		// visite expression
		if (ast.expr != null) {
		    ast.expr.visite(context);
		}
	    }
	});

	return map;
    }
;

}
