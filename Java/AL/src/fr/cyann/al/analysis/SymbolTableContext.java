/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.Types;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.symbolTable.DeclarationSymbol;
import fr.cyann.al.symbolTable.FunctionSymbol;
import fr.cyann.al.symbolTable.ObjectSymbol;
import fr.cyann.al.symbolTable.ScopeSymbol;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;
import java.util.Stack;

/**
 * <p>
 * @author caronyn
 */
public class SymbolTableContext implements Context {

    // attribute
    private final ScopeSymbol root;
    private final Stack<ScopeSymbol> nodeStack;

    // private
    public void appendToScope(Scope scope, boolean member) {
	// TODO CONTINUE HERE
	// TODO REFACTOR !!!!!!!!!!!!!!!
	
	for (Integer key : scope.getVariables().keySet()) {
	    String ident = Identifiers.valueOf(key);
	    MutableVariant value = scope.getVariables().get(key);
	    
	    if ("this".equals(ident)) return;

	    DeclarationSymbol decl = new DeclarationSymbol(new Token(Syntax.IDENT, ident), getRootScope(), ident);

	    if (value.isFunction()) {
		FunctionSymbol fun = new FunctionSymbol(new Token(TokenType.SYMBOL, "function"), getCurrentScope());
		
		for (VariableDeclaration param : value.getFunction().decl.params) {
		    fun.push(new DeclarationSymbol(new Token(Syntax.IDENT, param.partName), fun, param.partName));
		}
		
		decl.setExpression(fun);
	    } else if (value.isObject()) {
		ObjectSymbol obj = new ObjectSymbol(new Token(TokenType.SYMBOL, "object"), getCurrentScope());
		obj.setType(Types.OBJECT);
		enterScope(obj);
		appendToScope(value.getObject().scope, true);
		exitScope();
		decl.setExpression(obj);
	    }

	    decl.setType(value.getType());
	    getCurrentScope().push(decl);
	    
	}
    }

    // constructor
    public SymbolTableContext(RuntimeContext api) {
	this.root = new ScopeSymbol(new Token(TokenType.SYMBOL, "", 0, 0, 0), null);
	nodeStack = new Stack<ScopeSymbol>();
	nodeStack.push(root);
	appendToScope(api.getRoot(), false);
    }

    // property
    public ScopeSymbol getRootScope() {
	return root;
    }

    public ScopeSymbol getCurrentScope() {
	return nodeStack.peek();
    }

    // method
    public void enterScope(ScopeSymbol node) {
	nodeStack.push(node);
    }

    public void exitScope() {
	nodeStack.pop();
    }

}
