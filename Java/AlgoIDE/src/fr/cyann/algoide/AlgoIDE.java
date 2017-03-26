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

import fr.cyann.algoide.panels.TabletteBorder;
import fr.cyann.algoide.plugin.PluginLoader;
import fr.cyann.tools.FileTools;
import fr.cyann.tools.GATracker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
/**
 * The AlgoIDE main class. Creation date: 20 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class AlgoIDE {

    public static PluginLoader PLUGIN_LOADER;

    /**
     * Main method, program entry point.
     * <p>
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        boolean isLaf = true;
        boolean isBorder = false;
        String pluginPath = null;
        String filePath = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.matches("^[-]{0,2}defaultLaf$")) {
                isLaf = false;
            } else if (arg.matches("^[-]{0,2}border$")) {
                isBorder = true;
            } else if (arg.matches("^[-]{0,2}pluginPath")) {
                if (i + 1 < args.length) {
                    pluginPath = args[i + 1];
                }
            } else if (arg.matches("^[-]{0,2}file")) {
                if (i + 1 < args.length) {
                    filePath = args[i + 1];
                }
            } else if (arg.matches("^[-]{0,2}noAnalytics")) {
                GATracker.avoid();
            }
        }

        if (pluginPath == null) {
            try {
                pluginPath = new File(".").getCanonicalPath().concat(AlgoIDEConstant.PLUGINS_FOLDER);
            } catch (IOException ex) {
                Logger.getLogger(AlgoIDE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        GATracker.getInstance().trackEvent("launch", "launch.run", "Desktop");
        PLUGIN_LOADER = new PluginLoader(new File(pluginPath));

        if (isLaf) {
            MainPanel.setLaf();
            JFrame.setDefaultLookAndFeelDecorated(true);
        }

        final MainPanel mainPanel = new MainPanel();
        if (isBorder) {
            mainPanel.setBorder(new TabletteBorder());
        }

        // open file
        if (filePath == null) {
            File current = FileTools.getCurrentFile();
            if (current != null) {
                mainPanel.loadFile(current);
            } else {
                mainPanel.loadFile(AlgoIDEConstant.EXAMPLE_FOLDER + AlgoIDEConstant.EXAMPLE_DEFAULT);
            }
        } else {
            mainPanel.loadFile(new File(filePath));
        }

        FileTools.copyRessources();

        JFrame frame = new JFrame(AlgoIDEConstant.APP_NAME);
        frame.setPreferredSize(new Dimension(1200, 930));

        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(AlgoIDE.class.getResource("/images/ic_algoid.png")));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        //frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        mainPanel.halfScreen();

        //frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
