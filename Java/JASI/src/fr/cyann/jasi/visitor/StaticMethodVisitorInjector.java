
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
package fr.cyann.jasi.visitor;

import fr.cyann.jasi.ast.AST;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The MethodVisitorInjector class.
 * @param <T> the AST type to inject.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class StaticMethodVisitorInjector<T extends AST, C extends Context> implements VisitorInjector, StaticMethodVisitor<T, C> {

	/** @inheritDoc */
	@Override
	public MethodVisitor getVisitor(AST ast) {
		return this;
	}

	/**
	 * Return the string representation of the object.<br>
	 * Use is reserved to debug and traces in complex recursive stack.
	 * @return 
	 */
	@Override
	public String toString() {

		String typeStr = "";

		Type paramType = this.getClass().getGenericSuperclass();
		if (paramType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) paramType;
			Type type = pType.getActualTypeArguments()[0];
			typeStr = type.toString().substring(type.toString().lastIndexOf('.') + 1);
		}

		return "SimpleInjector#" + typeStr;
	}
}
