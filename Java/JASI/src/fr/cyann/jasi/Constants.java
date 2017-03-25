/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.jasi;

import fr.cyann.jasi.exception.MultilingMessage;

/**
 *
 * @author caronyn
 */
public interface Constants {

	static final MultilingMessage EX_MEMORIZER = new MultilingMessage(
			"Different statement cannot be stored in the same position !",
			"Des déclarations différentes ne peuvent pas être stoquée à la même position !");

	static final MultilingMessage EX_VISITOR_NOT_FOUND = new MultilingMessage(
			"No visitor defined for class '%s' !",
			"Aucun visiteur défini pour la classe '%s' !");

	static final MultilingMessage EX_INFINITE_RECURSION = new MultilingMessage(
			"Infinit recursion detected ! Verify your loops and calls.",
			"Recursion infinie detecté ! Vérifiez vos boucles et vos appels.");

	static final MultilingMessage EX_FACTORY = new MultilingMessage(
			"Cannot create instance for '%s' factory because: %s !",
			"L'instance '%s' n'a pas pu être créée parce que : %s !");

	static final MultilingMessage EX_INVALID_GRAMMAR = new MultilingMessage(
			"Unexpected grammar '%s' at line %d, column %d !",
			"Grammaire '%s' inatendue à la ligne %d, colonne %d");

	static final MultilingMessage EX_INVALID_SYMBOL = new MultilingMessage(
			"Invalid symbol '%s' at position %d",
			"Symbol '%s' invalid à la position %d");

	static final MultilingMessage EX_INVALID_SYMBOL_LINE = new MultilingMessage(
			"Invalid symbol '%s' at line %d, column %d !",
			"Symbol '%s' invalid à la ligne %d, colonne %d !");

	static final MultilingMessage EX_INVALID_SYMBOL_LINE_CLASS = new MultilingMessage(
			"Unexpected symbol '%s' at line %d, column %d, for class '%s' !",
			"Symbol '%s' invalid à la ligne %d, colonne %d, pour la classe '%s' !");

}
