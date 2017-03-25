/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.library;

import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;

/**
 *
 * @author cyann
 */
public interface ALLib extends Lib {

	public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods);

	public void addFrameworkObjects(ASTBuilder builder);

}
