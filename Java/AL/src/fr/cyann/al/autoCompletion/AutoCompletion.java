/*
 * Copyright (C) 2015 caronyn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.al.autoCompletion;

import fr.cyann.al.syntax.ALTools;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.exception.InvalidGrammarException;
import fr.cyann.jasi.exception.InvalidTokenException;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * <p>
 * @author caronyn
 */
public class AutoCompletion {

    // innerclass
    public interface AutoCompletionInitializer {

	void initializePrimitives(HashMap<String, String> map);

	RuntimeContext getBaseAPI();

	void applyMap(HashMap<String, String> map);
    }

    // attribute
    private static AutoCompletion singleton = null;
    private final HashMap<String, String> map;
    private final Stack<String> keys;
    private AutoCompletionInitializer initializer;
    private int staticLimit = 0;

    // constructor
    private AutoCompletion() {
	this.map = new HashMap<String, String>();
	this.keys = new Stack<String>();
    }

    public static AutoCompletion getInstance() {
	if (singleton == null) {
	    singleton = new AutoCompletion();
	}
	return singleton;
    }

    // function
    private void clearAfterLimit() {
	System.out.println("Clear " + staticLimit);
	while (keys.size() > staticLimit) {
	    String key = keys.pop();
	    map.remove(key);
	}
    }

    private void loadContext(RuntimeContext context) {
	Set<String> keywords = ALTools.getALObjects(context);

	for (String keyword : keywords) {
	    addKeyword(keyword, null);
	}
    }

    private void addKeyword(String keyword, String descrption) {
	map.put(keyword, descrption);
	keys.push(keyword);
    }

    // method
    public void initialize(AutoCompletionInitializer initializer) {
	this.initializer = initializer;

	// initialize base
	initializer.initializePrimitives(map);
	loadContext(initializer.getBaseAPI());

	staticLimit = keys.size();

	// set first time
	initializer.applyMap(map);
    }

    public void refresh(RuntimeContext context) {
	clearAfterLimit();
	loadContext(context);
    }

    public void refresh(Syntax syntax, String source, int pos) {
	CompletionContext context = new CompletionContext(pos);

	ASTBuilder builder = new ASTBuilder();
	CompletionVisitor completion = new CompletionVisitor();

	// parse
	try {
	    syntax.parse(source, builder);
	} catch (InvalidGrammarException ex) {
	    // do nothing
	} catch (InvalidTokenException ex) {
	    // do nothing
	}

	// execution phase
	try {
	    builder.injectVisitor(completion).visite(context);
	} catch (CompletionFinishedException ex) {
	    // do nothing
	}

	clearAfterLimit();

	// fill
	for (Definition keyword : context) {
	    addKeyword(keyword.getKeyword(), keyword.getDescription());
	}
    }

}
