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

import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.algoide.algo.Algo;
import fr.cyann.algoide.algo.AlgoLogger;
import fr.cyann.algoide.algo.TextInvit;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.algoide.runtime.TaskManager;

/**
 *
 * @author caronyn
 */
public class ApplicationContext {

	private final TaskManager taskManager;
	private final AlgoIDEPanel panel;
	private final Algo algoPanel;
	private final TextInvit invitPanel;
	private final AlgoLogger loggerPanel;

	public ApplicationContext(TaskManager taskManager, AlgoIDEPanel panel, Algo algoPanel, TextInvit invitPanel, AlgoLogger loggerPanel) {
		this.taskManager = taskManager;
		this.panel = panel;
		this.algoPanel = algoPanel;
		this.invitPanel = invitPanel;
		this.loggerPanel = loggerPanel;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public AlgoIDEPanel getIDEPanel() {
		return panel;
	}

	public Algo getAlgoPanel() {
		return algoPanel;
	}

	public TextInvit getInvitPanel() {
		return invitPanel;
	}

	public AlgoLogger getLoggerPanel() {
		return loggerPanel;
	}

	public void callFunction(final RuntimeContext context, final FunctionInstance callback, final MutableVariant... parameters) {
		if (taskManager.getState() == Thread.State.WAITING) {
			taskManager.postWeakTask(new Runnable() {

				@Override
				public void run() {

					for (int i = 0; i < parameters.length; i++) {
						MutableVariant mv = parameters[i];
						RuntimeVisitor.setOptionalParameter(callback, i, mv);
					}

					RuntimeVisitor.callFunction(context, callback, context.returnValue);
				}
			});
		}
	}
}
