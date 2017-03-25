/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.jasi.ast;

import fr.cyann.jasi.ast.interfaces.TraversalFunctor;
import fr.cyann.jasi.lexer.Token;

/**
 * <p>
 * @author cyann
 */
public class EOG extends AST {

	public EOG(Token token) {
		super(token);
	}

	@Override
	public boolean replaceChild(AST it, AST by) {
		return false;
	}

	@Override
	public void depthFirstTraversal(TraversalFunctor func) {
	}

}
