/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.cyann.al.visitor;

import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.Map;

/**
 <p>
 @author cyann
 */
public abstract class RuntimeDecorator extends AbstractRuntime {

	private final AbstractRuntime runtime;

	public RuntimeDecorator(AbstractRuntime runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public void addVisitors(Map<Class<? extends AST>, VisitorInjector> map) {
		runtime.addVisitors(map);
		addMoreVisitors(map);
	}

	@Override
	public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {
		runtime.addDynamicMethods(context, dynamicMethods);
		addMoreDynamicMethods(context, dynamicMethods);
	}

	@Override
	public void addFrameworkObjects(ASTBuilder builder) {
		runtime.addFrameworkObjects(builder);
		addMoreFrameworkObjects(builder);
	}
	
	public abstract void addMoreVisitors(Map<Class<? extends AST>, VisitorInjector> map);
	public abstract void addMoreDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods);
	public abstract void addMoreFrameworkObjects(ASTBuilder builder);
	
}
