/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.symbolTable.ScopeSymbol;
import fr.cyann.jasi.visitor.Context;
import java.util.Stack;

/**
 *
 * @author caronyn
 */
public class TransformContext implements Context {

    private final ScopeSymbol rootScope;
    private final Stack<ScopeSymbol> scopeContext;

    public TransformContext(SymbolTableContext symbolTable) {
	this.scopeContext = new Stack<ScopeSymbol>();

	// initialize
	rootScope = symbolTable.getRootScope();
	enterScope(rootScope);
    }

    // accessor
    public final ScopeSymbol getScope() {
	return scopeContext.peek();
    }

    public ScopeSymbol getRootScope() {
	return rootScope;
    }

    // method
    public final void enterScope(ScopeSymbol node) {
	scopeContext.push(node);
    }

    public final void exitScope() {
	scopeContext.pop();
    }

}
