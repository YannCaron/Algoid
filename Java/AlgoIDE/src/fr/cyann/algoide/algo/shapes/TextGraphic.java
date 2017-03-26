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
package fr.cyann.algoide.algo.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * The Line class. Creation date: 23 mai 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class TextGraphic extends Graphic {

	private float x;
	private float y;
	private String text;
	private Font font;

	public TextGraphic(float x, float y, String text, Font font, Color color, Stroke stroke) {
		super(color, stroke);
		this.x = x;
		this.y = y;
		this.text = text;
		this.font = font;
	}

	@Override
	public void drawShape(Graphics2D g) {
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int h = metrics.getHeight();
		int w = metrics.stringWidth(text);
		g.drawString(text, x - w / 2, y + h / 2);
	}
}
