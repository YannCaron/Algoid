/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 <p>
 @author CARONYN
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.
public @interface GenerateClass {

	public String name();

	ReferenceType type() default ReferenceType.object;

	Platform platform() default Platform.all;

	Dependency[] dependencies() default {};

	Section[] sections() default {};

}
