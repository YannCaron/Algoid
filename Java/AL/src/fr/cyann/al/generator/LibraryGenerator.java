/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

import java.lang.reflect.Method;

/**
 * <p>
 * @author cyann
 */
public class LibraryGenerator {

	// attributes
	private final Class<?> clazz;
	private final String alName;
	private final String javaName;
	private final StringBuilder source;
	private int indent = 0;

	public LibraryGenerator(String alName, String javaName, Class<?> clazz) {
		this.alName = alName;
		this.javaName = javaName;
		this.clazz = clazz;
		this.source = new StringBuilder();
	}

	// property
	public LibraryGenerator fixIndent(int indent) {
		this.indent = indent;
		return this;
	}

	// function
	private void indend(int n) {
		for (int i = 0; i < n + indent; i++) {
			source.append('\t');
		}
	}

	private void ret() {
		ret(0);
	}

	private void ret(int n) {
		source.append('\n');
		indend(n);
	}

	private void generateMethod(Method method) {
		String paramList = buildParameterList(method, "p%d", ", ", "\"", "\"");

		// header
		source.append(String.format("%s.addDeclaration(DeclarationFactory.factory(\"%s\", new MethodVisitor<Block<RuntimeContext>, RuntimeContext>() {", alName, Tools.getMethodName(method)));
		ret();
		ret(1);
		source.append("@Override");
		ret(1);
		source.append("public void visite(Block<RuntimeContext> ast, RuntimeContext context) {");

		// body
		ret(2);
		source.append("FunctionInstance f = ast.function;");
		generateParameterRetreivals(method);
		ret();
		generateCall(method);

		// footer
		ret(1);
		source.append("}");
		ret();
		source.append("}");
		if (!"".equals(paramList)) {
			source.append(", ");
			source.append(paramList);
		}
		source.append("));");
		ret();
		ret();

	}

	// float[] p1 = ArrayUtils.toArrayNumber(f.decl.params.get(0).var.mv.getArray());
	private void generateParameterRetreivals(Method method) {
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			Class<?> type = method.getParameterTypes()[i];

			ret(2);
			if (type.isArray()) {
				source.append(type.getComponentType().getName());
				source.append("[]");
			} else {
				source.append(type.getName());
			}
			source.append(" p");
			source.append(i + 1);
			source.append(" = ");
			if (type.equals(int.class) || type.equals(Integer.class)) {
				source.append("(");
				source.append(type.getName());
				source.append(") ");
			}
			if (type.isArray()) {
				source.append("ArrayUtils.toArrayNumber(");
			}
			source.append("f.decl.params.get(");
			source.append(i);
			source.append(").var.mv.");
			generateTypeGetter(type);
			if (type.isArray()) {
				source.append(")");
			}
			source.append(";");
		}
	}

	private void generateTypeGetter(Class<?> type) {
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			source.append("getBool()");
		} else if (type.equals(int.class) || type.equals(Integer.class) || type.equals(float.class) || type.equals(Float.class)) {
			source.append("getNumber()");
		} else if (type.equals(String.class)) {
			source.append("getString(context)");
		} else if (type.isArray()) {
			source.append("getArray()");
		} else {
			throw new RuntimeException("ERROR! Type [" + type.getName() + "] not specified!");
		}
	}

	private void generateCall(Method method) {

		ret(2);
		if (!method.getReturnType().equals(void.class)) {
			source.append("context.returnValue(");
		}

		source.append(String.format("%s.%s(%s)%s", javaName, method.getName(), buildParameterList(method, "p%d", ", ", "", ""), ifInstance(method)));

		if (!method.getReturnType().equals(void.class)) {
			source.append(')');
		}

		source.append(';');
	}

	private String ifInstance(Method method) {
		if (method.getReturnType().equals(clazz)) {
			return ".getALInstance()";
		}
		return "";
	}

	private static String buildParameterList(Method method, String format, String separator, String prefix, String postfix) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(prefix);
			sb.append(String.format(format, i + 1));
			sb.append(postfix);
		}

		return sb.toString();
	}

	// accessor
	public String getSource() {
		return source.toString();
	}

	public Class<?> getGeneratedClass() {
		return clazz;
	}

	// method
	public String generate() {

		source.delete(0, source.length());

		ret();
		for (Method method : clazz.getDeclaredMethods()) {
			if (Tools.containsAnnotation(method.getAnnotations(), GenerateMethod.class)) {
				generateMethod(method);
			}
		}

		return source.toString();
	}

	@Override
	public String toString() {
		return source.toString();
	}

}
