/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.data;

import fr.cyann.al.exception.ConvertionException;
import fr.cyann.al.exception.UnexpectedTypeException;
import fr.cyann.al.visitor.Jdk6Legacy;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.jasi.scope.Originated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The MutableVariant class. Creation date: 4 mars 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class MutableVariant implements Comparable<MutableVariant>, Originated {

    public static interface ChangedListener {

        void changed(MutableVariant value);
    }

    public static final transient MutableVariant NIL = new MutableVariant();
    public static final int MAP_INITIAL_CAPACITY = 100;
    private Types type;
    private boolean b;
    private float n;
    private String s;
    private List<MutableVariant> a;
    private HashMap<MutableVariant, MutableVariant> m;
    private MutableVariant k;
    private FunctionInstance f;
    private ObjectInstance o;
    private boolean system;
    private Nature nature;
    private ChangedListener changedListener;

    public MutableVariant() {
        type = Types.VOID;
    }

    public MutableVariant(ObjectInstance o) {
        this();
        setValue(o);
    }

    public MutableVariant(MutableVariant mv) {
        this();
        setValue(mv);
    }

    // property
    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public ChangedListener getChangedListener() {
        return changedListener;
    }

    public void setChangedListener(ChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    @Override
    public Nature getNature() {
        return nature;
    }

    @Override
    public void setNature(Enum nature) {
        this.nature = (Nature) nature;
    }

    // typing management
    public Types getType() {
        return type;
    }

    // void
    public void clear(Types to) {
        if (type != to) {
            switch (to) {
                case OBJECT:
                    o = null;
                    break;
                case FUNCTION:
                    f = null;
                    break;
                case ARRAY:
                    a = null;
                    m = null;
                    break;
            }
        }
    }

    public final void convertToVoid() {
        if (type != Types.VOID) {
            clear(Types.VOID);
            type = Types.VOID;
        }
    }

    public final void invalid() {
        clear(Types.INVALID);
        type = Types.INVALID;
    }

    public boolean isInvalid() {
        return type == Types.INVALID;
    }

    public boolean isNull() {
        return type == Types.VOID;
    }

    // bool
    public MutableVariant(boolean b) {
        type = Types.BOOL;
        this.b = b;
    }

    public boolean isBool() {
        return type == Types.BOOL;
    }

    public boolean getBool() {
        switch (type) {
            case BOOL:
                return b;
            case VOID:
                return false;
            case NUMBER:
                return b = n != 0F;
            case STRING:
                if (s == null) {
                    return false;
                }
                return !(s.toLowerCase().equals(String.valueOf(false)) || s.equals(String.valueOf(0)));
            default:
                throw new ConvertionException(type, Types.BOOL);
        }
    }

    public void convertToBool() {
        if (type != Types.BOOL) {
            b = getBool();
            type = Types.BOOL;
        }
    }

    public final void setValue(boolean b) {
        clear(Types.BOOL);
        this.type = Types.BOOL;
        this.b = b;
        fireChanged();
    }

    // number
    public MutableVariant(float n) {
        type = Types.NUMBER;
        this.n = n;
    }

    public boolean isNumber() {
        return type == Types.NUMBER;
    }

    public boolean isInteger() {
        return type == Types.NUMBER && n % 1 == 0;
    }

    public boolean isReal() {
        return type == Types.NUMBER && n % 1 != 0;
    }

    public boolean isNan() {
        return type == Types.NUMBER && Float.isNaN(n);
    }

    public boolean isInfinite() {
        return type == Types.NUMBER && Float.isInfinite(n);
    }

    public void convertToNumber() {
        if (type != Types.NUMBER) {
            n = getNumber();
            type = Types.NUMBER;
        }
    }

    public float getNumber() {
        switch (type) {
            case NUMBER:
                return n;
            case VOID:
                return 0f;
            case BOOL:
                if (b) {
                    return 1f;
                } else {
                    return 0f;
                }
            case STRING:
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException ex) {
                    return 0f;
                }
            default:
                throw new ConvertionException(type, Types.NUMBER);
        }
    }

    public final void setValue(float n) {
        clear(Types.NUMBER);
        this.type = Types.NUMBER;
        this.n = n;
        fireChanged();
    }

    // string
    public MutableVariant(String s) {
        type = Types.STRING;
        this.s = s;
    }

    public boolean isString() {
        return type == Types.STRING;
    }

    public void convertToString() {
        if (type != Types.STRING) {
            s = getString(null);
            type = Types.STRING;
        }
    }

    public String getString(RuntimeContext context) {
        switch (type) {
            case STRING:
                return s;
            case VOID:
                return "nil";
            case BOOL:
                return String.valueOf(b);
            case NUMBER:
                if (n % 1 == 0) {
                    return String.valueOf((int) n);
                } else if (Float.isNaN(n)) {
                    return "nan";
                } else if (Float.isInfinite(n)) {
                    return "infinity";
                } else {
                    return String.valueOf(n);
                }
            case FUNCTION:
                return f.getName();
            case OBJECT:
                if (o.nativeObject != null) {
                    return o.nativeObject.toString();
                }
                if (context != null) {
                    MutableVariant toString = o.scope.resolve(Identifiers.getID("toString"));
                    if (toString != null) {
                        RuntimeVisitor.callFunction(context, toString.getFunction(), RuntimeVisitor.returnValue);
                        return RuntimeVisitor.returnValue.getString(context);
                    }
                }
                return o.getName();
            case ARRAY:
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                int size = a.size();
                boolean first = true;
                for (int i = 0; i < size; i++) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(a.get(i).getString(context));
                    first = false;
                }
                sb.append("}");
                return sb.toString();
            default:
                throw new ConvertionException(type, Types.STRING);
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case STRING:
                return s;
            case VOID:
                return "nil";
            case BOOL:
                return String.valueOf(b);
            case NUMBER:
                if (n % 1 == 0) {
                    return String.valueOf((int) n);
                } else if (Float.isNaN(n)) {
                    return "nan";
                } else if (Float.isInfinite(n)) {
                    return "infinity";
                } else {
                    return String.valueOf(n);
                }
            case FUNCTION:
                return f.toString();
            case OBJECT:
                return o.toString();
            case ARRAY:
                StringBuilder sb = new StringBuilder();
                int size = a.size();
                for (int i = 0; i < size; i++) {
                    sb.append(a.get(i).toString());
                }
                return sb.toString();
            default:
                throw new ConvertionException(type, Types.STRING);
        }
    }

    public String toJson(int tab) {
        switch (type) {
            case STRING:
                return '"' + s + '"';
            case VOID:
                return "null";
            case BOOL:
                return String.valueOf(b);
            case NUMBER:
                if (n % 1 == 0) {
                    return String.valueOf((int) n);
                } else if (Float.isNaN(n) || Float.isInfinite(n)) {
                    return "-1";
                } else {
                    return String.valueOf(n);
                }
            case OBJECT:
                return o.toJson(tab);
            case ARRAY:
                StringBuilder sb = new StringBuilder();
                sb.append("[ ");

                boolean first = true;

                if (m == null) {
                    for (MutableVariant mv : a) {
                        if (!first) {
                            sb.append(", ");
                        }
                        first = false;

                        sb.append(mv.toJson(tab));
                    }
                } else {
                    for (MutableVariant key : m.keySet()) {
                        if (!first) {
                            sb.append(", ");
                        }
                        first = false;

                        sb.append(key.toJson(tab));
                        sb.append(" : ");
                        sb.append(m.get(key).toJson(tab));
                    }
                }

                sb.append(" ]");
                return sb.toString();
            default:
                throw new ConvertionException(type, Types.STRING);
        }
    }

    public Object toJavaObject() {
        switch (type) {
            case VOID:
                return null;
            case BOOL:
                return b;
            case NUMBER:
                if (n % 1 == 0) {
                    return (int) n;
                } else {
                    return n;
                }
            case STRING:
                return s;
            case OBJECT:
                return o.nativeObject;
            case ARRAY:
                return a;
            default:
                throw new ConvertionException(type, Types.OBJECT);
        }
    }

    public final void setValue(String s) {
        clear(Types.STRING);
        this.type = Types.STRING;
        this.s = s;
        fireChanged();
    }
    // function

    public boolean isFunction() {
        return type == Types.FUNCTION;
    }

    public FunctionInstance getFunction() {
        if (type != Types.FUNCTION) {
            throw new ConvertionException(type, Types.FUNCTION);
        }
        return f;
    }

    public final void setValue(FunctionInstance f) {
        clear(Types.FUNCTION);
        this.type = Types.FUNCTION;
        this.f = f;
    }

    public MutableVariant(FunctionInstance f) {
        type = Types.FUNCTION;
        this.f = f;
        fireChanged();
    }
    // object

    public boolean isObject() {
        return type == Types.OBJECT;
    }

    public ObjectInstance getObject() {
        if (type != Types.OBJECT) {
            throw new ConvertionException(type, Types.OBJECT);
        }
        return o;
    }

    public final void setValue(ObjectInstance o) {
        clear(Types.OBJECT);
        this.type = Types.OBJECT;
        this.o = o;
        fireChanged();
    }

    // array
    public boolean isArray() {
        return type == Types.ARRAY;
    }

    public List<MutableVariant> getArray() {
        convertToArray();
        return a;
    }

    public void setValue(List<MutableVariant> a) {
        clear(Types.ARRAY);
        this.type = Types.ARRAY;
        this.a = a;
        fireChanged();
    }

    public void add(MutableVariant mv) {
        convertToArray();
        a.add(mv);
        fireChanged();
    }

    public void addAll(List<MutableVariant> mvs) {
        convertToArray();
        a.addAll(mvs);
        fireChanged();

    }

    public void addAll(int i, List<MutableVariant> mvs) {
        convertToArray();
        toDimention(i - 1);
        a.addAll(i, mvs);
        fireChanged();

    }

    public void addCopy(MutableVariant mv) {
        convertToArray();
        a.add(new MutableVariant(mv));
        fireChanged();

    }

    public void addCopy(MutableVariant key, MutableVariant mv) {
        convertToArray();

        MutableVariant clone = new MutableVariant(mv);

        if (key.isNumber()) {
            int i = (int) key.getNumber();
            toDimention(i - 1);
            a.add(i, new MutableVariant(mv));
        } else {
            lazyMap();
            clone.k = key;
            a.add(clone);
            m.put(key, clone);
        }
        fireChanged();

    }

    public void addKey(MutableVariant key, int index) {
        convertToArray();
        lazyMap();
        MutableVariant value = a.get(index);
        value.k = new MutableVariant(key);
        m.put(value.k, value);
        fireChanged();

    }

    public void toDimention(int id) {
        for (int i = a.size(); i <= id; i++) {
            a.add(new MutableVariant());
        }
        fireChanged();
    }

    public MutableVariant getValue(int id) {
        convertToArray();
        toDimention(id);
        return a.get(id);
    }

    public MutableVariant getValue(MutableVariant key) {
        convertToArray();
        MutableVariant result = null;

        if (m != null && m.containsKey(key)) {
            result = m.get(key);
        } else if (key.isNumber()) {
            int id = (int) key.n;
            toDimention(id);
            result = a.get(id);
        } else if (a.contains(key)) {
            result = a.get(a.indexOf(key));
        } else {
            // ident not found or map is null
            lazyMap();
        }

        if (result == null) {
            result = new MutableVariant();
            a.add(result);
            if (m != null) {
                result.k = new MutableVariant(key);
                m.put(result.k, result);
            }
        }
        return result;

    }

    public void setCopyValue(int id, MutableVariant mv) {
        convertToArray();
        toDimention(id);
        a.set(id, new MutableVariant(mv));
        fireChanged();
    }

    public boolean containsValue(MutableVariant mv) {
        convertToArray();
        return a.contains(mv);
    }

    public void removeValue(int i) {
        convertToArray();
        a.remove(i);
        fireChanged();
    }

    public void removeKey(MutableVariant mv) {
        convertToArray();
        if (m != null) {
            m.remove(mv);
        }
        fireChanged();
    }

    public int size() {
        convertToArray();
        return a.size();
    }

    public int indexOf(MutableVariant value) {
        convertToArray();
        return a.indexOf(value);
    }

    public boolean isMap() {
        return m != null;
    }

    public int indexOfKey(MutableVariant key) {

        if (m != null && m.containsKey(key)) {
            return a.indexOf(m.get(key));
        } else if (key.isNumber()) {
            return (int) key.getNumber();
        } else {
            MutableVariant newV = new MutableVariant();
            a.add(newV);
            newV.k = key;
            m.put(key, newV);
            return a.size() - 1;
        }

    }

    public void sort() {
        convertToArray();
        Jdk6Legacy.sort(a);
    }

    public void sort(Comparator<MutableVariant> comparator) {
        convertToArray();
        Jdk6Legacy.sort(a, comparator);
    }

    public MutableVariant min() {
        convertToArray();
        return Collections.<MutableVariant>min(a);
    }

    public MutableVariant min(Comparator<MutableVariant> comparator) {
        convertToArray();
        return Collections.<MutableVariant>min(a, comparator);
    }

    public MutableVariant max() {
        convertToArray();
        return Collections.<MutableVariant>max(a);
    }

    public MutableVariant max(Comparator<MutableVariant> comparator) {
        convertToArray();
        return Collections.<MutableVariant>max(a, comparator);
    }

    /**
     * Swap two values
     * <p>
     * @param pos1 the position of item1
     * @param pos2 the position of item 2
     */
    public void swap(int pos1, int pos2) {
        convertToArray();
        Collections.swap(this.a, pos1, pos2);
    }

    public void convertToArray() {
        if (type != Types.ARRAY) {
            clear(Types.ARRAY);
            if (a == null) {
                a = new ArrayList<MutableVariant>();

                if (type != Types.INVALID && type != Types.VOID) {
                    a.add(new MutableVariant(this));
                }
            }
            type = Types.ARRAY;
        }
    }

    public void lazyMap() {
        if (this.m == null) {
            this.m = new HashMap<MutableVariant, MutableVariant>(MAP_INITIAL_CAPACITY);
        }
    }

    public MutableVariant getKey() {
        if (k != null) {
            return k;
        } else {
            return NIL;
        }
    }

    public MutableVariant cloneArray() {
        MutableVariant clone = new MutableVariant();

        int size = a.size();
        for (int i = 0; i < size; i++) {
            if (a.get(i).isArray()) {
                clone.add(a.get(i).cloneArray());
            } else {
                clone.addCopy(a.get(i));
            }
        }

        return clone;
    }

    public void clearArray() {
        convertToArray();
        a.clear();
    }

    public boolean isEmpty() {
        convertToArray();
        return a.isEmpty();
    }

    // event
    private void fireChanged() {
        if (changedListener != null) {
            changedListener.changed(this);
        }
    }

    // global
    public final void setValue(MutableVariant from) {
        if (from == null) {
            type = Types.INVALID;
            return;
        }

        clear(from.type);
        type = from.type;

        switch (from.type) {
            case INVALID:
                break;
            case VOID:
                break;
            case BOOL:
                b = from.b;
                break;
            case NUMBER:
                n = from.n;
                break;
            case STRING:
                s = from.s;
                break;
            case FUNCTION:
                f = from.f;
                break;
            case OBJECT:
                o = from.o;
                break;
            case ARRAY:
                a = from.a;
                m = from.m;
                break;
            default:
                throw new UnexpectedTypeException(from.type);
        }

        fireChanged();
    }

    /**
     * Calculate the hashcode of the object. Used for equals.
     * <p>
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;

        switch (type) {
            case VOID:
                return 29 * hash + type.ordinal();
            case BOOL:
                return 29 * hash + (this.b ? 1 : 0);
            case NUMBER:
                return 29 * hash + Float.floatToIntBits(this.n);
            case STRING:
                return 29 * hash + this.s.hashCode();
            case FUNCTION:
                return 29 * hash + this.f.decl.identity;
            case OBJECT:
                return 29 * hash + this.o.identity;
            case ARRAY:
                int mh = 0;
                if (this.m != null) {
                    mh = this.m.hashCode();
                }
                return 29 * hash + this.a.hashCode() + mh;
            default:
                throw new UnexpectedTypeException(type);
        }
    }

    /**
     * Get if two object are equals
     * <p>
     * @param obj the object to compare
     * <p>
     * @return true if equal else false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MutableVariant other = (MutableVariant) obj;
        if (this.type != other.type) {
            return false;
        }

        switch (type) {
            case BOOL:
                return b == other.b;
            case NUMBER:
                return n == other.n || (Float.isNaN(n) && Float.isNaN(other.n)) || (Float.isInfinite(n) && Float.isInfinite(other.n));
            case STRING:
                return s.equals(other.s);
            case FUNCTION:
                return f.decl.identity == other.f.decl.identity;
            case OBJECT:
                return o.identity == other.o.identity;
            case ARRAY:
                if (m != null && !m.equals(other.m)) {
                    return false;
                }
                return a.equals(other.a);
            default:
                return true;
        }
    }

    @Override
    public int compareTo(MutableVariant mv) {
        Types maxType = this.type;
        if (mv.type.compareTo(maxType) > 0) {
            maxType = mv.type;
        }

        switch (maxType) {
            case BOOL:
                convertToBool();
                mv.convertToBool();
                return (b == mv.b ? 0 : (mv.b ? -1 : 1));
            case NUMBER:
                convertToNumber();
                mv.convertToNumber();
                return Float.compare(this.n, mv.n);
            case STRING:
                convertToString();
                mv.convertToString();
                return s.compareTo(mv.s);
            case ARRAY:
            case FUNCTION:
            case OBJECT:
                if (this.equals(mv)) {
                    return 0;
                } else {
                    return 1;
                }
            default:
                throw new ConvertionException();
        }
    }
}
