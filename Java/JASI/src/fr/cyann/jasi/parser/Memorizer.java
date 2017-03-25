
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

import fr.cyann.jasi.Constants;
import fr.cyann.jasi.exception.MemorizerException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Memorizer class.<br>
 * Used to optimize the backtracking algorithm of the parser.<br>
 * It store the grammar for each position where it was found a first time. The next time the parser could take the good desition without test the grammar again.
 * @author Yann Caron
 * @version v0.1
 */
public class Memorizer implements Constants {

	private static int nbFound = 0;
	private static int nbSearched = 0;

	/**
	 * Get the memorized efficiency by comparing nb found backtracked choises and total of searched choises.
	 * @return the efficiency in percent.
	 */
	public static int getEfficiency() {
		return (nbFound * 100) / (nbFound + nbSearched);
	}

	private Map<Integer, Statement> memo;

	/**
	 * Default constructor is hidden because of singleton design pattern.
	 */
	public Memorizer() {
		memo = new HashMap<Integer, Statement>();
	}

	/**
	 * Clear the memoizer.
	 */
	public void clear() {
		memo.clear();
		nbFound = 0;
		nbSearched = 0;
	}

	/**
	 * Store the statement and its length at position if found.
	 * @param position the position where the grammar was found.
	 * @param statement the statement found to parse.
	 */
	public void store(int position, Statement statement) {
		if (memo.containsKey(position) && !statement.equals(memo.get(position))) {
			throw new MemorizerException(EX_MEMORIZER);
		} else {
			memo.put(position, statement);
		}
	}

	/**
	 * Get the statement memorized in position.
	 * @param position the position to search.
	 * @return the statement corresponding to the position.
	 */
	public Statement get(int position) {
		if (memo.containsKey(position)) {
			nbFound++;
			return memo.get(position);
		} else {
			nbSearched++;
			return null;
		}
	}

	/**
	 * Get if memorizer is empty.
	 * @return true if map is empty.
	 */
	public boolean isEmpty() {
		return memo.isEmpty();
	}

	/**
	 * Get the memorized size (the number of memorized grammars)
	 * @return the size of the map.
	 */
	public int size() {
		return memo.size();
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return
	 */
@Override
	public String toString() {
		return "Memorizer{" + "memo=" + memo + '}';
	}
}
