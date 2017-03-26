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
package fr.cyann.algoide.runtime;

import java.awt.Color;

/**
 * The Colors enum.
 * Creation date: 24 mai 2013.
 * @author CyaNn 
 * @version v0.1
 */
public enum Colors {

	TRANSPARENT(-1, new Color(0.0f, 0.0f, 0.0f, 0.0f)),
	BLACK(0, Color.BLACK),
	DKBLUE(1, Color.decode("#0000cc")),
	DKGREEN(2, Color.decode("#669900")),
	DKCYAN(3, Color.decode("#0099cc")),
	DKRED(4, Color.decode("#cc0000")),
	DKMAGENTA(5, Color.decode("#9933cc")),
	BROWN(6, Color.decode("#805500")),
	LTGRAY(7, Color.decode("#f6f4f2")),
	DKGRAY(8, Color.decode("#514d4a")),
	BLUE(9, Color.decode("#4444FF")),
	GREEN(10, Color.decode("#99cc00")),
	CYAN(11, Color.decode("#33b5e5")),
	RED(12, Color.decode("#ff4444")),
	MAGENTA(13, Color.decode("#aa66cc")),
	YELLOW(14, Color.decode("#fff444")),
	WHITE(15, Color.decode("#f3f3f3"));
	private int index;
	private Color color;

	private Colors(int index, Color color) {
		this.index = index;
		this.color = color;
	}

	/**
	 * Get the value by comparing the index.<
	 * 
	 * @param c
	 *            the color index.
	 * @return the color ENUM.
	 */
	public static Colors valueOf(int c) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].index == c) {
				return values()[i];
			}
		}

		return TRANSPARENT;
	}

	/**
	 * Get the color of the enum.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
}
