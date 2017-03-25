
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

/**
 * The leaf of the composite (interpreter) tree.
 * Implements add/remove and throw exception (no child for a leaf).
 * @author Yann Caron
 */
public abstract class StatementLeaf extends Statement {

	/** @inheritDoc */
	@Override
	public Statement get(int index) {
		throw new UnsupportedOperationException("Leaf cannot have child.");
	}

	/** @inheritDoc */
	@Override
	public StatementNode add(Statement e) {
		throw new UnsupportedOperationException("Leaf cannot add child.");
	}

	/** @inheritDoc */
	@Override
	public StatementNode remove(Statement e) {
		throw new UnsupportedOperationException("Leaf cannot remove child.");
	}
}
