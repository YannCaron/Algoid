/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.json;

import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.builder.InterpreterBuilder;
import fr.cyann.jasi.lexer.Alternation;
import fr.cyann.jasi.lexer.Char;
import fr.cyann.jasi.lexer.CharListAlternation;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.LexerBuilder;
import fr.cyann.jasi.lexer.RepeatTerm;
import fr.cyann.jasi.lexer.Sequence;
import fr.cyann.jasi.lexer.Term;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.lexer.UntilTerm;
import fr.cyann.jasi.lexer.Word;
import fr.cyann.jasi.parser.AssumptionStatement;
import fr.cyann.jasi.parser.CompoundStatement;
import fr.cyann.jasi.parser.KeyStatement;
import fr.cyann.jasi.parser.OptionalStatement;
import fr.cyann.jasi.parser.PEG;
import fr.cyann.jasi.parser.RepeatStatement;
import fr.cyann.jasi.parser.Statement;
import fr.cyann.jasi.parser.StatementNode;
import fr.cyann.jasi.parser.TypedStatement;

/**
 *
 * @author cyann
 */
public class JSonSyntax extends PEG {

    public static final TokenType NULL = TokenType.valueOf("NULL");
    public static final TokenType BOOLEAN = TokenType.valueOf("BOOLEAN");
    public static final TokenType NUMBER = TokenType.valueOf("NUMBER");
    public static final TokenType STRING = TokenType.valueOf("STRING");

    @Override
    protected Lexer constructLexer() {
        // initialize lexer with test syntax
        Term root = new Alternation();
        Lexer lexer = new Lexer(root);

        Term separator = new CharListAlternation(" \t\n\r\f");
        Term digit = new CharListAlternation("-0123456789");
        Term digitExt = new Alternation().add(digit).add(new Char('.'));
        Term symbol = new CharListAlternation("{}[]:,");

        // number = {digit} // regex: digit+
        Term number = new Sequence().add(digit).add(new RepeatTerm(digitExt));

        // string = '"' all '"'
        Term string = new Sequence().add(new Char('"')).add(new UntilTerm(new Char('"')));

        // null
        Term _null = new Word("null");

        // bool
        Term bool = new Alternation().add(new Word("true")).add(new Word("false"));

        // ident = all other words
        root.add(new LexerBuilder(string, STRING, lexer));
        root.add(separator);
        root.add(new LexerBuilder(symbol, TokenType.SYMBOL, lexer));
        root.add(new LexerBuilder(number, NUMBER, lexer));
        root.add(new LexerBuilder(_null, NULL, lexer));
        root.add(new LexerBuilder(bool, BOOLEAN, lexer));

        return lexer;
    }

    private static Statement listFactory(Statement expr, Statement separator, AgregatorStrategy agregator) {
        Statement list = new CompoundStatement("list");
        Statement list_sub = new CompoundStatement("_list");
        Statement element = new CompoundStatement("listElement", agregator).add(expr);

        list.add(element);
        list.add(new OptionalStatement(new RepeatStatement(list_sub)));
        list_sub.add(separator);
        list_sub.add(element);

        return list;
    }

    @Override
    protected Statement constructParser() {
        Statement _null = new CompoundStatement("null").add(new TypedStatement(NULL, new ClassFactory(Null.class)));
        Statement bool = new CompoundStatement("bool").add(new TypedStatement(BOOLEAN, new ClassFactory(Bool.class)));
        Statement number = new CompoundStatement("number").add(new TypedStatement(NUMBER, new ClassFactory(Number.class)));
        Statement string = new CompoundStatement("string").add(new TypedStatement(STRING, new ClassFactory(StringAST.class)));

        StatementNode value = new AssumptionStatement("value");

        // 
        Statement assoElement = new CompoundStatement("asso element", new AgregatorStrategy() {

            @Override
            public void build(InterpreterBuilder builder, StatementNode node) {
                Value value = (Value) builder.pop();
                StringAST string = (StringAST) builder.pop();
                builder.push(new Association(string.getToken(), string, value));
            }
        }).add(string).add(new KeyStatement(TokenType.SYMBOL, ":")).add(value);

        Statement simpleElement = new CompoundStatement("simple element", new AgregatorStrategy() {

            @Override
            public void build(InterpreterBuilder builder, StatementNode node) {
                Value value = (Value) builder.pop();
                builder.push(new Association(value.getToken(), null, value));
            }
        }).add(value);

        Statement element = new AssumptionStatement("element")
                .add(assoElement)
                .add(simpleElement);

        Statement arrayList = listFactory(element, new KeyStatement(TokenType.SYMBOL, ","), new AgregatorStrategy() {

            @Override
            public void build(InterpreterBuilder builder, StatementNode node) {
                Association element = (Association) builder.pop();
                Array array = (Array) builder.pop();

                array.addChild(element);

                builder.push(array);
            }
        });

        Statement attributList = listFactory(assoElement, new KeyStatement(TokenType.SYMBOL, ","), new AgregatorStrategy() {

            @Override
            public void build(InterpreterBuilder builder, StatementNode node) {
                Association element = (Association) builder.pop();
                ObjectAST object = (ObjectAST) builder.pop();

                object.addChild(element);

                builder.push(object);
            }
        });

        // array
        Statement array = new CompoundStatement("array")
                .add(new KeyStatement(TokenType.SYMBOL, "[", new ClassFactory(Array.class)))
                .add(arrayList)
                .add(new KeyStatement(TokenType.SYMBOL, "]"));

        Statement object = new CompoundStatement("object")
                .add(new KeyStatement(TokenType.SYMBOL, "{", new ClassFactory(ObjectAST.class)))
                .add(attributList)
                .add(new KeyStatement(TokenType.SYMBOL, "}"));

        value.add(object);
        value.add(array);

        value.add(_null);
        value.add(bool);
        value.add(number);
        value.add(string);

        return value;
    }

}
