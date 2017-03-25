
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
package fr.cyann.al.ast.interfaces;

import fr.cyann.al.ast.Expression;
import fr.cyann.jasi.visitor.Context;

/**
 * The Conditionable interface.<br>
 * Add the cappability to an ast to be conditionable (testable if else while ect....).
 * @author Yann Caron
 * @version v0.1
 */
public interface Conditionable<C extends Context> {

	/**
	 * The condition accessor.
	 * @return the condition.
	 */
	public Expression<? extends Expression, C> getCondition();

	/**
	 * The condition mutator.
	 * @param condition the condition.
	 */
	public void setCondition(Expression<? extends Expression, C> condition);
}
