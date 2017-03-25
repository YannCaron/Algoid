/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.jasi.exception;

/**
 * The NotACallException class.
 * Creation date: 28 nov. 2012.
 * @author CyaNn
 * @version v0.1
 */
public class NotACallException extends JASIException{

	public NotACallException(MultilingMessage message, int pos) {
		super(message, pos);
	}

}
