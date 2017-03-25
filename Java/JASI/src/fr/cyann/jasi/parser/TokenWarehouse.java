
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
package fr.cyann.jasi.parser;

import fr.cyann.jasi.lexer.Token;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The IdentWarehouse class.
 * @author Yann Caron
 * @version v0.1
 */
public final class TokenWarehouse {

	private volatile static TokenWarehouse singleton = null;
	private Map<String, Set<Token>> warehouse;

	/**
	 * Get the singleton unique instance.
	 * @return 
	 */
	public synchronized static TokenWarehouse getInstance() {
		if (singleton == null) {
			singleton = new TokenWarehouse();
		}
		return singleton;
	}

	/**
	 * Store the ident at is kind storage.
	 * e.g:
	 * JavaType
	 *	- type1
	 *  - type2
	 * JavaEnum
	 *  - enum1
	 *  - enum2
	 * @param kind the kind location to store.
	 * @param token the token to store.
	 */
	public void store(String kind, Token token) {
		Set idents = null;

		// lazy initialization
		if (!warehouse.containsKey(kind)) {
			idents = new HashSet<String>();
			warehouse.put(kind, idents);
		} else {
			idents = warehouse.get(kind);
		}

		idents.add(token);
	}

	/**
	 * Verify if name exist at specific location.
	 * @param kind the kind location.
	 * @param token the token to store.
	 * @return true if found.
	 */
	public boolean contains(String kind, Token token) {
		if (!warehouse.containsKey(kind)) {
			return false;
		} else {
			return warehouse.get(kind).contains(token);
		}
	}

	/**
	 * Default constructor private for singleton use.
	 */
	private TokenWarehouse() {
		warehouse = new HashMap<String, Set<Token>>();
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return 
	 */
	@Override
	public String toString() {
		return "TokenWarehouse{" + "warehouse=" + warehouse + '}';
	}
}
