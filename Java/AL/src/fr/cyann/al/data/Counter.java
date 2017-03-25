/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.data;

/**
 * The Counter class.
 * Creation date: 4 avr. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class Counter {
	
	public int count;
	
	public void increment() {
		count++;
	}
	
	public void decrement() {
		count--;
	}

	@Override
	public String toString() {
		return "Counter{" + "count=" + count + '}';
	}
	
}
