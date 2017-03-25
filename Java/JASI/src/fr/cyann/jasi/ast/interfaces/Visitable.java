
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
package fr.cyann.jasi.ast.interfaces;

import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.VisitorInjector;

/**
 * The Visitable interface.<br>
 * Add the inject and visite capability to object.<br>
 * Insired from GoF Visitor Design pattern and modified with depencdency injection.
 * @author Yann Caron
 * @version v0.1
 */
public interface Visitable<C extends Context> {
	
	/**
	 * Inject the visitor method object to node. (Dependency injection)
	 * @param injector the method to execute when node is visited.
	 */
	public void injectVisitor(VisitorInjector injector);
	
	/**
	 * Execute method previously injected by injector.
	 * @param <C> The specific context type
	 * @param context the execution context 
	 */
	public void visite(C context);

}
