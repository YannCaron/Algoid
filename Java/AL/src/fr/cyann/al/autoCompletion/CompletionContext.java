/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.autoCompletion;

import fr.cyann.jasi.visitor.Context;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 <p>
 @author caronyn
 */
public class CompletionContext implements Context, Iterable<Definition> {

	private final int pos;
	private final Stack<Definition> keywords;
	public final StringBuilder currentKeyword;

	// constructor
	public CompletionContext(int pos) {
		this.pos = pos;
		this.keywords = new Stack<Definition>();
		currentKeyword = new StringBuilder();
	}

	// property
	public int getPos() {
		return pos;
	}

	// override
	@Override
	public Iterator<Definition> iterator() {
		return keywords.iterator();
	}

	// method
	public void clearKeywordPart() {
		currentKeyword.delete(0, currentKeyword.length());
	}

	public void addKeywordPart(String part) {
		if (currentKeyword.length() > 0) {
			currentKeyword.append(".");
		}
		currentKeyword.append(part);
		addKeyword(currentKeyword.toString());
	}

	public void addKeyword(String keyword) {
		keywords.push(new Definition(keyword));
	}

	public void addDescription(String description) {
		keywords.peek().setDescription(description);
	}

}
