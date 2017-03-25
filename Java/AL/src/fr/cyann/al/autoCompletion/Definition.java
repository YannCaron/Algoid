/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.autoCompletion;

/**
 *
 * @author caronyn
 */
public class Definition {

	// attribute
	private final String keyword;
	private String description;

	// constructor
	public Definition(String keyword) {
		this.keyword = keyword;
	}

	// property
	public String getKeyword() {
		return keyword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// override
	@Override
	public String toString() {
		return "Definition{" + "keyword=" + keyword + ", description=" + description + '}';
	}

}
