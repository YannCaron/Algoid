/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.al.factory.FactoryUtils;
import fr.cyann.al.visitor.RuntimeContext;
import javax.script.ScriptException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ALScriptEngineTest {

	@Test
	public void testLiteral() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		engine.put("a", 7);
		engine.put("b", 8);

		int result = (Integer) engine.eval("return a + b");

		assertEquals(15, result);

	}

	@Test
	public void testLiteralReturn() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		engine.put("a", 7);
		engine.put("b", 8);

		int result = (Integer) engine.eval("a + b");

		assertEquals(15, result);

	}

	@Test
	public void testFunction() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		engine.put("a", 7);
		engine.put("b", 8);
		engine.put("f", ExpressionFactory.function("f", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				float a = FactoryUtils.getParam(ast, 0).getNumber();
				float b = FactoryUtils.getParam(ast, 1).getNumber();
				context.returnValue(a + b);
			}
		}, "a", "b"));

		int result = (Integer) engine.eval("return f (a, b)");

		assertEquals(15, result);

	}

	@Test
	public void testFunctionReturn() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		engine.put("a", 7);
		engine.put("b", 8);
		engine.put("f", ExpressionFactory.function("f", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				float a = FactoryUtils.getParam(ast, 0).getNumber();
				float b = FactoryUtils.getParam(ast, 1).getNumber();
				context.returnValue(a + b);
			}
		}, "a", "b"));

		int result = (Integer) engine.eval("f (a, b)");

		assertEquals(15, result);

	}

	@Test
	public void testObject() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		ObjectDeclaration o = ExpressionFactory.object("o");
		FactoryUtils.addAttribute(o, "a", 7);
		FactoryUtils.addAttribute(o, "b", 8);

		FactoryUtils.addMethod(o, "f", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				float a = FactoryUtils.getAttr(context, "a").getNumber();
				float b = FactoryUtils.getAttr(context, "b").getNumber();
				context.returnValue(a + b);
			}
		});

		engine.put("o", o);

		int result = (Integer) engine.eval("return o.f (a, b)");

		assertEquals(15, result);

	}

	@Test
	public void testObjectReturn() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		ObjectDeclaration o = ExpressionFactory.object("o");
		FactoryUtils.addAttribute(o, "a", 7);
		FactoryUtils.addAttribute(o, "b", 8);

		FactoryUtils.addMethod(o, "f", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				float a = FactoryUtils.getAttr(context, "a").getNumber();
				float b = FactoryUtils.getAttr(context, "b").getNumber();
				context.returnValue(a + b);
			}
		});

		engine.put("o", o);

		int result = (Integer) engine.eval("o.f (a, b)");

		assertEquals(15, result);

	}

	@Test
	public void testDynamicMethod() throws ScriptException {
		ALScriptEngine engine = new ALScriptEngine();

		engine.put("a", 7);
		engine.put("b", 8);
		engine.put("NUMBER", ExpressionFactory.function("addWith", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				float self = FactoryUtils.getSelf(ast).getNumber();
				float with = FactoryUtils.getParam(ast, 0).getNumber();

				context.returnValue(self + with);
			}
		}, "with"));

		int result = (Integer) engine.eval("return a.addWith(b)");

		assertEquals(15, result);

	}

	@Test
	public void testFactory() throws ScriptException {
	}


}
