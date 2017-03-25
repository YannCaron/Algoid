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
public enum ReferenceType {

	object("object"),
	enumeration("enum"),
	prototype("prototype"),
	factory("factory"),
	property("property"),
	method("method"),
	functionalMethod("functional method"),
	event("event");

	String text;

	String getText() {
		return text;
	}

	ReferenceType(String text) {
		this.text = text;
	}
}
