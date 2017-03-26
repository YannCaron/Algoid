/* 
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.algoide.plugin;

import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.library.Lib;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;

/**
 *
 * @author caronyn
 */
public interface Plugin extends Lib {

	void initialize();

	PluginPanel getAdditionalPanel();

	void addDynamicMethods(final ApplicationContext appContext, RuntimeContext context, TypeNameFunctionMap dynamicMethods);

	void addFrameworkObjects(final ApplicationContext appContext, ASTBuilder builder);

	void removeListeners();
	
}
