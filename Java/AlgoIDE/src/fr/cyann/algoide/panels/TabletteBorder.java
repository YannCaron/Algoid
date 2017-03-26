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

import fr.cyann.algoide.AlgoIDEConstant;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author caronyn
 */
public class TabletteBorder extends AbstractBorder {

	private Image img0_0;
	private Image img0_1;
	private Image img0_2;
	private Image img1_0;
	private Image img1_2;
	private Image img2_0;
	private Image img2_1;
	private Image img2_2;
	private Image title;

	public TabletteBorder() {
		try {
			img0_0 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet0_0.png"), "tablet0_0.png"));
			img0_1 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet0_1.png"), "tablet0_1.png"));
			img0_2 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet0_2.png"), "tablet0_2.png"));
			img1_0 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet1_0.png"), "tablet1_0.png"));
			img1_2 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet1_2.png"), "tablet1_2.png"));
			img2_0 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet2_0.png"), "tablet2_0.png"));
			img2_1 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet2_1.png"), "tablet2_1.png"));
			img2_2 = ImageIO.read(new java.net.URL(getClass().getResource("/images/tablet2_2.png"), "tablet2_2.png"));
			title = ImageIO.read(new java.net.URL(getClass().getResource("/images/title.png"), "title.png"));
		} catch (MalformedURLException ex) {
			Logger.getLogger(TabletteBorder.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TabletteBorder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(img0_0.getHeight(c), img0_0.getWidth(c), img2_2.getHeight(c), img2_2.getWidth(c));
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.top = img0_0.getHeight(c);
		insets.left = img0_0.getWidth(c);
		insets.bottom = img2_2.getHeight(c);
		insets.right = img2_2.getWidth(c);
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		int left = img0_0.getWidth(c);
		int right = width - img2_2.getWidth(c);

		int top = img0_0.getHeight(c);
		int bottom = height - img2_2.getHeight(c);

		g.setColor(AlgoIDEConstant.BACKGROUND_COLOR);
		g.fillRect(0, 0, width, top);
		g.fillRect(0, 0, left, height);
		g.fillRect(right, 0, width, height);
		g.fillRect(0, bottom, width, height);

		for (int i = left; i < right; i += img0_1.getWidth(c)) {
			g.drawImage(img0_1, i, 0, c);
			g.drawImage(img2_1, i, bottom, c);
		}

		for (int i = top; i < bottom; i += img1_0.getHeight(c)) {
			g.drawImage(img1_0, 0, i, c);
			g.drawImage(img1_2, right, i, c);
		}

		g.drawImage(img0_0, 0, 0, c);
		g.drawImage(img0_2, right, 0, c);
		g.drawImage(img2_0, 0, bottom, c);
		g.drawImage(img2_2, right, bottom, c);

		if (width > title.getWidth(c) * 2) {
			g.drawImage(title, ((right - left)) / 2, bottom, c);
		}
		
		g.setColor(AlgoIDEConstant.REFLECT_COLOR);
		g.fillRect(0, 0, width, top);
		g.fillRect(0, top, left, height / 3);
		g.fillRect(right, top, width, height / 3);

	}
}
