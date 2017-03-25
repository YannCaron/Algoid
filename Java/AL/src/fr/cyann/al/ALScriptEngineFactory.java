package fr.cyann.al;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/**
 *
 * @author CARONYN
 */
public class ALScriptEngineFactory implements ScriptEngineFactory {

	private static final String FILEEXT = "al";

	private static final String[] MIMETYPES = {
		"text/plain",
		"text/algoidlanguage",
		"application/algoidlanguage"
	};

	private static final String[] NAMES = {
		"AlgoidLanguage", "AL", "Algoid", "Algoid Language", "al", "algoidlanguage", "algoid language"
	};

	private final ScriptEngine scriptEngine;
	private final List<String> extensions;
	private final List<String> mimeTypes;
	private final List<String> names;

	public ALScriptEngineFactory() {
		scriptEngine = new ALScriptEngine();
		extensions = Collections.nCopies(1, FILEEXT);
		mimeTypes = Arrays.asList(MIMETYPES);
		names = Arrays.asList(NAMES);
	}

	@Override
	public String getEngineName() {
		return getScriptEngine().getBindings(ScriptContext.GLOBAL_SCOPE).get(ScriptEngine.ENGINE).toString();
	}

	@Override
	public String getEngineVersion() {
		return getScriptEngine().getBindings(ScriptContext.GLOBAL_SCOPE).get(ScriptEngine.ENGINE_VERSION).toString();
	}

	@Override
	public List<String> getExtensions() {
		return extensions;
	}

	@Override
	public List<String> getMimeTypes() {
		return mimeTypes;
	}

	@Override
	public List<String> getNames() {
		return names;
	}

	@Override
	public String getLanguageName() {
		return getScriptEngine().getBindings(ScriptContext.GLOBAL_SCOPE).get(ScriptEngine.LANGUAGE).toString();
	}

	@Override
	public String getLanguageVersion() {
		return getScriptEngine().getBindings(ScriptContext.GLOBAL_SCOPE).get(ScriptEngine.LANGUAGE_VERSION).toString();
	}

	@Override
	public Object getParameter(String key) {
		return getScriptEngine().get(key).toString();
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		StringBuffer sb = new StringBuffer();
		if (obj != null && !"".equals(obj)) {
			sb.append(obj + ".");
		}
		sb.append(m + "(");
		int len = args.length;
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(args[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return "print(" + toDisplay + ")";
	}

	@Override
	public String getProgram(String... statements) {
		StringBuffer sb = new StringBuffer();
		int len = statements.length;
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				sb.append('\n');
			}
			sb.append(statements[i]);
		}
		return sb.toString();
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public static void main(String[] args) throws FileNotFoundException, ScriptException, IOException {
		Main.main(args);
	}
}
