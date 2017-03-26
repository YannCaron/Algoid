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
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.visitor.Context;
import fr.cyann.jasi.visitor.MethodVisitor;
import fr.cyann.jasi.visitor.VisitorInjector;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * @author caronyn
 */
public class StepByStepExecution extends RuntimeVisitor {

	private final RuntimeVisitor decored;
	private final AlgoIDEPanel mainPanel;
	private final long timeout;
	private final Thread currentVisitorThread = null;
	private boolean active = true;

	/**
	 * Constructor.
	 * <p>
	 * @param decored the decored execution (visitor injection)
	 * @param timeout the time spent between each executions
	 * @param taskManager the interpreter thread
	 */
	public StepByStepExecution(RuntimeVisitor decored, long timeout, AlgoIDEPanel mainPanel) {
		super();
		this.decored = decored;
		this.timeout = timeout;
		this.mainPanel = mainPanel;
	}

	/**
	 * Interrupt thread execution
	 */
	public void interrupt() {
		if (currentVisitorThread != null) {
			currentVisitorThread.interrupt();
		}
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Decorate each visitor injected to AST when visitor is injected.
	 * <p>
	 * @param ast the ast to decorate.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MethodVisitor<AST, RuntimeContext> getVisitor(AST ast) {
		return new MethodVisitor<AST, RuntimeContext>() {
			@Override
			public void visite(AST ast, final RuntimeContext context) {
				try {
					if (ast.getToken().getPos() > 0 && active) {
						final int pos = ast.getToken().getPos();
						final int len = ast.getToken().getLength();

						try {
							// TODO
							//  mais aussi pointer sur l'élément dans le scope si possible
							mainPanel.scopeViewerDisplayScope(context, context.scope);
						} catch (ConcurrentModificationException ex) {
							Logger.getLogger(StepByStepExecution.class.getName()).log(Level.SEVERE, "Step by step display scope exception !");
						}
						mainPanel.editorHighlight(pos, pos + len);
						mainPanel.editorSetSelection(pos);

						decored.getVisitor(ast).visite(ast, context);

						SpeechUtil.speechAST(mainPanel ,ast);

						Thread.sleep(timeout);

					} else {
						decored.getVisitor(ast).visite(ast, context);

					}

				} catch (InterruptedException ex) {
					Logger.getLogger(StepByStepExecution.class.getName()).log(Level.SEVERE, "Step by step interrupted !");
					throw new RuntimeInterruptedExeption(ex);
				} finally {
					mainPanel.editorClearHighlight();
				}
			}
		};
	}

	/**
	 * Get the decored visitor map.
	 */
	public Map<Class<? extends AST>, VisitorInjector> getVisitorMap() {
		return decored.getVisitorMap();
	}

	/**
	 * Get the decored visitor hashcode.
	 */
	public int hashCode() {
		return decored.hashCode();
	}

	/**
	 * Get the decored visitor equals.
	 */
	public boolean equals(Object o) {
		return decored.equals(o);
	}

	/**
	 * Get the string representation of the decored object.
	 */
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
