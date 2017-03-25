/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.jasi.lexer.Token;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * <p>
 * @author cyann
 */
public abstract class NodeSymbol extends Symbol implements Iterable<Symbol> {

    // static
    @PrettyPrinter.Ignore
    public static final Comparator<NodeSymbol> SCOPE_COMPARATOR = new Comparator<NodeSymbol>() {

        @Override
        public int compare(NodeSymbol o1, NodeSymbol o2) {
            int i1 = o1.getToken().getPos();
            int i2 = o2.getToken().getPos();

            if (i1 < i2) {
                return -1;
            } else if (i1 > i2) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    // index
    @PrettyPrinter.Ignore
    final Set<NodeSymbol> nestedScopes;

	// attribute
    //@PrettyPrinter.Ignore
    final Stack<Symbol> stack;

    // constructor
    public NodeSymbol(Token token, ScopeSymbol parent) {
        super(token, parent);
        stack = new Stack<Symbol>();
        nestedScopes = new TreeSet<NodeSymbol>(SCOPE_COMPARATOR);
    }

	// abstract
    //public abstract void buildIndex(int pos);
    // method
    public void push(Symbol symbol) {
        stack.push(symbol);
    }

    public Symbol pop() {
        return stack.pop();
    }

    public Symbol getLast() {
        return stack.peek();
    }

    public Symbol get(int i) {
        return stack.get(i);
    }

    // implement
    @Override
    public Iterator<Symbol> iterator() {
        return stack.iterator();
    }

    // toString
    @Override
    public String toString() {
        return "NodeSymbol{" + "nestedScopes=" + nestedScopes + ", stack=" + stack + '}';
    }

}
