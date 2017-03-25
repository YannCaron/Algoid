/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.data;

/**
 <p>
 @author cyann
 */
public class Wrapper<T> {

	private T value;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	// constructor
	public Wrapper() {
	}

	public Wrapper(T value) {
		this.value = value;
	}

}
