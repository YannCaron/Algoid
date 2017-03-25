/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

/**
 *
 * @author CARONYN
 */
public enum Platform {

	all("both"), desktop("desktop"), android("android"), algea("algea");
	String text;

	String getText() {
		return text;
	}

	Platform(String text) {
		this.text = text;
	}
}
