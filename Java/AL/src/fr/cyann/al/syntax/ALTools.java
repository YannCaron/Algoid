
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

import fr.cyann.jasi.ast.AST;
import fr.cyann.al.ast.Block;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.data.Types;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.lexer.LexerBuilderKey;
import fr.cyann.jasi.lexer.Term;
import fr.cyann.jasi.parser.PEG;
import fr.cyann.utils.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The builder Tools class.<br>
 *
 * @author Yann Caron
 * @version v0.1
 */
public final class ALTools {

    // hide constructor
    private ALTools() {
    }

    /**
     * Add a default body if statement is alone.<br>
     * Used for optional body statement (eg. for, if while ect....)
     *
     * @param statement the statement en ecapsulae into block.
     * @return the encapsuled statement.
     */
    public static Block lazyBlock(AST statement) {
	if (statement instanceof Block) {
	    return ((Block) statement);
	} else {
	    Block block = new Block(statement.getToken());
	    block.addStatement(statement);
	    return block;
	}
    }

    public static Set<String> getALMagicMethods(AbstractRuntime runtime, String format) {
	final Set<String> set = new TreeSet<String>();

	Map<Types, Map<Integer, FunctionInstance>> map = runtime.getDynamicMethods().getAll();
	for (Types type : map.keySet()) {
	    Map<Integer, FunctionInstance> methods = map.get(type);
	    for (Integer ident : methods.keySet()) {
		String value = String.format(format, type.toString(), Identifiers.valueOf(ident));
		set.add(value);
	    }
	}
	return set;
    }

    public static Set<String> getALKeywords(PEG peg) {
	final Set<String> set = new TreeSet<String>();

	// dependency injection. Visit the lexer depth first to retreive defined keywords of lexer
	peg.getLexer().getRoot().breadthFirstTravel(new Method<Term, Term>() {
	    @Override
	    public Term invoke(Term... args) {
		if (args[0] instanceof LexerBuilderKey) {
		    LexerBuilderKey bk = (LexerBuilderKey) args[0];
		    set.addAll(bk.getKeys());
		}

		return null;
	    }
	});

	return set;
    }

    public interface Filter {

	public boolean predicate(MutableVariant value);
    }

    public static Filter FILTER_ALL = new Filter() {

	@Override
	public boolean predicate(MutableVariant value) {
	    return true;
	}
    };

    public static void accALObjects(Scope scope, Set<String> accSet, String accName, boolean recurs, Filter filter) {

	// loop on all keys
	for (Integer key : scope.getVariables().keySet()) {

	    // get value
	    MutableVariant value = scope.getVariables().get(key);
	    String name = Identifiers.valueOf(key);
	    String fullName = accName + name;

	    // test to avoir infinit recursions
	    if (!"this".equals(name) && filter.predicate(value)) {

		// accumulator
		accSet.add(fullName);

		// for object, do recursion (but not for Java object because of infinit recursion)
		if (recurs && value.isObject()) {
		    ObjectInstance o = value.getObject();
		    accALObjects(o.scope, accSet, fullName + ".", value.getObject().nativeObject == null, filter);
		}
	    }
	}
    }

    public static Set<String> getALObjects(RuntimeContext context) {

	// entry point for recursion
	final Set<String> set = new TreeSet<String>();

	Scope scope = context.getRoot();

	accALObjects(scope, set, "", true, FILTER_ALL);

	return set;
    }

}
