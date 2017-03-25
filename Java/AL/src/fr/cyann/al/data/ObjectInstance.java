/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.data;

import com.rits.cloning.Cloner;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.scope.ObjectScope;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.ast.interfaces.Identifiable;
import fr.cyann.jasi.visitor.ClassMapVisitorInjector;
import java.util.List;

/**
 * The ObjectInstance class. Creation date: 25 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class ObjectInstance implements Identifiable {

    public final Integer thisIdentifier = Identifiers.getID("this");
    public final Integer supersIdentifier = Identifiers.getID("supers");
    public final Integer cloneIdentifier = Identifiers.getID("clone");
    private MutableVariant _this, _supers;
    public ObjectDeclaration<RuntimeContext> decl;
    public ObjectScope scope;
    public ObjectScope destScope = null;
    public final int identity;
    public final Object nativeObject;

    public ObjectInstance(ObjectDeclaration<RuntimeContext> decl, Scope enclosing, Object nativeObject) {
        this.decl = decl;
        identity = Identifiers.getObjectIdent();
        this.scope = new ObjectScope(getName(), enclosing);
        this.nativeObject = nativeObject;
    }

    public ObjectInstance(ObjectDeclaration<RuntimeContext> decl, Scope enclosing) {
        this.decl = decl;
        identity = Identifiers.getObjectIdent();
        this.scope = new ObjectScope(getName(), enclosing);
        this.nativeObject = null;
    }

    public ObjectInstance clone(RuntimeContext context) {
        Object no = null;
        if (nativeObject != null) {
            Cloner cloner = new Cloner();
            no = cloner.deepClone(nativeObject);
        }

        ObjectInstance clone = new ObjectInstance(decl, scope.enclosing, no);
        clone.build(context);
        return clone;
    }

    public void build(RuntimeContext context) {

        // backup
        Scope backup = context.scope;
        context.scope = scope;

        buildObject(context);

        // restore
        context.scope = backup;
    }

    private void buildObject(RuntimeContext context) {
        // define this
        _this = new MutableVariant(this);
        scope.define(thisIdentifier, _this);

        // manage inheritance
        inherits(context);

        // set attributes
        int size = decl.attributs.size();
        for (int i = 0; i < size; i++) {
            VariableDeclaration<RuntimeContext> var = decl.attributs.get(i);
            var.visite(context);

            if (var.mv.isFunction()) {
                FunctionInstance f = var.getLast().mv.getFunction();
                f.objectContainer = this;
                f.use.increment();
                if (destScope != null) {
                    f.enclosing = destScope;
                }
            }
        }

    }

    public void inherits(RuntimeContext context) {
        List<Variable<RuntimeContext>> superClasses = decl.getSuperClasses();
        int size = superClasses.size();

        for (int i = 0; i < size; i++) {
            if (_supers == null) {
                _supers = new MutableVariant();
                scope.define(supersIdentifier, _supers);
            }

            // backup
            Scope backup = context.scope;
            context.scope = scope.enclosing;

            Variable<RuntimeContext> superVar = superClasses.get(i);
            superVar.visite(context);

            // restore
            context.scope = backup;

            ObjectInstance superObject = new ObjectInstance(superVar.getLast().mv.getObject().decl, scope.enclosing);
            superObject.destScope = scope;
            superObject.build(context);

            _supers.add(new MutableVariant(superObject));

            scope.addParent(superObject.scope);
        }
    }

    public void addAttribute(ClassMapVisitorInjector visitor, RuntimeContext context, VariableDeclaration<RuntimeContext> origin) {
        VariableDeclaration newVar = new VariableDeclaration(origin.getToken());
        newVar.setVar(new Variable(origin.getVar().getLast().getToken()));
        newVar.injectVisitor(visitor);

        decl.attributs.add(newVar);

        MutableVariant mv = origin.getLast().mv;
        if (mv.isFunction()) {
            FunctionInstance f = mv.getFunction();
            f.enclosing = scope;
            newVar.setExpr(f.decl);
        } else if (mv.isObject()) {
            ObjectInstance o = mv.getObject();
            o.scope.enclosing = scope;
            newVar.setExpr(o.decl);
        } else {
            newVar.setExpr(origin.expr);
        }
    }

    @Override
    public int hashCode() {
        int hash = 9;
        hash = 29 * hash + identity;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectInstance other = (ObjectInstance) obj;
        if (identity != other.identity) {
            return false;
        }
        return true;
    }

    public final String getName() {
        return "object#" + identity;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getIdentity() {
        return identity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (nativeObject == null) {
            sb.append("object#");
            sb.append(identity);
            sb.append("(");
        } else {
            sb.append("javaObject#");
            sb.append(nativeObject.toString());
            sb.append("(");
        }

        boolean first = true;
        for (Integer key : scope.getVariables().keySet()) {
            if (key != thisIdentifier) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(Identifiers.valueOf(key));
                sb.append("=");
                sb.append(scope.getVariables().get(key).toString());
                first = false;
            }
        }

        sb.append(")");
        return sb.toString();
    }

    public String toJson(int tab) {
        StringBuilder sb = new StringBuilder();

        if (nativeObject == null) {
            sb.append("{");
            tab++;
        } else {
            // TODO : Native object to JSON
        }

        appendJsonToContent(sb, tab);

        tab--;
        appendReturnAndTab(sb, tab);
        sb.append("}");
        return sb.toString();
    }

    void appendReturnAndTab(StringBuilder sb, int tab) {
        sb.append('\n');
        for (int i = 0; i < tab; i++) {
            sb.append('\t');
        }
    }

    void appendJsonToContent(StringBuilder sb, int tab) {

        boolean first = true;
        if (_supers != null && _supers.isArray()) {
            if (!first) {
                sb.append(", ");
            }
            for (MutableVariant mv : _supers.getArray()) {
                mv.getObject().appendJsonToContent(sb, tab);
            }
            first = false;
        }

        for (Integer key : scope.getVariables().keySet()) {
            if (key != thisIdentifier && key != supersIdentifier && !scope.getVariables().get(key).isFunction()) {
                if (!first) {
                    sb.append(", ");
                }

                appendReturnAndTab(sb, tab);

                sb.append('"');
                sb.append(Identifiers.valueOf(key));
                sb.append('"');
                sb.append(" : ");
                sb.append(scope.getVariables().get(key).toJson(tab));
                first = false;
            }
        }

    }

}
