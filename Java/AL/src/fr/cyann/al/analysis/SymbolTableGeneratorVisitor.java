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
import fr.cyann.al.symbolTable.ArraySymbol;
import fr.cyann.al.symbolTable.BinarySymbol;
import fr.cyann.al.symbolTable.CallSymbol;
import fr.cyann.al.symbolTable.DeclarationSymbol;
import fr.cyann.al.symbolTable.FunctionSymbol;
import fr.cyann.al.symbolTable.IdentifierSymbol;
import fr.cyann.al.symbolTable.IndexSymbol;
import fr.cyann.al.symbolTable.ObjectSymbol;
import fr.cyann.al.symbolTable.ReturnSymbol;
import fr.cyann.al.symbolTable.ScopeSymbol;
import fr.cyann.al.symbolTable.Symbol;
import fr.cyann.al.symbolTable.UnarySymbol;
import fr.cyann.al.symbolTable.ValueSymbol;
import fr.cyann.al.symbolTable.VariableSymbol;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.exception.InvalidTokenException;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitorInjector;
import fr.cyann.jasi.visitor.NameMapVisitorInjector;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * @author caronyn
 */
public class SymbolTableGeneratorVisitor extends ClassMapVisitorInjector {

    private static final Set<String> WEAK_SIGN = new HashSet<String>(Arrays.asList("==", "!=", "<", "<=", ">", ">="));

    private boolean enforce(AST ast) {
	return !WEAK_SIGN.contains(ast.getToken().getText());
    }

    private void visiteUnary(UnaryOperator<SymbolTableContext> ast, SymbolTableContext context, Types type) {
	ast.right.visite(context);
	Symbol rightSymbol = context.getCurrentScope().pop();

	Symbol symbol = new UnarySymbol(ast.getToken(), context.getCurrentScope(), type, enforce(ast), rightSymbol);
	context.getCurrentScope().push(symbol);

	ast.visitNext(context);
    }

    private void visiteBinary(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context, Types type) {
	ast.left.visite(context);
	Symbol leftSymbol = context.getCurrentScope().pop();

	ast.right.visite(context);
	Symbol rightSymbol = context.getCurrentScope().pop();

	Symbol symbol = new BinarySymbol(ast.getToken(), context.getCurrentScope(), type, enforce(ast), leftSymbol, rightSymbol);
	context.getCurrentScope().push(symbol);

	ast.visitNext(context);
    }

    private void pushIdentifier(Expression ast, SymbolTableContext context, IdentifierSymbol symbol) {
	if (ast.hasPrevious()) {
	    Symbol previous = context.getCurrentScope().getLast();
	    if (previous instanceof IdentifierSymbol) {
		((IdentifierSymbol) previous).setNext(symbol);
	    }
	} else {
	    context.getCurrentScope().push(symbol);
	}
    }

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

    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
	Map<Class<? extends AST>, VisitorInjector> map = new HashMap<Class<? extends AST>, VisitorInjector>();

	// misc
	map.put(Comment.class, new MethodVisitorInjector<Comment<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * Comment visitor. Skip expression.
	     */
	    @Override
	    public void visite(Comment<SymbolTableContext> ast, SymbolTableContext context) {
		// do nothing
	    }
	});

	// structure description
	map.put(VariableDeclaration.class, new MethodVisitorInjector<VariableDeclaration<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(VariableDeclaration<SymbolTableContext> ast, SymbolTableContext context) {

		ast.var.visite(context);
		VariableSymbol identifier = (VariableSymbol) context.getCurrentScope().pop();

		DeclarationSymbol symbol = new DeclarationSymbol(ast.getToken(), context.getCurrentScope(), identifier.getName());
		context.getCurrentScope().push(symbol);

		if (ast.expr != null) {
		    ast.expr.visite(context);

		    Symbol lastSymbol = context.getCurrentScope().pop();
		    symbol.setExpression(lastSymbol);
		}

		ast.visitNext(context);
	    }
	});

	map.put(FunctionDeclaration.class, new MethodVisitorInjector<FunctionDeclaration<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(FunctionDeclaration<SymbolTableContext> ast, SymbolTableContext context) {
		ScopeSymbol parent = context.getCurrentScope();
		FunctionSymbol symbol = new FunctionSymbol(ast.getToken(), parent);
		symbol.setType(Types.FUNCTION);
		parent.push(symbol);
		context.enterScope(symbol);

		for (Declaration parameter : ast.params) {
		    parameter.visite(context);
		    ((DeclarationSymbol) context.getCurrentScope().getLast()).setMember(true);
		}
		ast.statement.visite(context);

		context.exitScope();

		ast.visitNext(context);
	    }
	});

	map.put(ObjectDeclaration.class, new MethodVisitorInjector<ObjectDeclaration<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(ObjectDeclaration<SymbolTableContext> ast, SymbolTableContext context) {
		ScopeSymbol parent = context.getCurrentScope();
		ObjectSymbol symbol = new ObjectSymbol(ast.getToken(), parent);
		symbol.setType(Types.OBJECT);
		parent.push(symbol);
		context.enterScope(symbol);

		for (Variable superClass : ast.superClasses) {
		    superClass.visite(context);
		    VariableSymbol vs = (VariableSymbol) context.getCurrentScope().pop();

		    DeclarationSymbol decl = context.getCurrentScope().find(vs.getName(), vs.getToken().getPos(), false);
		    if (decl == null) {
			System.err.printf("Variable [%s] not found!\n", vs.getName());
			return;
		    }

		    if (!(decl.getExpression() instanceof ObjectSymbol)) {
			System.err.printf("Variable [%s] is not an object! Object must inherits from another object!\n", vs.getName());
			return;
		    }
		    symbol.addSuperClass((ObjectSymbol) decl.getExpression());

		}

		for (Declaration attribute : ast.attributs) {
		    attribute.visite(context);
		    ((DeclarationSymbol) context.getCurrentScope().getLast()).setMember(true);
		}

		context.exitScope();

		ast.visitNext(context);
	    }
	});

	map.put(ArrayDeclaration.class, new MethodVisitorInjector<ArrayDeclaration<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(ArrayDeclaration<SymbolTableContext> ast, SymbolTableContext context) {
		ArraySymbol symbol = new ArraySymbol(ast.getToken(), context.getCurrentScope());
		context.getCurrentScope().push(symbol);

		for (Expression element : ast.elements) {
		    element.visite(context);
		}

		context.exitScope();

		ast.visitNext(context);
	    }
	});

	// structure using
	map.put(Variable.class, new MethodVisitorInjector<Variable<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Variable<SymbolTableContext> ast, SymbolTableContext context) {

		VariableSymbol symbol = new VariableSymbol(ast.getToken(), context.getCurrentScope(), ast.partName);
		pushIdentifier(ast, context, symbol);

		ast.visitNext(context);
	    }
	});

	map.put(Call.class, new MethodVisitorInjector<Call<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Call<SymbolTableContext> ast, SymbolTableContext context) {

		CallSymbol symbol = new CallSymbol(ast.getToken(), context.getCurrentScope());
		pushIdentifier(ast, context, symbol);

		for (Expression arg : ast.args) {
		    arg.visite(context);
		    Symbol a = context.getCurrentScope().pop();
		    symbol.addArgument(a);
		}

		ast.visitNext(context);
	    }
	});

	map.put(Index.class, new MethodVisitorInjector<Index<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Index<SymbolTableContext> ast, SymbolTableContext context) {

		ast.expr.visite(context);
		Symbol expr = context.getCurrentScope().pop();

		IndexSymbol symbol = new IndexSymbol(ast.getToken(), context.getCurrentScope(), expr);
		pushIdentifier(ast, context, symbol);

		ast.visitNext(context);
	    }
	});

	// expression
	map.put(NullExpression.class, new MethodVisitorInjector<NullExpression<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NullExpression<SymbolTableContext> ast, SymbolTableContext context) {
		context.getCurrentScope().push(new ValueSymbol(ast.getToken(), context.getCurrentScope(), Types.VOID));

		ast.visitNext(context);
	    }
	});

	map.put(ValueExpression.class, new MethodVisitorInjector<ValueExpression<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(ValueExpression<SymbolTableContext> ast, SymbolTableContext context) {
		context.getCurrentScope().push(new ValueSymbol(ast.getToken(), context.getCurrentScope(), Types.VOID));

		ast.visitNext(context);
	    }
	});

	map.put(BooleanExpression.class, new MethodVisitorInjector<BooleanExpression<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(BooleanExpression<SymbolTableContext> ast, SymbolTableContext context) {
		context.getCurrentScope().push(new ValueSymbol(ast.getToken(), context.getCurrentScope(), Types.BOOL));

		ast.visitNext(context);
	    }
	});

	map.put(NumberExpression.class, new MethodVisitorInjector<NumberExpression<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(NumberExpression<SymbolTableContext> ast, SymbolTableContext context) {
		context.getCurrentScope().push(new ValueSymbol(ast.getToken(), context.getCurrentScope(), Types.NUMBER));

		ast.visitNext(context);
	    }
	});

	map.put(StringExpression.class, new MethodVisitorInjector<StringExpression<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * NumberExpression visitor.
	     */
	    @Override
	    public void visite(StringExpression<SymbolTableContext> ast, SymbolTableContext context) {
		context.getCurrentScope().push(new ValueSymbol(ast.getToken(), context.getCurrentScope(), Types.STRING));

		ast.visitNext(context);
	    }
	});

	map.put(Assignation.class, new MethodVisitorInjector<Assignation<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Assignation<SymbolTableContext> ast, SymbolTableContext context) {
		// TODO
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

		map.put("!", new MethodVisitorInjector<UnaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    /**
		     * Unary not operator. ! BooleanExpression;
		     */
		    @Override
		    public void visite(UnaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteUnary(ast, context, Types.BOOL);
		    }
		});

		map.put("-", new MethodVisitorInjector<UnaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(UnaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("++", new MethodVisitorInjector<UnaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    /**
		     * Increment unary operator. NumberExpression ++
		     */
		    @Override
		    public void visite(UnaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("--", new MethodVisitorInjector<UnaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    /**
		     * DeDcrement unary operator. NumberExpression ++
		     */
		    @Override
		    public void visite(UnaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteUnary(ast, context, Types.NUMBER);
		    }
		});

		map.put("new", new MethodVisitorInjector<UnaryOperator, SymbolTableContext>() {

		    @Override
		    public void visite(UnaryOperator ast, SymbolTableContext context) {
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

		map.put("&&", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("||", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("==", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("!=", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("<", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put(">", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("<=", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put(">=", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.BOOL);
		    }
		});

		map.put("+", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    /**
		     * The plus number binary operator
		     */
		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("-", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("*", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("/", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("%", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.NUMBER);
		    }
		});

		map.put("..", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
			visiteBinary(ast, context, Types.STRING);
		    }
		});

		map.put("->", new MethodVisitorInjector<BinaryOperator<SymbolTableContext>, SymbolTableContext>() {

		    @Override
		    public void visite(BinaryOperator<SymbolTableContext> ast, SymbolTableContext context) {
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
	map.put(Program.class, new MethodVisitorInjector<Program<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Program<SymbolTableContext> ast, SymbolTableContext context) {
		for (AST statement : ast.statements) {
		    statement.visite(context);
		}
	    }
	});

	map.put(Block.class, new MethodVisitorInjector<Block<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(Block<SymbolTableContext> ast, SymbolTableContext context) {
		ScopeSymbol parent = context.getCurrentScope();
		ScopeSymbol symbol = new ScopeSymbol(ast.getToken(), parent);
		parent.push(symbol);
		context.enterScope(symbol);

		for (AST statement : ast.statements) {
		    statement.visite(context);
		}

		context.exitScope();
	    }
	});

	map.put(EvalBlock.class, new MethodVisitorInjector<EvalBlock<SymbolTableContext>, SymbolTableContext>() {

	    @Override
	    public void visite(EvalBlock<SymbolTableContext> ast, SymbolTableContext context) {
		for (AST statement : ast.statements) {
		    statement.visite(context);
		}
	    }
	});

	// imperative statements
	map.put(If.class, new MethodVisitorInjector<If<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * If statement visitor.
	     */
	    @Override
	    public void visite(If<SymbolTableContext> ast, SymbolTableContext context) {
		ast.condition.visite(context);
		ast.trueStatement.visite(context);
		for (If _if : ast.elseIfs) {
		    _if.visite(context);
		}
		ast.falseStatement.visite(context);

		ast.visitNext(context);
	    }
	});

	map.put(Loop.class, new MethodVisitorInjector<Loop<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Loop<SymbolTableContext> ast, SymbolTableContext context) {
		ast.limit.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(For.class, new MethodVisitorInjector<For<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(For<SymbolTableContext> ast, SymbolTableContext context) {
		ast.decl.visite(context);
		ast.condition.visite(context);
		ast.increment.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(While.class, new MethodVisitorInjector<While<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(While<SymbolTableContext> ast, SymbolTableContext context) {
		ast.condition.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(Until.class, new MethodVisitorInjector<Until<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * For statement visitor.
	     */
	    @Override
	    public void visite(Until<SymbolTableContext> ast, SymbolTableContext context) {
		ast.condition.visite(context);
		ast.statement.visite(context);
	    }
	});

	map.put(Break.class, new MethodVisitorInjector<Break<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * Break statement visitor. Throw break exception.
	     */
	    @Override
	    public void visite(Break<SymbolTableContext> ast, SymbolTableContext context) {
	    }
	});

	map.put(Return.class, new MethodVisitorInjector<Return<SymbolTableContext>, SymbolTableContext>() {

	    /**
	     * Return statement visitor. Throw return exception.
	     */
	    @Override
	    public void visite(Return<SymbolTableContext> ast, SymbolTableContext context) {
		ReturnSymbol symbol = new ReturnSymbol(ast.getToken(), context.getCurrentScope());
		context.getCurrentScope().push(symbol);

		// visite expression
		if (ast.expr != null) {
		    ast.expr.visite(context);
		    symbol.setExpression(context.getCurrentScope().pop());
		}
	    }
	});

	return map;
    }

}
