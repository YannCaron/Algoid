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
package fr.cyann.jasi.parser;

import fr.cyann.jasi.builder.VoidFactory;
import fr.cyann.jasi.builder.FactoryStrategy;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;

/**
 * The KeyTokenStatement class. Creation date: 28 déc. 2011. Find the statement
 * by its token text and token type
 *
 * @author CyaNn
 * @version v0.1
 */
public class KeyStatement extends StatementLeafToken {

    private final TokenType tokenType;
    private final String key;
    private final boolean caseSensitive;

    /**
     * Default constructor.
     *
     * @param key the key to match
     */
    public KeyStatement(String key) {
        this(TokenType.SYMBOL, key, true, new VoidFactory());
    }

    /**
     * Constructor.
     *
     * @param type the type to match
     * @param key the key to match
     */
    public KeyStatement(TokenType type, String key) {
        this(type, key, true, new VoidFactory());
    }

    /**
     * Constructor.
     *
     * @param key the key to match
     * @param strategy the AST agregator to execute in case of grammar matched
     */
    public KeyStatement(String key, FactoryStrategy strategy) {
        this(TokenType.SYMBOL, key, true, strategy);
    }

    /**
     * Constructor.
     *
     * @param type the type to match
     * @param key the key to match
     * @param strategy the AST agregator to execute in case of grammar matched
     */
    public KeyStatement(TokenType type, String key, FactoryStrategy strategy) {
        this(type, key, true, strategy);
    }

    /**
     * Constructor.
     *
     * @param type the type to match
     * @param key the key to match
     * @param caseSensitive if the grammar is case sensitive
     * @param strategy the AST agregator to execute in case of grammar matched
     */
    public KeyStatement(TokenType type, String key, boolean caseSensitive, FactoryStrategy strategy) {
        super(strategy);
        this.tokenType = type;
        this.key = key;
        this.caseSensitive = caseSensitive;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return key;
    }

    /**
     * The tokentype accessor.
     *
     * @return the token type.
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isToken(Token token) {
        return tokenType.equals(token.getType())
                && caseSensitive ? key.equals(token.getText()) : key.equalsIgnoreCase(token.getText());
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toBNFString() {
        return '\'' + key + '\'';
    }
}
