
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
package fr.cyann.jasi.lexer;

import java.text.CharacterIterator;

/**
 * The UntilTerm class. Creation date: 14 mai 2012.
 *
 * @author CyaNn
 * @version v0.1
 */
public class UntilTerm extends TermLeafDecorator {

    private String term;
    private final Term escape;

    /**
     * Default constructor.
     *
     * @param decored the decored (repeated) term.
     */
    public UntilTerm(Term decored) {
        this(decored, null);
    }

    public UntilTerm(Term decored, Term escape) {
        super(decored);
        this.escape = escape;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getTerm() {
        return term;
    }

    /**
     * @inheritDoc
     */
    @Override
    boolean find(CharacterIterator it) {
        StringBuilder sb = new StringBuilder("");
        boolean wasFound = false;
        boolean isEscaped = false;

        while (it.current() != CharacterIterator.DONE) {

            isEscaped = false;
            if (escape != null) {
                if (escape.find(it)) {
                    isEscaped = true;
                    sb.append(it.current());
                }
            }
            wasFound = decored.find(it);
            if (!isEscaped && wasFound) {
                break;
            }

            sb.append(it.current());
            if (it.current() == '\n') {
                newLine();
            }
            it.next();
        }

        if (wasFound) {
            sb.append(decored.getTerm());
            term = sb.toString();
            return true;
        } else {
            return false;
        }
    }
}
