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
import java.awt.Color;
import java.util.HashMap;
import javax.swing.DefaultSyntaxColorizer;
import javax.swing.JXTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * The ALSyntaxColorizer class.
 * Creation date: 28 mai 2013.
 * @author CyaNn
 * @version v0.1
 */
public class ALSyntaxColorizer extends DefaultSyntaxColorizer {

	public ALSyntaxColorizer(JXTextPane component, HashMap<String, Color> keywords) {
		super(component, keywords);
	}

	@Override
	public void applyCommentStyle(MutableAttributeSet style) {
		StyleConstants.setForeground(style, AlgoIDEConstant.SyntaxColors.COMMENT.getColor());
	}

	@Override
	public void applyQuoteStyle(MutableAttributeSet style) {
		StyleConstants.setForeground(style, AlgoIDEConstant.SyntaxColors.STRING.getColor());
	}

	@Override
	public void applyOperatorStyle(MutableAttributeSet style) {
		StyleConstants.setForeground(style, AlgoIDEConstant.SyntaxColors.SYMBOL.getColor());
	}

	@Override
	public void applyNumberStyle(MutableAttributeSet style) {
		StyleConstants.setForeground(style, AlgoIDEConstant.SyntaxColors.NUMBER.getColor());
	}

	// TODO Tester la desactivation sur PI
	/*@Override
	public void processChangedLines(int offset, int length) throws BadLocationException {
		//super.processChangedLines(offset, length); //To change body of generated methods, choose Tools | Templates.
	}*/

}
