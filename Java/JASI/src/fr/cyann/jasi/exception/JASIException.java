
/*
	YANN CARON CONFIDENTIAL
	__________________

	Yann Caron Copyright (c) 2011
	All Rights Reserved.
	__________________

	NOTICE:  All information contained herein is, and remains
	the property of Yann Caron and its suppliers, if any.
	The intellectual and technical concepts contained
	herein are proprietary to Yann Caron
	and its suppliers and may be covered by U.S. and Foreign Patents,
	patents in process, and are protected by trade secret or copyright law.
	Dissemination of this information or reproduction of this material
	is strictly forbidden unless prior written permission is obtained
	from Yann Caron.
*/
package fr.cyann.jasi.exception;

/**
 * The JASIException class.
 * @author Yann Caron
 * @version v0.1
 */
public class JASIException extends RuntimeException {

	private int pos;

	/**
	 * Get the position where exception occured.
	 * @return the position
	 */
	public int getPos() {
		return pos;
	}

	/** @inheritDoc */
	public JASIException(Throwable cause, int pos) {
		super(cause);
		this.pos = pos;
	}

	/** @inheritDoc */
	public JASIException(MultilingMessage message, Throwable cause, int pos) {
		super(message.toString(), cause);
		this.pos = pos;
	}

	/** @inheritDoc */
	public JASIException(MultilingMessage message, int pos) {
		super(message.toString());
		this.pos = pos;
	}

	/** @inheritDoc */
	public JASIException(int pos) {
		this.pos = pos;
	}

}
