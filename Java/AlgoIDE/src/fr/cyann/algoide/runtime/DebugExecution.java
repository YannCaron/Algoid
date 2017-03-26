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
package fr.cyann.algoide.runtime;

import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.algoide.MainPanel;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 The DebugExecution class.
 Creation date: 9 juin 2013.
 <p>
 @author CyaNn
 @version v0.1
 */
public class DebugExecution extends RuntimeVisitor {

	/**
	 The Several kind of actions when process is on debug mode.
	 <p>
	 @author CyaNn
	 */
	public enum Action {

		NONE, NEXT_TOKEN, NEXT_LINE, CONTINUE;
	}
	private RuntimeVisitor decored;
	private AlgoIDEPanel mainPanel;
	private final TaskManager taskManager;
	private Action nextAction;
	private int prevLine;

	/**
	 Default constructor.
	 <p>
	 @param decored
	 the decored visitor
	 @param interpreterThread
	 the interpreter thread to manipulate runtime
	 */
	public DebugExecution(RuntimeVisitor decored, TaskManager taskManager, AlgoIDEPanel mainPanel) {
		super();
		this.decored = decored;
		this.mainPanel = mainPanel;
		this.taskManager = taskManager;
	}

	/**
	 Reset all parameters
	 */
	public void initialize() {
		prevLine = -1;
		nextAction = Action.NONE;
	}

	/**
	 Continue running execution and stop according the user action
	 <p>
	 @param action
	 the user action
	 */
	public void continueRunning(Action action) {
		nextAction = action;

		synchronized (taskManager) {
			taskManager.notify();
		}
	}

	/*
	 * Determine if running must be stopped according the defined action.
	 */
	private boolean mustStop(int line, int prevLine) {
		if (line != prevLine && mainPanel.editorIsLineHasBreakPoint(line)) {
			return true;
		}

		if (nextAction == Action.NEXT_TOKEN) {
			nextAction = Action.NONE;
			return true;
		}

		if (line != prevLine && nextAction == Action.NEXT_LINE) {
			return true;
		}

		return false;
	}

	/**
	 Decorate each visitor injected to AST when visitor is injected.
	 <p>
	 @param ast
	 the ast to decorate.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MethodVisitor<AST, RuntimeContext> getVisitor(AST ast) {
		return new MethodVisitor<AST, RuntimeContext>() {

			@Override
			public void visite(AST ast, final RuntimeContext context) {
				try {
					int line = ast.getToken().getLine();

					if (mustStop(line, prevLine)) {
						final int pos = ast.getToken().getPos();
						final int len = ast.getToken().getLength();

						mainPanel.scopeViewerDisplayScope(context, context.scope);
						mainPanel.editorHighlight(pos, pos + len);
						mainPanel.editorSetSelection(pos);
						mainPanel.showDebugMenu();

						Thread.currentThread().wait();

					}

					prevLine = line;

					decored.getVisitor(ast).visite(ast, context);

				} catch (InterruptedException e) {
					Logger.getLogger(DebugExecution.class.getName()).log(Level.SEVERE, "Debug interrupted !");
					throw new RuntimeInterruptedExeption(e);
				}
			}
		};
	}

	/**
	 Get the decored visitor map.
	 */
	@Override
	public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
		return decored.getVisitorMap();
	}

	/**
	 The hash code.
	 */
	@Override
	public int hashCode() {
		return decored.hashCode();
	}

	/**
	 Eval if equals.
	 */
	@Override
	public boolean equals(Object o) {
		return decored.equals(o);
	}

	/**
	 Send the string representation of the object.
	 */
	@Override
	public String toString() {
		return decored.toString();
	}

	@Override
	public void initialize(ASTBuilder builder, Context context) {
		decored.initialize(builder, context);
	}

	@Override
	public void initialize(ASTBuilder builder, RuntimeContext context) {
		decored.initialize(builder, context);
	}

	@Override
	public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {
		decored.addDynamicMethods(context, dynamicMethods);
	}

	@Override
	public void addFrameworkObjects(ASTBuilder arg0) {
		decored.addFrameworkObjects(arg0);
	}

	@Override
	public void addVisitors(Map<Class<? extends AST>, VisitorInjector> map) {
		decored.addVisitors(map);
	}
}
