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
package fr.cyann.al.data;

/**
 * The Types enum.<br>
 * Represent all the available data type in typing system. Creation date: 30
 * mars 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public enum Types {

	/**
	 * value
	 */
	INVALID,
	/**
	 * Null value
	 */
	VOID,
	/**
	 * Boolean value e.g. true or false
	 */
	BOOL,
	/**
	 * Number value e.g. -10 0 10 10.5
	 */
	NUMBER,
	/**
	 * String value "string"
	 */
	STRING,
	/**
	 * Functional pointer
	 */
	FUNCTION,
	/**
	 * Object pointer
	 */
	OBJECT,
	/**
	 * Array pointer
	 */
	ARRAY;

	public static boolean contains(String key) {
		for (Types type : values()) {
			if (type.toString().equals(key)) {
				return true;
			}
		}
		return false;
	}
}