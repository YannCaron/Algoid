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
package fr.cyann.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JComponent;

/**
 *
 * @author caronyn
 */
public final class ColorTools {

	private ColorTools() {
	}

	public static Color alphaColor(Color color, int alpha) {
		Color ret = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
		return ret;
	}

	public static void drawHorizontalTile(Graphics2D g, Image background, JComponent owner) {
		//int y = owner.getVisibleRect().y;
		int y = 0;
		int x = 0;

		while (x <= owner.getWidth()) {
			g.drawImage(background, x, y, owner);
			x += background.getWidth(owner);
		}


	}
}
