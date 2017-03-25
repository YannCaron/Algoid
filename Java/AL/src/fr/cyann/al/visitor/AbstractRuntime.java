/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Types;
import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.HashMap;
import java.util.Map;

/**
 * The AbstractExecution class. Creation date: 4 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public abstract class AbstractRuntime extends ClassMapVisitorInjector {

    HashMap<Class<? extends AST>, VisitorInjector> map;
    TypeNameFunctionMap dynamicMethods;

    public void initialize(ASTBuilder builder, Context context) {
	map = new HashMap<Class<? extends AST>, VisitorInjector>();
	addVisitors(map);

	addFrameworkObjects(builder);
    }

    public void initialize(ASTBuilder builder, RuntimeContext context) {
	map = new HashMap<Class<? extends AST>, VisitorInjector>();
	addVisitors(map);

	dynamicMethods = new TypeNameFunctionMap(this, context);
	addDynamicMethods(context, dynamicMethods);

	addFrameworkObjects(builder);
    }

    @Override
    public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
	return map;
    }

    public abstract void addVisitors(Map<Class<? extends AST>, VisitorInjector> map);

    public abstract void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods);

    public void addDynamicMethod(Types type, FunctionInstance f) {
	dynamicMethods.put(type, f);
    }

    public TypeNameFunctionMap getDynamicMethods() {
	return dynamicMethods;
    }

    public abstract void addFrameworkObjects(ASTBuilder builder);
    
    public abstract void clearRessources();
    
}
