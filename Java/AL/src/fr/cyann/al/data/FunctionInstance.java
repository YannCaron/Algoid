/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.data;

import fr.cyann.Cloneable;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.ast.interfaces.Identifiable;

/**
 The FunctionInstance class.
 Creation date: 25 mars 2013.
 <p>
 @author CyaNn
 @version v0.1
 */
public class FunctionInstance implements Identifiable, Cloneable<FunctionInstance> {

	public FunctionDeclaration<RuntimeContext> decl;
	public Scope enclosing;
	public final int identity;
	public Counter use;
	public ObjectInstance objectContainer;
	public Expression<? extends Expression, RuntimeContext> self;
	public Call<RuntimeContext> call;

	private FunctionInstance() {
		identity = Identifiers.getObjectIdent();
	}

	public FunctionInstance(FunctionDeclaration<RuntimeContext> decl, Scope enclosing) {
		this();
		this.decl = decl;
		this.enclosing = enclosing;
		this.use = new Counter();
	}

	public FunctionInstance(FunctionDeclaration<RuntimeContext> decl) {
		this();
		this.decl = decl;
		this.use = new Counter();
	}

	@Override
	public FunctionInstance clone() {
		FunctionInstance clone = new FunctionInstance(this.decl);
		clone.enclosing = enclosing;
		clone.use = this.use;
		return clone;
	}

	public final String getName() {
		return "function#" + identity;
	}

	/**
	 @inheritDoc
	 */
	@Override
	public int getIdentity() {
		return identity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("function#");
		sb.append(identity);

		if (!decl.params.isEmpty()) {
			sb.append("(");
			int size = decl.params.size();
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					sb.append(", ");
				}

				sb.append(Identifiers.valueOf(decl.params.get(i).var.identifier));
				if (!decl.params.get(i).mv.isNull()) {
					sb.append("=");
					sb.append(decl.params.get(i).mv);
				}
			}
			sb.append(")");
		}

		return sb.toString();
	}
}
