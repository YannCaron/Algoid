/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

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
import java.util.Stack;

/**
 * <p>
 * @author CARONYN
 */
public class TypeInferer implements SymbolTableVisitor {

    // attribute
    private final ScopeSymbol root;
    private final Stack<ScopeSymbol> scopeStack;

    // Debug TODO: remove
    private boolean hasPromoted;

    // constructor
    public TypeInferer(ScopeSymbol root) {
	this.root = root;
	scopeStack = new Stack<ScopeSymbol>();
	scopeStack.push(root);
    }

    // method
    public void infer() {
	hasPromoted = true;

	int i = 0;

	while (hasPromoted) {
	    i++;
	    hasPromoted = false;
	    root.visite(this);
	}

	System.out.printf("NEED [%d] cycle(s) to perform type inference.\n", i - 1);
    }

    // function
    private void promoteToType(Symbol symbol, Types toType) {
	Types fromType = symbol.getType();

	if (!(symbol instanceof ValueSymbol) && fromType == Types.VOID && toType != Types.VOID) {
	    symbol.setType(toType);
	    hasPromoted = true;
	} else if (fromType == toType || toType == Types.VOID) {
	} else {
	    // TODO
	    System.err.printf("Cannot promote type [%s] to [%s] for symbol [%s]!\n", fromType.name(), toType.name(), symbol.toString());
//			throw new RuntimeException();
	}

    }

    private void promoteOperationType(Symbol symbol, Types toType) {
	if (symbol instanceof DeclarationSymbol && ((DeclarationSymbol) symbol).isMember()) {
	    promoteToType(symbol, toType);
	}
    }

    private Symbol visiteOperande(Symbol symbol, Types type) {
	if (symbol != null) {
	    symbol.visite(this);

	    if (symbol instanceof IdentifierSymbol) {
		return ((IdentifierSymbol) symbol).getLast().getPointer();
	    } else {
		return symbol;
	    }
	}
	return null;
    }

    // visitor
    // declaration
    @Override
    public void visite(DeclarationSymbol symbol) {
	if (symbol.getExpression() != null) {
	    Symbol expression = symbol.getExpression();
	    expression.visite(this);

	    Types type;
	    if (expression instanceof IdentifierSymbol) {
		type = ((IdentifierSymbol) expression).getLast().getType();
	    } else {
		type = expression.getType();
	    }
	    promoteToType(symbol, type);
	}
    }

    @Override
    public void visite(ScopeSymbol symbol) {
	scopeStack.push(symbol);

	for (Symbol child : symbol) {
	    child.visite(this);
	}

	scopeStack.pop();
	symbol.setType(Types.VOID);
    }

    @Override
    public void visite(FunctionSymbol symbol) {
	scopeStack.push(symbol);

	for (Symbol child : symbol) {
	    child.visite(this);
	}

	scopeStack.pop();
    }

    @Override
    public void visite(ObjectSymbol symbol) {
	scopeStack.push(symbol);

	for (Symbol child : symbol) {
	    child.visite(this);
	}

	scopeStack.pop();
    }

    @Override
    public void visite(ArraySymbol symbol) {
    }

    // expression
    @Override
    public void visite(ValueSymbol symbol) {
    }

    @Override
    public void visite(UnarySymbol symbol) {
	Symbol right = visiteOperande(symbol.getRight(), symbol.getType());
	if (symbol.isEnforce()) {
	    promoteToType(right, symbol.getType());
	}
    }

    @Override
    public void visite(BinarySymbol symbol) {
	Symbol left = visiteOperande(symbol.getLeft(), symbol.getType());
	Symbol right = visiteOperande(symbol.getRight(), symbol.getType());

	if (symbol.isEnforce()) {
	    promoteOperationType(left, symbol.getType());
	    promoteOperationType(right, symbol.getType());
	} else {
	    promoteOperationType(left, right.getType());
	    promoteOperationType(right, left.getType());
	}
    }

    // variable resolution
    @Override
    public void visite(VariableSymbol symbol) {

	System.out.println("Visite " + symbol.getName() + " type " + symbol.getType());

	DeclarationSymbol pointer = scopeStack.peek().find(symbol.getName(), symbol.getToken().getPos(), false);

	//System.out.println(pointer.getExpression() instanceof ObjectSymbol);
	symbol.setPointer(pointer);
	if (symbol.hasPointer()) {
	    promoteToType(symbol, symbol.getPointer().getType());
	} else {
	    System.out.println("NO POINTER: " + symbol.getName());
	}

	ObjectSymbol obj = null;
	if (symbol.hasPointer() && symbol.getPointer() instanceof DeclarationSymbol) {
	    DeclarationSymbol decl = (DeclarationSymbol) symbol.getPointer();
	    if (decl.getExpression() instanceof ObjectSymbol) {
		obj = (ObjectSymbol) decl.getExpression();
	    }
	}

	if (obj != null) {
	    scopeStack.push(obj);
	}

	if (symbol.hasNext()) {
	    symbol.getNext().visite(this);
	}

	if (obj != null) {
	    scopeStack.pop();
	}
    }

    private static final Symbol getLast(Symbol symbol) {
	if (symbol instanceof IdentifierSymbol) {
	    return ((IdentifierSymbol) symbol).getLast();
	}
	return symbol;
    }

    @Override
    public void visite(CallSymbol symbol) {

	DeclarationSymbol declaration = (DeclarationSymbol) symbol.getPrevious().getPointer();

	Symbol expression = declaration.getExpression();

	if (expression instanceof FunctionSymbol) {
	    FunctionSymbol function = (FunctionSymbol) expression;

	    if (function.hasReturnSymbol()) {
		symbol.setPointer(function.getReturnSymbol());
		promoteToType(symbol, symbol.getPointer().getType());
	    }

	    // enforce function argument
	    int size = symbol.getArguments().size();
	    for (int i = 0; i < size; i++) {
		symbol.getArguments().get(i).visite(this);
		Symbol last = getLast(symbol.getArguments().get(i));

		promoteToType(last, function.get(i).getType());
		// and invert
		promoteToType(function.get(i), last.getType());
	    }

	} else {
	    System.err.printf("Only function can be called.\n");
	}

    }

    @Override
    public void visite(IndexSymbol symbol) {
	promoteToType(symbol, Types.ARRAY);
    }

    @Override
    public void visite(ReturnSymbol symbol) {
	ScopeSymbol current = scopeStack.peek();
	while (current != root && !(current instanceof FunctionSymbol)) {
	    current = current.getParent();
	}

	if (current instanceof FunctionSymbol) {
	    FunctionSymbol function = (FunctionSymbol) current;
	    symbol.getExpression().visite(this);

	    if (function.getReturnSymbol() != null) {
		Types ret1Type = function.getReturnSymbol().getType();
		Types ret2Type = symbol.getExpression().getType();
		if (ret1Type != ret2Type) {
		    System.err.printf("Cannot return twice with differenc types [%s] and [%s]!\n", ret1Type.name(), ret2Type.name());
		}
	    }

	    function.setReturnSymbol(symbol.getExpression());

	    promoteToType(symbol, function.getReturnSymbol().getType());
	} else {
	    // TODO
	    System.err.printf("Return is not within function!\n");
	}

    }

}
