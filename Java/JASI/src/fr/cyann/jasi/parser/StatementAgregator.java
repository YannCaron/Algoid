
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

import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.lexer.BacktrackingIterator;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.utils.Method;
import java.util.Queue;

/**
 * The StatementAgregator class.
 * Decorate any statement to add it an aggregation strategy.
 * @author Yann Caron
 * @version v0.1
 */
public class StatementAgregator extends Statement {

    private StatementNode decored;
    private AgregatorStrategy strategy;

    /**
     * Default constructor based on Decorator.
     * @param decored the statement to decorate
     * @param strategy the strategy to execute in case of grammar matched
     */
    public StatementAgregator(StatementNode decored, AgregatorStrategy strategy) {
        this.decored = decored;
        this.strategy = strategy;
    }

    /** @inheritDoc */
    @Override
    public String getName() {
        return decored.getName();
    }

    /** @inheritDoc */
    @Override
    public Statement get(int index) {
        return decored.get(index);
    }

    /** @inheritDoc */
    @Override
    public StatementNode add(Statement e) {
        return decored.add(e);
    }

    /** @inheritDoc */
    @Override
    public StatementNode remove(Statement e) {
        return decored.remove(e);
    }

    /** @inheritDoc */
    @Override
    public boolean tryParse(BacktrackingIterator<Token> iterator) {
        return decored.tryParse(iterator);
    }

    /** @inheritDoc */
    @Override
    public boolean parse(BacktrackingIterator<Token> iterator, InterpreterBuilder builder) {
        if (decored.parse(iterator, builder)) {
            strategy.build(builder, decored);
            return true;
        }
        return false;
    }

    /** @inheritDoc */
    @Override
    public String toBNFString() {
        return decored.toBNFString();
    }

    /** @inheritDoc */
    @Override
    public void initTravel() {
        decored.initTravel();
    }

    /** @inheritDoc */
    @Override
    public void depthFirstTravelImpl(Method<Statement, Statement> method) {
        decored.depthFirstTravelImpl(method);
    }

    /** @inheritDoc */
    @Override
    public void breadthFirstTravelImpl(Method<Statement, Statement> method, Queue<Statement> queue) {
        decored.breadthFirstTravelImpl(method, queue);
    }

    /** @inheritDoc */
    @Override
    void clearMemorizerImpl() {
        this.decored.clearMemorizerImpl();
    }
}
