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
package fr.cyann.algoide.algo;

import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.scope.NestedScope;
import fr.cyann.al.scope.ObjectScope;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.tools.swing.JAutoScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.VerticalLayout;

/**
 * The ScopeViewer class.
 * Creation date: 9 juin 2013.
 * @author CyaNn
 * @version v0.1
 */
public class ScopeViewer extends JPanel {

	private JAutoScrollPane scroll = null;
	private JPanel scopeView = null;

	public ScopeViewer() {
		super(new BorderLayout());
		//setBackground(AlgoIDEConstant.RUN_BACKGROUND_COLOR);

		scopeView = new JPanel(new GridLayout(0, 1));

		VerticalLayout layout = new VerticalLayout();
		//layout.setGap(5);
		scopeView.setLayout(layout);
		scopeView.setBackground(Color.WHITE);
		scopeView.setBorder(new EmptyBorder(10, 10, 10, 10));

		scroll = new JAutoScrollPane();
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setViewportView(scopeView);
		scroll.autoScroll();

		add(scroll);
	}

	public void clear() {
		scopeView.removeAll();
	}

	public synchronized void displayScope(RuntimeContext context, Scope scope) {
		clear();
		if (scope != null) {
			displayScopeRec(context, scope);
		}

		scroll.revalidate();
		scroll.repaint();
	}

	private void displayScopeRec(RuntimeContext context, Scope scope) {

		if (scope instanceof ObjectScope) {
			ObjectScope os = (ObjectScope) scope;

			displayScopeRec(context, os.getEnclosing());

			for (ObjectScope parent : os.getParents()) {
				displayScopeRec(context, parent);
			}
		} else if (scope instanceof NestedScope) {
			displayScopeRec(context, ((NestedScope) scope).getEnclosing());
		}

		addScope(context, scope);
	}

	private void addScopeLabel(String text) {

		if (scopeView.getComponentCount() != 0) {
			scopeView.add(Box.createRigidArea(new Dimension(0, 10)));
		}

		JLabel label = new JLabel(text);
		label.setOpaque(true);
		label.setBackground(AlgoIDEConstant.SCOPE_HIGHLIGHT_COLOR);
		label.setForeground(AlgoIDEConstant.RUN_TEXT_COLOR);
		label.setFont(AlgoIDEConstant.SCOPE_FONT);
		scopeView.add(label);
	}

	private void addVariableLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(AlgoIDEConstant.RUN_TEXT_COLOR);
		label.setFont(AlgoIDEConstant.SCOPE_FONT);
		label.setBorder(new EmptyBorder(0, 15, 0, 0));
		scopeView.add(label);
	}

	private synchronized void addScope(RuntimeContext context, Scope scope) {

		String text = scope.getName().replace("#", " - ");
		addScopeLabel(text);

		for (Integer key : scope.variables.keySet()) {
			addVariable(context, Identifiers.valueOf(key), scope.getVariables().get(key));
		}
	}

	public void addVariable(RuntimeContext context, String name, MutableVariant mv) {
		String text = name + " (" + mv.getType().toString().toLowerCase() + ")";
		if (!"".equals(mv.getString(context))) {
			text += " = " + mv.getString(context);
		}

		addVariableLabel(text);
	}
}
