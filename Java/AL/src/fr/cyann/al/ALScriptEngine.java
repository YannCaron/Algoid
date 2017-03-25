package fr.cyann.al;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.declaration.ArrayDeclaration;
import fr.cyann.al.ast.declaration.FunctionDeclaration;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.data.Types;
import fr.cyann.al.factory.DeclarationFactory;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.libs.Reflexion;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.jasi.builder.ASTBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

/**
 <p>
 @author CARONYN
 */
public class ALScriptEngine implements ScriptEngine, Invocable {

	private static class SeparatedContext extends SimpleScriptContext {

		public SeparatedContext() {
			super();
			globalScope = new SimpleBindings();
		}

	}

	public static abstract class APITemplate extends RuntimeVisitor {

		@Override
		public void addDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods) {
			super.addDynamicMethods(context, dynamicMethods);

			appendDynamicMethods(context, dynamicMethods);
		}

		public abstract void appendDynamicMethods(RuntimeContext context, TypeNameFunctionMap dynamicMethods);

		@Override
		public void addFrameworkObjects(ASTBuilder builder) {
			super.addFrameworkObjects(builder);

			appendFrameworkObjects(builder);
		}

		public abstract void appendFrameworkObjects(ASTBuilder builder);

	}

	private static final ScriptEngineFactory myFactory = new ALScriptEngineFactory();
	private ScriptContext defaultContext;

	private final Syntax syntax;
	private final AbstractRuntime runtime;
	private final RuntimeContext context;

	public ALScriptEngine() {
		setContext(new SeparatedContext());
		// set special values
		putInfo(LANGUAGE_VERSION, AL.VERSION);
		putInfo(LANGUAGE, AL.FULL_NAME);
		putInfo(ENGINE, AL.ENGINE_NAME);
		putInfo(ENGINE_VERSION, AL.ENGINE_VERSION);
		putInfo(ARGV, ""); // TODO: set correct value
		putInfo(FILENAME, ""); // TODO: set correct value
		putInfo(NAME, AL.SHORT_NAME);

		syntax = new Syntax();
		runtime = new RuntimeVisitor();

		// create context
		context = AL.createContext(syntax, runtime, new Reflexion());
	}

	public Syntax getSyntax() {
		return syntax;
	}

	public AbstractRuntime getRuntime() {
		return runtime;
	}

	public RuntimeContext getRuntimeContext() {
		return context;
	}

	@Override
	public Object eval(String script) throws ScriptException {
		return eval(script, getContext());
	}

	@Override
	public Object eval(String script, ScriptContext ctx) throws ScriptException {
		ASTBuilder builder = new ASTBuilder();

		// set script context
		for (String key : ctx.getBindings(ScriptContext.ENGINE_SCOPE).keySet()) {

			// get value
			Object value = ctx.getBindings(ScriptContext.ENGINE_SCOPE).get(key);

			if (value instanceof Expression) { // expression
				Expression expr = (Expression) value;

				if (Types.contains(key) && expr instanceof FunctionDeclaration) {
					// dynamic methods
					runtime.addDynamicMethod(Types.valueOf(key), new FunctionInstance((FunctionDeclaration<RuntimeContext>) expr));
				} else {
					// api
					builder.push(DeclarationFactory.factory(key, expr));
				}
			} else if (value != null && "javafx.event.ActionEvent".equals(value.getClass().getName())) {
				// JavaFX events
				Reflexion.buildJavaFXEventControls(context, value);

				// build event object anyway
				ObjectInstance o = Reflexion.buildObject(context, value.getClass(), value);
				context.root.define(Identifiers.getID(key), new MutableVariant(o));

			} else if (value != null) { // objects
				ObjectInstance o = Reflexion.buildObject(context, value.getClass(), value);
				context.root.define(Identifiers.getID(key), new MutableVariant(o));
			} else { // null
				//System.out.println("ERROR WITH NULL [" + key + "]");
				// do nothing
			}
		}

		// load expressions to context
		builder.injectVisitor(runtime).visite(context);

		// build JavaFX special context
		Object controller = ctx.getAttribute("controller");
		if (controller != null) {
			Reflexion.buildJavaFXContext(context, controller);
		}

		// execute
		MutableVariant ret = Main.executeScript(syntax, runtime, context, script);
		return ret.toJavaObject();
	}

	@Override
	public Object eval(String script, Bindings bindings) throws ScriptException {
		Bindings current = getContext().getBindings(ScriptContext.ENGINE_SCOPE);
		getContext().setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		Object result = eval(script);
		getContext().setBindings(current, ScriptContext.ENGINE_SCOPE);
		return result;
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		return eval(getScriptFromReader(reader));
	}

	@Override
	public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
		return eval(getScriptFromReader(reader), scriptContext);
	}

	@Override
	public Object eval(Reader reader, Bindings bindings) throws ScriptException {
		return eval(getScriptFromReader(reader), bindings);
	}

	public final void putInfo(String key, String value) {
		getBindings(ScriptContext.GLOBAL_SCOPE).put(key, value);
	}

	@Override
	public void put(String key, Object value) {
		Object object = null;

		if (value == null) {
		} else if (value instanceof Boolean) {
			object = ExpressionFactory.bool((Boolean) value);
		} else if (value instanceof Number) {
			object = ExpressionFactory.number(((Number) value).floatValue());
		} else if (value instanceof String) {
			object = ExpressionFactory.string((String) value);
		} else if (value instanceof ArrayDeclaration) {
			object = (ArrayDeclaration) value;
		} else if (value instanceof FunctionDeclaration) {
			object = (FunctionDeclaration) value;
		} else if (value instanceof ObjectDeclaration) {
			object = (ObjectDeclaration) value;
		} else {
			object = value;
		}

		getBindings(ScriptContext.ENGINE_SCOPE).put(key, object);
	}

	@Override
	public Object get(String key) {
		return getBindings(ScriptContext.ENGINE_SCOPE).get(key);
	}

	@Override
	public Bindings getBindings(int scope) {
		return getContext().getBindings(scope);
	}

	@Override
	public void setBindings(Bindings bindings, int scope) {
		getContext().setBindings(bindings, scope);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public ScriptContext getContext() {
		return defaultContext;
	}

	@Override
	public final void setContext(ScriptContext context) {
		defaultContext = context;
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return myFactory;
	}

	public static String getScriptFromReader(Reader reader) {
		try {
			StringWriter script = new StringWriter();
			int data;
			while ((data = reader.read()) != -1) {
				script.write(data);
			}
			script.flush();
			return script.toString();
		} catch (IOException ex) {
		}
		return null;
	}

	private String[] getArgs(Object... args) {
		String[] values = new String[args.length];
		for (int i = 0; i < values.length; i++) {
			if (args[i] instanceof String) {
				values[i] = "\"" + args[i] + "\"";
			} else {
				values[i] = String.valueOf(args[i]);
			}
		}
		return values;
	}

	@Override
	public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
		System.out.println("INVOKE METHOD !!!!");
		String functionCall = myFactory.getMethodCallSyntax(thiz.getClass().getName(), name, getArgs(args));
		return eval("return " + functionCall);
	}

	@Override
	public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
		System.out.println("INVOKE FUNCTION !!!!");
		String functionCall = myFactory.getMethodCallSyntax(null, name, getArgs(args));
		return eval("return " + functionCall);
	}

	@Override
	public <T> T getInterface(Class<T> clasz) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
