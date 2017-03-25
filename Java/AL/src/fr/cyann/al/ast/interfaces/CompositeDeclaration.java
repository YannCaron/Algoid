
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

import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.visitor.Context;
import java.util.List;

/**
 * The CompositeDeclaration interface.<br> Add the capability to AST to have
 * configured parameters.<br> IMPORTANT : Used for function Parameter and object
 * Attributes.
 *
 * @author Yann Caron
 * @version v0.1
 */
public interface CompositeDeclaration<C extends Context> {

	/**
	 * Add parameter to the list of AST parameters.
	 *
	 * @param e the declaration of the parameter.
	 */
	public void addDeclaration(VariableDeclaration<C> e);

	/**
	 * Remove parameter from the list of AST parameters.
	 *
	 * @param e the declaration of the parameter.
	 */
	public void removeDeclaration(String name);

	/**
	 * Verify if parameter or attribute exists in the AST definition.
	 *
	 * @param v the variable that represents the declaration.
	 * @return true if found.
	 */
	public boolean isDeclarationExists(Variable<C> v);

	/**
	 * Verify if parameter or attribute exists in the AST definition.
	 *
	 * @param name the name of the variable that represents the declaration.
	 * @return true if found.
	 */
	public boolean isDeclarationExists(String name);

	/**
	 * Get the declaration designed by the var
	 *
	 * @param v the variable that represents the declaration.
	 * @return the declaration.
	 */
	public VariableDeclaration<C> getDeclaration(Variable<C> v);

	/**
	 * Get the declaration designed by the var
	 *
	 * @param name the variable name that represents the declaration.
	 * @return the declaration.
	 */
	public VariableDeclaration<C> getDeclaration(String name);

	/**
	 * Get the declaration designed by the var
	 *
	 * @param index the position of the declaration
	 * @return the declaration.
	 */
	public VariableDeclaration<C> getDeclaration(int index);

	/**
	 * Get the list of declaration length
	 *
	 * @return the length of declaration
	 */
	public int declarationLength();

	/**
	 * The parameters iterator accessor to loop on.
	 *
	 * @return the parameters iterator.
	 */
	public List<VariableDeclaration<C>> getDeclarations();
}
