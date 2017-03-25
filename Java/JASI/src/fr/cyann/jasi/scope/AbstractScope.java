
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
package fr.cyann.jasi.scope;

import fr.cyann.jasi.Debuggable;
import fr.cyann.Cloneable;
import java.util.Map;

/**
 The AbstractScope class.
 <p>
 @param <T> the type of cotained variables
 <p>
 @author Yann Caron
 @version v0.1
 */
public interface AbstractScope<T extends Originated> extends Debuggable, Cloneable<AbstractScope<T>> {

	/**
	 Get the scope name;
	 <p>
	 @return the scope name;
	 */
	public String getName();

	/**
	 Get the root scope of the chain.<br>
	 Based on GoF chain of responcibility.
	 <p>
	 @return the root scope.
	 */
	public AbstractScope<T> getRoot();

	/**
	 Get the variable map.
	 <p>
	 @return the variable map.
	 */
	public Map<Integer, T> getVariables();

	/**
	 Get if variable is defined.
	 <p>
	 @param name the variable name
	 <p>
	 @return true if found in scope
	 */
	public boolean isAlreadyDefined(Integer identifier);

	/**
	 Define symbol in current scope.
	 <p>
	 @param name     the name of the variable.
	 @param variable the variable to define.
	 */
	public void define(Integer identifier, T variable);

	/**
	 Resolve the symbol by name.<br>
	 Based on GoF Chain of responcibility.<br>
	 <p>
	 @param name he variable name to resolve.
	 <p>
	 @return the founded variable looked first in local and nested until root.
	 <p>
	 @throws ScopeException if variable not found
	 */
	public T resolve(Integer identifier);

	/**
	 Resolve the symbol by name.<br>
	 Based on GoF Chain of responcibility.<br>
	 <p>
	 @param name       the variable name to resolve.
	 @param isForWrite ignore if on parameter scope.
	 <p>
	 @return the founded variable looked first in local and nested until root.
	 <p>
	 @throws ScopeException if variable not found
	 */
	public T resolve(Integer identifier, boolean isForWrite);

	/**
	 Remove the symbol by name.<br>
	 Based on GoF Chain of responcibility.<br>
	 <p>
	 @param name he variable name to resolve.
	 <p>
	 @return the founded variable looked first in local and nested until root.
	 */
	public void remove(Integer identifier);

	public AbstractScope<T> cloneUntil(ChainPredicate untilPredicate);

	public AbstractScope<T> cloneWhile(ChainPredicate whilePredicate);

}
