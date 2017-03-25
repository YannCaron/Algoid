
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
package fr.cyann.jasi.builder;

import fr.cyann.jasi.parser.StatementLeafToken;

/**
 * The VoidFactory class.<br>
 * Default strategy that make nothing to avoid behaviour.<br>
 * Used in case of Tracing to debug.
 * Creation date: 7 janv. 2012.
 * @author CyaNn 
 * @version v0.1
 */
public class VoidFactory implements FactoryStrategy {

	/** @inheritDoc */
	@Override
	public void buildLeaf(InterpreterBuilder builder, StatementLeafToken statement) {
		// do nothing
	}
}
