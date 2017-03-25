/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

/**
 *
 * @author Yann Caron
 */
public class UnitTesting {

	/**
	 * Test accessor of the object.
	 * @param obj the object to test
	 * @param fieldName the field name
	 * @param fieldValue the excepted value
	 * @param isInConstructor if value is initialize in constructor
	 */
	public static void assertAccessorAndMutator(Object obj, String fieldName, Object fieldValue, boolean isInConstructor) {
		try {
			assertNotNull("Object cannot be null !", obj);
			Field field = Reflect.getPrivateField(obj, fieldName);

			// constructor
			if (isInConstructor) {
				assertNotNull("Field must be instanciated by constructor !", field.get(obj));
				assertEquals("The field does not have the same value as instanciated by constructor ?", fieldValue, field.get(obj));
			} else {
				assertNull("Field must be null at beginning !", field.get(obj));
			}

			// getter
			String accessorName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Method accessor = obj.getClass().getMethod(accessorName);

			field.set(obj, fieldValue);
			assertNotNull("Field cannot be null if setted !", field.get(obj));

			Object result = accessor.invoke(obj);
			assertNotNull("Getter return null if variable setted ?", field.get(obj));
			assertEquals("The Accessor modify value of the attribute ?", fieldValue, result);

			// setter
			String mutatorName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			Method mutator = null;
			try {
				mutator = obj.getClass().getMethod(mutatorName, fieldValue.getClass());
			} catch (Exception ex) {
			}

			if (mutator != null) {
				field.set(obj, null);
				mutator.invoke(obj, fieldValue);

				result = field.get(obj);
				assertNotNull("Getter return null if mutator has setted it ?", result);
				assertEquals("The Mutator modify value of the attribute ?", fieldValue, result);
			} else {
				System.out.println(String.format("Mutator [%s] does not exists in class [%s] !", mutatorName, obj.getClass().getName()));
			}

		} catch (Exception ex) {
			fail("Exception occured: " + ex);
			ex.printStackTrace();
		}

	}

	/**
	 * Test the method and retreive the system output
	 * @param expected the expected system output
	 * @param runnable the method to test
	 */
	public static void assertOutput(String expected, Runnable runnable) {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		runnable.run();
		String result = outContent.toString();

		System.setOut(System.out);

		assertEquals("Output is not expected ?", expected.replace(result, result), result);
	}

	/**
	 * Test if execution throw an exception.
	 * @param runnable the test
	 */
	public static void assertException(Runnable runnable) {
		try {
			runnable.run();
			fail("Exception expected !");
		} catch (Exception e) {
		}
	}
}
