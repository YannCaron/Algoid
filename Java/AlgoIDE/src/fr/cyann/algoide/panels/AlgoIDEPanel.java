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
package fr.cyann.algoide.panels;

import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author caronyn
 */
public abstract class AlgoIDEPanel extends JPanel {

	// constructor
	public AlgoIDEPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public AlgoIDEPanel(LayoutManager layout) {
		super(layout);
	}

	public AlgoIDEPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public AlgoIDEPanel() {
	}

	// abstract methods
	public abstract void fullScreen();

	public abstract void halfScreen();

	public abstract void miniScreen();

	public abstract void showTextPanel();

	public abstract void showAlgoPanel();

	public abstract void showScope();

	public abstract void showLog();

	public abstract void showDebugMenu();

	public abstract void showPanel(String name);

	public abstract void hideDebugMenu();

	public abstract long getStepSpeed();

	public abstract void ExceptionPopup(String message);

	// scope
	public abstract void scopeViewerDisplayScope(RuntimeContext context, Scope scope);

	// editor
	public abstract void editorHighlight(int from, int to);

	public abstract void editorHighlightError(int from, int to);

	public abstract void editorSetSelection(int pos);

	public abstract void editorClearHighlight();

	public abstract boolean editorIsLineHasBreakPoint(int line);

	public abstract void editorSpeech(String message, int from, int to);

	public abstract void editorClearSpeech();

}
