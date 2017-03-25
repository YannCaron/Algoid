/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.exception;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;

/**
 *
 * @author caronyn
 */
public class ASTReplacementImpossible extends ALRuntimeException {

	public ASTReplacementImpossible(AST node, AST it, AST by) {
		super(EX_AST_REPLACE_IMPOSSIBLE.setArgs(it, by, node), node.getToken());
	}

}
