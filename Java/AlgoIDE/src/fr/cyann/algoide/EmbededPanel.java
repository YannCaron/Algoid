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
package fr.cyann.algoide;

import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.algoide.algo.Algo;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.algo.ScopeViewer;
import fr.cyann.algoide.algo.TextInvit;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.algoide.runtime.TaskManager;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author caronyn
 */
public class EmbededPanel extends AlgoIDEPanel {

	// controls
	//private ALEditor editor = null;
	private JTabbedPane mainTab = null;
	private Algo algo = null;
	private TextInvit invit = null;
	private AlgoLogger logger = null;
	private ScopeViewer scopeViewer = null;
	// parameters
	private String source;
	// task
	private TaskManager taskManager = null;

	// constructor
	public EmbededPanel() {
		super(new BorderLayout());

		//editor = new ALEditor();
		algo = new Algo();
		invit = new TextInvit();
		logger = new AlgoLogger();
		scopeViewer = new ScopeViewer();

		taskManager = new TaskManager(this, algo, invit, logger);

		mainTab = new JTabbedPane();
		//mainPane.addTab("source", editor);
		mainTab.addTab("algo", algo);
		mainTab.addTab("text", invit);
		mainTab.addTab("log", logger);
		mainTab.addTab("scope viewer", scopeViewer);

		add(mainTab, BorderLayout.CENTER);
	}

	// method
	public void setSource(String source) {
		this.source = source;
	}

	public void run() {
		stopRunning();
		taskManager.postWeakSource(source);
	}

	public void stopRunning() {
		algo.clearEvents();
		taskManager.terminateTasks();
		algo.initialize();
		invit.clear();
	}

	@Override
	public void fullScreen() {
		// do nothing
	}

	@Override
	public void halfScreen() {
		// do nothing
	}

	@Override
	public void miniScreen() {
		// do nothing
	}

	@Override
	public void showAlgoPanel() {
		mainTab.setSelectedIndex(0);
	}

	@Override
	public void showTextPanel() {
		mainTab.setSelectedIndex(1);
	}

	@Override
	public void showLog() {
		mainTab.setSelectedIndex(2);
	}

	@Override
	public void showScope() {
		mainTab.setSelectedIndex(3);
	}

	@Override
	public void showPanel(String name) {
	}

	@Override
	public void showDebugMenu() {
		// do nothing
	}

	@Override
	public void hideDebugMenu() {
		// do nothing
	}

	@Override
	public long getStepSpeed() {
		return 150L;
	}

	@Override
	public void ExceptionPopup(String message) {
		JOptionPane.showMessageDialog(this, message, "error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void scopeViewerDisplayScope(RuntimeContext context, Scope scope) {
		scopeViewer.displayScope(context, scope);
	}

	@Override
	public void editorHighlight(int from, int to) {
		// do nothing
	}

	@Override
	public void editorHighlightError(int from, int to) {
		// do nothing
	}

	@Override
	public void editorSetSelection(int pos) {
		// do nothing
	}

	@Override
	public void editorClearHighlight() {
		// do nothing
	}

	@Override
	public boolean editorIsLineHasBreakPoint(int line) {
		// do nothing
		return false;
	}

	@Override
	public void editorSpeech(String message, int from, int to) {
		// do nothing
	}

	@Override
	public void editorClearSpeech() {
		// do nothing
	}

}
