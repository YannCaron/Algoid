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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import static fr.cyann.algoide.AlgoIDE.PLUGIN_LOADER;
import fr.cyann.algoide.panels.TabletteBorder;
import fr.cyann.algoide.plugin.PluginLoader;
import fr.cyann.tools.GATracker;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 <p>
 @author caronyn
 */
public class AlgoApplet extends Applet {

	public enum Mode {

		FULL, FORUM
	}

	public static void main(String[] args) {
		fr.cyann.algoide.AlgoIDE.main(args);
	}

	@Override
	public void init() {
		// get parameters
		Mode md = Mode.FULL;
		String paramMode = this.getParameter("mode");
		String src = this.getParameter("source");

		if (paramMode != null && paramMode.equalsIgnoreCase("forum")) {
			md = Mode.FORUM;
		}
		final Mode mode = md;
		final String source = src;

		GATracker.getInstance().trackEvent("launch", "launch.run", "Applet");
		GATracker.getInstance().trackEvent("launch", "launch.mode", md.toString());

		String pluginPath = AlgoIDEConstant.PLUGINS_FOLDER;
		try {
			pluginPath = new File(".").getCanonicalPath().concat(AlgoIDEConstant.PLUGINS_FOLDER);
		} catch (IOException ex) {
			Logger.getLogger(AlgoApplet.class.getName()).log(Level.SEVERE, null, ex);
		}
		PLUGIN_LOADER = new PluginLoader(new File(pluginPath));

		MainPanel.setLaf();

		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					setName(AlgoIDEConstant.APP_NAME);
					setLayout(new BorderLayout());

					if (mode == Mode.FULL) {
						MainPanel mainPanel = new MainPanel();
						mainPanel.setBorder(new TabletteBorder());
						mainPanel.loadFile(AlgoIDEConstant.EXAMPLE_FOLDER + AlgoIDEConstant.EXAMPLE_DEFAULT);
						add(mainPanel, BorderLayout.CENTER);
					} else {
						EmbededPanel mainPanel = new EmbededPanel();
						mainPanel.setSource(source);
						add(mainPanel, BorderLayout.CENTER);
						mainPanel.run();
					}
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete");
			e.printStackTrace();
		}
	}
	Image buffer;

	private Graphics initBuffer() {
		if (buffer == null || buffer.getWidth(this) != this.getWidth() || buffer.getHeight(this) != this.getHeight()) {
			buffer = this.createImage(getWidth(), getHeight());
		}
		return buffer.getGraphics();
	}

	@Override
	public void paint(Graphics g) {
		Graphics bg = initBuffer();
		super.paint(bg);
		g.drawImage(buffer, 0, 0, this);
	}
}
