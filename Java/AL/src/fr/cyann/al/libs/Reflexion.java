/*
 * Copyright (C) 2014 cyann
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
package fr.cyann.al.libs;

import fr.cyann.al.ast.Block;
import fr.cyann.al.ast.declaration.ObjectDeclaration;
import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.data.ObjectInstance;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.Constants;
import fr.cyann.al.factory.ExpressionFactory;
import fr.cyann.al.factory.FactoryUtils;
import fr.cyann.al.factory.TypeNameFunctionMap;
import fr.cyann.al.library.ALLib;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.lexer.Token;
import fr.cyann.jasi.lexer.TokenType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 <p>
 @author cyann
 */
public class Reflexion implements ALLib, Constants {

	// inner class
	public static class ImportList extends ArrayList<Class<?>> {

		// add new unique element to the list
		@Override
		public boolean add(Class<?> e) {
			if (!this.contains(e)) {
				return super.add(e);
			} else {
				return false;
			}
		}

		// search by class name
		public Class<?> find(String name) {
			int size = size();
			for (int i = 0; i < size; i++) {
				Class<?> element = get(i);
				if (element.getSimpleName().equals(name)) {
					return element;
				}
			}
			return null;
		}

	}

	// const
	private static ObjectDeclaration<RuntimeContext> javaNull = null;

	// attributes
	private final ImportList imports;

	// constructor
	public Reflexion() {
		this.imports = new ImportList();
	}

	// properties
	@Override
	public String getName() {
		return "Java native reflexion AL lib";
	}

	@Override
	public String getVersion() {
		return "0.3.1";
	}

	@Override
	public String getAuthor() {
		return "Yann Caron aka CyaNn";
	}

	@Override
	public Date getCreationDate() {
		return new Date(2014, 02, 05);
	}

	// method
	public Class classForName(RuntimeContext context, String className) {

		Class<?> importedClassName = imports.find(className);
		if (importedClassName != null) {
			return importedClassName;
		} else {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException ex) {
				throw new ALRuntimeException(EX_CLASS_NOT_FOUND.setArgs(className), context.callAST.getToken());
			}
		}
	}

	// throw all exceptions happend in the lib
	public static void throwALException(RuntimeContext context, Throwable ex, Class<?> cls, String subject, String name, Class<?>[] types, Object[] values) {
		String ts = Arrays.toString(types);
		String vs = Arrays.toString(values);
		Token token = context.callAST.getToken();
		throw new ALRuntimeException(EX_ERROR.setArgs(token.getLine(), token.getCol(), ex.getMessage(), cls.getName(), subject, name, ts, vs), context.callAST.getToken());
	}

	private static int paramCount(MutableVariant... p) {
		for (int i = 0; i < p.length; i++) {
			if (p[i].isNull() || (p[i].isObject() && p[i].getObject().nativeObject == null && p[i].getObject().decl != javaNull)) {
				return i;
			}
		}
		return p.length;
	}

	private static ObjectInstance objectParam(MutableVariant... p) {
		for (int i = 0; i < p.length; i++) {
			if (p[i].isObject() && p[i].getObject().nativeObject == null) {
				return p[i].getObject();
			}
		}
		return null;
	}

	// convert
	private static void fromArray(RuntimeContext context, boolean[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static boolean[] toBoolArray(List<MutableVariant> list) {
		int size = list.size();
		boolean[] array = new boolean[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = list.get(i).getBool();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, byte[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static byte[] toByteArray(List<MutableVariant> list) {
		int size = list.size();
		byte[] array = new byte[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).byteValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, short[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static short[] toShortArray(List<MutableVariant> list) {
		int size = list.size();
		short[] array = new short[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).shortValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, int[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static int[] toIntArray(List<MutableVariant> list) {
		int size = list.size();
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).intValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, long[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static long[] toLongArray(List<MutableVariant> list) {
		int size = list.size();
		long[] array = new long[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).longValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, float[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static float[] toFloatArray(List<MutableVariant> list) {
		int size = list.size();
		float[] array = new float[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).floatValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, double[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static double[] toDoubleArray(List<MutableVariant> list) {
		int size = list.size();
		double[] array = new double[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = ((Float) list.get(i).getNumber()).doubleValue();
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, char[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static char[] toCharArray(RuntimeContext context, List<MutableVariant> list) {
		int size = list.size();
		char[] array = new char[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = list.get(i).getString(context).charAt(0);
		}

		return array;
	}

	private static String[] toStringArray(RuntimeContext context, List<MutableVariant> list) {
		int size = list.size();
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = list.get(i).getString(context);
		}

		return array;
	}

	private static void fromArray(RuntimeContext context, Object[] array, MutableVariant mv) {
		mv.convertToArray();

		for (int i = 0; i < array.length; i++) {
			MutableVariant item = new MutableVariant();
			setValueToMutableVariant(context, array[i], item);
			mv.add(item);
		}
	}

	private static Object[] toObjectArray(List<MutableVariant> list) {
		int size = list.size();
		Object[] array = new Object[size];
		for (int i = 0; i < size; i++) {
			array[i] = array[i] = list.get(i).toJavaObject();
		}

		return array;
	}

	// build object
	public static Object[] matchParameters(final RuntimeContext context, Class<?>[] paramTypes, MutableVariant... p) {

		// variables
		int size = paramTypes.length;
		Object[] values = new Object[size];

		// loop on parameters
		for (int i = 0; i < size; i++) {
			Class<?> paramType = paramTypes[i];

			// check type and get values
			// BOOL
			if ((paramType == boolean.class || paramType == Boolean.class) && p[i].isBool()) {
				values[i] = p[i].getBool();

				// BYTE
			} else if ((paramType == byte.class || paramType == Byte.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).byteValue();

				// SHORT
			} else if ((paramType == short.class || paramType == Short.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).shortValue();

				// INT
			} else if ((paramType == int.class || paramType == Integer.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).intValue();

				// LONG
			} else if ((paramType == long.class || paramType == Long.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).longValue();

				// FLOAT
			} else if ((paramType == float.class || paramType == Float.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).floatValue();

				// DOUBLE
			} else if ((paramType == double.class || paramType == Double.class) && p[i].isNumber()) {
				values[i] = new Float(p[i].getNumber()).doubleValue();

				// CHAR
			} else if ((paramType == char.class || paramType == Character.class) && p[i].isString() && p[i].getString(context).length() == 1) {
				values[i] = p[i].getString(context).charAt(0);

				// STRING
			} else if ((paramType == String.class || paramType == CharSequence.class) && p[i].isString()) {
				values[i] = p[i].getString(context);

				// BOOL ARRAY
			} else if ((paramType == boolean[].class || paramType == Boolean[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isBool())) {
				values[i] = toBoolArray(p[i].getArray());

				// BYTE ARRAY
			} else if ((paramType == byte[].class || paramType == Byte[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toByteArray(p[i].getArray());

				// SHORT ARRAY
			} else if ((paramType == short[].class || paramType == Short[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toShortArray(p[i].getArray());

				// INT ARRAY
			} else if ((paramType == int[].class || paramType == Integer[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toIntArray(p[i].getArray());

				// LONG ARRAY
			} else if ((paramType == long[].class || paramType == Long[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toLongArray(p[i].getArray());

				// FLOAT ARRAY
			} else if ((paramType == float[].class || paramType == Float[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toFloatArray(p[i].getArray());

				// DOUBLE ARRAY
			} else if ((paramType == double[].class || paramType == Double[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isNumber())) {
				values[i] = toDoubleArray(p[i].getArray());

				// CHAR ARRAY
			} else if ((paramType == char[].class || paramType == Character[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isString())) {
				values[i] = toCharArray(context, p[i].getArray());

				// STRING ARRAY
			} else if ((paramType == String[].class || paramType == CharSequence[].class) && p[i].isArray() && (p[i].isEmpty() || p[i].getValue(0).isString())) {
				values[i] = toStringArray(context, p[i].getArray());

				// OBJECT ARRAY
			} else if (paramType == Object[].class && p[i].isArray()) {
				values[i] = toObjectArray(p[i].getArray());

				// FUNCTION
			} else if (p[i].isFunction()) {
				final FunctionInstance f = p[i].getFunction();

				try {

					if (paramType.isInterface()) {
						Object object = Proxy.newProxyInstance(paramType.getClassLoader(), new Class<?>[]{paramType}, new InvocationHandler() {

							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								return proxyCallFunction(context, f, method.getName(), args);
							}
						});

						context.returnValue(buildObject(context, object.getClass(), object));
						values[i] = object;
					}

				} catch (SecurityException ex) {
					throwALException(context, ex, paramType, "matchParameters", null, paramTypes, values);
				} catch (IllegalArgumentException ex) {
					throwALException(context, ex, paramType, "matchParameters", null, paramTypes, values);
				}
				// OBJECT
			} else if (p[i].isObject()) {
				Object nativeObject = p[i].getObject().nativeObject;

				if (paramType.isInstance(nativeObject) || paramType == Object.class) {
					values[i] = nativeObject;
				} else if (p[i].getObject().decl.equals(javaNull)) {
					values[i] = null;
				} else {
					// does not match
					return null;
				}
			} else {
				// does not match
				return null;
			}
		}

		return values;
	}

	public static void clearParameters(FunctionInstance f) {
		int size = f.decl.params.size();
		for (int i = 0; i < size; i++) {
			f.decl.params.get(i).var.mv.convertToVoid();
		}
	}

	public static void setValueToMutableVariant(RuntimeContext context, Object value, MutableVariant mv) {
		/* NULL */ if (value == null) {
			// do nothing

			/* BOOLEAN */ } else if (value.getClass() == boolean.class || value instanceof Boolean) {
			mv.setValue((Boolean) value);

			/* BYTE */ } else if (value.getClass() == byte.class || value instanceof Byte) {
			mv.setValue(((Byte) value).floatValue());

			/* SHORT */ } else if (value.getClass() == short.class || value instanceof Short) {
			mv.setValue(((Short) value).floatValue());

			/* INT */ } else if (value.getClass() == int.class || value instanceof Integer) {
			mv.setValue(((Integer) value).floatValue());

			/* LONG */ } else if (value.getClass() == long.class || value instanceof Long) {
			mv.setValue(((Long) value).floatValue());

			/* FLOAT */ } else if (value.getClass() == float.class || value instanceof Float) {
			mv.setValue(((Float) value).floatValue());

			/* DOUBLE */ } else if (value.getClass() == double.class || value instanceof Double) {
			mv.setValue(((Double) value).floatValue());

			/* CHAR */ } else if (value.getClass() == char.class || value instanceof Character) {
			mv.setValue(((Character) value).toString());

			/* STRING */ } else if (value instanceof String || value instanceof CharSequence) {
			mv.setValue(value.toString()); // be carefull, Character sequence cannot be cast to String

			/* BOOLEAN[] */ } else if (value.getClass() == boolean[].class) {
			fromArray(context, (boolean[]) value, mv);

			/* BYTE[] */ } else if (value.getClass() == byte[].class) {
			fromArray(context, (byte[]) value, mv);

			/* SHORT[] */ } else if (value.getClass() == short[].class) {
			fromArray(context, (short[]) value, mv);

			/* INT[] */ } else if (value.getClass() == int[].class) {
			fromArray(context, (int[]) value, mv);

			/* LONG[] */ } else if (value.getClass() == long[].class) {
			fromArray(context, (long[]) value, mv);

			/* FLOAT[] */ } else if (value.getClass() == float[].class) {
			fromArray(context, (float[]) value, mv);

			/* DOUBLE[] */ } else if (value.getClass() == double[].class) {
			fromArray(context, (double[]) value, mv);

			/* CHAR[] */ } else if (value.getClass() == char[].class) {
			fromArray(context, (char[]) value, mv);

			/* OBJECT[] */ } else if (value.getClass().isArray()) {
			fromArray(context, (Object[]) value, mv);

			/* LIST */ } else if (value instanceof List) {
			// list is a native object
			ObjectInstance alObject = buildObject(context, value.getClass(), value);
			mv.setValue(alObject);

			/* OBJECT */ } else { // object
			ObjectInstance alObject = buildObject(context, value.getClass(), value);
			mv.setValue(alObject);
		}
	}

	private static ObjectInstance getInstanceFromMethod(Block<RuntimeContext> ast) {
		MutableVariant mv = ast.function.enclosing.resolve(Identifiers.getID("this"));
		if (mv == null) {
			System.out.println("NULL FROM AST " + ast);
		}
		return mv.getObject();
	}

	public static void callMethod(RuntimeContext context, Block<RuntimeContext> ast, String name, Class<?> cls, MutableVariant... p) {
		// init
		int nbParam = paramCount(p);

		// get native object from this
		Object object = getInstanceFromMethod(ast).nativeObject;

		// loop on all object methods
		for (Method method : cls.getMethods()) {
			// check by name and number of parameters
			if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == nbParam && method.getName().equals(name)) {

				// get corresponding values
				Object[] values = matchParameters(context, method.getParameterTypes(), p);

				if (values != null) {
					try {

						// invole native method
						Object value = null;
						if (object != null && Proxy.isProxyClass(object.getClass())) {

							// invoke proxy class
							try {
								value = Proxy.getInvocationHandler(object).invoke(object, method, values);
							} catch (Throwable ex) {
								throwALException(context, ex, cls, "method", name, method.getParameterTypes(), values);
							}
						} else {

							// invoke method
							value = method.invoke(object, values);
						}

						// return result
						if (value != null) {
							context.returning = true;
							setValueToMutableVariant(context, value, context.returnValue);
						}

					} catch (IllegalAccessException ex) {
						throwALException(context, ex, cls, "method", name, method.getParameterTypes(), values);
					} catch (IllegalArgumentException ex) {
						throwALException(context, ex, cls, "method", name, method.getParameterTypes(), values);
					} catch (InvocationTargetException ex) {
						throwALException(context, ex, cls, "method", name, method.getParameterTypes(), values);
					}
					clearParameters(ast.function);
					return;

				}
			}
		}

		throwALException(context, new Exception("Native Java method not found"), cls, "method", name, null, null);
	}

	public static void buildObjectGetter(RuntimeContext ctx, ObjectDeclaration<RuntimeContext> decl, final String name, final Field field) {
		FactoryUtils.addMethod(decl, name, new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {

				// get native object from this
				Object object = getInstanceFromMethod(ast).nativeObject;

				try {
					Object value = field.get(object);
					setValueToMutableVariant(context, value, context.returnValue);
					context.returning = true;
				} catch (IllegalArgumentException ex) {
					throwALException(context, ex, object.getClass(), "getter", name, null, null);
				} catch (IllegalAccessException ex) {
					throwALException(context, ex, object.getClass(), "getter", name, null, null);
				}
			}
		});
	}

	public static void buildObjectSetter(RuntimeContext ctx, ObjectDeclaration<RuntimeContext> decl, final String name, final Field field) {
		FactoryUtils.addMethod(decl, name, new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				MutableVariant p1 = ast.function.decl.params.get(0).var.mv;

				// get native object from this
				Object object = getInstanceFromMethod(ast).nativeObject;

				try {
					field.set(object, p1.toJavaObject());
					context.returning = false;
				} catch (IllegalArgumentException ex) {
					throwALException(context, ex, object.getClass(), "setter", name, null, null);
				} catch (IllegalAccessException ex) {
					throwALException(context, ex, object.getClass(), "setter", name, null, null);
				}
			}
		}, "p1");
	}

	public static void buildObjectMethod(RuntimeContext ctx, ObjectDeclaration<RuntimeContext> decl, final String name, final Class<?> cls) {
		FactoryUtils.addMethod(decl, name, new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				MutableVariant p1 = ast.function.decl.params.get(0).var.mv;
				MutableVariant p2 = ast.function.decl.params.get(1).var.mv;
				MutableVariant p3 = ast.function.decl.params.get(2).var.mv;
				MutableVariant p4 = ast.function.decl.params.get(3).var.mv;
				MutableVariant p5 = ast.function.decl.params.get(4).var.mv;
				MutableVariant p6 = ast.function.decl.params.get(5).var.mv;
				MutableVariant p7 = ast.function.decl.params.get(6).var.mv;
				MutableVariant p8 = ast.function.decl.params.get(7).var.mv;
				MutableVariant p9 = ast.function.decl.params.get(8).var.mv;
				MutableVariant p10 = ast.function.decl.params.get(9).var.mv;

				callMethod(context, ast, name, cls, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
			}
		}, "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10");
	}

	public static ObjectInstance buildObject(RuntimeContext context, Class<?> cls, Object object) {

		// create object declaration
		//Class<?> cls = object.getClass();
		ObjectDeclaration<RuntimeContext> objectDeclaration = ExpressionFactory.object(cls.getSimpleName());

		ObjectInstance alObject = new ObjectInstance(objectDeclaration, context.root, object);

		// add Static final fields
		for (Field field : cls.getFields()) {
			// only public static fields
			if (Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
				try {
					// add field by name
					Object value = field.get(null);
					MutableVariant mv = new MutableVariant();
					if (!value.getClass().equals(cls)) {
						setValueToMutableVariant(context, value, mv);
					} else if (object.equals(value)) {
						mv.setValue(alObject);
					} else {
						mv.setValue(alObject.clone(context));
					}
					alObject.scope.define(Identifiers.getID(field.getName()), mv);
					// TODO to be replaced by this implementation (generate stack overflow when injecting visitor or visite)
					//FactoryUtils.addAttribute(alObject.decl, field.getName(), mv);

				} catch (IllegalArgumentException ex) {
					throwALException(context, ex, object.getClass(), "field", field.toGenericString(), null, null);
				} catch (IllegalAccessException ex) {
					throwALException(context, ex, object.getClass(), "field", field.toGenericString(), null, null);
				}

				// only public
			} else if (Modifier.isPublic(field.getModifiers())) {
				String majName = field.getName();
				majName = majName.substring(0, 1).toUpperCase() + majName.substring(1);

				// getter
				String getterName = "get" + majName;
				buildObjectGetter(context, alObject.decl, getterName, field);

				// setter
				if (!Modifier.isFinal(field.getModifiers())) {
					String setterName = "set" + majName;
					buildObjectGetter(context, alObject.decl, setterName, field);
				}

			}

		}

		// add Methods
		String prevMethod = "";

		// loop on all methods
		for (final Method method : cls.getMethods()) {

			// only once
			if (Modifier.isPublic(method.getModifiers()) && !method.getName().equals(prevMethod)) {
				// add new method by name
				buildObjectMethod(context, alObject.decl, method.getName(), cls);
			}

			prevMethod = method.getName();
		}

		// build and return
		alObject.decl.injectVisitor(context.runtime);
		alObject.build(context);
		return alObject;

	}

	public static Object proxyCallFunction(RuntimeContext context, FunctionInstance f, String methodName, Object[] args) {
		// loop on args
		int size = args.length;
		for (int i = 0; i < size; i++) {
			Object arg = args[i];

			// set value
			if (i < f.decl.params.size()) {
				setValueToMutableVariant(context, arg, f.decl.params.get(i).var.mv);
			}

		}

		// set method name in the last variable parameter
		if (methodName != null && size < f.decl.params.size()) {
			setValueToMutableVariant(context, methodName, f.decl.params.get(size).var.mv);
		}

		RuntimeVisitor.callFunction(context, f, context.returnValue);
		context.returning = false;
		return context.returnValue.toJavaObject();
	}

	// javafx
	public static void buildJavaFXContext(RuntimeContext context, Object object) {
		Class<?> cls = object.getClass();

		try {

			// resolve controller
			ObjectInstance alObject = context.root.resolve(Identifiers.getID("controller")).getObject();

			// set all methods to global scope
			for (Method method : cls.getDeclaredMethods()) {
				method.setAccessible(true);
				// add new method by name

				// define if not yet
				Integer ident = Identifiers.getID(method.getName());
				if (!context.scope.isAlreadyDefined(ident)) {

					// resolve method from alObject
					MutableVariant mv = alObject.scope.resolve(ident);
					context.scope.define(ident, mv);
				}
			}
		} catch (IllegalArgumentException ex) {
			throwALException(context, ex, object.getClass(), "buildJavaFXContext", "", null, null);
		}

	}

	public static void buildJavaFXEventControls(RuntimeContext context, Object event) {
		try {

			// get root object from event
			Object source = event.getClass().getMethod("getSource").invoke(event);
			Object scene = source.getClass().getMethod("getScene").invoke(source);
			Object root = scene.getClass().getMethod("getRoot").invoke(scene);

			// get all nodes containes by root
			Set<Object> nodes = (Set<Object>) root.getClass().getMethod("lookupAll", String.class).invoke(root, "*");

			// loop on each
			for (Object node : nodes) {

				// get name then ident
				String name = (String) node.getClass().getMethod("getId").invoke(node);
				Integer ident = Identifiers.getID(name);

				// if not already defined then create it
				if (!context.root.isAlreadyDefined(ident)) {
					System.out.println("CREATE " + name);
					// create graphic object shortcut in current scope
					ObjectInstance o = buildObject(context, node.getClass(), node);
					context.root.define(ident, new MutableVariant(o));
				}
			}
		} catch (NoSuchMethodException ex) {
			throwALException(context, ex, event.getClass(), "FXEvent", "", null, null);
		} catch (SecurityException ex) {
			throwALException(context, ex, event.getClass(), "FXEvent", "", null, null);
		} catch (IllegalAccessException ex) {
			throwALException(context, ex, event.getClass(), "FXEvent", "", null, null);
		} catch (IllegalArgumentException ex) {
			throwALException(context, ex, event.getClass(), "FXEvent", "", null, null);
		} catch (InvocationTargetException ex) {
			throwALException(context, ex, event.getClass(), "FXEvent", "", null, null);
		}
	}

	// template method
	@Override
	public void addFrameworkObjects(final ASTBuilder builder) {

		// create my object, the library entry point
		ObjectDeclaration<RuntimeContext> java = FactoryUtils.addObject(builder, "java");

		javaNull = FactoryUtils.addObject(java, "null");

		FactoryUtils.addMethod(java, "import", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				String p1 = ast.function.decl.params.get(0).var.mv.getString(context);

				// verify if exists
				Class<?> clazz;
				try {
					clazz = Class.forName(p1);
					imports.add(clazz);
				} catch (ClassNotFoundException ex) {
					throw new ALRuntimeException(EX_CLASS_NOT_FOUND.setArgs(p1), context.callAST.getToken());
				}

			}
		}, "p1");

		FactoryUtils.addMethod(java, "newObject", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, final RuntimeContext context) {
				String className = FactoryUtils.getParam(ast, 0).getString(context);
				MutableVariant p1 = ast.function.decl.params.get(1).var.mv;
				MutableVariant p2 = ast.function.decl.params.get(2).var.mv;
				MutableVariant p3 = ast.function.decl.params.get(3).var.mv;
				MutableVariant p4 = ast.function.decl.params.get(4).var.mv;
				MutableVariant p5 = ast.function.decl.params.get(5).var.mv;
				MutableVariant p6 = ast.function.decl.params.get(6).var.mv;
				MutableVariant p7 = ast.function.decl.params.get(7).var.mv;
				MutableVariant p8 = ast.function.decl.params.get(8).var.mv;
				MutableVariant p9 = ast.function.decl.params.get(9).var.mv;
				MutableVariant p10 = ast.function.decl.params.get(10).var.mv;

				// init
				int nbParam = paramCount(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
				Object object = null;
				ObjectInstance o = null;

				final Class<?> cls = classForName(context, className);
				final ObjectInstance alObject = objectParam(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

				InvocationHandler handler = new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						FunctionInstance f = alObject.scope.resolve(Identifiers.getID(method.getName()), false).getFunction();
						return proxyCallFunction(context, f, method.getName(), args);
					}
				};

				if (cls.isInterface()) {
					// create instance from interface
					object = Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, handler);
					o = buildObject(context, object.getClass(), object);
				} else if (Modifier.isAbstract(cls.getModifiers())) {
					// create instance from abstract class
					object = Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), handler);
					o = buildObject(context, cls, object); // keep it important (create instance on cls)
				} else {
					// create instance
					// loop on all constructors
					for (Constructor<?> constructor : cls.getConstructors()) {

						if (Modifier.isPublic(constructor.getModifiers())) {
							// check if parameters number matches
							Class<?>[] types = constructor.getParameterTypes();
							if (types.length == nbParam) {

								// get corresponding values
								Object[] values = matchParameters(context, types, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

								if (values != null) {
									try {
										object = constructor.newInstance(values);
									} catch (InstantiationException ex) {
										throwALException(context, ex, cls, "constructor", cls.getSimpleName(), types, values);
									} catch (IllegalAccessException ex) {
										throwALException(context, ex, cls, "constructor", cls.getSimpleName(), types, values);
									} catch (IllegalArgumentException ex) {
										throwALException(context, ex, cls, "constructor", cls.getSimpleName(), types, values);
									} catch (InvocationTargetException ex) {
										throwALException(context, ex, cls, "constructor", cls.getSimpleName(), types, values);
									}
								}
							}
						}
					}

					/*if (object == null) {
					 throw new ALRuntimeException(String.format("Class [%s] cannot be instanciated or constructor not found !", cls.getName()), ast.getToken());
					 }*/
					o = buildObject(context, cls, object);
				}

				context.returnValue(o);

			}
		}, "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11");

		FactoryUtils.addMethod(java, "staticField", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				String className = FactoryUtils.getParam(ast, 0).getString(context);
				String fieldName = FactoryUtils.getParam(ast, 1).getString(context);

				Class<?> cls = null;
				try {

					cls = Class.forName(className);

					Field field = cls.getDeclaredField(fieldName);
					if (Modifier.isStatic(field.getModifiers())) {

						// get value
						Object value = field.get(null);

						// return it
						context.returning = true;
						setValueToMutableVariant(context, value, context.returnValue);
						return;
					}

					throw new ALRuntimeException(EX_CONSTRUCTOR_NOT_FOUND.setArgs(cls.getName()), new Token(TokenType.EOF, "x"));

				} catch (SecurityException ex) {
					throwALException(context, ex, cls, "field", fieldName, null, null);
				} catch (ClassNotFoundException ex) {
					throwALException(context, ex, Class.class, "field", fieldName, null, null);
				} catch (NoSuchFieldException ex) {
					throwALException(context, ex, cls, "field", fieldName, null, null);
				} catch (IllegalArgumentException ex) {
					throwALException(context, ex, cls, "field", fieldName, null, null);
				} catch (IllegalAccessException ex) {
					throwALException(context, ex, cls, "field", fieldName, null, null);
				}

			}
		}, "p1", "p2");

		FactoryUtils.addMethod(java, "staticMethod", new FactoryUtils.Behaviour() {

			@Override
			public void visite(Block<RuntimeContext> ast, RuntimeContext context) {
				String className = FactoryUtils.getParam(ast, 0).getString(context);
				String fieldName = FactoryUtils.getParam(ast, 1).getString(context);
				MutableVariant p1 = ast.function.decl.params.get(2).var.mv;
				MutableVariant p2 = ast.function.decl.params.get(3).var.mv;
				MutableVariant p3 = ast.function.decl.params.get(4).var.mv;
				MutableVariant p4 = ast.function.decl.params.get(5).var.mv;
				MutableVariant p5 = ast.function.decl.params.get(6).var.mv;
				MutableVariant p6 = ast.function.decl.params.get(7).var.mv;
				MutableVariant p7 = ast.function.decl.params.get(8).var.mv;
				MutableVariant p8 = ast.function.decl.params.get(9).var.mv;
				MutableVariant p9 = ast.function.decl.params.get(10).var.mv;
				MutableVariant p10 = ast.function.decl.params.get(11).var.mv;
				try {
					Class cls = Class.forName(className);
					callMethod(context, ast, fieldName, cls, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
				} catch (ClassNotFoundException ex) {
					throwALException(context, ex, Class.class, "method", fieldName, null, null);
				}

			}
		}, "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12");
	}

	@Override
	public void addDynamicMethods(RuntimeContext rc, TypeNameFunctionMap tnfm) {
	}

}
