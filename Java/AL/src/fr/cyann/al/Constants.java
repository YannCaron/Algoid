/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al;

import fr.cyann.jasi.exception.MultilingMessage;

/**
 *
 * @author caronyn
 */
public interface Constants {

    	public static final String VERSION = "1.3.0";
	public static final String ENGINE_VERSION = "0.1.2";
	public static final String ENGINE_NAME = "AL Scripting engine";
	public static final String SHORT_NAME = "AL";
	public static final String FULL_NAME = "Algoid Language";
	
	static final MultilingMessage EX_VARIABLE_NOT_FOUND = new MultilingMessage(
			"Variable '%s' not found in current scope !",
			"La variable '%s' n'a pas été trouvée dans le context courant !");

	static final MultilingMessage EX_VARIABLE_NOT_FOUND_LINE = new MultilingMessage(
			"Variable '%s' not found in current scope at line: %s !",
			"La variable '%s' n'a pas été trouvée dans le context courant à la ligne : %s !");

	static final MultilingMessage EX_UNEXPECTED_TYPE = new MultilingMessage(
			"Unexpected type!",
			"Type inapproprié!");

	static final MultilingMessage EX_UNEXPECTED_TYPE_TYPE = new MultilingMessage(
			"Unexpected type '%s'!",
			"Type inapproprié '%s'!");

	static final MultilingMessage EX_INVALID_NUMBER_OF_PARAMETER = new MultilingMessage(
			"Invalid number of parameter for function %s, expecter: %d, found: %d !",
			"Nombre de paramètre invalide pour la fonction '%s', nombre attendu : %d, trouvé : '%d' !");

	static final MultilingMessage EX_INVALID_EXPRESSION = new MultilingMessage(
			"Invalid variable expression: %s !",
			"Variable invalide : %s !");

	static final MultilingMessage EX_CONVERTION = new MultilingMessage(
			"Type cannot been converted.",
			"Le type ne peut pas être converti.");

	static final MultilingMessage EX_CONVERTION_TYPE = new MultilingMessage(
			"Type '%s' cannot been converted to '%s'.",
			"Le type '%s' ne peut pas être converti en '%s'.");

	static final MultilingMessage EX_ASSERT = new MultilingMessage(
			"Expression '%s' expected but '%s' found at line %d col %d." ,
			"L'expression '%s' est attendue, mais c'est '%s' qui a été renvoyé à la ligne %d colonne %d.");

	static final MultilingMessage EX_CLASS_NOT_FOUND = new MultilingMessage(
			"Class '%s' does not exists !",
			"La classe '%s' n'existe pas !");

	static final MultilingMessage EX_CONSTRUCTOR_NOT_FOUND = new MultilingMessage(
			"Native Java constructor not found for object '%s' !",
			"Le constructeur natif Java n'a pas été trouvé pour l'objet '%s' !");

	static final MultilingMessage EX_ERROR = new MultilingMessage(
			"Error at line %d , col %d '%s' occured with object '%s' '%s' '%s' with parameters '%s' and values '%s' !",
			"Une erreur à la ligne %d, column %d '%s' est survenue avec l'objet '%s' '%s' '%s' avec pour paramètres '%s' et valeurs '%s' !");

	static final MultilingMessage EX_CALL_SYMBOL = new MultilingMessage(
			"Symbol '%s' is not a function call symbol !",
			"Le symbol '%s' n'est pas une symbol d'appel de fonction !");

		static final MultilingMessage EX_AST_REPLACE_IMPOSSIBLE = new MultilingMessage(
			"Impossible AST replacement of element '%s' by '%s' in node '%s' !",
			"Remplacement AST impossible de l'élément '%s' par '%s' dans le noeud '%s' !");

}
