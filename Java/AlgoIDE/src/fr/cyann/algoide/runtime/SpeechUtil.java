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

import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.algoide.panels.AlgoIDEPanel;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.lexer.Token;

/**
 <p>
 @author cyann
 */
public final class SpeechUtil {
	
	private SpeechUtil(){}
	
	public static void speechAST(AlgoIDEPanel panel, AST ast) {

		if (ast instanceof VariableDeclaration) {
			VariableDeclaration var = (VariableDeclaration) ast;
			addSpeech(panel, var.var.getLast().mv, var.getVar().getToken());
		} else if (ast instanceof Assignation) {
			Assignation var = (Assignation) ast;
			addSpeech(panel, var.var.getLast().mv, var.getVar().getToken());
		} else if (ast instanceof UnaryOperator) {
			UnaryOperator var = (UnaryOperator) ast;
			addSpeech(panel, var.getLast().mv, var.right.getToken());
		} else if (ast instanceof Variable) {
			Variable var = (Variable) ast;
			addSpeech(panel, var.getLast().mv, var.getToken());
			//System.out.println("Variable " + var.getToken().getText() + " = " + var.getLast().mv);
		}
	}

	public static void addSpeech(AlgoIDEPanel panel, MutableVariant v, Token token) {
		final int pos = token.getPos();
		final int len = token.getLength();

		if (!v.isNull() && !v.isFunction() && !v.isObject()) {
			panel.editorSpeech(v.toString(), pos, pos + len);
		}
	}
}
