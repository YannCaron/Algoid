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
package fr.cyann.jasi.lexer;

import java.util.HashMap;
import java.util.Map;

/**
 * The TokenType class. Creation date: 27 déc. 2011.<br>
 * Based on typeSafe enum design pattern and modified to become dynamic
 * flyweight.<br>
 * It is made to create an enum that members can be added in future use.
 *
 * @author CyaNn
 * @version v0.1
 */
public final class TokenType implements Comparable<TokenType> {

    /**
     * SYMBOL default token types *
     */
    public static final TokenType SYMBOL = new TokenType("SYMBOL");
    /**
     * EOF default token types *
     */
    public static final TokenType EOF = new TokenType("EOF");
    private static final Map<String, TokenType> map;
    private static int nextOrdinal;
    private final int ordinal;
    // attributs
    private final String name;
    private boolean ignoreParsing;

    /**
     * Initialize static context *
     */
    static {
	map = new HashMap<String, TokenType>();
	map.put(EOF.name, EOF);
	//map.put(CTRL.name, CTRL);
	map.put(SYMBOL.name, SYMBOL);
    }

    /**
     * Default constructor. The type name is mandatory.
     *
     * @param name the type name.
     */
    private TokenType(String name) {
	this.ordinal = nextOrdinal++;
	this.name = name;
	this.ignoreParsing = false;
    }

    private TokenType(String name, boolean ignoreParsing) {
	this.ordinal = nextOrdinal++;
	this.name = name;
	this.ignoreParsing = ignoreParsing;
    }

    public boolean isIgoreParsing() {
	return ignoreParsing;
    }

    /**
     * Verify if type name exists in type map.
     *
     * @param name the type name.
     * @return true if found.
     */
    public static boolean exist(String name) {
	return map.containsKey(name);
    }

    /**
     * Get the token type corresponding to the name.<br>
     * If exists, return existing, otherwise create it like flyweight.
     *
     * @param name the token type name
     * @return the token type corresponding
     */
    public static TokenType valueOf(String name) {
	return valueOf(name, false);
    }

    public static TokenType valueOf(String name, boolean ignoreParsing) {
	// flyweight lazy initialization
	if (!map.containsKey(name)) {
	    map.put(name, new TokenType(name, ignoreParsing));
	}

	return map.get(name);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int compareTo(TokenType o) {
	int res = ordinal - o.ordinal;
	if (res > 0) {
	    return 1;
	} else if (res < 0) {
	    return -1;
	} else {
	    return 0;
	}
    }

    /**
     * Return the string representation of the object.<br>
     * Use is reserved to debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
	return name;
    }
}
