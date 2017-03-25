/*
 * Copyright (C) 2012 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.syntax.Syntax;
import fr.cyann.jasi.lexer.Lexer;
import fr.cyann.jasi.lexer.Token;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author CyaNn
 */
public class MultiLineTest {

	@Test
	public void multiLine1Test() {
		String source = ""
				+ "/*" + '\n'
				+ "my comment on several lines" + '\n'
				+ "another one" + '\n'
				+ "*/" + '\n'
				+ "set a = 7;"
				+ "";

		Syntax syntax = new Syntax();
		syntax.initalize();

		Lexer lexer = syntax.getLexer();
		lexer.match(source);
		lexer.next();
		lexer.next();

		Token token = lexer.next();

		System.out.println(token);
		assertEquals(4, token.getLine());

	}

	@Test
	public void multiLine2Test() {
		String source = ""
				+ "\"" + '\n'
				+ "my comment on several lines" + '\n'
				+ "another one" + '\n'
				+ "\"" + '\n'
				+ "set a = 7;"
				+ "";

		Syntax syntax = new Syntax();
		syntax.initalize();

		Lexer lexer = syntax.getLexer();
		lexer.match(source);
		lexer.next();
		lexer.next();

		Token token = lexer.next();

		System.out.println(token);
		assertEquals(4, token.getLine());

	}
}
