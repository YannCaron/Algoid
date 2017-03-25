
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
package fr.cyann.al.ast;

import fr.cyann.al.data.MutableVariant;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.visitor.Context;

/**
 * The Number class. Creation date: 8 janv. 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class ValueExpression<C extends Context> extends Expression<ValueExpression, C> {

    /**
     * Default constructor.
     *
     * @param token the token corresponding to the AST.
     */
    public ValueExpression() {
	super(new Token(TokenType.SYMBOL, "value"));
	super.mv = new MutableVariant();
    }
    
    public ValueExpression(Token token) {
	this();
    }

    @Override
    public void visite(C context) {
    }

    /**
     * Return the string representation of the object.<br> Use is reserved to
     * debug and traces in complex recursive stack.
     *
     * @return
     */
    @Override
    public String toString() {
	return "ValueExpression{value=" + super.mv + '}';
    }
}
