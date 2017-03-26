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
import fr.cyann.al.library.LibraryBasket;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;

/**
 *
 * @author caronyn
 */
public class PluginLoader extends LibraryBasket<Plugin> {

	public PluginLoader(File path) {
		super(Plugin.class, path);
	}

	private ApplicationContext appContext;

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	// method
	public void loadAdditionalPanels(JTabbedPane tab) {
		try {
			for (Plugin plugin : this) {
				plugin.initialize();
				PluginPanel panel = plugin.getAdditionalPanel();

				if (panel != null) {
					tab.addTab(panel.getName(), panel);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, "Plugins error. Cannot add them in context !", ex);
		}
	}

	public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {
		try {
			if (appContext == null) {
				throw new RuntimeException("Plugin needs application context to work !");
			}

			for (Plugin plugin : this) {
				plugin.addDynamicMethods(appContext, context, dynamicMethods);
			}
		} catch (Exception ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, "Plugins error. Cannot add them in context !", ex);
		}
	}

	public void addFrameworkObjects(ASTBuilder builder) {
		try {
			if (appContext == null) {
				throw new RuntimeException("Plugin needs application context to work !");
			}

			for (Plugin plugin : this) {
				plugin.addFrameworkObjects(appContext, builder);
			}
		} catch (Exception ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, "Plugins error. Cannot add them in context !", ex);
		}
	}

	public void clearEvents() {
		try {
			for (Plugin plugin : this) {
				plugin.removeListeners();
			}
		} catch (Exception ex) {
			Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, "Plugins error. Cannot add them in context !", ex);
		}
	}
}
