/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import fr.cyann.al.analysis.SymbolTableVisitor;
import fr.cyann.al.data.Types;
import fr.cyann.jasi.lexer.Token;
import java.util.AbstractList;

/**
 * <p>
 * @author cyann
 */
public abstract class Symbol implements PrettyPrinter.Printable {

    // static
    private static <T extends Symbol> T findByTokenBinarySearch(AbstractList<T> list, Token token, int start, int stop) {

        if (start > stop) {
            if (stop < list.size()) {
                T item = list.get(stop);
                return (T) item.find(token);
            }
            return null;
        };

        int i = start + ((stop - start) / 2);
        T item = list.get(i);

        //System.out.println("search " + token + " in " + item.getToken());

        if (token.getPos() < item.getToken().getPos()) {
            return findByTokenBinarySearch(list, token, start, i - 1);
        } else if (token.getPos() > item.getToken().getPos()) {
            return findByTokenBinarySearch(list, token, start + 1, stop);
        }

        return item;
    }

    protected static <T extends Symbol> T find(AbstractList<T> list, Token token) {
        return findByTokenBinarySearch(list, token, 0, list.size() - 1);
    }
    
    // attribute
    @PrettyPrinter.Ignore
    final ScopeSymbol parent;

    @PrettyPrinter.Ignore()
    private final Token token;

    //@PrettyPrinter.Optional(forValues = {"VOID"})
    private Types type;

    // property
    public Token getToken() {
        return token;
    }

    public ScopeSymbol getParent() {
	return parent;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public abstract Symbol find(Token token);

    // constructor
    public Symbol(Token token, ScopeSymbol parent) {
        this.token = token;
	this.parent = parent;
        this.type = Types.VOID;
    }

    // override
    @Override
    public PrettyPrinter print(PrettyPrinter printer) {
        return printer.print(this);
    }

    @Override
    public PrettyPrinter println(PrettyPrinter printer) {
        return printer.println(this);
    }

    // method
    public abstract void visite(SymbolTableVisitor visitor);

    // toString
    @Override
    public String toString() {
        return "Symbol{" + "token=" + token + '}';
    }

}
