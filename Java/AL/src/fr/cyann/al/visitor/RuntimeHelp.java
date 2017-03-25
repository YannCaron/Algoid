/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.visitor;

import fr.cyann.al.generator.GenerateClass;
import fr.cyann.al.generator.Lang;
import fr.cyann.al.generator.Platform;
import fr.cyann.al.generator.Reference;
import fr.cyann.al.generator.ReferenceType;
import fr.cyann.al.generator.Section;

/**
 * <p>
 * @author cyann
 */
public final class RuntimeHelp {

	@GenerateClass(name = "")
	public interface ALHelp {

		@Reference(
				type = ReferenceType.method,
				platform = Platform.all,
				parameters = "text",
				example = "%s%s (\"Hi Algoid!\");",
				sections = {
					@Section(lang = Lang.en, content = "Print the text parameter to the standard output."),
					@Section(lang = Lang.fr, content = "Ecrit le text pris en paramètre, dans la sortie standard.")
				}
		)
		void print();

		@Reference(
				type = ReferenceType.method,
				platform = Platform.all,
				parameters = "script",
				example = "%s%s (\"print (7 + 7);\");",
				sections = {
					@Section(lang = Lang.en, content = "Evaluate (run) the AL script."),
					@Section(lang = Lang.fr, content = "Exécute le script AL passé en paramètre.")
				}
		)
		void eval();

		@Reference(
				type = ReferenceType.method,
				platform = Platform.all,
				parameters = "variable",
				example = "%s%s (\"print (exists(doesNotExistsVariable));\");",
				sections = {
					@Section(lang = Lang.en, content = "Return true if the variable was declared and is reachable."),
					@Section(lang = Lang.fr, content = "Retourne vrai si la variable a été déclarée et qu'elle est accessible.")
				}
		)
		void exists();

	}
}
