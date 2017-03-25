/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.scope.Scope;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.parser.PEG;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import fr.cyann.jasi.visitor.Context;
import java.util.List;

/**
 *
 * @author caronyn
 */
public class RuntimeContext implements Context {

	// token for exceptions
	public static AST currentAST;
	// al
	public final PEG syntax;
	public final ClassMapVisitorInjector runtime;
	// symbols table
	public final Identifiers identifiers;
	// mutable variant
	public MutableVariant returnValue; // value
	// current ast object definitions
	public ObjectInstance currentObject;
	// cache
	public boolean cache;
	// states
	public boolean returning, breaking, breakCallChain;
	public Call<RuntimeContext> callAST;

	// trampoline
	public Expression trampolineCall;
	public Scope trampolineScope;
	public Scope trampolineScopeCall;

	public Scope root, scope, call;

	public RuntimeContext(PEG syntax, ClassMapVisitorInjector runtime) {
		this.syntax = syntax;
		this.runtime = runtime;

		returnValue = new MutableVariant();

		root = new Scope("root");
		scope = root;
		call = root;

		identifiers = new Identifiers();

		cache = true;

		returning = false;
		breaking = false;

	}

	// accessor
	public Scope getRoot() {
		return root;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	// return
	public void returnValue() {
		returning = true;
		returnValue.convertToVoid();
	}

	public void returnValue(boolean value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(float value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(String value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(FunctionInstance value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(ObjectInstance value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(List<MutableVariant> value) {
		returning = true;
		returnValue.setValue(value);
	}

	public void returnValue(MutableVariant mv) {
		returning = true;
		returnValue.setValue(mv);
	}

	// data tools
	public boolean getOptionalBoolOf(Expression<? extends Expression, RuntimeContext> expr, boolean defaultValue) {
		if (expr != null) {
			expr.visite(this);
			return expr.mv.getBool();
		}

		return defaultValue;
	}

	public float getOptionalNumberOf(Expression<? extends Expression, RuntimeContext> expr, float defaultValue) {
		if (expr != null) {
			expr.visite(this);
			return expr.mv.getNumber();
		}

		return defaultValue;
	}

	public String getOptionalStringOf(Expression<? extends Expression, RuntimeContext> expr, String defaultValue) {
		if (expr != null) {
			expr.visite(this);
			return expr.mv.getString(this);
		}

		return defaultValue;
	}

	@Override
	public String toString() {
		return "RuntimeContext{" + "root=" + root + '}';
	}
}
