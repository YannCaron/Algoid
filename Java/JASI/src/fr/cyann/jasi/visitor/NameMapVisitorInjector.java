
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
import java.util.Map;

/**
 * The TokenMapVisitorInjector class.
 * @author Yann Caron
 * @version v0.1
 */
public abstract class NameMapVisitorInjector implements VisitorInjector {

	private Map<String, VisitorInjector> map;

	/**
	 * Template method inspired from [GoF].<br>
	 * Configure here the matching between AST token text and method to execute when visited.<br>
	 * Prefer the HashMap for more efficiency.
	 * @return the map of visitor injector.
	 */
	protected abstract Map<String, VisitorInjector> getVisitorMap();

	/**
	 * Template method inspired from [GoF].<br>
	 * Configure here the default visitor in case of map does not match.
	 * @param ast the AST to visite
	 * @return the visitor injector.
	 */
	protected abstract VisitorInjector getDefault(AST ast);

	/** @inheritDoc */
	@Override
	public MethodVisitor getVisitor(AST ast) {
		// lazy initialization
		if (map == null) {
			map = getVisitorMap();
		}

		String text = ast.getName();
		VisitorInjector v = map.get(text);

		if (v != null) {
			return v.getVisitor(ast);
		} else {
			return getDefault(ast).getVisitor(ast);
		}

	}
}
