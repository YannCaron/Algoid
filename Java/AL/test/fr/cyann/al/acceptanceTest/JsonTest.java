/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.acceptanceTest;

import fr.cyann.al.AL;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.scope.Scope;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.al.visitor.UnitTestRuntime;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author cyann
 */
public class JsonTest {

    @Test
    public void testVoidToJson() throws Exception {

        String source = "set json = nil.toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("null", mv.toString());

    }

    @Test
    public void testBooleanToJson() throws Exception {

        String source = "set json = true.toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("true", mv.toString());

    }

    @Test
    public void testNumberToJson() throws Exception {

        String source = "set json = (7).toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("7", mv.toString());

    }

    @Test
    public void testStringToJson() throws Exception {

        String source = "set json = \"test\".toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("\"test\"", mv.toString());

    }

    @Test
    public void testFunctionToJson() throws Exception {

        String source = "set json = function(){}.toJSon();";

        try {
            AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
            fail("Function cannot be serialized;");
        } catch (Exception ex) {
        }

    }

    @Test
    public void testArrayToJson() throws Exception {

        String source = "set json = array{1, 2, 3, 4}.toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("[ 1, 2, 3, 4 ]", mv.toString());

    }

    @Test
    public void testAssociativeArrayToJson() throws Exception {

        String source = "set json = array{\"one\" : 1, \"two\" : 2, \"three\" : 3, \"four\" : 4}.toJSon();";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals("[ \"four\" : 4, \"one\" : 1, \"two\" : 2, \"three\" : 3 ]", mv.toString());

    }

    @Test
    public void testObjectToJson() throws Exception {

        String source = ""
          + "set m = object { set a = 1; };"
          + "set n = object { set b = 2; set f = function () {}; };"
          + "set o = object (n) { set c = 3; set obj = m; set arr = array () {1, 2, 3, 4}};"
          + "set json = o.toJSon();";

        String expectedJSon = ""
          + "{\n"
          + "	\"b\" : 2, \n"
          + "	\"c\" : 3, \n"
          + "	\"obj\" : {\n"
          + "		\"a\" : 1\n"
          + "	}, \n"
          + "	\"arr\" : [ 1, 2, 3, 4 ]\n"
          + "}";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals(expectedJSon, mv.toString());

    }

    @Test
    public void testNullFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "null";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(nil, v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testBooleanFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "true";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(true, v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testNumberFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "-0.7";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(-0.7, v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testStringFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "\"My text\"";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(\"My text\", v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testArrayFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "[1, 2, 3, 4]";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(array {1, 2, 3, 4}, v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testAssociativeArrayFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = "[\"one\" : 1, \"two\" : 2, \"three\" : 3, \"four\" : 4]";
        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set v = fromJSon(json);"
          + "print(v);"
          + "unit.assertEquals(array {\"one\" : 1, \"two\" : 2, \"three\" : 3, \"four\" : 4}, v);";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testObjectFromJson() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = ""
          + "{\n"
          + "	\"a\" : 2, \n"
          + "	\"b\" : 2, \n"
          + "	\"c\" : 3, \n"
          + "	\"obj\" : {\n"
          + "		\"a\" : 1\n"
          + "	}, \n"
          + "	\"arr\" : [ 1, 2, 3, 4 ]\n"
          + "}";

        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set obj = fromJSon(json);"
          + "print(obj);"
          + ""
          + "unit.assertEquals(2, obj.a);"
          + "unit.assertEquals(2, obj.b);"
          + "unit.assertEquals(3, obj.c);"
          + "unit.assertEquals(1, obj.obj.a);"
          + "unit.assertEquals(array {1, 2, 3, 4}, obj.arr);"
          + "";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testObjectFromJsonDynamic() throws Exception {

        Syntax syntax = new Syntax();
        RuntimeVisitor runtime = new UnitTestRuntime();
        RuntimeContext context = AL.createContext(syntax, runtime);

        String json = ""
          + "{\n"
          + "	\"a\" : 2 \n"
          + "}";

        context.getRoot().getVariables().put(Identifiers.getID("json"), new MutableVariant(json));

        String source = ""
          + "set obj = object () {"
          + " set getA = function () {"
          + "     return this.a;"
          + " };"
          + "}.fromJSon(json);"
          + "print(obj.toString());"
          + ""
          + "unit.assertEquals(2, obj.a);"
          + "unit.assertEquals(2, obj.getA());"
          + "";

        AL.execute(syntax, runtime, context, source).getRoot();

    }

    @Test
    public void testKeywordsToJson() throws Exception {

        String source = ""
          + "set o = object { "
          + " set FrOm = 7;"
          + " set FrOm2 = 8;"
          + "};"
          + "set json = o.toJson();";

        String expectedJSon = ""
          + "{\n"
          + "	\"FrOm\" : 7, \n"
          + "	\"FrOm2\" : 8\n"
          + "}";

        Scope root = AL.execute(new Syntax(), new UnitTestRuntime(), source).getRoot();
        MutableVariant mv = root.getVariables().get(Identifiers.getID("json"));
        System.out.println(mv);
        Assert.assertEquals(expectedJSon, mv.toString());

    }

    @Test
    public void testBuggy() throws Exception {

        String source = ""
          + "set o = util.fromJSon(json);"
          + "print (o.toJSon());"
          + "print (o.MsgData.MsgValue[0].cm);"
          + "set cm = o.MsgData.MsgValue[0].cm;";

        String json = ""
          + "{\n"
          + "	\"MsgID\" : 258632, \n"
          + "	\"MsgTo\" : \"algo\", \n"
          + "	\"MsgFrom\" : \"buggy_542aa276d5ec\", \n"
          + "	\"MsgData\" : {\n"
          + "		\"MsgType\" : \"response\", \n"
          + "		\"MsgParam\" : \"distance\",\n"
          + "	        \"MsgValue\": [ \n"
          + "			{\n"
          + "			\"sonar\": \"0\",\n"
          + "			\"cm\" : 52,\n"
          + "			\"angle\" : 23,\n"
          + "			\"event\": \"on\",\n"
          + "			\"event_lower\": 20,\n"
          + "			\"event_higher\": 180,\n"
          + "			\"safety_stop\": \"off\",\n"
          + "			\"safety_value\": 0\n"
          + "			}\n"
          + "        	]\n"
          + "	}\n"
          + "} ";

        Syntax syntax = new Syntax();
        AbstractRuntime runtime = new UnitTestRuntime();
        
        RuntimeContext context = AL.createContext(syntax, runtime);
        context.getRoot().define(Identifiers.getID("json"), new MutableVariant(json));
        Scope root = AL.execute(syntax, runtime, context, source).getRoot();
        
        
        MutableVariant mv = root.getVariables().get(Identifiers.getID("cm"));
        Assert.assertEquals(52, mv.getNumber(), 0);

    }

}
