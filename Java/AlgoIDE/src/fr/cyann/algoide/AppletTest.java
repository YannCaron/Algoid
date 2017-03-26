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


import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.algoide.EmbededPanel;
import fr.cyann.algoide.MainPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author caronyn
 */
public class AppletTest {

	public static void main(String[] args) {
		MainPanel.setLaf();
		JFrame.setDefaultLookAndFeelDecorated(true);

		EmbededPanel panel = new EmbededPanel();

		JFrame frame = new JFrame(AlgoIDEConstant.APP_NAME);
		frame.setPreferredSize(new Dimension(700, 700));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		panel.setSource("loop (4) {\n\talgo.go(100);\n\talgo.turnLeft(90);\n}");
		panel.run();

	}
}
