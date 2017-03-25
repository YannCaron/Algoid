/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.exception.AssertException;
import fr.cyann.al.factory.DeclarationFactory;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.MethodVisitor;

/**
 * The JVMRuntime class. Creation date: 11 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class UnitTestRuntime extends RuntimeVisitor {

    @Override
    public void addFrameworkObjects(ASTBuilder builder) {

	super.addFrameworkObjects(builder);

	// unit testing
	ObjectDeclaration unit = ExpressionFactory.object("unit");
	builder.push(DeclarationFactory.factory(unit));

	unit.addDeclaration(DeclarationFactory.factory("assertEquals", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;
		MutableVariant v2 = f.decl.params.get(1).var.mv;

		if (!v1.equals(v2)) {
		    throw new AssertException(v1.getString(context), v2.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1", "v2"));

	unit.addDeclaration(DeclarationFactory.factory("assertNotEquals", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;
		MutableVariant v2 = f.decl.params.get(1).var.mv;

		if (v1.equals(v2)) {
		    throw new AssertException(v1.getString(context), v2.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1", "v2"));

	unit.addDeclaration(DeclarationFactory.factory("assertNull", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (!v1.isNull()) {
		    throw new AssertException("nil", v1.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1"));

	unit.addDeclaration(DeclarationFactory.factory("assertNotNull", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (v1.isNull()) {
		    throw new AssertException("not nil", v1.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1"));

	unit.addDeclaration(DeclarationFactory.factory("assertExists", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (v1.isInvalid()) {
		    throw new AssertException("exists", "not exists", ast.function.call.getToken());
		}
	    }
	}, true, "v1"));

	unit.addDeclaration(DeclarationFactory.factory("assertNotExists", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (!v1.isInvalid()) {
		    throw new AssertException("not exists", "exists", ast.function.call.getToken());
		}
	    }
	}, true, "v1"));

	unit.addDeclaration(DeclarationFactory.factory("assertTrue", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (!v1.isBool() || !v1.getBool()) {
		    throw new AssertException("true", v1.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1"));

	unit.addDeclaration(DeclarationFactory.factory("assertFalse", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		FunctionInstance f = ast.function;
		MutableVariant v1 = f.decl.params.get(0).var.mv;

		if (v1.isBool() && v1.getBool()) {
		    throw new AssertException("false", v1.getString(context), ast.function.call.getToken());
		}
	    }
	}, "v1"));

	// debug
	ObjectDeclaration debug = ExpressionFactory.object("debug");
	builder.push(DeclarationFactory.factory(debug));

	debug.addDeclaration(DeclarationFactory.factory("printScope", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		context.call.debug(0, false);
	    }
	}));

	debug.addDeclaration(DeclarationFactory.factory("printFunctionScope", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		context.call.functionScope().debug(0, false);
	    }
	}));

	debug.addDeclaration(DeclarationFactory.factory("printAllScope", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {

	    @Override
	    public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
		context.call.debug(0, true);
	    }
	}));

    }
}
