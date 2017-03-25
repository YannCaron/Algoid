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
package fr.cyann.al.syntax;

import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.Assignation;
import fr.cyann.al.ast.BinaryOperator;
import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.BooleanExpression;
import fr.cyann.al.ast.Break;
import fr.cyann.al.ast.Comment;
import fr.cyann.al.ast.For;
import fr.cyann.al.ast.If;
import fr.cyann.al.ast.NumberExpression;
import fr.cyann.al.ast.Return;
import fr.cyann.al.ast.Loop;
import fr.cyann.al.ast.NullExpression;
import fr.cyann.al.ast.UnaryOperator;
import fr.cyann.al.ast.Until;
import fr.cyann.al.ast.While;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.ast.declaration.VariableDeclaration;
import fr.cyann.al.ast.reference.Call;
import fr.cyann.al.ast.reference.Variable;
import fr.cyann.jasi.builder.ClassFactory;
import fr.cyann.jasi.builder.AgregatorStrategy;
import fr.cyann.jasi.builder.EndOfGrammarFactory;
import fr.cyann.jasi.parser.StatementAgregator;
import fr.cyann.jasi.lexer.Alternation;
import fr.cyann.jasi.lexer.Char;
import fr.cyann.jasi.lexer.CharListAlternation;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.LexerBuilder;
import fr.cyann.jasi.lexer.LexerBuilderKey;
import fr.cyann.jasi.lexer.RepeatTerm;
import fr.cyann.jasi.lexer.Sequence;
import fr.cyann.jasi.lexer.Term;
import fr.cyann.jasi.lexer.TokenType;
import fr.cyann.jasi.lexer.UntilTerm;
import fr.cyann.jasi.lexer.Word;
import fr.cyann.jasi.parser.AssumptionStatement;
import fr.cyann.jasi.parser.CompoundStatement;
import fr.cyann.jasi.parser.KeyStatement;
import fr.cyann.jasi.parser.OnceCloseStatement;
import fr.cyann.jasi.parser.OnceOpenStatement;
import fr.cyann.jasi.parser.OnceStatement;
import fr.cyann.jasi.parser.OptionalStatement;
import fr.cyann.jasi.parser.PEG;
import fr.cyann.jasi.parser.RepeatStatement;
import fr.cyann.jasi.parser.Statement;
import fr.cyann.jasi.parser.StatementNode;
import fr.cyann.jasi.parser.TypedStatement;
import java.util.HashSet;
import java.util.Set;

/**
 * The Parsing Expression Grammar class. Use to define a new language
 * definition, syntax and grammar.
 * <p>
 * @author CyaNn
 */
public class Syntax extends PEG {

    /**
     * The keyword token type (function, object, for, if ect....)
     */
    public static final TokenType KEYWORD = TokenType.valueOf("KEYWORD");
    /**
     * The boolean keyword token type (true false)
     */
    public static final TokenType NULL = TokenType.valueOf("NULL");
    /**
     * The boolean keyword token type (true false)
     */
    public static final TokenType BOOLEAN = TokenType.valueOf("BOOLEAN");
    /**
     * The number token type (10.57)
     */
    public static final TokenType NUMBER = TokenType.valueOf("NUMBER");
    /**
     * The string token type ("string")
     */
    public static final TokenType STRING = TokenType.valueOf("STRING");
    /**
     * The comment token type ("comment")
     */
    public static final TokenType COMMENT = TokenType.valueOf("COMMENT", true);
    /**
     * The ident token type (a = 5)
     */
    public static final TokenType IDENT = TokenType.valueOf("IDENT");

    /**
     * @inheritDoc
     */
    @Override
    public Lexer constructLexer() {
        // initialize lexer with test syntax
        Term root = new Alternation();
        Lexer lexer = new Lexer(root);

        Term separator = new CharListAlternation(" \t\n\r\f");
        Term digit = new CharListAlternation("0123456789");
        Term digitExt = new Alternation().add(digit).add(new Char('.'));
        Term alpha = new CharListAlternation("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Term alphaNum = new Alternation().add(alpha).add(digit);
        Term symb = new CharListAlternation("!=%+*-/(){}[];<>&|,.:");

        // symbol = '++' | '--' | '+' | '-' ect...
        Term symbol = new Alternation();
        symbol.add(new Word("+="));
        symbol.add(new Word("-="));
        symbol.add(new Word("*="));
        symbol.add(new Word("/="));
        symbol.add(new Word("%="));
        symbol.add(new Word("..="));
        symbol.add(new Word(".."));
        symbol.add(new Word("->="));
        symbol.add(new Word("->"));
        symbol.add(new Word("=="));
        symbol.add(new Word("<="));
        symbol.add(new Word(">="));
        symbol.add(new Word("!="));
        symbol.add(new Word("++"));
        symbol.add(new Word("--"));
        symbol.add(new Word("&&"));
        symbol.add(new Word("||"));
        symbol.add(symb);

        // number = {digit} // regex: digit+
        Term number = new Sequence().add(digitExt).add(new RepeatTerm(digitExt));

        // string = '"' all '"'
        Term string = new Sequence().add(new Char('"')).add(new UntilTerm(new Char('"'), new Char('\\')));

        // comment1 = '//' all '\n'
        Term comment1 = new Sequence().add(new Word("//")).add(new UntilTerm(new Char('\n')));

        // comment2 = '/*' all '*/'
        Term comment2 = new Sequence().add(new Word("/*")).add(new UntilTerm(new Word("*/")));

        // word = alpha{[alphaNum]} // regex: alpha aplphaNum*
        Term word = new Sequence().add(alpha).add(new RepeatTerm(alphaNum));

        // null = 'nil'
        Set<String> nulls = new HashSet<String>();
        nulls.add("nil");
        Term nil = new LexerBuilderKey(word, nulls, NULL, lexer);

        // boolean = 'true' | 'false'
        Set<String> booleans = new HashSet<String>();
        booleans.add("true");
        booleans.add("false");
        Term bool = new LexerBuilderKey(nil, booleans, BOOLEAN, lexer);

        // nums = 'nan' | 'infinity'
        Set<String> nums = new HashSet<String>();
        nums.add("nan");
        nums.add("infinity");
        Term num = new LexerBuilderKey(bool, nums, NUMBER, lexer);

        // keyword = 'set' | 'function' | 'return' ....
        Set<String> keywords = new HashSet<String>();
        // structural keywords
        keywords.add("set");
        keywords.add("array");
        keywords.add("function");
        keywords.add("object");
        keywords.add("new");

        // reference keywords
        keywords.add("this");
        keywords.add("supers");

        // flow control keywords
        keywords.add("return");
        keywords.add("loop");
        keywords.add("for");
        keywords.add("break");
        keywords.add("if");
        keywords.add("elseif");
        keywords.add("else");
        keywords.add("while");
        keywords.add("do");
        keywords.add("until");
        Term keyword = new LexerBuilderKey(num, keywords, KEYWORD, lexer);

        // ident = all other words
        Term ident = new LexerBuilder(keyword, IDENT, lexer);

        root.add(new LexerBuilder(comment1, COMMENT, lexer));
        root.add(new LexerBuilder(comment2, COMMENT, lexer));
        root.add(new LexerBuilder(string, STRING, lexer));
        root.add(separator);
        root.add(new LexerBuilder(symbol, TokenType.SYMBOL, lexer));
        root.add(new LexerBuilder(number, NUMBER, lexer));
        root.add(ident);

        return lexer;
    }

    private static void binaryFactory(Statement stmt, Statement prev, Statement op) {
        Statement sub = new CompoundStatement("#" + stmt.getName(), new Builders.Expr.BinaryAgregator());

        stmt.add(prev);
        stmt.add(new OptionalStatement(new RepeatStatement(sub)));
        sub.add(op).add(prev);
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

    private static Statement getAssignOperator() {
        Statement assign = new AssumptionStatement("assign"); // level 14

        // assign = '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '..=' | ->=
        assign.add(new KeyStatement("=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("+=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("-=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("*=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("/=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("%=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("..=", new ClassFactory(Assignation.class)));
        assign.add(new KeyStatement("->=", new ClassFactory(Assignation.class)));

        return assign;
    }

    private static Statement getConcatOperator() {
        Statement oper = new AssumptionStatement("concat"); // level 13

        oper.add(new KeyStatement("..", new ClassFactory(BinaryOperator.class)));
        oper.add(new KeyStatement("->", new ClassFactory(BinaryOperator.class)));

        return oper;
    }

    private static Statement getOrOperator() {
        Statement or = new AssumptionStatement("or"); // level 12

        // or = '&&'
        or.add(new KeyStatement("||", new ClassFactory(BinaryOperator.class)));

        return or;
    }

    private static Statement getAndOperator() {
        Statement and = new AssumptionStatement("and"); // level 11

        // and = '&&'
        and.add(new KeyStatement("&&", new ClassFactory(BinaryOperator.class)));

        return and;
    }

    private static Statement getComparOperator() {
        Statement compar = new AssumptionStatement("compar"); // level 7 & 6

        // compar = '==' | '!=' | '<' | '<=' | '>' | '>='
        compar.add(new KeyStatement("==", new ClassFactory(BinaryOperator.class)));
        compar.add(new KeyStatement("!=", new ClassFactory(BinaryOperator.class)));
        compar.add(new KeyStatement("<", new ClassFactory(BinaryOperator.class)));
        compar.add(new KeyStatement("<=", new ClassFactory(BinaryOperator.class)));
        compar.add(new KeyStatement(">", new ClassFactory(BinaryOperator.class)));
        compar.add(new KeyStatement(">=", new ClassFactory(BinaryOperator.class)));

        return compar;
    }

    private static Statement getOperOperator() {
        Statement oper = new AssumptionStatement("oper"); // level 4

        // oper = '+' | '-'
        oper.add(new KeyStatement("+", new ClassFactory(BinaryOperator.class)));
        oper.add(new KeyStatement("-", new ClassFactory(BinaryOperator.class)));

        return oper;
    }

    private static Statement getFactOperator() {
        Statement fact = new AssumptionStatement("mult"); // level 3

        // fact = '*' | '/' | '%'
        fact.add(new KeyStatement("*", new ClassFactory(BinaryOperator.class)));
        fact.add(new KeyStatement("/", new ClassFactory(BinaryOperator.class)));
        fact.add(new KeyStatement("%", new ClassFactory(BinaryOperator.class)));

        return fact;
    }

    private static Statement getLeftUnaryOperator() {
        Statement unary = new AssumptionStatement("unaryLeft"); // level 2

        // unaryLeft = '-' | '!'
        unary.add(new KeyStatement("-", new ClassFactory(UnaryOperator.class)));
        unary.add(new KeyStatement("!", new ClassFactory(UnaryOperator.class)));
        unary.add(new KeyStatement(KEYWORD, "new", false, new ClassFactory(UnaryOperator.class)));

        return unary;
    }

    private static Statement getRightUnaryOperator() {
        Statement unary = new AssumptionStatement("unaryRight"); // level 2Bis

        // unaryRight = '++' | '--'
        unary.add(new KeyStatement("++", new ClassFactory(UnaryOperator.class)));
        unary.add(new KeyStatement("--", new ClassFactory(UnaryOperator.class)));

        return unary;
    }

    /**
     * Build the expression statement with cascading composite.<br> Respect the
     * Descartes level of operators.
     * <p>
     * @param outExpr the expression to build
     * @param ident the ident statement
     */
    public static void buildExpression(Statement outExpr, Statement ident) {

        Statement level14_sub = new CompoundStatement("_assignExpr", new Builders.Expr.AssignmentAgregator());
        Statement level13 = new CompoundStatement("concatExpr");
        Statement level12 = new CompoundStatement("orExpr");
        Statement level11 = new CompoundStatement("andExpr");
        Statement level7 = new CompoundStatement("comparExpr");
        Statement level4 = new CompoundStatement("operExpr");
        Statement level3 = new CompoundStatement("multExpr");
        Statement level2 = new AssumptionStatement("unary");
        Statement level2Unary = new CompoundStatement("unaryLeftExpr", new Builders.Expr.UnaryLeftAgregator());
        Statement level2Bis = new CompoundStatement("unaryRightExpr", new Builders.Expr.UnaryRightAgregator());
        Statement level1 = new AssumptionStatement("level1");

		// element = expr
        // manage operator level
        // level14  = level13 [assign level13]
        outExpr.add(level13);
        outExpr.add(new OptionalStatement(level14_sub));
        level14_sub.add(getAssignOperator());
        level14_sub.add(level13);

        binaryFactory(level13, level12, getConcatOperator());

        // level12 = level11 [or level11]
        binaryFactory(level12, level11, getOrOperator());

        // level11 = level7 [and level7]
        binaryFactory(level11, level7, getAndOperator());

        // level7 = level4 [compar level4]
        binaryFactory(level7, level4, getComparOperator());

        // level4 = level3 [(oper level3)]
        binaryFactory(level4, level3, getOperOperator());

        // level3 = level2 [(mult level2)]
        binaryFactory(level3, level2, getFactOperator());

        // level2 = unary level2Bis | level2Bis
        level2Unary.add(getLeftUnaryOperator());
        level2Unary.add(level2Bis);

        // level2Bis = level1 [unary]
        level2Bis.add(level1);
        level2Bis.add(new OptionalStatement(getRightUnaryOperator()));

        level2.add(level2Unary);
        level2.add(level2Bis);

        // level1 = boolean | string | number | functionCall | ident | (expr)
        level1.add(ident);
    }

    /**
     * Build the ident statement.
     * <p>
     * @param outIdent the ident to build.
     * @param expr the expression to use
     * @param function the function definition bnf
     * @param object the object definition bnf
     * @param if_ the if definition bnf
     */
    public static void buildIdent(Statement outIdent, Statement expr, Statement function, Statement object, Statement lambda, Statement if_) {
        Statement identChain = new CompoundStatement("ident_chain", new Builders.Expr.VariableNextAgregator());
        Statement call = new CompoundStatement("call");
        Statement ref = new AssumptionStatement("ref");

        Statement callSub = new CompoundStatement("call_sub", new Builders.Function.CallAgregator());
        Statement indexSub = new CompoundStatement("index_sub", new Builders.Expr.VariableIndexAgregator());

        Statement identifier = new AssumptionStatement("ident");

        // callSub = '(' expr ')'
        callSub.add(new KeyStatement("(", new ClassFactory(Call.class)));
        callSub.add(new OptionalStatement(listFactory(new AssumptionStatement("xp").add(lambda).add(expr), new KeyStatement(","), new Builders.Expr.ArgumentAgregator())));
        callSub.add(new KeyStatement(")"));

        // indexSub = '[' expr ']'
        indexSub.add(new KeyStatement("["));
        indexSub.add(expr);
        indexSub.add(new KeyStatement("]"));

        // ref = callSub | indexSub
        ref.add(callSub).add(indexSub);

        // ident = call [ '.' call ]
        outIdent.add(call);
        outIdent.add(new OptionalStatement(identChain.add(new KeyStatement(".")).add(outIdent)));

        // call = index [( '(' [ expr [ ',' expr ] ] ')' )]
        call.add(identifier);
        call.add(new OptionalStatement(new RepeatStatement(ref)));

        // identifier = object | function | ident
        identifier.add(new CompoundStatement("parenthesis").add(new KeyStatement("(")).add(expr).add(new KeyStatement(")")));
        identifier.add(object);
        identifier.add(function);
        identifier.add(getArray(expr));
        identifier.add(if_);
        identifier.add(lambda);
        identifier.add(new KeyStatement(Syntax.KEYWORD, "this", false, new ClassFactory(Variable.class)));
        identifier.add(new KeyStatement(Syntax.KEYWORD, "supers", false, new ClassFactory(Variable.class)));
        identifier.add(new TypedStatement(Syntax.IDENT, new ClassFactory(Variable.class)));
        identifier.add(new TypedStatement(NULL, (new ClassFactory(NullExpression.class))));
        identifier.add(new TypedStatement(BOOLEAN, (new ClassFactory(BooleanExpression.class))));
        identifier.add(new TypedStatement(NUMBER, new ClassFactory(NumberExpression.class)));
        identifier.add(new TypedStatement(STRING, (new Builders.StringFactory())));

    }

    /**
     * Get the Comment BNF definition<br> comment = comment
     * <p>
     * @return the comment statement
     */
    public static StatementNode getComment() {
        return new CompoundStatement("comment").add(new TypedStatement(Syntax.COMMENT, new ClassFactory(Comment.class)));
    }

    /**
     * Get the variable BNF definition<br> variable = 'var' ident { '=' expr }
     * ';'
     * <p>
     * @param ident the ident statement
     * @param expr the expression statement
     * <p>
     * @return the variable statement
     */
    public static StatementNode getVariable(Statement ident, Statement expr) {
        StatementNode variable = new CompoundStatement("var", new Builders.Expr.VariableAgregator());
        StatementNode assign = new CompoundStatement("var_assign", new Builders.Expr.VariableAssignAgregator());

        variable.add(new KeyStatement(Syntax.KEYWORD, "set", false, new ClassFactory(VariableDeclaration.class)));
        variable.add(ident);
        variable.add(new OptionalStatement(assign));

        assign.add(new KeyStatement("="));
        assign.add(expr);

        return variable;
    }

    /**
     * Get the function BNF definition<br> function = 'function' '(' [arguments]
     * ')' block
     * <p>
     * @param body the body statement
     * @param expr the expression statement
     * <p>
     * @return the variable statement
     */
    public static StatementNode getFunction(Statement expr, Statement body) {
        StatementNode function = new CompoundStatement("function", new Builders.Function.FunctionAgregator());
        Statement params = new CompoundStatement("params");
        Statement paramList = new CompoundStatement("parameters");
        Statement paramList_sub = new CompoundStatement("_parameters");

        StatementNode declaration = new CompoundStatement("decl", new Builders.Obj.AttributeDeclarationAgregator());
        Statement declaration_sub = new CompoundStatement("_decl");

        // parameter = ident '=' expr;
        declaration.add(new TypedStatement(Syntax.IDENT, new ClassFactory(Variable.class)));
        declaration.add(new OptionalStatement(declaration_sub));
        declaration_sub.add(new KeyStatement("=", new ClassFactory(VariableDeclaration.class)));
        declaration_sub.add(expr);

        // arguments = ident [(',' ident)]
        paramList.add(new StatementAgregator(declaration, new Builders.Function.ParameterAgregator()));
        paramList.add(new OptionalStatement(new RepeatStatement(paramList_sub)));
        paramList_sub.add(new KeyStatement(","));
        paramList_sub.add(new StatementAgregator(declaration, new Builders.Function.ParameterAgregator()));

        params.add(new KeyStatement("("));
        params.add(new OptionalStatement(paramList));
        params.add(new KeyStatement(")"));

        function.add(new KeyStatement(Syntax.KEYWORD, "function", false, new ClassFactory(FunctionDeclaration.class)));
        function.add(new OptionalStatement(params));
        function.add(body);

        return function;
    }

    /**
     * Get the lambda (simplified function) BNF definition<br> lambda = '('
     * [arguments] ')' block
     * <p>
     * @param block the block statement
     * @param expr the expression statement
     * <p>
     * @return the variable statement
     */
    public static StatementNode getLambda(Statement expr, Statement block) {
        StatementNode lambda = new CompoundStatement("lambda", new Builders.Function.LambdaAgregator());
        Statement params = new CompoundStatement("params");
        Statement paramList = new CompoundStatement("parameters");
        Statement paramList_sub = new CompoundStatement("_parameters");

        StatementNode declaration = new CompoundStatement("decl", new Builders.Obj.AttributeDeclarationAgregator());
        Statement declaration_sub = new CompoundStatement("_decl");

        // parameter = ident '=' expr;
        declaration.add(new TypedStatement(Syntax.IDENT, new ClassFactory(Variable.class)));
        declaration.add(new OptionalStatement(declaration_sub));
        declaration_sub.add(new KeyStatement("=", new ClassFactory(VariableDeclaration.class)));
        declaration_sub.add(expr);

        // arguments = ident [(',' ident)]
        paramList.add(new StatementAgregator(declaration, new Builders.Function.LambdaParameterAgregator()));
        paramList.add(new OptionalStatement(new RepeatStatement(paramList_sub)));
        paramList_sub.add(new KeyStatement(","));
        paramList_sub.add(new StatementAgregator(declaration, new Builders.Function.LambdaParameterAgregator()));

        params.add(new KeyStatement("("));
        params.add(new OptionalStatement(paramList));
        params.add(new KeyStatement(")"));

        lambda.add(new OptionalStatement(params));
        lambda.add(block);

        return lambda;
    }

    /**
     * Get the Return BNF definition<br> return = return = 'return' [expr];
     * <p>
     * @param expr the expression statement
     * <p>
     * @return the Return statement
     */
    public static StatementNode getReturn(Statement expr) {
        StatementNode return_ = new CompoundStatement("return");
        Statement retExpr = new CompoundStatement("returnExpr", new Builders.Function.ReturnAgregator());

        retExpr.add(expr);
        return_.add(new KeyStatement(Syntax.KEYWORD, "return", false, new ClassFactory(Return.class)));
        return_.add(new OptionalStatement(retExpr));
        return_.add(new OptionalStatement(new KeyStatement(";")));

        return return_;
    }

    public static StatementNode getArray(Statement expr) {

        // array = [ 'array' ]1 ['(' ')'] {' itemList '}'
        StatementNode array = new CompoundStatement("array");

        // dict = expr ':' expr
        Statement dict = new CompoundStatement("dict", new Builders.Expr.VariableDictAgregator());
        dict.add(expr);
        dict.add(new KeyStatement(TokenType.SYMBOL, ":"));
        dict.add(expr);

        // item = dist | expr
        Statement item = new AssumptionStatement("item");
        item.add(dict);
        item.add(expr);

        // itemList = item [ ',' item ]
        Statement itemList = listFactory(item, new KeyStatement(","), new Builders.Expr.VariableArrayAgregator());

        OnceStatement onceKey = new OnceStatement(new KeyStatement(Syntax.KEYWORD, "array"));

        // parenthesis
        Statement parenth = new CompoundStatement("parenthesis");
        parenth.add(new KeyStatement("("));
        parenth.add(new KeyStatement(")"));

        array.add(onceKey);
        array.add(new OptionalStatement(parenth));
        array.add(new OnceOpenStatement(new KeyStatement("{", new ClassFactory(ArrayDeclaration.class)), onceKey));
        array.add(new OptionalStatement(itemList));
        array.add(new OnceCloseStatement(new KeyStatement("}"), onceKey));

        return array;
    }

    /**
     * Get the variable BNF definition<br> object = 'object' ident '('
     * [inherits] ')' [attributes]
     * <p>
     * @param ident the ident statement
     * @param varStmt the variableStatement statement
     * <p>
     * @return the variable statement
     */
    public static StatementNode getObject(StatementNode ident, StatementNode varStmt, Statement comment) {
        StatementNode object = new CompoundStatement("object");

        // inherits = '(' [ident [(',' ident)]]
        Statement inherits = new CompoundStatement("inherit");
        Statement inList = new CompoundStatement("inList");
        Statement inList_sub = new CompoundStatement("_inList");
        Statement declaration = new AssumptionStatement("obj_decl");

        inherits.add(new KeyStatement("("));
        inherits.add(new OptionalStatement(inList));
        inherits.add(new KeyStatement(")"));
        inList.add(new StatementAgregator(ident, new Builders.Obj.InheritanceAgregator()));
        inList.add(new OptionalStatement(new RepeatStatement(inList_sub)));
        inList_sub.add(new KeyStatement(","));
        inList_sub.add(new StatementAgregator(ident, new Builders.Obj.InheritanceAgregator()));

        declaration.add(comment);
        declaration.add(new StatementAgregator(varStmt, new Builders.Obj.AttributeListAgregator()));

        object.add(new KeyStatement(Syntax.KEYWORD, "object", false, new ClassFactory(ObjectDeclaration.class)));
        object.add(new OptionalStatement(inherits));
        object.add(new KeyStatement("{"));
        object.add(new OptionalStatement(new RepeatStatement(declaration)));
        object.add(new KeyStatement("}"));

        return object;
    }

    /**
     * Get the Block BNF definition<br> block = '{' [ ( statement ) ] '}'
     * <p>
     * @param body the statement element statement
     * <p>
     * @return the clock statement
     */
    public static StatementNode getBlock(StatementNode body) {
        StatementNode block = new CompoundStatement("block", new Builders.BlockTerminator());
        block.add(new KeyStatement("{", new ClassFactory(Block.class)));
        block.add(new OptionalStatement(new RepeatStatement(new StatementAgregator(body, new Builders.BlockAgregator()))));
        block.add(new KeyStatement("}", new EndOfGrammarFactory()));
        return block;
    }

    /**
     * Get the Condition BNF definition<br> condition = '(' expr ')'
     * <p>
     * @param expr the expression statement
     * <p>
     * @return the condition statement
     */
    public static StatementNode getCondition(Statement expr) {
        StatementNode condition = new CompoundStatement("condition", new Builders.Condition.ConditionableAgregator());

        condition.add(new KeyStatement("("));
        condition.add(expr);
        condition.add(new KeyStatement(")"));

        return condition;
    }

    /**
     * Get the If BNF definition<br> if = 'if' condition body [('elseif'
     * condition statement )] ['else' statement]
     * <p>
     * @param condition the condition statement
     * @param body the body statement
     * <p>
     * @return the if statement
     */
    public static StatementNode getIf(Statement condition, Statement body) {
        StatementNode if_ = new CompoundStatement("if");
        Statement truebody = new CompoundStatement("truebody", new Builders.Condition.IfTrueAgregator());
        Statement elseif_ = new CompoundStatement("elseif", new Builders.Condition.ElseIfAgregator());
        Statement elseifbody = new CompoundStatement("elseifbody", new Builders.Condition.IfTrueAgregator());
        Statement else_ = new CompoundStatement("else");
        Statement falsebody = new CompoundStatement("falsebody", new Builders.Condition.IfFalseAgregator());

        if_.add(new KeyStatement(Syntax.KEYWORD, "if", false, new ClassFactory(If.class)));
        if_.add(condition);
        if_.add(truebody.add(body));
        if_.add(new OptionalStatement(new RepeatStatement(elseif_)));
        if_.add(new OptionalStatement(else_));
        elseif_.add(new KeyStatement(Syntax.KEYWORD, "elseif", false, new ClassFactory(If.class)));
        elseif_.add(condition);
        elseif_.add(elseifbody.add(body));
        else_.add(new KeyStatement(Syntax.KEYWORD, "else"));
        else_.add(falsebody.add(body));

        return if_;
    }

    /**
     * Get the for BNF definition<br> for = 'for' '(' expr | var ; [ expr ]; [
     * expr ] ')' body
     * <p>
     * @param variable the variable definition statement
     * @param body the body definition statement
     * @param expr the expression statement
     * <p>
     * @return the variable statement
     */
    public static StatementNode getFor(StatementNode variable, StatementNode expr, StatementNode body) {
        StatementNode for_ = new CompoundStatement("for", new Builders.Loop.ForAgregator());
        StatementNode assign = new AssumptionStatement("for_assign");

        // assign = var | expr
        assign.add(variable).add(expr);

        for_.add(new KeyStatement(Syntax.KEYWORD, "for", false, new ClassFactory(For.class)));
        for_.add(new KeyStatement("("));
        for_.add(new OptionalStatement(new StatementAgregator(assign, new Builders.Loop.ForAssignmentAgregator())));
        for_.add(new KeyStatement(";"));
        for_.add(new OptionalStatement(new StatementAgregator(expr, new Builders.Condition.ConditionableAgregator())));
        for_.add(new KeyStatement(";"));
        for_.add(new OptionalStatement(new StatementAgregator(expr, new Builders.Loop.ForIncrementAgregator())));
        for_.add(new KeyStatement(")"));
        for_.add(body);

        return for_;
    }

    /**
     * Get the for BNF definition<br> loop = 'loop' '(' expr ')' body
     * <p>
     * @param variable the variable definition statement
     * @param body the body definition statement
     * @param expr the expression statement
     * <p>
     * @return the loop statement
     */
    public static StatementNode getLoop(StatementNode expr, StatementNode body) {
        StatementNode loop = new CompoundStatement("loop", new Builders.Loop.LoopAgregator());

        loop.add(new KeyStatement(Syntax.KEYWORD, "loop", false, new ClassFactory(Loop.class)));
        loop.add(new KeyStatement("("));
        loop.add(new OptionalStatement(new StatementAgregator(expr, new Builders.Loop.LoopLimitAgregator())));
        loop.add(new KeyStatement(")"));
        loop.add(body);

        return loop;
    }

    /**
     * Get the While BNF definition<br> while = 'while' '(' expr ')' body
     * <p>
     * @param condition
     * @param body
     * <p>
     * @return the while statement
     */
    public static StatementNode getWhile(Statement condition, Statement body) {
        StatementNode while_ = new CompoundStatement("while", new Builders.Loop.WhileAgregator());

        while_.add(new KeyStatement(Syntax.KEYWORD, "while", false, new ClassFactory(While.class)));
        while_.add(condition);
        while_.add(body);

        return while_;
    }

    /**
     * Get the Until BNF definition<br> while = 'while' '(' expr ')' body
     * <p>
     * @param condition
     * @param body
     * <p>
     * @return the until statement
     */
    public static StatementNode getUntil(Statement condition, Statement body) {
        StatementNode until_ = new CompoundStatement("until", new Builders.Loop.UntilAgregator());

        until_.add(new KeyStatement(Syntax.KEYWORD, "do"));
        until_.add(body);
        until_.add(new KeyStatement(Syntax.KEYWORD, "until", false, new ClassFactory(Until.class)));
        until_.add(condition);

        return until_;
    }

    /**
     * Get the Break BNF definition<br> break = 'break' ';'
     * <p>
     * @return the break statement
     */
    public static StatementNode getbreak() {
        StatementNode break_ = new CompoundStatement("break");

        break_.add(new KeyStatement(Syntax.KEYWORD, "break", false, new ClassFactory(Break.class)));
        break_.add(new OptionalStatement(new KeyStatement(";")));

        return break_;
    }

    /**
     * Create new statement by adding semicolon at the following of existing
     * one<br> "name" = statement ';'
     * <p>
     * @param name the new statement name
     * @param statement the statement to decorate
     * <p>
     * @return the created statement
     */
    public static StatementNode semiColonDeco(String name, StatementNode statement) {
        return new CompoundStatement(name).add(statement).add(new OptionalStatement(new KeyStatement(";")));
    }

    /**
     * @inheritDoc
     */
    @Override
    public Statement constructParser() {

        // statement
        StatementNode body = new AssumptionStatement("body");
        Statement block = getBlock(body);

        // initialize
        StatementNode ident = new CompoundStatement("ident");
        StatementNode expr = new CompoundStatement("expr");

        Statement comment = getComment();
        StatementNode varDecl = getVariable(ident, expr);
        StatementNode varStmt = semiColonDeco("varStmt", varDecl);
        Statement condition = getCondition(expr);
        Statement function = getFunction(expr, body);
        Statement lambda = getLambda(expr, block);
        Statement object = getObject(ident, varStmt, comment);

        buildExpression(expr, ident);
        buildIdent(ident, expr, function, object, lambda, getIf(condition, body));

        // body = ....
        body.add(comment); // comment
        body.add(varStmt); // || variable
        body.add(getReturn(expr)); // || return
        body.add(getLoop(expr, body)); // || for
        body.add(getFor(varDecl, expr, body)); // || for
        body.add(getWhile(condition, body)); // || while
        body.add(getUntil(condition, body)); // || until
        body.add(getbreak()); // || break
        body.add(block); // || block
        body.add(semiColonDeco("statement", expr)); // || statement
        body.add(new KeyStatement(";")); // || ';'

        return body;
    }
}
