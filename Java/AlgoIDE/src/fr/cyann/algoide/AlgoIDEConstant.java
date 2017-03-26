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

import fr.cyann.tools.ColorTools;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SpinnerNumberModel;

/**
 * The AlgoIDEConstant interface.
 * Creation date: 20 mai 2013.
 * @author CyaNn
 * @version v0.1
 */
public interface AlgoIDEConstant {

	public static final String APP_VERSION = "1.0.0";
	public static final String APP_NAME = "AlgoIDE - v" + APP_VERSION + " - learning programming made simple and funny !";
	public static final boolean DEBUG_MODE = false;
	public static final Font SOURCE_FONT = Font.decode(Font.MONOSPACED + " " + 15);
	public static final Font INVIT_FONT = Font.decode(Font.MONOSPACED + " " + 15);
	public static final Font LOG_FONT = Font.decode(Font.MONOSPACED + " " + 15);
	public static final Font SCOPE_FONT = Font.decode(Font.MONOSPACED + " " + 15);
	public static final Color TRANSPARENT = ColorTools.alphaColor(Color.BLACK, 0);
	public static final Color REFLECT_COLOR = ColorTools.alphaColor(Color.WHITE, 30);
	public static final Color BACKGROUND_COLOR = Color.decode("#333333");
	public static final Color FORE_COLOR = Color.decode("#333333");
	public static final Color RUN_BACKGROUND_COLOR = Color.decode("#ffffff");
	public static final Color RUN_TEXT_COLOR = Color.decode("#999999");
	public static final int EDITOR_FONT_SIZE = 15;
	public static final Color EDITOR_LINE_COLOR = ColorTools.alphaColor(Color.decode("#0099cc"), 0x33);
	public static final Color EDITOR_HIGHLIGHT_COLOR = ColorTools.alphaColor(Color.decode("#33b5e5"), 0x99);
	public static final Color EDITOR_ERROR_COLOR = ColorTools.alphaColor(Color.decode("#ff4444"), 0x33);
	public static final Color EDITOR_BREAKPOINT_COLOR = ColorTools.alphaColor(Color.decode("#9933cc"), 0x33);
	public static final Color SCOPE_HIGHLIGHT_COLOR = Color.decode("#254449");
	public static final String CURRENT_FILE = "current.al";
	public static final String AL_EXTENSION = ".al";
	public static final String IMAGE_FOLDER = "/images/";
	public static final String RUN_BACKGROUND_IMAGE_NAME = "bg_algo.png";
	public static final String RUN_BACKGROUND_IMAGE = IMAGE_FOLDER + RUN_BACKGROUND_IMAGE_NAME;
	public static final String PLUGINS_FOLDER = "/plugins/";
	public static final String EXAMPLE_FOLDER = "examples/";
	public static final String EXAMPLE_DEFAULT = "demo.al";
	public static final String[] EXAMPLE_FILES = {
		"demo.al",
		"fractal/",
		"fractal/binary touch.al",
		"fractal/binary tree.al",
		"fractal/koch fractal.al",
		"games/",
		"games/breakout.al",
		"games/space invaders.al",
		"games/star battle.al",
		"graphics/",
		"graphics/3d.al",
		"graphics/3dAlpha.al",
		"graphics/3dSpline.al",
		"graphics/spiral1.al",
		"graphics/spiral2.al",
		"graphics/square1.al",
		"graphics/square2.al",
		"graphics/square3.al",
		"graphics/square4.al",
		"graphics/target.al",
		"language/",
		"language/aspect.al",
		"language/benchmark.al",
		"language/cascade.al",
		"language/count word.al",
		"language/duck typing.al",
		"language/filter map reduce.al",
		"language/functional.al",
		"language/lexical closure.al",
		"language/parsing1.al",
		"language/parsing2.al",
		"math/",
		"math/calculator.al",
		"math/cellular automaton.al",
		"math/graphic calculator.al",
		"math/graphic calculator 2d.al",
		"text/",
		"text/hello you.al"};

	public static final String NEW_FILE_TEMPLATE = "";

	public enum Severity {

		INFO(Color.decode("#999999")),
		WARN(Color.decode("#ff8800")),
		ERR(Color.decode("#ff4444"));
		private Color color;

		private Severity(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
	}

	public enum SyntaxColors {

		SYMBOL(Color.decode("#cc0000")),
		COMMENT(Color.decode("#669900")),
		KEYWORD(Color.decode("#0099cc")),
		NIL(Color.decode("#9933cc")),
		BOOLEAN(Color.decode("#9933cc")),
		NUMBER(Color.decode("#9933cc")),
		STRING(Color.decode("#9933cc")),
		IDENT(Color.decode("#000000"));
		private Color color;

		private SyntaxColors(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
	}
	public static final SpinnerNumberModel DEFAULT_STEP_SPEED = new SpinnerNumberModel(150, 10, 1500, 10);
	public static final String ANALYTICS_UID = "UA-36056995-4";
}
