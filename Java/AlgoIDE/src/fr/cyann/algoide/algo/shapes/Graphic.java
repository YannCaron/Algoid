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
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * The Graphic class.
 * Creation date: 21 mai 2013.
 * @author CyaNn 
 * @version v0.1
 */
public abstract class Graphic {
	
	private Color color;
	private Stroke stroke;

	public Graphic(Color color, Stroke stroke) {
		this.color = color;
		this.stroke = stroke;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setStroke(stroke);

		drawShape(g);
	}
	
	public abstract void drawShape(Graphics2D g);
	
}
