/*
 * Copyright (C) 2011 Yann Caron
 * License modality not yet defined.
 */
package fr.cyann.al.analysis;

import fr.cyann.al.autoCompletion.AutoCompletion;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.UnitTestRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The EvalTest class.<br>
 * creation date : 8 mai 2012.
 * <p>
 * @author Yann Caron
 * @version v0.1
 */
public class AutoCompletionTest {

    private static final Syntax syntax = new Syntax();
    private static final AbstractRuntime runtime = new UnitTestRuntime();
    private static HashMap<String, String> mapMockup;

    public AutoCompletionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
	AutoCompletion.getInstance().initialize(new AutoCompletion.AutoCompletionInitializer() {

	    @Override
	    public void initializePrimitives(HashMap<String, String> map) {
	    }

	    @Override
	    public RuntimeContext getBaseAPI() {
		//return createContext(syntax, runtime);
		return new RuntimeContext(syntax, runtime);
	    }

	    @Override
	    public void applyMap(HashMap<String, String> map) {
		mapMockup = map;
	    }
	});

    }

    @Test
    public void AutoCompletion1Test() {
	String source = ""
		+ "set a = 7;"
		+ "set b = 8;"
		+ "set g = function (a, b, c) {"
		+ "	set no;"
		+ "}"
		+ "set f = function () {"
		+ ""
		+ "";

	AutoCompletion.getInstance().refresh(syntax, source, 72);
	System.out.println(mapMockup);

	Assert.assertTrue(mapMockup.containsKey("a"));
	Assert.assertTrue(mapMockup.containsKey("b"));
	Assert.assertTrue(mapMockup.containsKey("g"));
	Assert.assertTrue(mapMockup.containsKey("f"));
	Assert.assertFalse(mapMockup.containsKey("no"));
	Assert.assertEquals("{f=null, g=function (a, b, c), b=null, a=null}", mapMockup.toString());
    }

    @Test
    public void AutoCompletion2Test() {
	String source = ""
		+ "set f = function () {"
		+ "}"
		+ ""
		+ "set x();"
		+ "";

	AutoCompletion.getInstance().refresh(syntax, source, 23);
	System.out.println(mapMockup);

	Assert.assertTrue(mapMockup.containsKey("f"));
	Assert.assertTrue(mapMockup.containsKey("x"));
	Assert.assertEquals("{f=function (), x=null}", mapMockup.toString());

    }

    @Test
    public void TO_BE_CONTINUED_AutoCompletion3Test() {
	String source = ""
		+ "set o = object() {"
		+ " set a = function () {};"
		+ " set b = 7;"
		+ "}"
		+ ""
		+ ""
		+ "";

	AutoCompletion.getInstance().refresh(syntax, source, 72);
	System.out.println(mapMockup);
	
	System.out.println(mapMockup.toString());
	Assert.assertTrue(mapMockup.containsKey("o.a"));

/*	Assert.assertEquals("{f=null, g=function (a, b, c), b=null, a=null}", mapMockup.toString());*/
    }

    
    @Test
    public void AutoCompletion4Test() {
	String source = ""
		+ "{"
		+ " set o = object() {"
		+ "	set a = function () {};"
		+ "	set b = 7;"
		+ " }"
		+ "}"
		+ ""
		+ "set f = function () {"
		+ "";

	AutoCompletion.getInstance().refresh(syntax, source, 72);
	System.out.println(mapMockup);
	
	System.out.println(mapMockup.toString());
	Assert.assertFalse(mapMockup.containsKey("o.a"));
	
	Assert.assertEquals("{f=null}", mapMockup.toString());
    }

}
