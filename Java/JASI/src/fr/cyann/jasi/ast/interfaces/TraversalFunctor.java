/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.jasi.ast.interfaces;

import fr.cyann.jasi.ast.AST;

/**
 *
 * @author caronyn
 */
public interface TraversalFunctor {

	void traverse(AST ast);
}
