/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.symbolTable.ArraySymbol;
import fr.cyann.al.symbolTable.BinarySymbol;
import fr.cyann.al.symbolTable.CallSymbol;
import fr.cyann.al.symbolTable.DeclarationSymbol;
import fr.cyann.al.symbolTable.FunctionSymbol;
import fr.cyann.al.symbolTable.IndexSymbol;
import fr.cyann.al.symbolTable.ObjectSymbol;
import fr.cyann.al.symbolTable.ReturnSymbol;
import fr.cyann.al.symbolTable.ScopeSymbol;
import fr.cyann.al.symbolTable.UnarySymbol;
import fr.cyann.al.symbolTable.ValueSymbol;
import fr.cyann.al.symbolTable.VariableSymbol;

/**
 * <p>
 * @author cyann
 */
public interface SymbolTableVisitor {

	public void visite(DeclarationSymbol symbol);

	public void visite(ValueSymbol symbol);

	public void visite(UnarySymbol symbol);

	public void visite(BinarySymbol symbol);

	public void visite(VariableSymbol symbol);

	public void visite(CallSymbol symbol);

	public void visite(IndexSymbol symbol);

	public void visite(FunctionSymbol symbol);

	public void visite(ObjectSymbol symbol);

	public void visite(ArraySymbol symbol);

	public void visite(ReturnSymbol symbol);

	public void visite(ScopeSymbol symbol);

}
