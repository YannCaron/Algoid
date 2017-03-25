/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.visitor;

import fr.cyann.jasi.visitor.Context;

/**
 *
 * @author caronyn
 */
public class ToStringContext implements Context {

	private final StringBuilder result;

	public ToStringContext() {
		result = new StringBuilder();
	}

	public void append(boolean text) {
		result.append(text);
	}

	public void append(float text) {
		result.append(text);
	}

	public void append(char text) {
		result.append(text);
	}

	public void append(String text) {
		result.append(text);
	}

	public String getSource() {
		return result.toString();
	}

}
