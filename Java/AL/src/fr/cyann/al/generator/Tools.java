/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 *
 * @author CARONYN
 */
public final class Tools {

	private Tools() {
		throw new RuntimeException("Static class cannot be instancied!");
	}

	public static boolean containsAnnotation(Annotation[] annotations, Class<? extends Annotation> typeToFind) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(typeToFind)) {
				return true;
			}
		}
		return false;
	}

	public static <T extends Annotation> T findAnnotation(Annotation[] annotations, Class<T> typeToFind) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(typeToFind)) {
				return (T) annotation;
			}
		}
		return null;
	}

	public static String getMethodName(Method method) {
		Rename anot = Tools.findAnnotation(method.getAnnotations(), Rename.class);
		if (anot != null && !"".equals(anot.newName())) {
			return anot.newName();
		} else {
			return method.getName();
		}

	}
}
