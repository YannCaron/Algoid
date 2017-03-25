
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
package fr.cyann.utils;

/**
 * The Methode class.\n
 * Used as "generic visitor" instead of closures that are not available yet.
 * @param <A> The arguments type. Force to use super class or Object classes for arguments with differents types.
 * @param <R> The return type of the methode.
 * @author Yann Caron
 * @version v0.1
 */
public interface Method<R, A> {

	/**
	 * Invoke the method.
	 * @param args method arguments.
	 * @return the method result.
	 */
	public R invoke(A... args);
	
}
