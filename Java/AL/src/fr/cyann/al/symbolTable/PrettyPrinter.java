/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.symbolTable;

import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * @author cyann
 */
public class PrettyPrinter {

	public interface PrinterOption {

		boolean mustPrintAll();
	}

	public interface Printable {

		PrettyPrinter print(PrettyPrinter printer);

		PrettyPrinter println(PrettyPrinter printer);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Ignore {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Optional {

		String[] forValues() default {"false", "0", ""};
	}

	// static
	public static final PrinterOption DEFAULT_OPTIONS = new PrinterOption() {

		@Override
		public boolean mustPrintAll() {
			return false;
		}
	};

	private final PrintStream out;
	private int indent;
	private final Set<Object> printed;
	PrinterOption options;

	public PrettyPrinter() {
		this(System.out, DEFAULT_OPTIONS);
	}

	public PrettyPrinter(PrintStream out) {
		this(out, DEFAULT_OPTIONS);
	}

	public PrettyPrinter(PrintStream out, PrinterOption options) {
		this.out = out;
		indent = 0;
		printed = new HashSet<Object>();
		this.options = options;
	}

	public PrettyPrinter printIndent() {
		for (int i = 0; i < indent; i++) {
			if (i < indent - 1) {
				out.print(" | ");
			} else {
				out.print(" + ");
			}
		}
		return this;
	}

	public PrettyPrinter increaseIndentation() {
		indent++;
		return this;
	}

	public PrettyPrinter decreaseIndentation() {
		if (indent <= 0) {
			throw new RuntimeException(String.format("Indentation error, indent [%d] cannot be less than 0", indent));
		}
		indent--;
		return this;
	}

	public PrettyPrinter print(Object instance) {
		if (printed.contains(instance)) {
			print("...");
			return this;
		} else {
			printed.add(instance);
		}

		Class<?> clazz = instance.getClass();

		out.print(clazz.getSimpleName());
		out.print(" [");

		printClassFields(clazz, instance, true, false);

		out.print("]");
		return this;
	}

	private static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof List && ((List) value).isEmpty()) {
			return true;
		} else if (value instanceof Map && ((Map) value).isEmpty()) {
			return true;
		}

		return false;
	}

	private void printClassFields(Class<?> clazz, Object instance, boolean first, boolean ret) {
		for (Field field : clazz.getDeclaredFields()) {
			Ignore nop = field.getAnnotation(Ignore.class);
			Optional opt = field.getAnnotation(Optional.class);

			increaseIndentation();
			try {
				field.setAccessible(true);
				Object value = field.get(instance);

				if (nop == null && (opt == null || !Arrays.asList(opt.forValues()).contains("" + value))) {

					if (options.mustPrintAll() || !isEmpty(value)) {
						if (!first) {
							out.print(", ");
						}

						if (ret || value instanceof List || value instanceof Map || value instanceof Printable) {
							println().printIndent();
						}

						first = false;

						out.print(field.getName());
						out.print(" = ");

						if (value instanceof List) {
							print((List) value);
						} else if (value instanceof Map) {
							print((Map) value);
						} else if (value instanceof Printable) {
							((Printable) value).print(this);
						} else if (value instanceof String) {
							out.printf("\"%s\"", value);
						} else {
							out.print(value);
						}

						if (value instanceof List || value instanceof Map || value instanceof Printable) {
							ret = true;
						} else {
							ret = false;
						}

					}
					printed.add(instance);
				}

			} catch (IllegalArgumentException ex) {
				Logger.getLogger(PrettyPrinter.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(PrettyPrinter.class.getName()).log(Level.SEVERE, null, ex);
			}

			decreaseIndentation();
		}

		if (clazz.getSuperclass() != null) {
			printClassFields(clazz.getSuperclass(), instance, first, ret);
		}
	}

	public PrettyPrinter println(Object instance) {
		print(instance).println();
		return this;
	}

	public PrettyPrinter println() {
		out.println();
		return this;
	}

	public PrettyPrinter print(String string) {
		out.print(string);
		return this;
	}

	public PrettyPrinter print(List list) {
		return print(list.toArray());
	}

	public PrettyPrinter println(List list) {
		return print(list).println();
	}

	public PrettyPrinter print(Object[] instances) {
		Class<?> clazz = instances.getClass();

		int size = instances.length;

		out.print(clazz.getSimpleName().replace("[]", ""));
		out.printf(" [%s]", size);

		if (size > 0) {
			out.println();

			increaseIndentation();

			boolean first = true;
			for (int i = 0; i < size; i++) {
				Object instance = instances[i];

				if (!first) {
					print(", ").println();
				} else {
					first = false;
				}
				printIndent();
				out.printf("[%d] => ", i);

				if (instance instanceof Printable) {
					print(instance);
				} else {
					out.print(instance);
				}
			}

			decreaseIndentation();
		}

		return this;
	}

	public PrettyPrinter println(Object[] instances) {
		return print(instances).println();
	}

	public PrettyPrinter print(Map instances) {
		Class<?> clazz = instances.getClass();

		Object[] keys = instances.keySet().toArray();
		int size = keys.length;

		out.print(clazz.getSimpleName().replace("[]", ""));
		out.printf(" [%s] ", size);

		if (size > 0) {
			out.println();

			increaseIndentation();

			boolean first = true;
			for (int i = 0; i < size; i++) {
				Object instance = instances.get(keys[i]);

				if (!first) {
					print(", ").println();
				} else {
					first = false;
				}
				printIndent();
				out.printf("[%s] => ", keys[i]);

				if (instance instanceof Printable) {
					print(instance);
				} else {
					out.print(instance);
				}
			}

			decreaseIndentation();

		}

		return this;
	}

	public PrettyPrinter println(Map instances) {
		return print(instances).println();
	}

}
