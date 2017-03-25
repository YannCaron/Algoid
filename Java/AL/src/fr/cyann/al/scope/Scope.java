
/*
 YANN CARON CONFIDENTIAL
 __________________

 Yann Caron Copyright (c) 2011
 All Rights Reserved.
 __________________

 NOTICE:  All information contained herein is, and remains
 the property of Yann Caron and its suppliers, if any.
 The intellectual and technical concepts contained
 herein are proprietary to Yann Caron
 and its suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Yann Caron.
 */
package fr.cyann.al.scope;

import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.Nature;
import fr.cyann.jasi.scope.AbstractScope;
import fr.cyann.utils.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import fr.cyann.jasi.scope.ChainPredicate;

/**
 * The ScopeImpl class.
 * <p>
 * @param <T> the variable type.
 * <p>
 * @author Yann Caron
 * @version v0.1
 */
public class Scope implements AbstractScope<MutableVariant> {

    public static final boolean DEBUG_VALUES = true;

    //private final Lock readLock, writeLock;
    protected String name;
    /**
     * The variables map. All scope variables are referenced here.
     */
    public TreeMap<Integer, MutableVariant> variables;
    private final List<MutableVariant> items;

    /**
     * default constructor
     * <p>
     * @param name the scope name.
     */
    public Scope(String name) {
        this.name = name;
        variables = new TreeMap<Integer, MutableVariant>();
        items = new ArrayList<MutableVariant>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Scope getRoot() {
        return this;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<Integer, MutableVariant> getVariables() {
        return variables;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isAlreadyDefined(Integer identifier) {
        return variables.containsKey(identifier);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void define(Integer identifier, MutableVariant variable) {
        try {
            //writeLock.lock();
            variables.put(identifier, variable);
            items.add(variable);
            variable.setNature(Nature.GLOBAL);
        } finally {
            //writeLock.unlock();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public MutableVariant resolve(Integer identifier) {
        return resolve(identifier, false);
    }

    /**
     * @inheritDoc
     */
    @Override
    public MutableVariant resolve(Integer identifier, boolean isForWrite) {
        MutableVariant variable = null;
        variable = variables.get(identifier);
        return variable;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove(Integer identifier) {
        try {
            //writeLock.lock();
            MutableVariant mv = variables.remove(identifier);
            items.remove(mv);
        } finally {
            //writeLock.unlock();
        }
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     * <p>
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("scope (");
        sb.append(getName());
        sb.append(") [");

        boolean first = true;
        Iterator<Integer> it = variables.keySet().iterator();
        while (it.hasNext()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            Integer item = it.next();
            sb.append(Identifiers.valueOf(item));

            MutableVariant variable = variables.get(item);

            if (DEBUG_VALUES) {
                sb.append("=");
                sb.append(variable.getString(null));
            }

        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void debug(int indent, boolean system) {
        if (indent == 0) {
            System.out.println("");
        }
        System.out.print(StringUtils.repeat("  ", indent));
        System.out.println("scope \"" + getName() + "\"");
        for (Entry<Integer, MutableVariant> entry : variables.entrySet()) {
            if (entry.getValue() instanceof MutableVariant) {
                MutableVariant mv = entry.getValue();

                if (!entry.getKey().equals(Identifiers.getID("this")) && (system || !mv.isSystem())) {
                    System.out.print(StringUtils.repeat("  ", indent + 1));
                    System.out.print(Identifiers.valueOf(entry.getKey()) + ":" + mv.getType() + " = " + mv.toString());

                    if (mv.isFunction()) {
                        System.out.print(" method of " + mv.getFunction().enclosing.getName());
                    }
                    System.out.println("");

                    if (mv.isObject()) {
                        mv.getObject().scope.debug(indent + 2, system);
                    }
                }

            }
        }
    }

    public ParameterScope functionScope() {
        return null;
    }

    @Override
    public Scope clone() {
        Scope clone = new Scope(name);
        copyParametersTo(clone);
        return clone;
    }

    public void copyParametersTo(Scope clone) {
        Iterator<Entry<Integer, MutableVariant>> it = variables.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, MutableVariant> entry = it.next();
            clone.variables.put(entry.getKey(), new MutableVariant(entry.getValue()));
        }
    }

    @Override
    public Scope cloneUntil(ChainPredicate untilPredicate) {
        return clone();
    }

    @Override
    public Scope cloneWhile(ChainPredicate whilePredicate) {
        if (whilePredicate.eval(this)) {
            return this.clone();
        }
        return this;
    }

}
