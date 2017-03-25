/*
 YANN CARON CONFIDENTIAL
 __________________

 Yann Caron Copyright (c) 2011
 All Rights Reserved.
 __________________

 NOTICE:  All information contained herein is, and remains
 the property of Yann Caron and its suppliers, if any.
 The intellectual and technical concepts contained
 herein are proprietary to Yann Caron
 and its suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Yann Caron.
 */
package fr.cyann.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The reflect static class.<br>
 * Set of several usefull class for manipulating objects and attributes by
 * reflection.
 *
 * @author Yann Caron
 */
public class Reflect {

    /**
     * Get the private field recursivelly over class hierarchy.
     *
     * @param obj the object containing the field.
     * @param name the field name.
     * @return return the field.
     */
    public static Object getPrivateFieldValue(Object obj, String name) {
	try {
	    return getPrivateField(obj, name).get(obj);
	} catch (Exception ex) {
	    return null;
	}
    }

    /**
     * Set the private field recursivelly over class hierarchy.
     *
     * @param obj the object containing the field.
     * @param name the field name.
     * @param value the value to set
     */
    public static void setPrivateFieldValue(Object obj, String name, Object value) {
	Field field = getPrivateField(obj, name);
	if (field == null) {
	    throw new RuntimeException(String.format("Field [%s] in class [%s] not found !", name, obj.getClass().getName()));
	}
	try {
	    field.set(obj, value);
	} catch (Exception ex) {
	    throw new RuntimeException(String.format("Field [%s] in class [%s] throw exception !", name, obj.getClass().getName()));
	}
    }

    /**
     * Get the private field of a class with reflection.
     *
     * @param obj the object to get the private field
     * @param name the name of the field
     * @return the private field
     */
    public static Field getPrivateField(Object obj, String name) {
	return getPrivateField(obj.getClass(), obj, name);
    }

    /**
     * Get the private field of a class with reflection.
     *
     * @param cls the class concerned
     * @param obj the object to get the private field
     * @param name the name of the field
     * @return the private field
     */
    private static Field getPrivateField(Class cls, Object obj, String name) {
	try {
	    Field field = cls.getDeclaredField(name);
	    field.setAccessible(true);
	    return field;
	} catch (Exception ex) {
	    // recursive
	    if (cls.getSuperclass() != null) {
		Field res = getPrivateField(cls.getSuperclass(), obj, name);
		if (res != null) {
		    return res;
		}
	    }
	    for (Class in : cls.getInterfaces()) {
		Field res = getPrivateField(in, obj, name);
		if (res != null) {
		    return res;
		}
	    }
	}

	return null;
    }

    /**
     * Tool to get all class in java package.
     *
     * @param packageName the package name to explore.
     * @return the list of all classes.
     * @throws IOException if IOException occured.
     */
    public static List<String> getClassNamesFromPackage(String packageName) throws IOException {
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	URL packageURL;
	List<String> names = new ArrayList<String>();

	packageURL = classLoader.getResource(packageName.replace(".", "/"));

	File folder = new File(packageURL.getFile());
	File[] content = folder.listFiles();
	for (File file : content) {
	    String name = file.getName();

	    if (file.isDirectory()) {
		// recursive
		names.addAll(getClassNamesFromPackage(packageName + '.' + name));
	    } else if (name.contains(".")) {
		name = name.substring(0, name.lastIndexOf(".class"));
		names.add(packageName + '.' + name);
	    }
	}
	return names;
    }

    /**
     * Get recursivelly all the nested classes contained in a class.
     *
     * @param cls the class to inspect
     * @return all the nested classes
     */
    public static List<Class> getNestedClasses(Class cls) {

	List<Class> result = new ArrayList<Class>();

	for (Class inner : cls.getClasses()) {
	    if (inner.getClasses().length > 0) {
		result.addAll(getNestedClasses(inner));
	    } else {
		result.add(inner);
	    }
	}

	return result;
    }
}
