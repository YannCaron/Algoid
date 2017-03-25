/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al;

import static fr.cyann.al.AL.setProgramToBlock;
import fr.cyann.al.ast.Expression;
import fr.cyann.al.ast.Program;
import fr.cyann.al.data.MutableVariant;
import fr.cyann.al.exception.ALRuntimeException;
import fr.cyann.al.library.ALLib;
import fr.cyann.al.library.LibraryBasket;
import fr.cyann.al.libs.Reflexion;
import fr.cyann.al.syntax.ALTools;
import fr.cyann.al.syntax.Syntax;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import fr.cyann.al.visitor.RuntimeVisitor;
import fr.cyann.jasi.ast.AST;
import fr.cyann.jasi.builder.ASTBuilder;
import fr.cyann.jasi.exception.JASIException;
import fr.cyann.jasi.parser.PEG;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.script.ScriptException;
import jline.ConsoleReader;
import jline.SimpleCompletor;

/**
 <p>
 @author cyann
 */
public class Main {
	
	private static final List<ALLib> libs = new ArrayList<ALLib>();
	
	private static final List<LibraryBasket<ALLib>> libraryBaskets = new ArrayList<LibraryBasket<ALLib>>();

	public static void main(String[] args) throws FileNotFoundException, ScriptException, IOException {

		boolean match = false;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.matches("^[-]{0,2}help$") || arg.equals("-h")) {
				match = true;
				printConsoleHelp();
			} else if (arg.matches("^[-]{0,2}script$") || arg.equals("-s")) {
				if (args.length > i) {
					match = true;
					i++;
					executeScript("shell", args[i]);
				}
			} else if (arg.matches("^[-]{0,2}lib$") || arg.equals("-l")) {
				if (args.length > i) {
					match = true;
					i++;
					addLibraryBasket(new File(args[i]));
				}
			} else if (arg.matches("^[-]{0,2}file$") || arg.equals("-f")) {
				if (args.length > i) {
					match = true;
					i++;
					executeFile(new File(args[i]));
				}
			} else if (arg.matches("^[-]{0,2}shell$") || arg.equals("-sh")) {
				match = true;
				shell();
			} else if (arg.matches("^[-]{0,2}version$") || arg.equals("-v")) {
				match = true;
				printVersion();
			}
		}

		if (!match) {
			System.out.println("AL: missing options.");
			System.out.println("Use \"java -jar AL-script.jar --help\" for more informations.");
		}

	}

	public static void printConsoleHelp() {
		System.out.println();
		System.out.println();
		System.out.println("AL Script engine help");
		System.out.println("Written by Yann Caron - Jan 2013");
		System.out.println("Eval Algoid Language scripts");
		System.out.println("Usage java -jar AL-script.jar [OPTION]");
		System.out.println();
		System.out.println("  -f, --file\t[FILE]\t\tLoad AL file and run it.");
		System.out.println("  -h, --help\t\t\tPrint this help.");
		System.out.println("  -s, --script\t[SCRIPT]\tExecute the AL script.");
		System.out.println("  -sh, --shell\t\t\tLaunch the AL script shell.");
		System.out.println("  -v, --version\t\t\tPrint the AL script version.");
		System.out.println();
	}

	public static void printShellHelp() {
		System.out.println();
		System.out.println();
		System.out.println("AL shell");
		System.out.println("Written by: Yann Caron - jan 2013");
		System.out.println();
		System.out.println("Commands to use:");
		System.out.println("help, file [file.al], exit or your AL script");
		System.out.println("use [tab] to view all API objects");
		System.out.println();

		System.out.println(" ");
	}

	public static void printVersion() {
		System.out.println();
		System.out.println("AL script engine version : " + AL.ENGINE_VERSION);
		System.out.println("Written by Yann Caron - Jan 2013");
		System.out.println("AL script version : " + AL.VERSION);
		System.out.println();
	}

	public static void addLibraryBasket(File path) {
		LibraryBasket<ALLib> libraryBasket = new LibraryBasket<ALLib>(ALLib.class, path);
		libraryBaskets.add(libraryBasket);
	}

	private static ALLib[] getLibs() {
		addLib(new Reflexion());

		for (LibraryBasket<ALLib> libraryBasket : libraryBaskets) {
			for (ALLib lib : libraryBasket) {
				addLib(lib);
			}
		}
		return libs.toArray(new ALLib[libs.size()]);
	}

	public static void addLib(ALLib lib) {
		if (!libs.contains(lib)) {
			libs.add(lib);
		}
	}

	public static void executeFile(File file) throws FileNotFoundException, ScriptException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		executeScript(file.getPath(), ALScriptEngine.getScriptFromReader(br));
	}

	public static void executeScript(String filename, String script) throws ScriptException {

		// create parser and visitor
		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new RuntimeVisitor();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, getLibs());

		try {
			AL.execute(syntax, runtime, context, script);
		} catch (ALRuntimeException ex) {
			throw new ScriptException(ex.getMessage(), filename, ex.getToken().getLine() + 1, ex.getToken().getCol() + 1);
		} catch (JASIException ex) {
			throw new ScriptException(ex.getMessage(), filename, 1, ex.getPos());
		} catch (Exception ex) {
			throw new ScriptException(ex.getMessage());
		}

		MutableVariant ret = new MutableVariant();
		if (context.returning) {
			System.out.println(context.returnValue);
		}

	}

	public static MutableVariant executeScript(PEG syntax, AbstractRuntime runtime, RuntimeContext context, String script) {
		// init RUNTIME resources
		ASTBuilder builder = new ASTBuilder();

		try {

			syntax.parse(script, builder);
			setProgramToBlock(builder);

			// execution phase
			builder.injectVisitor(runtime).visite(context);
		} catch (ALRuntimeException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (JASIException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

		// get returned values
		MutableVariant ret = new MutableVariant();
		if (context.returning) {
			ret.setValue(context.returnValue);
			// or evaluated expression
		} else {
			List<AST> p0 = builder.getProgram();
			if (p0.size() > 0) {
				List<AST> p1 = ((Program) p0.get(0)).statements;
				if (p1.size() > 0) {
					AST tail = p1.get(p1.size() - 1);

					if (tail instanceof Expression) {
						Expression tailExp = ((Expression) tail);
						ret.setValue(tailExp.getLast().mv);
					}
				}
			}
		}

		if (ret.isInvalid() || ret.isFunction()) {
			ret.convertToVoid();
		}
		return ret;
	}

	public static void shell() throws IOException {

		System.out.println();
		System.out.println("Welcome to AL shell");
		System.out.println("Written by: Yann Caron - jan 2013");
		System.out.println();

		Syntax syntax = new Syntax();
		AbstractRuntime runtime = new RuntimeVisitor();

		// create context
		RuntimeContext context = AL.createContext(syntax, runtime, getLibs());

		// console
		String commandLine;
		ConsoleReader reader = new ConsoleReader();
		reader.setBellEnabled(false);

		Set<String> keys = new HashSet<String>();
		keys.add("(shell) help");
		keys.add("(shell) file [file.al]");
		keys.add("(shell) exit");
		keys.addAll(ALTools.getALKeywords(syntax));
		keys.addAll(ALTools.getALMagicMethods(runtime, "[%s].%s"));
		keys.addAll(ALTools.getALObjects(context));
		Object[] keyObjects = keys.toArray();
		String[] keywords = Arrays.copyOf(keyObjects, keyObjects.length, String[].class);
		reader.addCompletor(new SimpleCompletor(keywords));

		//Break with Ctrl+C
		while ((commandLine = reader.readLine("al> ")) != null) {
			//while (true) {
			try {
				//if just a return, loop
				if (commandLine.equals("")) {
					continue;
				} else if (commandLine.equals("help")) {
					printShellHelp();
				} else if (commandLine.startsWith("file ")) {
					String filename = commandLine.substring(4).trim();
					BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
					String script = ALScriptEngine.getScriptFromReader(br);
					executeScript(syntax, runtime, context, script);
				} else if (commandLine.equals("exit")) {
					System.out.println("Good bye");
					System.exit(0);
				} else {
					MutableVariant ret = executeScript(syntax, runtime, context, commandLine);
					if (!ret.isNull()) {
						System.out.println(ret.toString());
					}
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

		}
	}

}
