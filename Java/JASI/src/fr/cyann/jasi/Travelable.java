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
package fr.cyann.jasi;

import fr.cyann.jasi.parser.Statement;
import fr.cyann.utils.Method;
import java.util.Queue;

/**
 * The Travelable interface. Give the ability of a tree to be traveled in depth first or breadth first algorithms.
 * @param <T> the abstract type of the tree to travel
 * @author Yann Caron
 * @version v0.1
 */
public interface Travelable<T> {

	/**
	 * Init the infinit loop status to false.<br>
	 * Mendatory for depthFirstTravel and breadthFirstTravelImpl.
	 * @see Statement#depthFirstTravel(fr.cyann.tools.Method, fr.cyann.tools.Method)
	 */
	public void initTravel();

	/**
	 * Visite the tree with the generic visitor in depth first algorithm.<br>
	 * Manage the case of infinit loop.<br>
	 * Present simple signature to the client.<br>
	 * Based on GoF Template methode.
	 * @see Statement#initTravel()
	 * @param method the method invoked before recursion
	 */
	public void depthFirstTravel(Method<T, T> method);

	/**
	 * Visite the tree with the generic visitor.<br>
	 * Template methode.
	 * @see Statement#initTravel()
	 * @param method the method invoked before recursion
	 */
	public void depthFirstTravelImpl(Method<T, T> method);

	/**
	 * Visite the tree with the generic visitor in breadth first algorithm.<br>
	 * Manage the case of infinit loop.<br>
	 * Present simple signature to the client.<br>
	 * Based on GoF Template methode.
	 * @see Statement#initTravel()
	 * @param method the method invoked before recursion
	 */
	public void breadthFirstTravel(Method <T, T>method);

	/**
	 * Visite the tree with the generic visitor.<br>
	 * Template methode.
	 * @see Statement#initTravel()
	 * @param method the method invoked before recursion
	 * @param queue the wueue to permeet breadth first 
	 */
	public void breadthFirstTravelImpl(Method<T, T> method, Queue<T> queue);
}
