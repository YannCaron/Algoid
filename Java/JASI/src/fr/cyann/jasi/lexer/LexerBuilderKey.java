
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
import java.util.Set;

/**
 * The KeyLexerBuilder class. Based on Builder (or Factory) and Decorator GoF
 * Design pattern.<br>
 *
 * @author Yann Caron
 * @version v0.1
 */
public class LexerBuilderKey extends TermLeafDecorator {

    private Set<String> keys;
    private TokenType tokenType;
    private Lexer lexer;
    private String term;

    /**
     * Default Constructor.
     *
     * @param decored the term to decorate (Based on GoF Decorator)
     * @param keys the keys to verify.
     * @param tokenType the token type used for token creation.
     * @param lexer the lexer where to add the created token.
     */
    public LexerBuilderKey(Term decored, Set<String> keys, TokenType tokenType, Lexer lexer) {
        super(decored);
        this.keys = keys;
        this.tokenType = tokenType;
        this.lexer = lexer;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getTerm() {
        return term;
    }

    /**
     * The keys accessor.
     *
     * @return the keys
     */
    public Set<String> getKeys() {
        return keys;
    }

    /**
     * The tokentype accessor.
     *
     * @return the token type
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * @inheritDoc <br>
     * If the decored correspond to the pattern, the routine verify if the key
     * is contains on key set to create token.<br>
     * Else it return true but token is not created.
     */
    @Override
    boolean find(CharacterIterator it) {
        int pos = it.getIndex();
        int col = getCol();

        boolean result = decored.find(it);
        if (result) {
            term = decored.getTerm();
            if (keys.contains(decored.getTerm())) {
                lexer.add(new Token(tokenType, decored.getTerm(), getLine(), col, pos));
            }
        }

        return result;
    }
}
