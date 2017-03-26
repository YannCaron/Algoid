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
package fr.cyann.tools.swing;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollPane;

/**
 * The JAutoScrollPane class.
 * Creation date: 1 juin 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class JAutoScrollPane extends JScrollPane {

	public static final int THRESHOLD = 20;
	private boolean autoScroll = false;

	public JAutoScrollPane() {
		init();
	}

	public JAutoScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
		init();
	}

	public JAutoScrollPane(Component view) {
		super(view);
		init();
	}

	public JAutoScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		init();
	}

	public void autoScroll() {
		this.autoScroll = true;
	}

	private void init() {

		this.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int max = JAutoScrollPane.this.getVerticalScrollBar().getMaximum() - JAutoScrollPane.this.getHeight();

				if (e.getValueIsAdjusting()) {
					if (e.getValue() + THRESHOLD >= max) {
						autoScroll();
					} else {
						autoScroll = false;
					}
				}

				if (autoScroll) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			}
		});
	}
}
