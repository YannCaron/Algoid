/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.jasi.exception;

import java.util.Locale;

/**
 *
 * @author caronyn
 */
public class MultilingMessage {

	private final String english, french;
	private Object[] args;

	public MultilingMessage(String english, String french) {
		this.english = english;
		this.french = french;
	}

	public MultilingMessage setArgs(Object ... args) {
		this.args = args;
		return this;
	}

	@Override
	public String toString() {
		if (Locale.getDefault() == Locale.FRENCH || Locale.getDefault() == Locale.FRANCE) {
			return String.format(french, args);
		}
		return String.format(english, args);
	}

}
