/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * @author CARONYN
 */
public class DocGenerator {

	// const
	private static final Comparator<Doc> DOC_COMPARATOR = new Comparator<Doc>() {

		@Override
		public int compare(Doc o1, Doc o2) {
			if (o1.getType().ordinal() < o2.getType().ordinal()) {
				return -1;
			} else if (o1.getType().ordinal() > o2.getType().ordinal()) {
				return 1;
			} else {
				return o1.getName().compareTo(o2.getName());
			}
		}
	};

	public static String CapFirstLetter(String text) {
		if (text.length() > 0) {
			return text.substring(0, 1).toUpperCase() + text.substring(1);
		}
		return text;
	}

	// inner class
	private static class Doc {

		private final ReferenceType type;
		private final String name;
		private final Method method;

		public ReferenceType getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public Method getMethod() {
			return method;
		}

		public Doc(ReferenceType type, String name, Method method) {
			this.type = type;
			this.name = name;
			this.method = method;
		}

	}

	// attributes
	private final Class<?> clazz;
	private final String alName;
	private final StringBuilder source;
	private int indent = 0;
	private final GenerateClass genClass;

	private String getGenerateClassName(Class<?> clazz) {
		GenerateClass gc = Tools.findAnnotation(clazz.getAnnotations(), GenerateClass.class);
		if (gc == null) {
			throw new RuntimeException(String.format("GenerateClass annotation forbiden for class [%s]!", clazz));
		}
		return gc.name();
	}

	public DocGenerator(Class<?> clazz) {
		genClass = Tools.findAnnotation(clazz.getAnnotations(), GenerateClass.class);

		alName = getGenerateClassName(clazz);
		this.clazz = clazz;
		this.source = new StringBuilder();
	}

	// property
	public DocGenerator fixIndent(int indent) {
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

	private String getKeywordFormat(Reference reference, String alName) {
		StringBuilder format = new StringBuilder();
		if (!"".equals(alName)) {
			format.append("%1$s.");
		}
		format.append("%2$s");

		return format.toString();
	}

	private String getSyntaxFormat(Reference reference, String alName) {
		StringBuilder format = new StringBuilder();
		if (!"".equals(alName)) {
			format.append("%1$s.");
		}
		format.append("%2$s");
		if (reference.type() != ReferenceType.enumeration) {
			format.append("(%3$s)");
		}

		return format.toString();
	}

	private void generateLinks(Section section, StringBuilder content) {
		Class<?>[] links = section.links();
		for (Class<?> link : links) {
			String name = getGenerateClassName(link);
			if (section.lang() == Lang.en) {
				content.append(String.format(" For complet %1$s reference see &lt;a href=\"#%1$s\"&gt;%1$s&lt;/a&gt;.", name));
			} else if (section.lang() == Lang.fr) {
				content.append(String.format(" Voir l'objet &lt;a href=\"#%1$s\"&gt;%1$s&lt;/a&gt; pour une référence complète.", name));
			} else {
				throw new RuntimeException(String.format("Unsuported lang [%s] for link!", section.lang()));
			}
		}
	}

	private void generateFactoryLink(Section section, String methodName) {
		Class<?> factory = section.factory();
		if (factory != Object.class && methodName.length() > 0) {
			String content;
			String objectName = getGenerateClassName(factory);
			if (section.lang() == Lang.en) {
				content = String.format(
						"%3$s object could be created by factory: %1$s.create%3$s(). For complete reference, see &lt;a href=\"#%1$s.create%3$s\"&gt;%1$s.create%3$s()&lt;/a&gt;.",
						objectName, methodName, CapFirstLetter(methodName));
			} else if (section.lang() == Lang.fr) {
				content = String.format(
						"L'objet %2$s doit être créé par la méthode de fabrication : %1$s.create%3$s(). Voir : &lt;a href=\"#%1$s.create%3$s\"&gt;%1$s.create%3$s()&lt;/a&gt; pour une référence complète.",
						objectName, methodName, CapFirstLetter(methodName));
			} else {
				throw new RuntimeException(String.format("Unsuported lang [%s] for link!", section.lang()));
			}

			ret(2);
			source.append(String.format("<section type=\"%s\" lang=\"%s\">%s</section>",
					section.type(), section.lang(), content));

		}
	}

	private void generateSeeAlso(Reference reference) {
		String[] paths = reference.seeAlso();

		StringBuilder links = new StringBuilder();

		if (paths.length > 0) {
			for (String path : paths) {

				if (links.length() > 0) {
					links.append(", ");
				}
				links.append(String.format("&lt;a href=\"#%1$s\"&gt;%1$s&lt;/a&gt;.", path));

			}

			ret(2);
			source.append(String.format("<section type=\"%s\" lang=\"%s\">See algo:%s</section>",
					SectionType.text, Lang.en.toString(), links));

			ret(2);
			source.append(String.format("<section type=\"%s\" lang=\"%s\">Voir aussi :%s</section>",
					SectionType.text, Lang.fr.toString(), links));
		}

	}

	private void generateDependenciesLink(Section section) {

		String objectName = getGenerateClassName(clazz);

		for (Dependency dependency : genClass.dependencies()) {
			if (dependency.action() == Dependency.Action.inheritanceLink) {
				String inheritedObject = getGenerateClassName(dependency.reference());
				String content;
				if (section.lang() == Lang.en) {
					content = String.format(
							"%2$s object inherits from %3$s object, so all of methods of %3$s are available. For complete reference, see &lt;a href=\"#%3$s\"&gt;%3$s&lt;/a&gt;.",
							objectName, CapFirstLetter(objectName), inheritedObject);
				} else if (section.lang() == Lang.fr) {
					content = String.format(
							"L'objet %1$s hérite de l'objet %3$s donc, toutes les méthodes de l'objet %3$s lui sont accessibles. Voir : &lt;a href=\"#%3$s\"&gt;%3$s&lt;/a&gt; pour une référence complète.",
							objectName, CapFirstLetter(objectName), inheritedObject);
				} else {
					throw new RuntimeException(String.format("Unsuported lang [%s] for link!", section.lang()));
				}

				ret(2);
				source.append(String.format("<section type=\"%s\" lang=\"%s\">%s</section>",
						section.type(), section.lang(), content));

			}
		}

	}

	private void generateObject(GenerateClass genClass) {
		Section[] sections = genClass.sections();

		ret();
		source.append(String.format(
				"<reference keyword=\"%1$s\" package=\"%1$s\" function=\"\" syntax=\"\" type=\"%2$s\" kind=\"method\" platform=\"%3$s\">",
				alName, genClass.type().getText(), genClass.platform().getText()
		));

		if (sections.length > 0) {

			ret(1);
			source.append("<description>");
			for (Section section : sections) {

				StringBuilder content = new StringBuilder(section.content());

				generateSection(section, content);

				generateDependenciesLink(section);

				generateFactoryLink(section, alName);

			}

			ret(1);
			source.append("</description>");

		}

		ret();
		source.append("</reference>");

	}

	private void generateSection(Section section, StringBuilder content) {
		ret(2);

		if (section.type() == SectionType.text) {
			source.append(String.format("<section type=\"%s\" lang=\"%s\">%s</section>",
					section.type(), section.lang(), content.toString()
			));
		} else {
			source.append(String.format("<section type=\"%s\">%s</section>",
					section.type(), content.toString()
			));
		}
	}

	private void generateMethod(Method method) {
		Reference reference = Tools.findAnnotation(method.getAnnotations(), Reference.class);

		if (reference != null) {
			ret();
			String keywordFormat = getKeywordFormat(reference, alName);
			String syntaxFormat = getSyntaxFormat(reference, alName);
			source.append(String.format(
					"<reference keyword=\"" + keywordFormat + "\" package=\"%1$s\" function=\"%2$s\" syntax=\"" + syntaxFormat + "\" type=\"%4$s\" kind=\"method\" platform=\"%5$s\">",
					alName, Tools.getMethodName(method), reference.parameters(), reference.type().getText(), reference.platform().getText()
			));

			String example = reference.example();
			if (example != null && !"".equals(example)) {
				ret(1);
				source.append(
						String.format("<example>%s</example>",
								String.format(example, alName, Tools.getMethodName(method))
						));
			}

			Section[] sections = reference.sections();
			if (sections.length > 0) {
				ret(1);
				source.append("<description>");
				for (Section section : sections) {

					StringBuilder content = new StringBuilder(section.content());
					generateLinks(section, content);

					generateSection(section, content);

				}

				generateSeeAlso(reference);

				ret(1);
				source.append("</description>");
			}

			ret();
			source.append("</reference>");

		}
	}

	private static void appendMethods(List<Doc> docs, Class<?> cls) {
		for (Method method : cls.getDeclaredMethods()) {
			if (Tools.containsAnnotation(method.getAnnotations(), Reference.class
			)) {
				Reference reference = Tools.findAnnotation(method.getAnnotations(), Reference.class);

				docs.add(
						new Doc(reference.type(), Tools.getMethodName(method), method));
			}
		}
	}

	public String generate() {

		source.delete(0, source.length());

		List<Doc> docs = new ArrayList<Doc>();

		// get all methods
		appendMethods(docs, clazz);

		// copy all methods of dependencies
		for (Dependency dependency : genClass.dependencies()) {
			if (dependency.action() == Dependency.Action.copy) {
				appendMethods(docs, dependency.reference());
			}
		}

		// sort
		Collections.sort(docs, DOC_COMPARATOR);

		generateObject(genClass);

		for (Doc doc : docs) {
			generateMethod(doc.getMethod());
		}

		return source.toString();
	}

	@Override
	public String toString() {
		return source.toString();
	}
}
