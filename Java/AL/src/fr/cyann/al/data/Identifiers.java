/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.data;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The Identifiers class. Creation date: 26 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class Identifiers {

    private static class Ident {

        public final Integer i;
        public final String name;

        public Ident(Integer i, String name) {
            this.i = i;
            this.name = name;
        }

    }

    public static final int HASH_MAP_CAPACITY = 2000;
    private static final Map<String, Ident> map;
    private static final Map<Integer, String> revertMap;
    private static int objectIdent;
    private static final boolean caseSensitive = false;

    static {
        map = new TreeMap<String, Ident>();
        revertMap = new HashMap<Integer, String>(HASH_MAP_CAPACITY);
        objectIdent = 0;
    }

    public static synchronized Integer getID(String name) {
        String compName = caseSensitive ? name : name.toLowerCase();
        if (!map.containsKey(compName)) {
            Integer ident = map.size() + 10;
            map.put(compName, new Ident(ident, name));
            revertMap.put(ident, name);
        }
        return map.get(compName).i;
    }

    public static String valueOf(Integer index) {
        String name = revertMap.get(index);
        return name == null ? "IDENTIFIER ERROR" : name;
    }

    public static void initialize() {
        map.clear();
        revertMap.clear();
        objectIdent = 0;
    }

    public static int getObjectIdent() {
        return objectIdent++;
    }

    public static void debug() {
        System.out.println("Identifiers [map = " + map.toString() + "]");
    }
}
