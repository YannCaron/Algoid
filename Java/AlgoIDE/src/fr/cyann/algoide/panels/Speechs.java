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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * @author cyann
 */
public class Speechs {

	public static final int MAX_LENGTH = 20;

	public static class Speech {

		public static final int MAX_DIST = 25;

		private final String message;
		private final int x;
		private final int y;
		private final int line;

		public String getMessage() {
			return message;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getLine() {
			return line;
		}

		public boolean isOverloaded(int x, int y) {
			return x == this.x && y == this.y;
		}

		public Speech(String message, int x, int y, int line) {
			this.message = message;
			this.x = x;
			this.y = y;
			this.line = line;
		}

	}

	public interface EachFunction {

		public void invoke(String text, int x, int y);
	}

	private final List<Speech> speechs;

	public Speechs() {
		speechs = new ArrayList<Speechs.Speech>();
	}

	public String normalize(String text) {
		int beLen = (MAX_LENGTH - 5) / 2;

		if (text.length() > MAX_LENGTH) {
			String begin = text.substring(0, beLen);
			String end = text.substring(text.length() - beLen);
			return begin + " ... " + end;
		} else {
			return text;
		}
	}

	public synchronized void addSpeech(String message, int x, int y, int line) {
		// remove overloaded
		for (int i = speechs.size() - 1; i >= 0; i--) {
			Speech item = speechs.get(i);
			if (item.isOverloaded(x, y)) {
				speechs.remove(i);
			} else if (item.getLine()-1 == line) {
				speechs.remove(i);
			}
		}

		speechs.add(new Speech(normalize(message), x, y, line));

	}

	public synchronized void each(EachFunction f) {
		for (Speech speech : speechs) {
			f.invoke(speech.message, speech.x, speech.y);
		}
	}

	public void clear() {
		speechs.clear();
	}

}
