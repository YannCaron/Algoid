/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.scope;

import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caronyn
 */
public class NestedScopeTest {

    Scope root;
    NestedScope scope;

    @Before
    public void setUp() throws Exception {
        root = new Scope("root");
        scope = new NestedScope("nested", root);
    }

    /**
     * Test of getRoot method, of class NestedScope.
     */
    @Test
    public void testGetRoot() {
        assertEquals(root, scope.getRoot());
    }

    /**
     * Test of resolve method, of class NestedScope.
     */
    @Test
    public void testResolve() throws Exception {
        root.define(Identifiers.getID("a"), new MutableVariant("a var"));
        scope.define(Identifiers.getID("b"), new MutableVariant("b var"));

        assertEquals(new MutableVariant("a var"), scope.resolve(Identifiers.getID("a")));
        assertEquals(new MutableVariant("b var"), scope.resolve(Identifiers.getID("b")));

        scope.define(Identifiers.getID("a"), new MutableVariant("a overriding var"));
        assertEquals(new MutableVariant("a overriding var"), scope.resolve(Identifiers.getID("a")));

    }

    /**
     * Test of getEnclosing method, of class NestedScope.
     */
    @Test
    public void testGetEnclosing() {
        assertEquals(root, scope.getEnclosing());
    }

    /**
     * Test of toString method, of class NestedScope.
     */
    @Test
    public void testToString() {
        root.define(Identifiers.getID("a"), new MutableVariant("a var"));
        scope.define(Identifiers.getID("b"), new MutableVariant("b var"));

        assertEquals("scope (nested) [b] => scope (root) [a]", scope.toString());
    }
}
